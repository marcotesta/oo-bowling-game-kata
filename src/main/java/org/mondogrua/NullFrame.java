package org.mondogrua;

public class NullFrame implements IFrame {
    @Override
    public Integer getPartialScore() {
        return 0;
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
