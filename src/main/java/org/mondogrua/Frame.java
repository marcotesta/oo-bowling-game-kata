package org.mondogrua;

import java.util.Optional;
import java.util.stream.Stream;

public class Frame {
    private final int index;
    private final Optional<Frame> nextFrame;
    private Optional<Roll> firstRoll = Optional.empty();
    private Optional<Roll> secondRoll = Optional.empty();
    private Optional<Roll> thirdRoll = Optional.empty();
    private Status status = new Empty();

    public Frame(int index, Optional<Frame> nextFrame) {
        this.index = index;
        this.nextFrame = nextFrame;
    }

    public void add(Roll roll) {
        status.handle(roll);
        status.passNext(roll);
        setStatus(status.getNextStatus());
    }

    public Optional<Integer> getScore() {
        return status.getScore();
    }

    void setStatus(Optional<Status> optionalStatus) {
        optionalStatus.ifPresent(status -> this.status = status);
    }

    public void addScore(ScoreAccumulator scoreAcc) {
        Optional<Integer> optonalScore = getScore();
        scoreAcc.add(optonalScore);
        nextFrame.ifPresent(frame -> frame.addScore(scoreAcc));
    }

    Optional<Integer> getPartialScore() {
        return Stream.concat(
                Stream.concat(
                        firstRoll.stream(),
                        secondRoll.stream()),
                        thirdRoll.stream())
                .map(Roll::getPins)
                .reduce(Integer::sum);
    }


    interface Status {
        Optional<Status> getNextStatus();

        void handle(Roll roll);

        Optional<Integer> getScore();

        void passNext(Roll roll);

    }

    private class Empty implements Status {
        public Optional<Status> getNextStatus() {
            Optional<Integer> partialScore = getPartialScore();
            if (partialScore.isEmpty()) {
                return Optional.empty();
            }
            return partialScore.get().equals(10)
                    ? Optional.of(new Strike())
                    : Optional.of(new FirstRoll());
        }

        @Override
        public void handle(Roll roll) {
            firstRoll = Optional.of(roll);
        }

        @Override
        public Optional<Integer> getScore() {
            return Optional.empty();
        }

        @Override
        public void passNext(Roll roll) {
        }
    }

    private class FirstRoll implements Status {

        @Override
        public Optional<Status> getNextStatus() {
            Optional<Integer> partialScore = getPartialScore();
            if (partialScore.isEmpty()) {
                return Optional.empty();
            }
            return partialScore.get().equals(10)
                    ? Optional.of(new Spare())
                    : Optional.of(new Open());
        }

        @Override
        public void handle(Roll roll) {
            secondRoll = Optional.of(roll);
        }

        @Override
        public Optional<Integer> getScore() {
            return Optional.empty();
        }

        @Override
        public void passNext(Roll roll) {
        }
    }

    private class Open implements Status {
        @Override
        public Optional<Status> getNextStatus() {
            return Optional.empty();
        }

        @Override
        public void handle(Roll roll) {
        }

        @Override
        public Optional<Integer> getScore() {
            return getPartialScore();
        }

        @Override
        public void passNext(Roll roll) {
            nextFrame.ifPresent(frame -> frame.add(roll));
        }
    }

    private class Spare implements Status {
        @Override
        public Optional<Status> getNextStatus() {
            return Optional.of(new SpareWithBonus());
        }

        @Override
        public void handle(Roll roll) {
            thirdRoll = Optional.of(roll);
        }

        @Override
        public Optional<Integer> getScore() {
            return Optional.empty();
        }

        @Override
        public void passNext(Roll roll) {
            nextFrame.ifPresent(frame -> frame.add(roll));
        }
    }

    private class SpareWithBonus implements Status {
        @Override
        public Optional<Status> getNextStatus() {
            return Optional.empty();
        }

        @Override
        public void handle(Roll roll) {

        }

        @Override
        public Optional<Integer> getScore() {
            return getPartialScore();
        }

        @Override
        public void passNext(Roll roll) {
            nextFrame.ifPresent(frame -> frame.add(roll));
        }
    }

    private class Strike implements Status {
        @Override
        public Optional<Status> getNextStatus() {
            return Optional.of(new StrikeWitOneBonus());
        }

        @Override
        public void handle(Roll roll) {
            secondRoll = Optional.of(roll);
        }

        @Override
        public Optional<Integer> getScore() {
            return Optional.empty();
        }

        @Override
        public void passNext(Roll roll) {
            nextFrame.ifPresent(frame -> frame.add(roll));
        }
    }

    private class StrikeWitOneBonus implements Status {
        @Override
        public Optional<Status> getNextStatus() {
            return Optional.of(new StrikeWithTwoBonuses());
        }

        @Override
        public void handle(Roll roll) {
            thirdRoll = Optional.of(roll);
        }

        @Override
        public Optional<Integer> getScore() {
            return Optional.empty();
        }

        @Override
        public void passNext(Roll roll) {
            nextFrame.ifPresent(frame -> frame.add(roll));
        }
    }

    private class StrikeWithTwoBonuses implements Status {
        @Override
        public Optional<Status> getNextStatus() {
            return Optional.empty();
        }

        @Override
        public void handle(Roll roll) {
        }

        @Override
        public Optional<Integer> getScore() {
            return getPartialScore();
        }

        @Override
        public void passNext(Roll roll) {
            nextFrame.ifPresent(frame -> frame.add(roll));
        }
    }
}
