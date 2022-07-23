package org.mondogrua;

import java.util.Optional;

public interface IFrame {
    void addScoreTo(ScoreAccumulator scoreAcc);

    void addReportTo(ReportAccumulator reportAccumulator);

    void add(Roll roll);
    Optional<Integer> getPartialScore();
}
