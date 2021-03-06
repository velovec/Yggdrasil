package ru.linachan.midgard;

import org.apache.commons.lang.ArrayUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class MidgardHTTPResponse {

    private String responseCode = "200 OK";
    private String contentType = "text/html";
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> cookies = new HashMap<>();
    private byte[] responseData = new byte[0];

    private Boolean headMode = false;

    public MidgardHTTPResponse() {}

    public MidgardHTTPResponse(Boolean headMode) {
        this.headMode = headMode;
    }

    public void setCookie(String name, String value) {
        if(cookies.containsKey(name)) {
            cookies.remove(name);
            cookies.put(name, value);
        } else {
            cookies.put(name, value);
        }
    }

    public void setHeader(String name, String value) {
        if(headers.containsKey(name)) {
            headers.remove(name);
            headers.put(name, value);
        } else {
            headers.put(name, value);
        }
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setResponseCode(MidgardHTTPCodes responseCode) {
        this.responseCode = responseCode.getCode();
    }
    
    public void setResponseData(JSONObject responseData) {
        this.contentType = "application/json";
        this.responseData = responseData.toJSONString().getBytes();
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData.getBytes();
    }

    public void setResponseData(byte[] responseData) {
        this.responseData = responseData;
    }

    public byte[] getBytes() {
        String content = "HTTP/1.1 " + this.responseCode + "\r\n";

        int contentLength = this.responseData.length;

        this.setHeader("Content-Type", this.contentType);
        this.setHeader("Content-Length", String.valueOf(contentLength));

        for (String key : this.headers.keySet()) {
            content += key + ": " + this.headers.get(key) + "\r\n";
        }
        for (String key : this.cookies.keySet()) {
            content += "Set-Cookie: " + key + "=" + this.cookies.get(key) + "; path=/\r\n";
        }

        content += "\r\n";

        byte[] contentBytes;

        if (!this.headMode) {
            contentBytes = ArrayUtils.addAll(content.getBytes(), this.responseData);
        } else {
            contentBytes = content.getBytes();
        }

        return contentBytes;
    }
}