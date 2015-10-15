package ru.linachan.skuld;

public class SkuldSynthesizableComplexExponent extends SkuldSynthesizableSignal {

    @Override
    public SkuldSignal synthesize(SkuldSignal sourceSignal) {
        final int sampleRate = sourceSignal.sampleRate();

        final double frequency = (double) getProperty("frequency");

        final double argument = 2 * Math.PI * frequency / sampleRate;

        for (int sampleID = 0; sampleID < sampleRate; sampleID++) {
            sourceSignal.setSample(sampleID, Math.cos(argument * sampleID), Math.sin(argument * sampleID));
        }

        return sourceSignal;
    }

}
