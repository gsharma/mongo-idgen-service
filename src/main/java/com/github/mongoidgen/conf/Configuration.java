package com.github.mongoidgen.conf;

public class Configuration {
    private Long startupBreather;

    private String dataStoreName;
    private String dataStoreReplicas;
    private String dataStoreUsername;
    private String dataStorePassword;

    private Long idCacheRejuvenatorFreqMillis;
    private int idCacheCapacity;
    private String idRootPrefix;

    public Long getIdCacheRejuvenatorFreqMillis() {
        return idCacheRejuvenatorFreqMillis;
    }

    public int getIdCacheCapacity() {
        return idCacheCapacity;
    }

    public String getIdRootPrefix() {
        return idRootPrefix;
    }

    public String getDataStoreName() {
        return dataStoreName;
    }

    public String getDataStoreReplicas() {
        return dataStoreReplicas;
    }

    public String getDataStoreUsername() {
        return dataStoreUsername;
    }

    public String getDataStorePassword() {
        return dataStorePassword;
    }

    public void setDataStoreName(String dataStoreName) {
        this.dataStoreName = dataStoreName;
    }

    public void setDataStoreReplicas(String dataStoreReplicas) {
        this.dataStoreReplicas = dataStoreReplicas;
    }

    public void setDataStoreUsername(String dataStoreUsername) {
        this.dataStoreUsername = dataStoreUsername;
    }

    public void setDataStorePassword(String dataStorePassword) {
        this.dataStorePassword = dataStorePassword;
    }

    public void setIdCacheRejuvenatorFreqMillis(Long idCacheRejuvenatorFreqMillis) {
        this.idCacheRejuvenatorFreqMillis = idCacheRejuvenatorFreqMillis;
    }

    public void setIdCacheCapacity(int idCacheCapacity) {
        this.idCacheCapacity = idCacheCapacity;
    }

    public void setIdRootPrefix(String idRootPrefix) {
        this.idRootPrefix = idRootPrefix;
    }

    public Long getStartupBreather() {
        return startupBreather;
    }

    public void setStartupBreather(Long startupBreather) {
        this.startupBreather = startupBreather;
    }

    @Override
    public String toString() {
        return "Configuration [startupBreather=" + startupBreather + ", dataStoreName=" + dataStoreName
                + ", dataStoreReplicas=" + dataStoreReplicas + ", dataStoreUsername=" + dataStoreUsername
                + ", dataStorePassword=" + dataStorePassword + ", idCacheRejuvenatorFreqMillis="
                + idCacheRejuvenatorFreqMillis + ", idCacheCapacity=" + idCacheCapacity + ", idRootPrefix="
                + idRootPrefix + "]";
    }
}