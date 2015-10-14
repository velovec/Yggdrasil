package ru.linachan.midgard.handler.html;

import org.apache.velocity.VelocityContext;
import ru.linachan.midgard.MidgardRequestHandler;

import java.util.ArrayList;

public class TemplatePage extends MidgardRequestHandler {

    private String pageTemplate;
    private String pageTitle;

    public TemplatePage(String pageTemplate, String pageTitle) {
        this.pageTemplate = pageTemplate;
        this.pageTitle = pageTitle;
    }

    @Override
    protected void GET() {
        VelocityContext ctx = new VelocityContext();

        ArrayList notificationList = new ArrayList();
        ArrayList taskList = new ArrayList();

        ctx.put("notificationList", notificationList);
        ctx.put("taskList", taskList);

        response.setResponseData(renderTemplate(pageTemplate, pageTitle, ctx));
    }

    @Override
    protected void POST() {
        methodNotImplemented();
    }

    @Override
    protected void PUT() {
        methodNotImplemented();
    }

    @Override
    protected void DELETE() {
        methodNotImplemented();
    }

    @Override
    protected void OPTIONS() {
        methodNotImplemented();
    }

    @Override
    protected void HEAD() {
        methodNotImplemented();
    }

    @Override
    protected void onInit() {}
}
