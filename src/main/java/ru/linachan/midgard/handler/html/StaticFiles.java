package ru.linachan.midgard.handler.html;

import org.apache.commons.io.FilenameUtils;
import ru.linachan.midgard.MidgardHTTPCodes;
import ru.linachan.midgard.MidgardRequestHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class StaticFiles extends MidgardRequestHandler {

    private Map<String, String> mimeTypes;

    @Override
    protected void GET() {
        File staticFile = Paths.get(".").resolve(request.getPath().substring(1)).toFile();
        if (staticFile.exists()&&staticFile.isFile()) {
            if (staticFile.canRead()) {
                try {
                    byte[] fileContent = Files.readAllBytes(staticFile.toPath());
                    response.setResponseData(fileContent);
                    String staticType = FilenameUtils.getExtension(staticFile.getPath());
                    String contentType = (mimeTypes.containsKey(staticType)) ? mimeTypes.get(staticType) : "text/plain";
                    response.setContentType(contentType);
                } catch (IOException e) {
                    core.logException(e);
                }
            } else {
                response.setResponseCode(MidgardHTTPCodes.FORBIDDEN);
                response.setResponseData("Access Denied");
            }
        } else {
            response.setResponseCode(MidgardHTTPCodes.NOT_FOUND);
            response.setResponseData("File Not Found: " + staticFile.getPath());
        }
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
    protected void onInit() {
        mimeTypes = new HashMap<>();

        mimeTypes.put("css", "text/css");
        mimeTypes.put("js", "application/javascript");

        mimeTypes.put("png", "image/png");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("svg", "image/svg");
    }
}
