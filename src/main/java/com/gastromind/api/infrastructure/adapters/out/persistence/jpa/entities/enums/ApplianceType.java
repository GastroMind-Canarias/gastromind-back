package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums;

/**
 * Persistido hogar/recetas. Nombres = {@link com.gastromind.api.domain.models.enums.Appliance}.
 */
public enum ApplianceType {
    /** Horno conv./electrico: horneado, gratinado, asado temp. controlada. */
    HORNO,
    /** Microondas: recalentar, descongelar, coccion rapida MW. */
    MICROONDAS,
    /**
     * Air fryer: aire caliente cavidad cerrada, poco/sin aceite.
     * != {@link #FREIDORA} (aceite).
     */
    AIR_FRYER,
    /** Vitro / induccion / placa electrica: olla/sarten sobre fuego. */
    VITROCERAMICA,
    /** Robot cocina multifuncion: picado, sofrito, programas guiados. */
    ROBOT_COCINA,
    /** Batidora vaso/mano: liquidos, cremas, salsas. */
    BATIDORA,
    /** Olla presion electrica/express: vapor presion, tiempos cortos. */
    OLLA_EXPRESS,
    /** Freidora aceite: immersion/cesta aceite caliente. */
    FREIDORA,
    /** Grill/plancha electrica (incl. BBQ electrica): contacto directo, no horno cerrado. */
    GRILL
}
