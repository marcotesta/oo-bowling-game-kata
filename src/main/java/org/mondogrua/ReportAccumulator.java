package org.mondogrua;

import java.util.Optional;

public class ReportAccumulator {
    private String accumulator = "";

    public void add(String report) {
        if(report != null) {
            if (accumulator.length() > 0) accumulator += "\n";
            accumulator += report;
        }
    }

    public String value() {
        return this.accumulator;
    }
}
