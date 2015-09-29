package ru.linachan.midgard.handler.api;

import org.json.simple.JSONObject;
import ru.linachan.alfheim.AlfheimImage;
import ru.linachan.midgard.MidgardHTTPRequest;
import ru.linachan.midgard.MidgardHTTPResponse;
import ru.linachan.midgard.MidgardRequestHandler;
import ru.linachan.yggdrasil.YggdrasilCore;

@SuppressWarnings({"unchecked"})
public class ImageBuilderAPI implements MidgardRequestHandler {
    @Override
    public MidgardHTTPResponse handleRequest(YggdrasilCore core, String[] path, MidgardHTTPRequest request) {
        MidgardHTTPResponse response = new MidgardHTTPResponse();

        if (request.getMethod().equals("POST")) {
            AlfheimImage image = new AlfheimImage(request.getData());

            JSONObject responseData = new JSONObject();
            responseData.put("build_id", core.getImageBuilder().buildImage(image));
            response.setResponseData(responseData);
        }

        return response;
    }
}
