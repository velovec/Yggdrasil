package ru.linachan.skuld;

import ru.linachan.util.MathHelper;

import java.util.List;

public class SkuldAnalyzer {

    private SkuldSignal sourceSignal;
    private double minimalAmplitude;
    private double heterodinAccuracy;
    private int windowSize;

    private final int MAX_CUT = 10;

    public SkuldAnalyzer(SkuldSignal sourceSignal, double minimalAmplitude, double heterodinAccuracy, int windowSize) {
        this.sourceSignal = sourceSignal;
        this.minimalAmplitude = minimalAmplitude;
        this.heterodinAccuracy = heterodinAccuracy;
        this.windowSize = windowSize;
    }

    public List<SkuldSynthesizableSignal> detectHarmonics() {
        SkuldSignalCutter signalCutter = new SkuldSignalCutter(sourceSignal, new SkuldSignal(sourceSignal));

        SkuldSynthesizableComplexExponent heterodinParameter = new SkuldSynthesizableComplexExponent();
        heterodinParameter.setProperty("frequency", 0.0);

        SkuldSignal heterodinSignal = new SkuldSignal(sourceSignal.sampleRate());
        SkuldSignal heterodinedSignal = signalCutter.getTargetSignal();
        SkuldSpectrum signalSpectrum = new SkuldSpectrum(heterodinedSignal);

        int harmonic;
        while ((harmonic = signalSpectrum.detectStrongPeak(minimalAmplitude)) != -1) {
            if (signalCutter.getCuttersCount() > MAX_CUT) {
                return null;
            }

            double heterodinSelected = 0.0;
            double noiseLevel = signalSpectrum.getAmplitude(harmonic) / signalSpectrum.getAverageAmplitude(harmonic, windowSize);

            for (double heterodinFrequency = -0.5; heterodinFrequency < (0.5 + heterodinAccuracy); heterodinFrequency += heterodinAccuracy) {
                heterodinParameter.setProperty("frequency", heterodinFrequency);
                heterodinParameter.synthesize(heterodinSignal);

                heterodinedSignal.copyFrom(signalCutter.getTargetSignal());
                heterodinedSignal.multiply(heterodinSignal);

                signalSpectrum.reCalculate();

                double reCalculatedNoiseLevel = signalSpectrum.getAmplitude(harmonic) / signalSpectrum.getAverageAmplitude(harmonic, windowSize);

                if (reCalculatedNoiseLevel > noiseLevel) {
                    noiseLevel = reCalculatedNoiseLevel;
                    heterodinSelected = heterodinFrequency;
                }
            }

            SkuldSynthesizableCosine cutterParameters = new SkuldSynthesizableCosine();

            heterodinParameter.setProperty("frequency", heterodinSelected);
            heterodinParameter.synthesize(heterodinSignal);

            heterodinedSignal.copyFrom(signalCutter.getTargetSignal());
            heterodinedSignal.multiply(heterodinSignal);

            signalSpectrum.reCalculate();

            cutterParameters.setProperty("amplitude", MathHelper.adaptiveRound(signalSpectrum.getAverageAmplitude(harmonic, windowSize)));
            cutterParameters.setProperty("frequency", harmonic - heterodinSelected);
            cutterParameters.setProperty("phase", MathHelper.round(signalSpectrum.getPhase(harmonic), 1));

            signalCutter.addCutter(cutterParameters);
            signalCutter.applyNextCutter();

            heterodinedSignal.copyFrom(signalCutter.getTargetSignal());

            signalSpectrum.reCalculate();
        }

        return signalCutter.getCutterParameters();
    }
}
