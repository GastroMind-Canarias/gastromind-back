package com.gastromind.api.application.usecases;

import com.gastromind.api.application.services.TicketProductResolutionService;
import com.gastromind.api.application.services.TicketQuantityUnitResolver;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.models.Store;
import com.gastromind.api.domain.models.Ticket;
import com.gastromind.api.domain.models.TicketItem;
import com.gastromind.api.domain.models.Unit;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.TicketLineVerificationStatus;
import com.gastromind.api.domain.models.ticket.ExtractedTicketLine;
import com.gastromind.api.domain.models.ticket.ExtractedTicketReceipt;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.ports.in.ITicketService;
import com.gastromind.api.domain.ports.out.StoreRepository;
import com.gastromind.api.domain.ports.out.TicketExtractionPort;
import com.gastromind.api.domain.ports.out.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImportTicketFromImageUseCase {

    private final TicketExtractionPort extraction;
    private final TicketProductResolutionService productResolution;
    private final TicketQuantityUnitResolver unitResolver;
    private final ITicketService ticketService;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    public ImportTicketFromImageUseCase(
            TicketExtractionPort extraction,
            TicketProductResolutionService productResolution,
            TicketQuantityUnitResolver unitResolver,
            ITicketService ticketService,
            UserRepository userRepository,
            StoreRepository storeRepository) {
        this.extraction = extraction;
        this.productResolution = productResolution;
        this.unitResolver = unitResolver;
        this.ticketService = ticketService;
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
    }

    @Transactional
    public Ticket execute(byte[] imageBytes, String mimeType, String userId, String storeIdOrNull) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        ExtractedTicketReceipt extracted = extraction.extractFromImage(imageBytes, mimeType);

        Store store = resolveStore(storeIdOrNull, extracted.storeName());

        List<TicketItem> items = new ArrayList<>();
        for (ExtractedTicketLine line : extracted.lines()) {
            Product product = productResolution.resolveOrCreate(line.productName());
            BigDecimal qtyAmt = line.quantityAmount().max(BigDecimal.ZERO);
            if (qtyAmt.compareTo(BigDecimal.ZERO) <= 0) {
                qtyAmt = BigDecimal.ONE;
            }
            Unit unit = unitResolver.resolveFromAiCode(line.quantityUnit());

            BigDecimal unitPrice = inferUnitPrice(line, qtyAmt, unit);

            TicketItem ti = new TicketItem();
            ti.setProduct(product);
            ti.setQuantity(qtyAmt);
            ti.setUnit(unit);
            ti.setPriceUnit(unitPrice);
            boolean pending = line.lineNeedsVerification()
                    || (line.lineQualityNote() != null && !line.lineQualityNote().isBlank());
            if (pending) {
                ti.setVerificationStatus(TicketLineVerificationStatus.PENDING_REVIEW);
                ti.setLineNote(line.lineQualityNote() != null ? line.lineQualityNote().trim() : null);
            } else {
                ti.setVerificationStatus(TicketLineVerificationStatus.OK);
                ti.setLineNote(null);
            }
            items.add(ti);
        }

        float total = resolveTicketTotal(extracted, items);

        LocalDate purchaseDate = extracted.purchaseDate() != null ? extracted.purchaseDate() : LocalDate.now();

        User userRef = new User(userId);
        Ticket ticket = new Ticket();
        ticket.setUser_id(userRef);
        ticket.setStore_id(store);
        ticket.setTotal_amount(total);
        ticket.setPurchaseDate(purchaseDate);
        ticket.setItems(items);

        return ticketService.create(ticket);
    }

    /**
     * Precio referido a la unidad de medida: €/kg (g o kg), €/l (ml o l), €/unidad (ud).
     */
    private static BigDecimal inferUnitPrice(ExtractedTicketLine line, BigDecimal qtyAmt, Unit unit) {
        if (line.unitPrice() != null && line.unitPrice().compareTo(BigDecimal.ZERO) > 0) {
            return line.unitPrice().setScale(4, RoundingMode.HALF_UP);
        }
        if (line.lineTotal() == null || line.lineTotal().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        }
        String c = TicketQuantityUnitResolver.canonicalCode(unit);
        return switch (c) {
            case "g" -> line.lineTotal().divide(
                    qtyAmt.divide(BigDecimal.valueOf(1000), 8, RoundingMode.HALF_UP),
                    4, RoundingMode.HALF_UP);
            case "kg" -> line.lineTotal().divide(qtyAmt, 4, RoundingMode.HALF_UP);
            case "ml" -> line.lineTotal().divide(
                    qtyAmt.divide(BigDecimal.valueOf(1000), 8, RoundingMode.HALF_UP),
                    4, RoundingMode.HALF_UP);
            case "l" -> line.lineTotal().divide(qtyAmt, 4, RoundingMode.HALF_UP);
            default -> line.lineTotal().divide(qtyAmt, 4, RoundingMode.HALF_UP);
        };
    }

    private static float resolveTicketTotal(ExtractedTicketReceipt extracted, List<TicketItem> items) {
        BigDecimal sumLines = BigDecimal.ZERO;
        int withTotal = 0;
        for (ExtractedTicketLine l : extracted.lines()) {
            if (l.lineTotal() != null && l.lineTotal().compareTo(BigDecimal.ZERO) > 0) {
                sumLines = sumLines.add(l.lineTotal());
                withTotal++;
            }
        }
        if (withTotal == extracted.lines().size() && sumLines.compareTo(BigDecimal.ZERO) > 0) {
            return sumLines.floatValue();
        }
        if (extracted.totalAmount().compareTo(BigDecimal.ZERO) > 0) {
            return extracted.totalAmount().floatValue();
        }
        return sumLineTotals(items);
    }

    private Store resolveStore(String storeIdOrNull, String extractedStoreName) {
        if (storeIdOrNull != null && !storeIdOrNull.isBlank()) {
            return storeRepository.findById(storeIdOrNull)
                    .orElseThrow(() -> new NotFoundException("Tienda no encontrada"));
        }
        String name = extractedStoreName != null ? TicketProductResolutionService.normalizeName(extractedStoreName) : "";
        if (!name.isEmpty()) {
            return storeRepository.findFirstByNameIgnoreCase(name)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No se indicó store_id y no hay tienda en catálogo con nombre: " + name
                                    + ". Cree la tienda o envíe store_id."));
        }
        throw new IllegalArgumentException(
                "No se indicó store_id y el ticket no muestra un nombre de tienda reconocible. Envíe store_id.");
    }

    private static float sumLineTotals(List<TicketItem> items) {
        BigDecimal sum = BigDecimal.ZERO;
        for (TicketItem ti : items) {
            BigDecimal line = approximateLineAmount(ti);
            sum = sum.add(line);
        }
        return sum.floatValue();
    }

    private static BigDecimal approximateLineAmount(TicketItem ti) {
        String c = TicketQuantityUnitResolver.canonicalCode(ti.getUnit());
        BigDecimal q = ti.getQuantity();
        BigDecimal p = ti.getPriceUnit();
        return switch (c) {
            case "g" -> p.multiply(q.divide(BigDecimal.valueOf(1000), 8, RoundingMode.HALF_UP));
            case "ml" -> p.multiply(q.divide(BigDecimal.valueOf(1000), 8, RoundingMode.HALF_UP));
            default -> p.multiply(q);
        };
    }
}
