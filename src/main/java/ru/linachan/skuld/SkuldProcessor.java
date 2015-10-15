package ru.linachan.skuld;

import ru.linachan.yggdrasil.service.YggdrasilService;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Line;
import javax.sound.sampled.TargetDataLine;
import java.util.HashSet;
import java.util.Set;

public class SkuldProcessor extends YggdrasilService {

    private TargetDataLine audioLine;

    private SkuldCore analyzer;

    private Integer sampleRate;
    private Integer sampleBytes;

    public SkuldProcessor(Line audioLine) {
        this.audioLine = (TargetDataLine) audioLine;
    }

    @Override
    protected void onInit() {
        analyzer = core.getComponent(SkuldCore.class);

        AudioFormat audioFormat = audioLine.getFormat();

        sampleRate = (int) audioFormat.getSampleRate();
        sampleBytes = audioFormat.getSampleSizeInBits() / 8;
    }

    @Override
    protected void onShutdown() {

    }

    @Override
    public void run() {
        while (isRunning) {
            byte[][] samples = new byte[sampleRate][sampleBytes];
            for (int sampleNumber = 0; sampleNumber < sampleRate; sampleNumber++) {
                while (audioLine.available() < sampleBytes) {}

                audioLine.read(samples[sampleNumber], 0, sampleBytes);
            }
            analyzer.pushSignal(new SkuldSignal(samples));
        }
    }
}
