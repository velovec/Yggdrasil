package ru.linachan.skuld;

import ru.linachan.yggdrasil.YggdrasilCore;

import javax.sound.sampled.*;

public class SkuldCore {

    private YggdrasilCore core;
    private TargetDataLine audioLine;
    private SkuldListener lineListener;

    public SkuldCore(YggdrasilCore core) {
        this.core = core;

        initializeAudioSystem();
    }

    private void initializeAudioSystem() {
        AudioFormat audioFormat = new AudioFormat(32000, 16, 1, true, false);

        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);

        if (!AudioSystem.isLineSupported(info)) {
            this.core.logWarning("SkuldCore: Requested AudioLine is not supported!");
        } else {
            try {
                audioLine = (TargetDataLine) AudioSystem.getLine(info);
                audioLine.open(audioFormat);
            } catch (LineUnavailableException e) {
                this.core.logException(e);
            }
        }

        if (isLineActive()) {
            lineListener = new SkuldListener(core);
            audioLine.addLineListener(lineListener);
        } else {
            this.core.logWarning("SkuldCore: No active line found!");
        }
    }

    private boolean isLineActive() {
        return (audioLine != null)&&(audioLine.isOpen());
    }

    public void startListening() {
        if (isLineActive()) {
            audioLine.start();
        }
    }

    public void stopListening() {
        if (isLineActive()) {
            audioLine.stop();
        }
    }

    public boolean execute_tests() {
        return true;
    }
}
