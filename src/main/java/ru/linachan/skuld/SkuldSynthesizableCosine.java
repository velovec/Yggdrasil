package ru.linachan.skuld;

public class SkuldSynthesizableCosine extends SkuldSynthesizableSignal {

    @Override
    public SkuldSignal synthesize(SkuldSignal sourceSignal) {
        final int sampleRate = sourceSignal.sampleRate();

        final double frequency = (double) getProperty("frequency");
        final double amplitude = (double) getProperty("amplitude");
        final double phase = (double) getProperty("phase");

        final double argument = 2 * Math.PI * frequency / sampleRate;

        for (int sampleID = 0; sampleID < sampleRate; sampleID++) {
            sourceSignal.setSample(sampleID, amplitude * Math.cos(argument * sampleID + phase), 0.0);
        }

        return sourceSignal;
    }

}
