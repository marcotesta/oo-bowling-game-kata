package org.mondogrua;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Frame implements IFrame {
    private final int index;

    private final IFrame previousFrame;
    private IFrame nextFrame;
    private IRoll firstRoll = new NullRoll();
    private IRoll secondRoll = new NullRoll();
    private IRoll thirdRoll = new NullRoll();
    private State state = new NotStarted();

    private Integer partialScore = null;

    public Frame(int index, Frame previousFrame) {
        this.index = index;
        this.previousFrame = previousFrame != null ? previousFrame : new NullFrame();
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

    public void addReportTo(ReportAccumulator reportAccumulator) {
        reportAccumulator.add(getFrameReport());
        nextFrame.addReportTo(reportAccumulator);
    }

    @Override
    public Integer getPartialScoreOr(Integer score) {
        if (partialScore == null) {
            return score;
        }
        return nextFrame.getPartialScoreOr(partialScore);
    }

    @Override
    public Optional<Integer> getPartialScore() {
        return Optional.ofNullable(partialScore);
    }

    private void setPartialScore() {
        partialScore =  Stream.concat(previousFrame.getPartialScore().stream(),
                getFrameScore().stream()).reduce(0, Integer::sum);
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

    private String getFrameReport() {
        return  Stream.of(getRoll1Report(), getRoll2Report(), getPartialScoreReport())
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(", ", "Frame " + index + ": ", ""));
    }

    private String getRoll1Report() {
        return state.getRoll1Report();
    }

    private String getRoll2Report() {
        return state.getRoll2Report();
    }

    private String getPartialScoreReport() {
        return state.getPartialScoreReport();
    }

    private void setState(State state) {
        this.state = state;
        this.setPartialScore();
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

        default String getPartialScoreReport() { return ""; }
    }

    private class NotStarted implements State {
        public void setNextState() {
            Optional<Integer> pins = getPins();
            if (pins.isEmpty()) {
                return;
            }
            State nextStatus = pins.get().equals(10)
                    ? new Strike()
                    : new OneRoll();
            setState(nextStatus);
        }

        @Override
        public void handle(Roll roll) {
            firstRoll = roll;
        }
    }

    private class OneRoll implements State {

        @Override
        public void setNextState() {
            Optional<Integer> pins = getPins();
            if (pins.isEmpty()) {
                return;
            }
            State nextStatus = pins.get().equals(10)
                    ? new Spare()
                    : new JustOpen();
            setState(nextStatus);
        }

        @Override
        public void handle(Roll roll) {
            secondRoll = roll;
        }

        @Override
        public String getRoll1Report() {
            return firstRoll.getReport();
        }
    }

    private class JustOpen implements State {
        @Override
        public void setNextState() {
            State nextStatus =  new Open();
            setState(nextStatus);
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

        public String getPartialScoreReport() {
            return "score: " + partialScore;
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


        public String getPartialScoreReport() {
            return "score: " + partialScore;
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

        public String getPartialScoreReport() {
            return  "score: " + partialScore;
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

        public String getPartialScoreReport() {
            return "score: " + partialScore;
        }
    }
}
