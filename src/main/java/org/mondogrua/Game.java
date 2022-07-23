package org.mondogrua;

public class Game {

    private final Frame[] frames = new Frame[10];

    public Game() {
        Frame previousFrame = null;
        for (int frameIdx = 0; frameIdx <= 9; frameIdx++) {
            Frame frame = new Frame(frameIdx+1, previousFrame);
            if (previousFrame != null) {
                previousFrame.setNextFrame(frame);
            }
            previousFrame = frame;
            frames[frameIdx] = frame;
        }
    }

    public void add(Roll roll) {
        this.frames[0].add(roll);
    }

    public Integer getScore() {
        return frames[0].getPartialScoreOr(0);
    }

    public String getReport() {
        ReportAccumulator reportAccumulator = new ReportAccumulator();
        frames[0].addReportTo(reportAccumulator);
        return reportAccumulator.value();
    }
}
