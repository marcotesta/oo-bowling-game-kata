package org.mondogrua;

import java.util.Objects;
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
        getFrameScore().ifPresent(scoreAcc::add);
        nextFrame.addScoreTo(scoreAcc);
    }

    public void addReportTo(ReportAccumulator reportAccumulator) {
        reportAccumulator.add(getFrameReport());
        nextFrame.addReportTo(reportAccumulator);
    }

    @Override
    public Integer getPartialScore() {
        return partialScore;
    }

    private void setPartialScore() {
        partialScore = previousFrame.getPartialScore() + getFrameScore().orElse(0);
    }

    private Optional<Integer> getFrameScore() {
        return status.getFrameScore();
    }

    private Optional<Integer> getPins() {
        return Stream.concat(
                        Stream.concat(
                                firstRoll.getPins().stream(),
                                secondRoll.getPins().stream()),
                        thirdRoll.getPins().stream())
                .reduce(Integer::sum);
    }

    private String getFrameReport() {
        return  Stream.of(getRoll1Report(), getRoll2Report(), getPartialScoreReport())
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(", ", "Frame " + index + ": ", ""));
    }

    private String getRoll1Report() {
        return status.getRoll1Report();
    }

    private String getRoll2Report() {
        return status.getRoll2Report();
    }

    private String getPartialScoreReport() {
        return status.getPartialScoreReport();
    }

    private void setStatus(Status status) {
        this.status = status;
        this.setPartialScore();
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

        default Optional<Integer> getFrameScore() {
            return Optional.empty();
        }

        default void passNext(Roll roll) {}

        default String getRoll1Report() { return ""; }

        default String getRoll2Report() { return ""; }

        default String getPartialScoreReport() { return ""; }
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
        public String getRoll1Report() {
            return firstRoll.getPins().map(Object::toString).orElse("");
        }
    }

    private class JustOpen implements Status {
        @Override
        public void setNextStatus() {
            Status nextStatus =  new Open();
            setStatus(nextStatus);
        }

        @Override
        public Optional<Integer> getFrameScore() {
            return getPins();
        }

        @Override
        public String getRoll1Report() {
            return firstRoll.getPins().map(Object::toString).orElse("");
        }
        @Override
        public String getRoll2Report() {
            return secondRoll.getPins().map(Objects::toString).orElse("");
        }

        public String getPartialScoreReport() {
            return "score: " + partialScore;
        }
    }

    private class Open implements Status {

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
            return firstRoll.getPins().map(Objects::toString).orElse("");
        }
        @Override
        public String getRoll2Report() {
            return secondRoll.getPins().map(Objects::toString).orElse("");
        }


        public String getPartialScoreReport() {
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
        public String getRoll1Report() {
            return firstRoll.getPins().map(Objects::toString).orElse("");
        }
        @Override
        public String getRoll2Report() {
            return "/";
        }
    }

    private class SpareWithBonus implements Status {
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
            return firstRoll.getPins().map(Objects::toString).orElse("");
        }
        @Override
        public String getRoll2Report() {
            return "/";
        }

        public String getPartialScoreReport() {
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
        public String getRoll2Report() {
            return "X";
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
        public String getRoll2Report() {
            return "X";
        }
    }

    private class StrikeWithTwoBonuses implements Status {
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
