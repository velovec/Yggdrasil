package ru.linachan.midgard;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MidgardHTTPRequest {

    private Map<String, String> cookies = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private Map<String, Set<String>> requestParams = new HashMap<>();
    private JSONObject requestData = new JSONObject();

    private String method = "GET";
    private String path = "/";

    public MidgardHTTPRequest(InputStream clientInputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientInputStream));
        while(true) {
            try {
                String raw_header = reader.readLine();
                if (raw_header == null || raw_header.trim().length() == 0) {
                    if (this.getMethod().equals("POST") || this.getMethod().equals("PUT")) {
                        char[] raw_data = new char[this.getContentLength()];
                        reader.read(raw_data);
                        this.addData(new String(raw_data));
                    }
                    break;
                } else {
                    this.addHeader(raw_header);
                }
            } catch(SocketTimeoutException e) {
                break;
            }
        }
    }

    private void addHeader(String raw_header) {
        if (Pattern.matches("[^:]+: .*", raw_header)) {
            String[] header = raw_header.split(": ");

            if(header[0].equals("Cookie")) {
                String[] cookie = header[1].split("=");
                cookies.put(cookie[0], cookie[1]);
            } else {
                headers.put(header[0], header[1]);
            }
        } else if(Pattern.matches(".* .* HTTP/.*", raw_header)) {
            String[] header = raw_header.split(" ");
            this.method = header[0];

            if(header[1].contains("?")) {
                this.path = header[1].split("\\?")[0];
                parseRequest(header[1].split("\\?")[1]);
            } else {
                this.path = header[1];
            }
        }
    }

    private void parseRequest(String requestURI) {
        for (String raw_param : requestURI.split("&")) {
            try {
                String param_name, param_value;
                if (raw_param.contains("=")) {
                    String[] param = raw_param.split("=");
                    param_name = URLDecoder.decode(param[0], "UTF-8");
                    param_value = URLDecoder.decode(param[1], "UTF-8");
                } else {
                    param_name = URLDecoder.decode(raw_param, "UTF-8");
                    param_value = "true";
                }

                if (requestParams.containsKey(param_name)) {
                    requestParams.get(param_name).add(param_value);
                } else {
                    Set<String> values = new HashSet<>();
                    values.add(param_value);
                    requestParams.put(param_name, values);
                }
            } catch (UnsupportedEncodingException e) {}
        }
    }

    private void addData(String post) {
        String contentType = (headers.containsKey("Content-Type")) ? headers.get("Content-Type") : "text/plain";

        switch (contentType) {
            case "application/json":
                JSONParser parser = new JSONParser();
                try {
                    this.requestData = (JSONObject) parser.parse(post);
                } catch (ParseException e) {
                    // Incorrect request
                }
                break;
            case "application/x-www-form-urlencoded":
            case "multipart/form-data":
                parseRequest(post);
                break;
            default:
                break;
        }
    }

    public String getMethod() {
        return method;
    }

    public JSONObject getData() {
        return requestData;
    }

    public String getPath() {
        return path;
    }

    public Map<String, Set<String>> getParams() {
        return requestParams;
    }
    
    public boolean matchURL(String regex) {
        return Pattern.matches(regex, this.path);
    }
    
    public List<String> splitURLRegExp(String regex) {
        List<String> result = new LinkedList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matches = pattern.matcher(this.path);
        if (matches.matches()) {
            for (int i = 1; i <= matches.groupCount(); i++) {
                result.add(matches.group(i));
            }
        }
        return result;
    }

    public List<String> splitURL(String pattern) {
        List<String> result = new LinkedList<>();

        Collections.addAll(result, this.path.split(pattern));

        return result;
    }

    public String getHeader(String header) {
        if (this.headers.containsKey(header)) {
            return this.headers.get(header);
        } else {
            return null;
        }
    }

    public String getCookie(String cookie) {
        if (this.cookies.containsKey(cookie)) {
            return this.cookies.get(cookie);
        } else {
            return null;
        }
    }

    public int getContentLength() {
        if (this.headers.containsKey("Content-Length")) {
            return Integer.parseInt(this.headers.get("Content-Length"));
        }
        if (this.headers.containsKey("Content-length")) {
            return Integer.parseInt(this.headers.get("Content-length"));
        }
        if (this.headers.containsKey("content-length")) {
            return Integer.parseInt(this.headers.get("content-length"));
        }
        return 0;
    }
}
