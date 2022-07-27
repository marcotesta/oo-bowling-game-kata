package org.mondogrua;

import java.util.Optional;

public interface IRoll {
    int MAX_PINS = 10;

    void addPinsTo(ScoreAccumulator accumulator);

    String getReport();

    void addPossibleScoreTo(ScoreAccumulator maxPossibleScoreAccumulator);

    boolean isStrike();

    int getPinsLeft();
}
