package org.mondogrua;

import java.util.Optional;

public class ReportAccumulator {


    private String accumulator = "";

    public void add(Optional<String> optionalReport) {
        optionalReport.ifPresent(report -> accumulator += report);
    }

    public String value() {
        return this.accumulator;
    }
}
