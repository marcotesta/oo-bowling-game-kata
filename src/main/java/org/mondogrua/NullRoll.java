package org.mondogrua;

import java.util.Optional;

public class NullRoll implements IRoll {

    @Override
    public void addPinsTo(ScoreAccumulator accumulator) {
    }

    @Override
    public String getReport() {
        return "";
    }

    @Override
    public void addPossibleScoreTo(ScoreAccumulator maxPossibleScoreAccumulator) {
        maxPossibleScoreAccumulator.add(MAX_PINS);
    }

    @Override
    public boolean isStrike() {
        return false;
    }

    @Override
    public int getPinsLeft() {
        return MAX_PINS;
    }
}
