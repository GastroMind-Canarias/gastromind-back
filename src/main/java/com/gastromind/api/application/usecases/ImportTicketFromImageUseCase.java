package com.gastromind.api.application.usecases;

import com.gastromind.api.application.services.TicketProductResolutionService;
import com.gastromind.api.application.services.TicketQuantityUnitResolver;
import com.gastromind.api.application.services.StoreResolutionResult;
import com.gastromind.api.application.services.StoreResolutionService;
import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.models.Ticket;
import com.gastromind.api.domain.models.TicketItem;
import com.gastromind.api.domain.models.Unit;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.TicketLineVerificationStatus;
import com.gastromind.api.domain.models.ticket.ExtractedTicketLine;
import com.gastromind.api.domain.models.ticket.ExtractedTicketReceipt;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.ports.in.IFridgeItemService;
import com.gastromind.api.domain.ports.in.ITicketService;
import com.gastromind.api.domain.ports.out.FridgeRepository;
import com.gastromind.api.domain.ports.out.ProductRepository;
import com.gastromind.api.domain.ports.out.TicketExtractionPort;
import com.gastromind.api.domain.ports.out.UserRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ItemStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
/**
 * Caso de uso que importa un ticket desde una imagen y lo persiste en el sistema.
 * Ademas, proyecta sus lineas al inventario de nevera del hogar cuando corresponde.
 */
public class ImportTicketFromImageUseCase {

    private final TicketExtractionPort extraction;
    private final ProductRepository productRepository;
    private final TicketQuantityUnitResolver unitResolver;
    private final ITicketService ticketService;
    private final UserRepository userRepository;
    private final StoreResolutionService storeResolutionService;
    private final FridgeRepository fridgeRepository;
    private final IFridgeItemService fridgeItemService;
    /**
     * Constructor con las dependencias necesarias para extraer, normalizar y guardar tickets.
     *
     * @param extraction puerto de extraccion OCR/IA de tickets
     * @param productRepository repositorio de productos del catalogo
     * @param unitResolver resolvedor de unidades detectadas en lineas del ticket
     * @param ticketService servicio de persistencia de tickets
     * @param userRepository repositorio de usuarios
     * @param storeRepository repositorio de tiendas
     * @param fridgeRepository repositorio de neveras
     * @param fridgeItemService servicio para anadir lineas de ticket a nevera
     */

    public ImportTicketFromImageUseCase(
            TicketExtractionPort extraction,
            ProductRepository productRepository,
            TicketQuantityUnitResolver unitResolver,
            ITicketService ticketService,
            UserRepository userRepository,
            StoreResolutionService storeResolutionService,
            FridgeRepository fridgeRepository,
            IFridgeItemService fridgeItemService) {
        this.extraction = extraction;
        this.productRepository = productRepository;
        this.unitResolver = unitResolver;
        this.ticketService = ticketService;
        this.userRepository = userRepository;
        this.storeResolutionService = storeResolutionService;
        this.fridgeRepository = fridgeRepository;
        this.fridgeItemService = fridgeItemService;
    }
    /**
     * Procesa la imagen del ticket y devuelve el ticket persistido con sus lineas.
     *
     * @param imageBytes contenido binario de la imagen
     * @param mimeType tipo MIME de la imagen
     * @param userId identificador del usuario propietario del ticket
     * @param storeIdOrNull identificador de tienda opcional; si es nulo, se intenta resolver por nombre extraido
     * @return ticket guardado en base de datos
     * @throws NotFoundException si el usuario o la tienda indicada no existen
     * @throws IllegalArgumentException si faltan datos minimos para resolver productos o tienda
     */

    @Transactional
    public ImportTicketFromImageResult execute(byte[] imageBytes, String mimeType, String userId, String storeIdOrNull) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        ExtractedTicketReceipt extracted = extraction.extractFromImage(imageBytes, mimeType);

        StoreResolutionResult resolution = storeResolutionService.resolve(storeIdOrNull, extracted.storeName());

        List<TicketItem> items = new ArrayList<>();
        for (ExtractedTicketLine line : extracted.lines()) {
            String normalized = TicketProductResolutionService.normalizeName(line.productName());
            if (normalized.isEmpty()) {
                throw new IllegalArgumentException("Nombre de producto vacio en una linea del ticket");
            }
            Optional<Product> catalog = productRepository.findFirstByNameIgnoreCase(normalized);

            BigDecimal qtyAmt = line.quantityAmount().max(BigDecimal.ZERO);
            if (qtyAmt.compareTo(BigDecimal.ZERO) <= 0) {
                qtyAmt = BigDecimal.ONE;
            }
            Unit unit = unitResolver.resolveFromAiCode(line.quantityUnit());

            BigDecimal unitPrice = inferUnitPrice(line, qtyAmt, unit);

            TicketItem ti = new TicketItem();
            catalog.ifPresentOrElse(ti::setProduct, () -> ti.setLineProductName(normalized));
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
        ticket.setStore_id(resolution.store());
        ticket.setTotal_amount(total);
        ticket.setPurchaseDate(purchaseDate);
        ticket.setItems(items);

        Ticket saved = ticketService.create(ticket);
        pushTicketLinesToFridge(saved);
        return new ImportTicketFromImageResult(saved, resolution.pendingStore(), resolution.detectedStoreName());
    }

    private void pushTicketLinesToFridge(Ticket ticket) {
        if (ticket.getHouseHold_id() == null || ticket.getHouseHold_id().getId() == null
                || ticket.getItems() == null || ticket.getItems().isEmpty()) {
            return;
        }
        Fridge fridge = fridgeRepository.findFirstByHouseholdId(ticket.getHouseHold_id().getId()).orElse(null);
        if (fridge == null || fridge.getId() == null) {
            return;
        }
        String fridgeId = fridge.getId();
        for (TicketItem item : ticket.getItems()) {
            if (item.getQuantity() == null || item.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            if (item.getProduct() != null && item.getProduct().getId() != null) {
                fridgeItemService.addProductToFridge(fridgeId, item.getProduct().getId(), item.getQuantity(), null,
                        ItemStatus.GOOD);
            } else if (item.getLineProductName() != null && !item.getLineProductName().isBlank()) {
                fridgeItemService.addLabeledItemToFridge(fridgeId, item.getLineProductName().trim(),
                        item.getQuantity(), null, ItemStatus.GOOD);
            }
        }
    }

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




