package ru.linachan.midgard;

import ru.linachan.yggdrasil.YggdrasilCore;

public abstract class MidgardRequestHandler {

    protected YggdrasilCore core;
    protected MidgardHTTPRequest request;
    protected MidgardHTTPResponse response;

    public MidgardHTTPResponse handleRequest(YggdrasilCore core, MidgardHTTPRequest request) {
        this.core = core;
        this.request = request;
        this.response = new MidgardHTTPResponse(this.request.getMethod().equals("HEAD"));

        switch (this.request.getMethod()) {
            case "GET":     GET();     break;
            case "POST":    POST();    break;
            case "PUT":     PUT();     break;
            case "DELETE":  DELETE();  break;
            case "OPTIONS": OPTIONS(); break;
            case "HEAD":    HEAD();    break;
            default: methodNotImplemented(); break;
        }

        return this.response;
    }

    protected void methodNotImplemented() {
        this.response.setResponseCode(MidgardHTTPCodes.METHOD_NOT_ALLOWED);
        this.response.setResponseData("Method Not Allowed");
    }

    protected abstract void GET();
    protected abstract void POST();
    protected abstract void PUT();
    protected abstract void DELETE();
    protected abstract void OPTIONS();
    protected abstract void HEAD();

}
