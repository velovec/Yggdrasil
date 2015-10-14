package ru.linachan.skuld;

import ru.linachan.yggdrasil.YggdrasilCore;

import javax.sound.sampled.*;
import javax.sound.sampled.AudioFormat.Encoding;

public class SkuldCore {

    private YggdrasilCore core;
    private TargetDataLine audioLine;
    private SkuldListener lineListener;

    private final int sampleRate;
    private final int sampleBits;
    private final int audioChannels;

    public SkuldCore(YggdrasilCore core) {
        this.core = core;

        this.sampleRate = Integer.parseInt(core.getConfig("SkuldSampleRate", "32768"));
        this.sampleBits = Integer.parseInt(core.getConfig("SkuldSampleBits", "32"));
        this.audioChannels = Integer.parseInt(core.getConfig("SkuldChannels", "1"));

        initializeAudioSystem();
    }

    private void initializeAudioSystem() {
        AudioFormat audioFormat = new AudioFormat(
            Encoding.PCM_SIGNED,            // Signed Integer PCM
            sampleRate,                     // Sample Rate in Hz
            sampleBits,                     // Sample Size in Bits
            audioChannels,                  // Audio Channel number
            sampleBits / 8 * audioChannels, // Frame Size In Bytes = Sample Size in Bits / 8 * Channels
            sampleRate,                     // Frame Rate = Sample Rate
            false                           // Use Big-Endian (true) or Little-Endian (false)
        );

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
        startListening();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            core.logException(e);
        }
        stopListening();

        return true;
    }
}
