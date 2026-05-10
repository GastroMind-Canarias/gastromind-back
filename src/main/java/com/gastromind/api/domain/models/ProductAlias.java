package com.gastromind.api.domain.models;

/**
 * Sinónimo de texto (p. ej. línea de ticket) resuelto contra un producto del catálogo mediante {@code productId} y forma normalizada {@code aliasNorm}.
 */
public class ProductAlias {
    private String id;
    private String productId;
    private String alias;
    private String aliasNorm;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAliasNorm() {
        return aliasNorm;
    }

    public void setAliasNorm(String aliasNorm) {
        this.aliasNorm = aliasNorm;
    }
}
