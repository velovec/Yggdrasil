package ru.linachan.skuld;

import org.apache.commons.math3.complex.Complex;

import java.nio.ByteBuffer;

public class SkuldSignal {

    private Complex[] samples;

    public SkuldSignal(byte[][] rawSamples) {
        int sampleCounter = 0;

        samples = new Complex[rawSamples.length];

        for (byte[] sample : rawSamples) {
            samples[sampleCounter] = new Complex(ByteBuffer.wrap(sample).getInt() / Math.pow(2, 8 * sample.length));
            sampleCounter++;
        }
    }

    public SkuldSignal(int sampleRate) {
        samples = new Complex[sampleRate];

        for (int sampleID = 0; sampleID < sampleRate(); sampleID++) {
            samples[sampleID] = new Complex(0.0);
        }
    }

    public Complex[] getSamples() {
        return samples;
    }

    public void copyFrom(SkuldSignal sourceSignal) {
        this.samples = sourceSignal.getSamples();
    }

    public void subtract(SkuldSignal targetSignal) {
        if (this.sampleRate() == targetSignal.sampleRate()) {
            Complex[] targetSamples = targetSignal.getSamples();
            for (int sampleID = 0; sampleID < sampleRate(); sampleID++) {
                this.samples[sampleID].subtract(targetSamples[sampleID]);
            }
        } else {
            throw new RuntimeException("SkuldSignals should have the same sample rate!");
        }
    }

    public int sampleRate() {
        return samples.length;
    }
}
