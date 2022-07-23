package org.mondogrua;

import java.util.Optional;

public class NullFrame implements IFrame {
    @Override
    public Optional<Integer> getPartialScore() {
        return Optional.empty();
    }

    @Override
    public void addScoreTo(ScoreAccumulator scoreAcc) {

    }

    @Override
    public void addReportTo(ReportAccumulator reportAccumulator) {

    }

    @Override
    public void add(Roll roll) {

    }
}
