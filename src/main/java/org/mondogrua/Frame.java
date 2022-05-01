package org.mondogrua;

import java.util.Optional;

public class Frame {
    private final int index;
    private final Optional<Frame> nextFrame;
    private Roll firstRoll;
    private Roll secondRoll;
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

    interface Status {
        public Optional<Status> getNextStatus();

        void handle(Roll roll);

        Optional<Integer> getScore();

        void passNext(Roll roll);

    }

    private class Empty implements Status {
        public Optional<Status> getNextStatus() {
            return Optional.of(new FirstRoll());
        }

        @Override
        public void handle(Roll roll) {
            firstRoll = roll;
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
            return Optional.of(new Open());
        }

        @Override
        public void handle(Roll roll) {
            secondRoll = roll;
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
            return Optional.of(firstRoll.getPins() + secondRoll.getPins());
        }

        @Override
        public void passNext(Roll roll) {
            nextFrame.ifPresent(frame -> frame.add(roll));
        }
    }
}
