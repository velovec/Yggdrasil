package ru.linachan.midgard;

import com.google.common.base.Joiner;
import org.json.simple.JSONObject;
import ru.linachan.yggdrasil.YggdrasilCore;

import ru.linachan.midgard.handler.api.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unchecked", "unused"})
public class MidgardClientHandler implements Runnable {

    private YggdrasilCore core;
    private Socket clientSocket;

    private InputStream inputStream;
    private OutputStream outputStream;

    private Map<String, MidgardRequestHandler> apiHandlers = new HashMap<>();

    public MidgardClientHandler(YggdrasilCore core, Socket clientSocket) throws IOException {
        this.core = core;
        this.clientSocket = clientSocket;

        this.clientSocket.setSoTimeout(5000);

        this.inputStream = this.clientSocket.getInputStream();
        this.outputStream = this.clientSocket.getOutputStream();

        setUpApiHandlers();
    }

    private MidgardHTTPRequest readRequest() throws IOException {
        MidgardHTTPRequest request = new MidgardHTTPRequest();

        BufferedReader reader = new BufferedReader(new InputStreamReader(this.inputStream));
        while(true) {
            try {
                String raw_header = reader.readLine();
                if (raw_header == null || raw_header.trim().length() == 0) {
                    if (request.getMethod().equals("POST") || request.getMethod().equals("PUT")) {
                        char[] raw_data = new char[request.getContentLength()];
                        reader.read(raw_data);
                        request.addData(new String(raw_data));
                    }
                    break;
                } else {
                    request.addHeader(raw_header);
                }
            } catch(SocketTimeoutException e) {
                this.core.logWarning("Unable to read request...");
                break;
            }
        }

        return request;
    }

    @Override
    public void run() {
        try {
            MidgardHTTPRequest request = readRequest();
            this.core.logInfo("MidgardServer: Incoming request: " + request.getMethod() + " " + request.getPath());
            MidgardHTTPResponse response = handleRequest(request);
            writeResponse(response);
            this.clientSocket.close();
        } catch (IOException e) {
            this.core.logException(e);
        }
    }

    private void setUpApiHandlers() {
        apiHandlers.put("^/api/image/(.*?)$", new ImageBuilderAPI());
    }

    private MidgardHTTPResponse handleRequest(MidgardHTTPRequest request) {
        MidgardHTTPResponse response = null;

        if (request.matchURL("^/api/(.*?)$")) {
            for (String pattern : apiHandlers.keySet()) {
                if (request.matchURL(pattern)) {
                    response = apiHandlers.get(pattern).handleRequest(this.core, request);
                    break;
                }
            }
        }

        if (response == null) {
            response = new MidgardHTTPResponse();
            response.setResponseCode(MidgardHTTPCodes.NOT_FOUND);
            response.setResponseData("");
        }

        return response;
    }

    private void writeResponse(MidgardHTTPResponse response) throws IOException {
        this.outputStream.write(response.toByteArray());
        this.outputStream.flush();
    }
}