package ru.linachan.skuld;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.nio.ByteBuffer;

public class SkuldSample {

    private Complex[] samples;

    public SkuldSample(byte[][] rawSamples) {
        int sampleCounter = 0;

        double[] samples = new double[rawSamples.length];

        for (byte[] sample : rawSamples) {
            samples[sampleCounter] = ByteBuffer.wrap(sample).getInt() / Math.pow(2, 8 * sample.length);
            sampleCounter++;
        }
        FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);

        this.samples = transformer.transform(samples, TransformType.FORWARD);
    }

    public Complex[] getSamples() {
        return samples;
    }
}
