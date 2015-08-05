package ru.linachan.midgard;

import org.json.simple.JSONObject;

import java.util.*;

public class MidgardHTTPResponse {

    private String responseCode = "200 OK";
    private String contentType = "application/json";
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> cookies = new HashMap<>();
    private JSONObject responseData = new JSONObject();

    private boolean toJSONp = false;
    private String callbackName = null;

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

    public void setResponseCode(MidgardHTTPCodes responseCode) {
        this.responseCode = responseCode.getCode();
    }
    
    public void setResponseData(JSONObject responseData) {
        this.responseData = responseData;
    }

    public void setJSONPParameters(String callbackName) {
        this.toJSONp = true;
        this.callbackName = callbackName;
    }

    public byte[] toByteArray() {
        String content = "HTTP/1.1 " + this.responseCode + "\r\n";

        int contentLength = this.responseData.toJSONString().getBytes().length;
        contentLength += (toJSONp) ? callbackName.length() + 3 : 0;

        this.setHeader("Content-Type", this.contentType);
        this.setHeader("Content-Length", String.valueOf(contentLength));

        for (String key : this.headers.keySet()) {
            content += key + ": " + this.headers.get(key) + "\r\n";
        }
        for (String key : this.cookies.keySet()) {
            content += "Set-Cookie: " + key + "=" + this.cookies.get(key) + "; path=/\r\n";
        }

        content += "\r\n";

        if (toJSONp) {
            content += callbackName + "(";
            content += this.responseData.toJSONString();
            content += ");";
        } else {
            content += this.responseData.toJSONString();
        }

        return content.getBytes();
    }
}