package org.mondogrua;

import java.util.Optional;

public class ScoreAccumulator {
    private Integer accumulator = 0;

    public void add(Integer score) {
        if (score != null) {
            accumulator += score;
        }
    }

    public Optional<Integer> value() {
        return accumulator == 0 ? Optional.empty() : Optional.of(this.accumulator);
    }
}
