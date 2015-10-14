package ru.linachan.skuld;

import ru.linachan.yggdrasil.YggdrasilCore;

import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class SkuldListener implements LineListener {

    private YggdrasilCore core;
    private SkuldProcessor processor;

    public SkuldListener(YggdrasilCore core) {
        this.core = core;
    }
    
    @Override
    public void update(LineEvent lineEvent) {
        switch (lineEvent.getType().toString()) {
            case "Start":
                this.core.logInfo("SkuldListener: Voice processing started");

                this.processor = new SkuldProcessor(lineEvent.getLine());
                this.core.startService(this.processor);

                break;
            case "Stop":
                this.core.logInfo("SkuldListener: Voice processing stopped");

                this.core.stopService("SkuldProcessor", true);
                this.processor = null;

                break;
            case "Open":
                this.core.logInfo("SkuldListener: AudioLine opened");
                break;
            case "Close":
                this.core.logInfo("SkuldListener: AudioLine closed");
                break;
            default:
                break;
        }

    }
}
