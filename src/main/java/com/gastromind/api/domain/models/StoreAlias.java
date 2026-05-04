package com.gastromind.api.domain.models;

public class StoreAlias {
    private String id;
    private String storeId;
    private String alias;
    private String aliasNorm;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
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
