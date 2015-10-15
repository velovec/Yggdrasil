package ru.linachan.skuld;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class SkuldSpectrum {

    private SkuldSignal sourceSignal;
    private Complex[] signalSpectrum;
    private FastFourierTransformer fastFourierTransformer = new FastFourierTransformer(DftNormalization.STANDARD);

    public SkuldSpectrum(int sampleRate) {
        signalSpectrum = new Complex[sampleRate];
    }

    public SkuldSpectrum(SkuldSignal sourceSignal) {
        this.sourceSignal = sourceSignal;
        reCalculate();
    }

    public void reCalculate() {
        signalSpectrum = fastFourierTransformer.transform(sourceSignal.getSamples(), TransformType.FORWARD);
    }

    public int sampleRate() {
        return sourceSignal.sampleRate();
    }

    public double getAmplitude(int sampleID) {
        double amplitude = abs(sampleID);

        return (sampleID == 0) ? amplitude / sampleRate() : amplitude / sampleRate() * 2;
    }

    public double abs(int sampleID) {
        if ((sampleID >= 0)&&(sampleID <= sampleRate())) {
            return signalSpectrum[sampleID].abs();
        }
        return 0.0;
    }

    public double getPhase(int sampleID) {
        if ((sampleID >= 0)&&(sampleID <= sampleRate())) {
            return signalSpectrum[sampleID].getArgument();
        }
        return 0.0;
    }

    public int detectStrongPeak(double minimalAmplitude) {
        int peakID = -1;

        for (int sampleID = 0; sampleID < sampleRate(); sampleID++) {
            if ((abs(sampleID) / sampleRate() * 2 > minimalAmplitude)&&((peakID == -1)||(abs(sampleID) > abs(peakID)))) {
                peakID = sampleID;
            }
        }

        return peakID;
    }

    public double getAverageAmplitude(int harmonic, int windowSize) {
        int minimalAmplitude = Math.max(harmonic - windowSize, 0);
        int maximalAmplitude = Math.min(harmonic + windowSize, sampleRate() - 1);

        double averageAmplitude = 0.0;

        for (int sampleID = 0; sampleID < sampleRate(); sampleID++) {
            if (sampleID == harmonic)
                continue;

            averageAmplitude += getAmplitude(sampleID);
        }

        return averageAmplitude / (maximalAmplitude - minimalAmplitude - 1);
    }

    private int[] getDistributionFunction(int confidence) {
        double amplitudeStep = getAmplitude(detectStrongPeak(0.0)) / confidence;
        int[] distributionFunction = new int[confidence];

        for (int sampleID = 0; sampleID < sampleRate(); sampleID++) {
            int distributionPosition = Math.min((int) (Math.ceil(getAmplitude(sampleID)) / amplitudeStep), confidence);

            for (int distributionStep = 1; distributionStep < distributionPosition; distributionStep++) {
                distributionFunction[distributionStep]++;
            }
        }

        return distributionFunction;
    }

    private int[] getDistributionDensity(int confidence) {
        int[] distributionFunction = getDistributionFunction(confidence);
        int[] distributionDensity = new int[confidence - 1];

        for (int valueID = 0; valueID < confidence; valueID++) {
            distributionDensity[valueID] = distributionFunction[valueID + 1] - distributionFunction[valueID];
        }

        return distributionDensity;
    }

    public double estimatedNoise(int distributionConfidence, int peakTolerance, int maximalDistance) {
        int[] distributionDensity = getDistributionDensity(distributionConfidence);

        int peakID = 0;

        for (int densityValueID = 0; densityValueID < distributionConfidence - 1; densityValueID++) {
            peakID = (distributionDensity[densityValueID] > distributionDensity[peakID]) ? distributionDensity[densityValueID] : peakID;
        }

        int startingPeakID = peakID;
        int currentTolerance = 0;

        while ((currentTolerance < peakTolerance)&&((peakID - startingPeakID) < maximalDistance)) {
            if (distributionDensity[peakID + 1] > distributionDensity[peakID]) {
                currentTolerance++;
            }
            peakID++;
        }

        return peakID * getAmplitude(detectStrongPeak(0.0)) / distributionConfidence;
    }
}
