package org.mondogrua;

import java.util.Optional;

public class Game {

    private final Frame[] frames = new Frame[10];

    public Game() {
        Optional<Frame> nextFrame = Optional.empty();
        for (int frameIdx = 9; frameIdx >= 0; frameIdx--) {
            Frame frame = new Frame(frameIdx+1, nextFrame);
            nextFrame = Optional.of(frame);
            frames[frameIdx] = frame;
        }
    }

    public void add(Roll roll) {
        this.frames[0].add(roll);
    }

    public Integer getScore() {
        ScoreAccumulator scoreAccumulator = new ScoreAccumulator();
        frames[0].addScoreTo(scoreAccumulator);
        return scoreAccumulator.value();
    }
}
