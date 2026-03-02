package com.gastromind.api.application.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.FrequentPurchaseSuggestion;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.UsualPurchase;
import com.gastromind.api.domain.ports.in.IFrequentPurchaseService;
import com.gastromind.api.domain.ports.in.IUserService;
import com.gastromind.api.domain.ports.out.UsualPurchaseRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.ProductEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.TicketEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.TicketItemEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.ProductMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.TicketJpaRepository;

/**
 * IdentificarComprasHabituales (AnalyzeFrequentPurchases)
 *
 * Analiza el histórico de tickets del usuario para identificar qué productos
 * aparecen con mayor frecuencia y sugiere añadirlos a usual_purchase.
 *
 * Algoritmo:
 * 1. Obtiene todos los tickets del usuario con JOIN FETCH de sus ítems y
 * productos.
 * 2. Agrupa los ítems por producto y cuenta en cuántos tickets distintos
 * aparece (frecuencia).
 * 3. Calcula la cantidad media comprada por ticket.
 * 4. Filtra productos que superan el umbral minFrequency.
 * 5. Ordena de mayor a menor frecuencia.
 * 6. Marca cuáles ya están en usual_purchase.
 */
@Service
public class FrequentPurchaseServiceImpl implements IFrequentPurchaseService {

    // Acceso directo al JpaRepository para aprovechar JOIN FETCH con los items
    @Autowired
    private TicketJpaRepository ticketJpaRepository;

    @Autowired
    private UsualPurchaseRepository usualPurchaseRepository;

    @Autowired
    private IUserService userService;

    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public List<FrequentPurchaseSuggestion> analyzeFrequentPurchases(String userId, int minFrequency) {
        // Verificar que el usuario existe
        userService.findById(userId);

        // Obtener tickets históricos con ítems cargados (single query con JOIN FETCH)
        List<TicketEntity> tickets = ticketJpaRepository.findByUserIdWithItems(userId);

        if (tickets.isEmpty()) {
            return new ArrayList<>();
        }

        // Agrupar ticket_items por productId → lista de ítems
        // Clave: productId, Valor: lista de todos los TicketItemEntity de ese producto
        Map<String, List<TicketItemEntity>> itemsByProduct = tickets.stream()
                .flatMap(t -> t.getItems() != null ? t.getItems().stream() : java.util.stream.Stream.empty())
                .filter(ti -> ti.getProduct() != null)
                .collect(Collectors.groupingBy(ti -> ti.getProduct().getId()));

        // Construir sugerencias: frecuencia = número de tickets distintos en los que
        // apareció
        List<FrequentPurchaseSuggestion> suggestions = new ArrayList<>();

        for (Map.Entry<String, List<TicketItemEntity>> entry : itemsByProduct.entrySet()) {
            String productId = entry.getKey();
            List<TicketItemEntity> items = entry.getValue();

            // Contar tickets distintos (un producto puede repetirse dentro del mismo
            // ticket)
            long frequency = items.stream()
                    .map(ti -> ti.getTicket().getId())
                    .distinct()
                    .count();

            if (frequency < minFrequency)
                continue;

            // Cantidad media comprada por ticket
            double avgQuantity = items.stream()
                    .collect(Collectors.groupingBy(
                            ti -> ti.getTicket().getId(),
                            Collectors.summingDouble(ti -> ti.getQuantity() != null
                                    ? ti.getQuantity().doubleValue()
                                    : 0.0)))
                    .values().stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);

            // Comprobar si ya está registrado en usual_purchase
            boolean alreadyRegistered = usualPurchaseRepository
                    .findByUserIdAndProductId(userId, productId)
                    .isPresent();

            // Mapear ProductEntity → Product dominio
            ProductEntity productEntity = items.get(0).getProduct();
            Product product = productMapper.toDomain(productEntity);

            suggestions.add(new FrequentPurchaseSuggestion(product, frequency, avgQuantity, alreadyRegistered));
        }

        // Ordenar por frecuencia descendente
        suggestions.sort((a, b) -> Long.compare(b.getFrequency(), a.getFrequency()));

        return suggestions;
    }

    @Override
    @Transactional
    public List<UsualPurchase> analyzeAndPersist(String userId, int minFrequency) {
        User user = userService.findById(userId);
        List<FrequentPurchaseSuggestion> suggestions = analyzeFrequentPurchases(userId, minFrequency);

        List<UsualPurchase> result = new ArrayList<>();

        for (FrequentPurchaseSuggestion suggestion : suggestions) {
            String productId = suggestion.getProduct().getId();
            Optional<UsualPurchase> existing = usualPurchaseRepository.findByUserIdAndProductId(userId, productId);

            if (existing.isPresent()) {
                // Actualizar la cantidad objetivo con la media recalculada
                UsualPurchase up = existing.get();
                up.setTarget_quantity((float) suggestion.getAvgQuantity());
                result.add(usualPurchaseRepository.save(up));
            } else {
                // Crear nuevo registro usual_purchase
                UsualPurchase newUp = new UsualPurchase();
                newUp.setUser_id(user);
                newUp.setProduct_id(suggestion.getProduct());
                newUp.setTarget_quantity((float) suggestion.getAvgQuantity());
                result.add(usualPurchaseRepository.save(newUp));
            }
        }

        return result;
    }
}
