package ru.linachan.midgard.handler.html;

import org.apache.velocity.VelocityContext;
import ru.linachan.midgard.MidgardRequestHandler;

public class LoginPage extends MidgardRequestHandler {
    @Override
    protected void GET() {
        VelocityContext ctx = new VelocityContext();
        response.setResponseData(renderTemplate("login.html", "Login", ctx));
    }

    @Override
    protected void POST() {
        core.logInfo(String.valueOf(request.getParams()));
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
