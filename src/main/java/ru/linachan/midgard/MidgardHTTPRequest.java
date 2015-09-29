package ru.linachan.midgard;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

    public void addHeader(String raw_header) {
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
                if (this.method.toUpperCase().equals("GET")) {
                    for(String raw_param : header[1].split("\\?")[1].split("&")) {
                        String param_name, param_value;
                        if (raw_param.contains("=")) {
                            String[] param = raw_param.split("=");
                            param_name = param[0];
                            param_value = param[1];
                        } else {
                            param_name = raw_param;
                            param_value = null;
                        }
                        if(requestParams.containsKey(param_name)) {
                            requestParams.get(param_name).add(param_value);
                        } else {
                            Set<String> values = new HashSet<>();
                            values.add(param_value);
                            requestParams.put(param_name, values);
                        }
                    }
                }
            } else {
                this.path = header[1];
            }
        }
    }

    public String getMethod() {
        return method;
    }

    public void addData(String post) {
        JSONParser parser = new JSONParser();
        try {
            this.requestData = (JSONObject)parser.parse(post);
        } catch (ParseException e) {
            // Incorrect request
        }
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
