package org.mondogrua;

public interface IFrame {
    Integer getPartialScore();

    void addScoreTo(ScoreAccumulator scoreAcc);

    void addReportTo(ReportAccumulator reportAccumulator);

    void add(Roll roll);
}
