package ru.linachan.midgard;

import ru.linachan.yggdrasil.YggdrasilCore;

public interface MidgardRequestHandler {

    MidgardHTTPResponse handleRequest(YggdrasilCore core, String[] path, MidgardHTTPRequest request);

}
