package ru.linachan.skuld;

import ru.linachan.yggdrasil.component.YggdrasilComponent;

import javax.sound.sampled.*;
import javax.sound.sampled.AudioFormat.Encoding;
import java.util.ArrayList;
import java.util.List;

public class SkuldCore extends YggdrasilComponent {

    private TargetDataLine audioLine;
    private SkuldListener lineListener;
    private List<SkuldSignal> signals = new ArrayList<>();

    private int sampleRate;
    private int sampleBits;
    private int audioChannels;

    @Override
    protected void onInit() {
        this.sampleRate = Integer.parseInt(core.getConfig("SkuldSampleRate", "32768"));
        this.sampleBits = Integer.parseInt(core.getConfig("SkuldSampleBits", "32"));
        this.audioChannels = Integer.parseInt(core.getConfig("SkuldChannels", "1"));

        initializeAudioSystem();
    }

    @Override
    protected void onShutdown() {

    }

    @Override
    public boolean executeTests() {
        startListening();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            core.logException(e);
        }
        stopListening();

        if (signals.size() > 0) {
            SkuldAnalyzer signalAnalyzer = new SkuldAnalyzer(signals.get(0), 0.0, 0.1, 10);
            List<SkuldSynthesizableSignal> analysis = signalAnalyzer.detectHarmonics();

            if (analysis != null) {
                for(SkuldSynthesizableSignal signalCutter: analysis) {
                    core.logInfo(signalCutter.getProperties().toString());
                }
            }

            return true;
        }
        return false;
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

    public void pushSignal(SkuldSignal signal) {
        this.signals.add(signal);
    }
}
