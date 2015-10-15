package ru.linachan.skuld;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class SkuldSynthesizableSignal {

    private Map<String, Object> signalProperties = new HashMap<>();

    public final Set<String> getProperties() {
        return signalProperties.keySet();
    }

    public final Object getProperty(String name) {
        return signalProperties.get(name);
    }

    public final void setProperty(String name, Object value) {
        signalProperties.put(name, value);
    }

    public final void copyFrom(SkuldSynthesizableSignal signal) {
        if (signal.getClass() != this.getClass())
            throw new RuntimeException("Exception occurred while trying to set synthesizable signal!");

        for (Map.Entry<String, Object> property : signal.signalProperties.entrySet()) {
            signalProperties.put(property.getKey(), property.getValue());
        }
    }

    public abstract SkuldSignal synthesize(SkuldSignal source);

    public final SkuldSignal synthesize(int sampleRate) {
        return synthesize(new SkuldSignal(sampleRate));
    }
}
