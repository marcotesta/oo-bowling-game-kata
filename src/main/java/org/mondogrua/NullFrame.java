package org.mondogrua;

import java.util.Optional;

public class NullFrame implements IFrame {

    @Override
    public void addReportTo(ReportAccumulator reportAccumulator, Integer previousFramePartialScore) {
    }

    @Override
    public void add(Roll roll) {
    }

    @Override
    public Integer getLastPartialScore(Integer previousFramePartialScore) {
        return previousFramePartialScore;
    }

    @Override
    public int currentFrame(Integer previousFrameIndex) {
        return previousFrameIndex;
    }

    @Override
    public void addPossibleScoreTo(ScoreAccumulator maxPossibleScoreAccumulator) {
    }

    @Override
    public int getPinsLeft(int previousPinsLeft) {
        return previousPinsLeft;
    }
}
