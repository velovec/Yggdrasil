package ru.linachan.midgard.handler.api;

import org.jooq.tools.json.JSONArray;
import org.json.simple.JSONObject;
import ru.linachan.alfheim.AlfheimImage;
import ru.linachan.jormungand.JormungandSubProcess;
import ru.linachan.jormungand.JormungandSubProcessState;
import ru.linachan.midgard.MidgardRequestHandler;
import ru.linachan.util.Pair;

import java.util.List;

@SuppressWarnings({"unchecked"})
public class ImageBuilderAPI extends MidgardRequestHandler {

    @Override
    protected void GET() {
        if (request.matchURL("^/api/image/list$")) {
            List<Pair<Long, JormungandSubProcess>> buildList = core.getExecutionManager().getProcessesByTag("alfheimBuild");

            JSONObject responseData = new JSONObject();
            JSONArray processesData = new JSONArray();

            for (Pair<Long, JormungandSubProcess> process : buildList) {
                JSONObject processData = new JSONObject();

                processData.put("buildID", process.key);
                processData.put("buildStatus", process.value.getState());

                processesData.add(processData);
            }
            responseData.put("builds", processesData);
            response.setResponseData(responseData);
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
    }

    @Override
    protected void POST() {
        if (request.matchURL("^/api/image/build$")) {
            AlfheimImage image = new AlfheimImage(request.getData());

            JSONObject responseData = new JSONObject();
            responseData.put("build_id", core.getImageBuilder().buildImage(image));
            response.setResponseData(responseData);
        }
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
