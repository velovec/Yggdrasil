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

    public SkuldSignal(SkuldSignal sourceSignal) {
        copyFrom(sourceSignal);
    }

    public void copyFrom(SkuldSignal sourceSignal) {
        samples = sourceSignal.getSamples();
    }

    public void subtract(SkuldSignal targetSignal) {
        if (this.sampleRate() == targetSignal.sampleRate()) {
            Complex[] targetSamples = targetSignal.getSamples();
            for (int sampleID = 0; sampleID < sampleRate(); sampleID++) {
                samples[sampleID].subtract(targetSamples[sampleID]);
            }
        } else {
            throw new RuntimeException("SkuldSignals should have the same sample rate!");
        }
    }

    public void multiply(SkuldSignal targetSignal) {
        if (this.sampleRate() == targetSignal.sampleRate()) {
            Complex[] targetSamples = targetSignal.getSamples();
            for (int sampleID = 0; sampleID < sampleRate(); sampleID++) {
                samples[sampleID].multiply(targetSamples[sampleID]);
            }
        } else {
            throw new RuntimeException("SkuldSignals should have the same sample rate!");
        }
    }

    public int sampleRate() {
        return samples.length;
    }

    public Complex[] getSamples() {
        return samples;
    }

    public Complex getSample(int sampleID) {
        return samples[sampleID];
    }

    public void setSample(int sampleID, Complex sampleValue) {
        samples[sampleID] = sampleValue;
    }

    public void setSample(int sampleID, double realValue, double imaginaryValue) {
        samples[sampleID] = new Complex(realValue, imaginaryValue);
    }
}
