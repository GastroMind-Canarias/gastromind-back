package com.gastromind.api.application.services;

import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.models.Ticket;
import com.gastromind.api.domain.models.TicketItem;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.UsualPurchase;
import com.gastromind.api.domain.ports.out.UsualPurchaseRepository;
import com.gastromind.api.infrastructure.config.UsualPurchaseProperties;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
/**
 * Sincroniza compras habituales a partir de tickets confirmados.
 */
public class UsualPurchaseTicketSyncService {

    private final UsualPurchaseRepository usualPurchaseRepository;
    private final UsualPurchaseProperties properties;
    /**
     * Crea el servicio con sus dependencias de persistencia y configuracion.
     * @param usualPurchaseRepository repositorio de compras habituales
     * @param properties propiedades de ajuste del algoritmo de sincronizaciAn
     */

    public UsualPurchaseTicketSyncService(
            UsualPurchaseRepository usualPurchaseRepository,
            UsualPurchaseProperties properties) {
        this.usualPurchaseRepository = usualPurchaseRepository;
        this.properties = properties;
    }
    /**
     * Define sugerencias de compra habitual a partir de un ticket reciAn creado.
     * @param ticket ticket sobre el que se calcularA la sincronizaciAn
     */

    public void syncAfterTicketCreated(Ticket ticket) {
        if (ticket == null || ticket.getUser_id() == null || ticket.getUser_id().getId() == null
                || ticket.getItems() == null || ticket.getItems().isEmpty()) {
            return;
        }
        String userId = ticket.getUser_id().getId();
        double w = properties.getTicketSyncBlendWeight();
        if (w < 0 || w > 1) {
            w = 0.35;
        }
        BigDecimal oneMinusW = BigDecimal.ONE.subtract(BigDecimal.valueOf(w));
        BigDecimal wBd = BigDecimal.valueOf(w);

        for (TicketItem item : ticket.getItems()) {
            if (item.getProduct() == null || item.getProduct().getId() == null) {
                continue;
            }
            String canonical = TicketQuantityUnitResolver.canonicalCode(item.getUnit());
            BigDecimal observed = UsualPurchaseQuantityMath.toCanonicalAmount(item.getQuantity(), canonical);

            Optional<UsualPurchase> existing = usualPurchaseRepository.findByUserIdAndProductId(
                    userId, item.getProduct().getId());

            UsualPurchase toSave;
            if (existing.isEmpty()) {
                toSave = new UsualPurchase();
                toSave.setUser_id(new User(userId));
                toSave.setProduct_id(item.getProduct());
                toSave.setTarget_quantity(observed.setScale(4, RoundingMode.HALF_UP).floatValue());
            } else {
                toSave = existing.get();
                BigDecimal old = BigDecimal.valueOf(toSave.getTarget_quantity());
                BigDecimal blended = old.multiply(oneMinusW).add(observed.multiply(wBd));
                toSave.setTarget_quantity(blended.setScale(4, RoundingMode.HALF_UP).floatValue());
            }
            usualPurchaseRepository.save(toSave);
        }
    }
}




