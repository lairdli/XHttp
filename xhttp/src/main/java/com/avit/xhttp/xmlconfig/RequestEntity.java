package com.avit.xhttp.xmlconfig;

public class RequestEntity {
    private String key;
    private long expires;
    private String netType;
    private String url;
    private String entityClass;
    private String mockJson;



    public RequestEntity() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }


    public String getMockJson() {
        return mockJson;
    }

    public void setMockJson(String mockClass) {
        this.mockJson = mockClass;
    }

    @Override
    public String toString() {
        return "RequestEntity{" +
                "key='" + key + '\'' +
                ", expires=" + expires +
                ", netType='" + netType + '\'' +
                ", url='" + url + '\'' +
                ", entityClass='" + entityClass + '\'' +
                ", mockJson='" + mockJson + '\'' +
                '}';
    }
}
