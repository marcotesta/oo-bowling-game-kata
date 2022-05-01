package org.mondogrua;

import java.util.Optional;

public class ScoreAccumulator {
    private Integer accumulator = 0;

    public void add(Optional<Integer> optionalScore) {
        optionalScore.ifPresent(score -> accumulator += score);
    }

    public Integer value() {
        return this.accumulator;
    }
}
