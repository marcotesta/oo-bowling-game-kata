package org.mondogrua;

public class Game {

    private final Frame framesHead = new Frame(1);

    public Game() {
        Frame previousFrame = framesHead;
        for (int frameIdx = 2; frameIdx <= 10; frameIdx++) {
            Frame frame = new Frame(frameIdx);
            previousFrame.setNextFrame(frame);
            previousFrame = frame;
        }
    }

    public void add(Roll roll) {
        this.framesHead.add(roll);
    }

    public Integer getScore() {
        return framesHead.getLastPartialScore(0);
    }

    public String getReport() {
        ReportAccumulator reportAccumulator = new ReportAccumulator();
        framesHead.addReportTo(reportAccumulator, 0);
        return reportAccumulator.value();
    }
}
