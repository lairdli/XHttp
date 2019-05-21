package com.avit.xhttp;

public class HttpXmlConfig {
    private int xmlResId;
    private String baseUrl;
    private String nickName;

    public HttpXmlConfig(int xmlResId, String nickName) {
        this.xmlResId = xmlResId;
        this.nickName = nickName;
    }

    public HttpXmlConfig(int xmlResId, String baseUrl, String nickName) {
        this.xmlResId = xmlResId;
        this.baseUrl = baseUrl;
        this.nickName = nickName;
    }

    public int getXmlResId() {
        return xmlResId;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getNickName() {
        return nickName;
    }

    @Override
    public String toString() {
        return "HttpXmlConfig{" +
                "xmlResId=" + xmlResId +
                ", baseUrl='" + baseUrl + '\'' +
                ", nickName='" + nickName + '\'' +
                '}';
    }
}

