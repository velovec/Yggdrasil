package ru.linachan.midgard;

import ru.linachan.yggdrasil.YggdrasilCore;

import java.util.HashMap;
import java.util.Map;

public class MidgardHTTPRouter {

    private YggdrasilCore core;
    private Map<String, MidgardRequestHandler> routeMap = new HashMap<>();

    public MidgardHTTPRouter(YggdrasilCore core) {
        this.core = core;

        setUpRoutes();
    }

    private void setUpRoutes() {
        // TODO: Implement Yggdrasil API
    }

    private void addRoute(String routePattern, MidgardRequestHandler handler) {
        if (!this.routeMap.containsKey(routePattern)) {
            this.routeMap.put(routePattern, handler);
        }
    }

    private MidgardHTTPResponse resourceNotFound() {
        MidgardHTTPResponse response =  new MidgardHTTPResponse();
        response.setResponseCode(MidgardHTTPCodes.NOT_FOUND);
        response.setResponseData("");
        return response;
    }

    public MidgardHTTPResponse routeRequest(MidgardHTTPRequest request) {
        MidgardHTTPResponse response = null;

        for (String pattern : routeMap.keySet()) {
            if (request.matchURL(pattern)) {
                response = routeMap.get(pattern).handleRequest(this.core, request);
                break;
            }
        }

        return (response != null) ? response : resourceNotFound();
    }
}
