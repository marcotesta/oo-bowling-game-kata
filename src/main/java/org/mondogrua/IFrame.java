package org.mondogrua;

public interface IFrame {
    void addReportTo(ReportAccumulator reportAccumulator, Integer previousFramePartialScore);

    void add(Roll roll);
    Integer getLastPartialScore(Integer previousFramePartialScore);
}
