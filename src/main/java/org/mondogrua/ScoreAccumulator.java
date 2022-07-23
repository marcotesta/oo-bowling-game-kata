package org.mondogrua;

public class ScoreAccumulator {
    private Integer accumulator = 0;

    public void add(Integer score) {
        if (score != null) {
            accumulator += score;
        }
    }

    public Integer value() {
        return accumulator ;
    }
}
