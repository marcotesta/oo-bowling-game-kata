package org.mondogrua;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Frame implements IFrame {
    private final int index;
    private IFrame nextFrame;
    private IRoll firstRoll = new NullRoll();
    private IRoll secondRoll = new NullRoll();
    private IRoll thirdRoll = new NullRoll();
    private State state = new NotStarted();

    public Frame(int index) {
        this.index = index;
        this.nextFrame = new NullFrame();
    }

    public void setNextFrame(Frame frame) {
        this.nextFrame = frame != null ? frame : new NullFrame();
    }

    public void add(Roll roll) {
        state.handle(roll);
        state.setNextState();
        state.passNext(roll);
    }

    public void addReportTo(ReportAccumulator reportAccumulator, Integer previousFramePartialScore) {
        reportAccumulator.add(getFrameReport(previousFramePartialScore));
        nextFrame.addReportTo(reportAccumulator, getFramePartialScore(previousFramePartialScore).orElse(previousFramePartialScore));
    }

    @Override
    public Integer getLastPartialScore(Integer previousFramePartialScore) {
        Optional<Integer> framePartialScore = getFramePartialScore(previousFramePartialScore);
        if (framePartialScore.isEmpty()) {
            return previousFramePartialScore;
        }
        return nextFrame.getLastPartialScore(getFramePartialScore(previousFramePartialScore).orElse(previousFramePartialScore));
    }

    @Override
    public int currentFrame(Integer previousFrameIndex) {
        return state.currentFrame(previousFrameIndex);
    }

    private Optional<Integer> getFramePartialScore(Integer previousFramePartialScore) {
        return getFrameScore().map(frameScore -> previousFramePartialScore + frameScore);
    }

    private Optional<Integer> getFrameScore() {
        return state.getFrameScore();
    }

    private Optional<Integer> getPins() {
        ScoreAccumulator scoreAccumulator = new ScoreAccumulator();
        firstRoll.addPinsTo(scoreAccumulator);
        secondRoll.addPinsTo(scoreAccumulator);
        thirdRoll.addPinsTo(scoreAccumulator);
        return scoreAccumulator.value();
    }

    private String getFrameReport(Integer previousFramePartialScore) {
        return  Stream.of(
                        getRoll1Report(),
                        getRoll2Report(),
                        getPartialScoreReport(previousFramePartialScore)
                )
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(", ", "Frame " + index + ": ", ""));
    }

    private String getRoll1Report() {
        return state.getRoll1Report();
    }

    private String getRoll2Report() {
        return state.getRoll2Report();
    }

    public String getPartialScoreReport(Integer previousFramePartialScore) {
        return getFramePartialScore(previousFramePartialScore)
                .map(framePartialScore -> "score: " + framePartialScore)
                .orElse("");
    }

    private void setState(State state) {
        this.state = state;
    }

    // State:
    // NotStarted
    // OneRoll
    // JustOpen
    // Open
    // Spare
    // SpareWithBonus
    // Strike
    // StrikeWitOneBonus
    // StrikeWithTwoBonuses

    interface State {
        default void setNextState() {}

        default void handle(Roll roll) {}

        default Optional<Integer> getFrameScore() {
            return Optional.empty();
        }

        default void passNext(Roll roll) {}

        default String getRoll1Report() { return ""; }

        default String getRoll2Report() { return ""; }

        int currentFrame(Integer previousFrameIndex);
    }

    private class NotStarted implements State {
        public void setNextState() {
            getPins().ifPresent(pins -> setState(pins.equals(10) ? new Strike() : new OneRoll()));
        }

        @Override
        public void handle(Roll roll) {
            firstRoll = roll;
        }

        @Override
        public int currentFrame(Integer previousFrameIndex) {
            return index;
        }
    }

    private class OneRoll implements State {

        @Override
        public void setNextState() {
            getPins().ifPresent(pins -> setState(pins.equals(10) ? new Spare() : new JustOpen()));
        }

        @Override
        public void handle(Roll roll) {
            secondRoll = roll;
        }

        @Override
        public String getRoll1Report() {
            return firstRoll.getReport();
        }

        @Override
        public int currentFrame(Integer previousFrameIndex) {
            return index;
        }
    }

    private class JustOpen implements State {
        @Override
        public void setNextState() {
            setState(new Open());
        }

        @Override
        public Optional<Integer> getFrameScore() {
            return getPins();
        }

        @Override
        public String getRoll1Report() {
            return firstRoll.getReport();
        }
        @Override
        public String getRoll2Report() {
            return secondRoll.getReport();
        }

        @Override
        public int currentFrame(Integer previousFrameIndex) {
            return nextFrame.currentFrame(-1);
        }
    }

    private class Open implements State {

        @Override
        public Optional<Integer> getFrameScore() {
            return getPins();
        }

        @Override
        public void passNext(Roll roll) {
            nextFrame.add(roll);
        }
        @Override
        public String getRoll1Report() {
            return firstRoll.getReport();
        }
        @Override
        public String getRoll2Report() {
            return secondRoll.getReport();
        }

        @Override
        public int currentFrame(Integer previousFrameIndex) {
            return nextFrame.currentFrame(-1);
        }
    }

    private class Spare implements State {
        @Override
        public void setNextState() {
            setState( new SpareWithBonus());
        }

        @Override
        public void handle(Roll roll) {
            thirdRoll = roll;
        }

        @Override
        public String getRoll1Report() {
            return firstRoll.getReport();
        }
        @Override
        public String getRoll2Report() {
            return "/";
        }

        @Override
        public int currentFrame(Integer previousFrameIndex) {
            return nextFrame.currentFrame(index);
        }
    }

    private class SpareWithBonus implements State {
        @Override
        public Optional<Integer> getFrameScore() {
            return getPins();
        }

        @Override
        public void passNext(Roll roll) {
            nextFrame.add(roll);
        }
        @Override
        public String getRoll1Report() {
            return firstRoll.getReport();
        }
        @Override
        public String getRoll2Report() {
            return "/";
        }

        @Override
        public int currentFrame(Integer previousFrameIndex) {
            return nextFrame.currentFrame(-1);
        }
    }

    private class Strike implements State {
        @Override
        public void setNextState() {
            setState( new StrikeWitOneBonus());
        }

        @Override
        public void handle(Roll roll) {
            secondRoll = roll;
        }

        @Override
        public String getRoll2Report() {
            return "X";
        }

        @Override
        public int currentFrame(Integer previousFrameIndex) {
            return nextFrame.currentFrame(index);
        }
    }

    private class StrikeWitOneBonus implements State {
        @Override
        public void setNextState() {
            setState(new StrikeWithTwoBonuses());
        }

        @Override
        public void handle(Roll roll) {
            thirdRoll = roll;
        }

        @Override
        public void passNext(Roll roll) {
            nextFrame.add(roll);
        }
        @Override
        public String getRoll2Report() {
            return "X";
        }

        @Override
        public int currentFrame(Integer previousFrameIndex) {
            return nextFrame.currentFrame(index);
        }
    }

    private class StrikeWithTwoBonuses implements State {
        @Override
        public Optional<Integer> getFrameScore() {
            return getPins();
        }

        @Override
        public void passNext(Roll roll) {
            nextFrame.add(roll);
        }

        @Override
        public String getRoll2Report() {
            return "X";
        }

        @Override
        public int currentFrame(Integer previousFrameIndex) {
            return nextFrame.currentFrame(-1);
        }
    }
}
