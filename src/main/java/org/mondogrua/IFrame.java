package org.mondogrua;

import java.util.Optional;

public interface IFrame {
    void addReportTo(ReportAccumulator reportAccumulator);

    void add(Roll roll);
    Integer getPartialScoreOr(Integer score);

    Optional<Integer> getPartialScore();
}
