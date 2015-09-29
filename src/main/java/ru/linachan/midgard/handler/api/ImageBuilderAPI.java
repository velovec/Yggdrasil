package ru.linachan.midgard.handler.api;

import org.json.simple.JSONObject;
import ru.linachan.alfheim.AlfheimImage;
import ru.linachan.jormungand.JormungandSubProcess;
import ru.linachan.jormungand.JormungandSubProcessState;
import ru.linachan.midgard.MidgardHTTPRequest;
import ru.linachan.midgard.MidgardHTTPResponse;
import ru.linachan.midgard.MidgardRequestHandler;
import ru.linachan.yggdrasil.YggdrasilCore;

@SuppressWarnings({"unchecked"})
public class ImageBuilderAPI implements MidgardRequestHandler {
    @Override
    public MidgardHTTPResponse handleRequest(YggdrasilCore core, String[] path, MidgardHTTPRequest request) {
        MidgardHTTPResponse response = new MidgardHTTPResponse();

        if (request.matchURL("^/api/image/build$")) {
            if (request.getMethod().equals("POST")) {
                AlfheimImage image = new AlfheimImage(request.getData());

                JSONObject responseData = new JSONObject();
                responseData.put("build_id", core.getImageBuilder().buildImage(image));
                response.setResponseData(responseData);
            }
        } else if (request.matchURL("^/api/image/([0-9]+)$")) {
            Long processID = Long.parseLong(request.splitURLRegExp("^/api/image/([0-9]+)$").get(0));
            JormungandSubProcess process = core.getExecutionManager().getProcess(processID);

            JSONObject responseData = new JSONObject();

            if (process != null) {
                if (process.isFinished()) {
                    responseData.put("output", process.getProcessOutput());
                    responseData.put("returnCode", process.getReturnCode());
                }
                responseData.put("status", process.getState().getState());
            } else {
                responseData.put("status", JormungandSubProcessState.NOT_EXISTS.getState());
            }
            response.setResponseData(responseData);
        }

        return response;
    }
}
