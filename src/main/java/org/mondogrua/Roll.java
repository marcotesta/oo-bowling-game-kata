package org.mondogrua;

import java.util.Optional;

public class Roll implements IRoll {
    private final Integer pins;

    public Roll(int pins) {
        this.pins = pins;
    }

    public Optional<Integer> getPins() {
        return Optional.of(this.pins);
    }
    @Override
    public String getReport() {
        return pins.toString();
    }
}
