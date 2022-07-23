package org.mondogrua;

import java.util.Optional;

public class NullRoll implements IRoll {

    @Override
    public void addPinsTo(ScoreAccumulator accumulator) {

    }

    @Override
    public String getReport() {
        return "";
    }
}
