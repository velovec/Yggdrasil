package ru.linachan.midgard.handler.api;

import com.google.api.services.storage.model.StorageObject;
import com.google.common.base.Joiner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.linachan.jormungand.JormungandCore;
import ru.linachan.midgard.MidgardHTTPCodes;
import ru.linachan.midgard.MidgardHTTPRequest;
import ru.linachan.midgard.MidgardHTTPResponse;
import ru.linachan.midgard.MidgardRequestHandler;
import ru.linachan.util.Pair;
import ru.linachan.yggdrasil.YggdrasilCore;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"unchecked", "unused"})
public class StorageAPI implements MidgardRequestHandler {

    YggdrasilCore core;
    MidgardHTTPRequest request;
    MidgardHTTPResponse response;
    JSONObject responseData;

    @Override
    public MidgardHTTPResponse handleRequest(YggdrasilCore core, String[] path, MidgardHTTPRequest request) {
        this.core = core;
        this.request = request;
        this.response = new MidgardHTTPResponse();
        this.responseData = new JSONObject();

        if (path.length > 0) {
            switch (path[0]) {
                case "list":
                    listFiles(Joiner.on("/").join(Arrays.copyOfRange(path, 1, path.length)));
                    break;
                case "info":
                    fileInfo(Joiner.on("/").join(Arrays.copyOfRange(path, 1, path.length)));
                    break;
                default:
                    responseData.put("message", "Action not found");
                    response.setResponseCode(MidgardHTTPCodes.NOT_FOUND);
                    break;
            }
        } else {
            responseData.put("message", "No action specified");
            response.setResponseCode(MidgardHTTPCodes.BAD_REQUEST);
        }

        response.setResponseData(responseData);

        return response;
    }

    void listFiles(String prefix) {
        JormungandCore storage = this.core.getStorageManager();

        prefix = (prefix.endsWith("/")) ? prefix : prefix + "/";

        Pair<List<String>, List<StorageObject>> listing = storage.listObjectsByPath(prefix);

        JSONArray directoryList = new JSONArray();
        JSONArray fileList = new JSONArray();

        for (String directory : listing.key) {
            directoryList.add(directory);
        }

        for (StorageObject file : listing.value) {
            fileList.add(file.getName());
        }

        if ((directoryList.size() > 0)||(fileList.size() > 0)) {
            responseData.put("directories", directoryList);
            responseData.put("files", fileList);
            response.setResponseCode(MidgardHTTPCodes.OK);
        } else {
            responseData.put("message", "Object not found");
            response.setResponseCode(MidgardHTTPCodes.NOT_FOUND);
        }
    }

    void fileInfo(String filePath) {
        JormungandCore storage = this.core.getStorageManager();

        StorageObject fileObject = storage.getObjectByPath(filePath);

        if (fileObject != null) {
            responseData.put("name", fileObject.getName());
            responseData.put("owner", fileObject.getOwner());
            responseData.put("md5", fileObject.getMd5Hash());
            responseData.put("content-type", fileObject.getContentType());
            responseData.put("size", fileObject.getSize());
            responseData.put("link", fileObject.getMediaLink());
            response.setResponseCode(MidgardHTTPCodes.OK);
        } else {
            responseData.put("message", "Object not found");
            response.setResponseCode(MidgardHTTPCodes.NOT_FOUND);
        }
    }
}
