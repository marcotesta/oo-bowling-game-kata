package org.mondogrua;

import java.util.Optional;

public class NullFrame implements IFrame {

    @Override
    public void addReportTo(ReportAccumulator reportAccumulator) {

    }

    @Override
    public void add(Roll roll) {

    }

    @Override
    public Integer getPartialScoreOr(Integer score) {
        return score;
    }

    @Override
    public Optional<Integer> getPartialScore() {
        return Optional.empty();
    }
}
