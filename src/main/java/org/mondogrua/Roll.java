package org.mondogrua;

import java.util.Optional;

public class Roll implements IRoll {
    private final int pins;

    public Roll(int pins) {
        this.pins = pins;
    }

    public Optional<Integer> getPins() {
        return Optional.of(this.pins);
    }
}
