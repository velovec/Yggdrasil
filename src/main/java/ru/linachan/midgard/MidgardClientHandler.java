package ru.linachan.midgard;

import ru.linachan.yggdrasil.YggdrasilCore;

import java.io.*;
import java.net.Socket;

public class MidgardClientHandler implements Runnable {

    private YggdrasilCore core;
    private Socket clientSocket;

    private InputStream inputStream;
    private OutputStream outputStream;

    private MidgardHTTPRouter requestRouter;

    public MidgardClientHandler(YggdrasilCore core, Socket clientSocket) throws IOException {
        this.core = core;
        this.clientSocket = clientSocket;

        this.clientSocket.setSoTimeout(5000);

        this.inputStream = this.clientSocket.getInputStream();
        this.outputStream = this.clientSocket.getOutputStream();

        this.requestRouter = new MidgardHTTPRouter(core);
    }

    @Override
    public void run() {
        try {
            MidgardHTTPRequest request = readRequest();
            this.core.logInfo("MidgardServer: Incoming request: " + request.getMethod() + " " + request.getPath());
            MidgardHTTPResponse response = this.requestRouter.routeRequest(request);
            writeResponse(response);
            this.clientSocket.close();
        } catch (IOException e) {
            this.core.logException(e);
        }
    }

    private MidgardHTTPRequest readRequest() throws IOException {
        return new MidgardHTTPRequest(this.inputStream);
    }

    private void writeResponse(MidgardHTTPResponse response) throws IOException {
        this.outputStream.write(response.getBytes());
        this.outputStream.flush();
    }
}