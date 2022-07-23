package org.mondogrua;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class Frame implements IFrame {
    private final int index;

    private final IFrame previousFrame;
    private IFrame nextFrame;
    private IRoll firstRoll = new NullRoll();
    private IRoll secondRoll = new NullRoll();
    private IRoll thirdRoll = new NullRoll();
    private Status status = new Empty();

    private Integer partialScore = 0;

    public Frame(int index, Frame previousFrame) {
        this.index = index;
        this.previousFrame = previousFrame != null ? previousFrame : new NullFrame();
        this.nextFrame = new NullFrame();
    }

    public void setNextFrame(Frame frame) {
        this.nextFrame = frame != null ? frame : new NullFrame();
    }

    public void add(Roll roll) {
        status.handle(roll);
        status.setNextStatus();
        status.passNext(roll);
    }

    public void addScoreTo(ScoreAccumulator scoreAcc) {
        Optional<Integer> optonalScore = getScore();
        optonalScore.ifPresent(scoreAcc::add);
        nextFrame.addScoreTo(scoreAcc);
    }

    public void addReportTo(ReportAccumulator reportAccumulator) {
        Optional<String> optionalReport = getReport();

        optionalReport.ifPresent(reportAccumulator::add);
        nextFrame.addReportTo(reportAccumulator);
    }

    private Optional<Integer> getScore() {
        return status.getScore();
    }

    public Optional<String> getReport() {
        Optional<String> roll1Report = getRoll1Report();
        Optional<String> roll2Report = getRoll2Report();
        Optional<String> scoreReport = Optional.of(getScoreReport());
        return Stream.of(roll1Report, roll2Report, scoreReport)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(value -> !value.isEmpty())
                .reduce((first, second) -> first +", "+second);
    }

    private String getScoreReport() {
        return status.getScoreReport();
    }

    private Optional<String> getRoll1Report() {
        return status.getRoll1Report();
    }

    private Optional<String> getRoll2Report() {
        return status.getRoll2Report();
    }

    private void setStatus(Status status) {
        this.status = status;
        this.setPartialScore();
    }

    private Optional<Integer> getPins() {
        return Stream.concat(
                Stream.concat(
                        firstRoll.getPins().stream(),
                        secondRoll.getPins().stream()),
                        thirdRoll.getPins().stream())
                .reduce(Integer::sum);
    }

    private void setPartialScore() {
        Integer previousFramesScore = previousFrame.getPartialScore();
        Integer currentFrameScore = getScore().orElse(0);
        partialScore = previousFramesScore + currentFrameScore;
    }

    @Override
    public Integer getPartialScore() {
        return partialScore;
    }

    // Status:
    // Empty
    // FirstRoll
    // JustOpen
    // Open
    // Spare
    // SpareWithBonus
    // Strike
    // StrikeWitOneBonus
    // StrikeWithTwoBonuses

    interface Status {
        default void setNextStatus() {}

        default void handle(Roll roll) {}

        default Optional<Integer> getScore() {
            return Optional.empty();
        }

        default void passNext(Roll roll) {}

        default Optional<String> getRoll1Report() { return Optional.empty(); }

        default Optional<String> getRoll2Report() { return Optional.empty(); }

        default String getScoreReport() { return ""; }
    }

    private class Empty implements Status {
        public void setNextStatus() {
            Optional<Integer> pins = getPins();
            if (pins.isEmpty()) {
                return;
            }
            Status nextStatus = pins.get().equals(10)
                    ? new Strike()
                    : new FirstRoll();
            setStatus(nextStatus);
        }

        @Override
        public void handle(Roll roll) {
            firstRoll = roll;
        }
    }

    private class FirstRoll implements Status {

        @Override
        public void setNextStatus() {
            Optional<Integer> pins = getPins();
            if (pins.isEmpty()) {
                return;
            }
            Status nextStatus = pins.get().equals(10)
                    ? new Spare()
                    : new JustOpen();
            setStatus(nextStatus);
        }

        @Override
        public void handle(Roll roll) {
            secondRoll = roll;
        }

        @Override
        public Optional<String> getRoll1Report() {
            return firstRoll.getPins().map(Object::toString);
        }
    }

    private class JustOpen implements Status {
        @Override
        public void setNextStatus() {
            Status nextStatus =  new Open();
            setStatus(nextStatus);
        }

        @Override
        public Optional<Integer> getScore() {
            return getPins();
        }

        @Override
        public Optional<String> getRoll1Report() {
            return firstRoll.getPins().map(Object::toString);
        }
        @Override
        public Optional<String> getRoll2Report() {
            return secondRoll.getPins().map(Objects::toString);
        }

        public String getScoreReport() {
            return "score: " + partialScore;
        }
    }

    private class Open implements Status {

        @Override
        public Optional<Integer> getScore() {
            return getPins();
        }

        @Override
        public void passNext(Roll roll) {
            nextFrame.add(roll);
        }
        @Override
        public Optional<String> getRoll1Report() {
            return firstRoll.getPins().map(Objects::toString);
        }
        @Override
        public Optional<String> getRoll2Report() {
            return secondRoll.getPins().map(Objects::toString);
        }


        public String getScoreReport() {
            return "score: " + partialScore;
        }
    }

    private class Spare implements Status {
        @Override
        public void setNextStatus() {
            setStatus( new SpareWithBonus());
        }

        @Override
        public void handle(Roll roll) {
            thirdRoll = roll;
        }

        @Override
        public Optional<String> getRoll1Report() {
            return firstRoll.getPins().map(Objects::toString);
        }
        @Override
        public Optional<String> getRoll2Report() {
            return Optional.of( "/");
        }
    }

    private class SpareWithBonus implements Status {
        @Override
        public Optional<Integer> getScore() {
            return getPins();
        }

        @Override
        public void passNext(Roll roll) {
            nextFrame.add(roll);
        }
        @Override
        public Optional<String> getRoll1Report() {
            return firstRoll.getPins().map(Objects::toString);
        }
        @Override
        public Optional<String> getRoll2Report() {
            return Optional.of( "/");
        }

        public String getScoreReport() {
            return  "score: " + partialScore;
        }
    }

    private class Strike implements Status {
        @Override
        public void setNextStatus() {
            setStatus( new StrikeWitOneBonus());
        }

        @Override
        public void handle(Roll roll) {
            secondRoll = roll;
        }

        @Override
        public Optional<String> getRoll2Report() {
            return Optional.of("X");
        }
    }

    private class StrikeWitOneBonus implements Status {
        @Override
        public void setNextStatus() {
            setStatus(new StrikeWithTwoBonuses());
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
        public Optional<String> getRoll2Report() {
            return Optional.of("X");
        }
    }

    private class StrikeWithTwoBonuses implements Status {
        @Override
        public Optional<Integer> getScore() {
            return getPins();
        }

        @Override
        public void passNext(Roll roll) {
            nextFrame.add(roll);
        }

        @Override
        public Optional<String> getRoll2Report() {
            return Optional.of( "X");
        }

        public String getScoreReport() {
            return "score: " + partialScore;
        }
    }
}
