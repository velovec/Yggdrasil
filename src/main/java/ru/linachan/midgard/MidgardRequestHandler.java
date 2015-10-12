package ru.linachan.midgard;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import ru.linachan.yggdrasil.YggdrasilCore;

import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class MidgardRequestHandler {

    protected YggdrasilCore core;
    protected MidgardHTTPRequest request;
    protected MidgardHTTPResponse response;

    private VelocityEngine templateEngine;

    public MidgardRequestHandler() {
        this.templateEngine = new VelocityEngine();
        this.templateEngine.init();
    }

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

    protected String renderTemplate(String templatePath, VelocityContext context) {
        Path fullTemplatePath = Paths.get(core.getConfig("MidgardTemplatePath", "templates")).resolve(templatePath);
        Template template = templateEngine.getTemplate(fullTemplatePath.toString());
        StringWriter output = new StringWriter();

        template.merge(context, output);

        return output.toString();
    }

    protected abstract void GET();
    protected abstract void POST();
    protected abstract void PUT();
    protected abstract void DELETE();
    protected abstract void OPTIONS();
    protected abstract void HEAD();

}
