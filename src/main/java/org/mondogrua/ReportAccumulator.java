package org.mondogrua;

public class ReportAccumulator {
    private String accumulator = "";

    public void add(String report) {
        if(report != null && !report.isEmpty()) {
            if (accumulator.length() > 0) accumulator += "\n";
            accumulator += report;
        }
    }

    public String value() {
        return this.accumulator;
    }
}
