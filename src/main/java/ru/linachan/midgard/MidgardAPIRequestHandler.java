package ru.linachan.midgard;

import ru.linachan.yggdrasil.YggdrasilCore;

public interface MidgardAPIRequestHandler {

    MidgardHTTPResponse handleRequest(YggdrasilCore core, String[] path, MidgardHTTPRequest request);

}
