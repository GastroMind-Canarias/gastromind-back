package com.gastromind.api.domain.ports.in;

import java.util.List;

import com.gastromind.api.domain.models.FrequentPurchaseSuggestion;
import com.gastromind.api.domain.models.UsualPurchase;

public interface IFrequentPurchaseService {

    /**
     * IdentificarComprasHabituales: analiza el histórico de tickets del usuario
     * para identificar los productos comprados con mayor frecuencia.
     *
     * @param userId       ID del usuario a analizar
     * @param minFrequency Número mínimo de apariciones en tickets para considerarlo
     *                     habitual (por defecto 2)
     * @return Lista ordenada por frecuencia descendente de sugerencias de compras
     *         habituales
     */
    List<FrequentPurchaseSuggestion> analyzeFrequentPurchases(String userId, int minFrequency);

    /**
     * Registrar compras habituales: persiste en la tabla usual_purchase
     * los productos identificados como habituales (upsert).
     * Retorna la lista de registros guardados/actualizados.
     *
     * @param userId       ID del usuario
     * @param minFrequency Umbral mínimo de frecuencia para guardar
     * @return Lista de UsualPurchase guardados/actualizados
     */
    List<UsualPurchase> analyzeAndPersist(String userId, int minFrequency);
}
