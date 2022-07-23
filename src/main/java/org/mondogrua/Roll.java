package org.mondogrua;

import java.util.Optional;

public class Roll implements IRoll {
    private final Integer pins;

    public Roll(int pins) {
        this.pins = pins;
    }

    @Override
    public void addPinsTo(ScoreAccumulator accumulator) {
        accumulator.add(pins);
    }

    @Override
    public String getReport() {
        return pins.toString();
    }
}
