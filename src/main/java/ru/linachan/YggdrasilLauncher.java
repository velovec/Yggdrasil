package ru.linachan;

import ru.linachan.yggdrasil.YggdrasilCore;

import java.io.IOException;

public class YggdrasilLauncher {

    public static void main(String[] args) throws InterruptedException, IOException {
            YggdrasilCore yggdrasil = new YggdrasilCore("conf/Yggdrasil.ini");

            yggdrasil.mainLoop();
    }

}
