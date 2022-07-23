package org.mondogrua;

import java.util.Optional;

public class ScoreAccumulator {
    private Integer accumulator = null;

    public void add(Integer score) {
        if (score == null) {
            return;
        }
        accumulator = accumulator == null? score : accumulator + score;
    }

    public Optional<Integer> value() {
        return Optional.ofNullable(accumulator);
    }
}
