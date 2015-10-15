package ru.linachan.skuld;

import java.util.ArrayList;
import java.util.List;

public class SkuldSignalCutter {

    private SkuldSignal sourceSignal;
    private SkuldSignal targetSignal;

    private List<SkuldSynthesizableSignal> cutterParameters = new ArrayList<>();
    private List<SkuldSignal> cutterSignals = new ArrayList<>();

    private int lastCutterID = 0;

    public SkuldSignalCutter(SkuldSignal sourceSignal, SkuldSignal targetSignal) {
        this.sourceSignal = sourceSignal;
        this.targetSignal = targetSignal;
    }

    public void resetCutter() {
        targetSignal.copyFrom(sourceSignal);
        lastCutterID = 0;
    }

    public int getCuttersCount() {
        return cutterParameters.size();
    }

    public SkuldSynthesizableSignal getLastCutter() {
        return cutterParameters.get(lastCutterID);
    }

    public SkuldSignal getTargetSignal() {
        return targetSignal;
    }

    public List<SkuldSynthesizableSignal> getCutterParameters() {
        return cutterParameters;
    }

    public void reCut() {
        resetCutter();

        for (SkuldSignal cutterSignal: cutterSignals) {
            targetSignal.subtract(cutterSignal);
        }

        lastCutterID = cutterSignals.size();
    }

    public boolean applyNextCutter() {
        if (lastCutterID >= cutterSignals.size()) {
            return false;
        }

        targetSignal.subtract(cutterSignals.get(lastCutterID++));

        return true;
    }

    public void addCutter(SkuldSynthesizableSignal cutter) {
        cutterParameters.add(cutter);

        SkuldSignal cutterSignal = new SkuldSignal(targetSignal.sampleRate());

        cutter.synthesize(cutterSignal);

        cutterSignals.add(cutterSignal);
    }

    public void removeCutter(SkuldSynthesizableSignal cutter) {
        int cutterID = cutterParameters.indexOf(cutter);

        cutterParameters.remove(cutterID);
        cutterSignals.remove(cutterID);
    }

    public void tearDown() {
        cutterParameters.clear();
        cutterSignals.clear();
    }

    public void reSyntesizeCutterSignal(SkuldSynthesizableSignal cutter) {
        int cutterID = cutterParameters.indexOf(cutter);

        cutter.synthesize(cutterSignals.get(cutterID));
    }
}
