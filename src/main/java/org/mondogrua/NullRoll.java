package org.mondogrua;

import java.util.Optional;

public class NullRoll implements IRoll {
    @Override
    public Optional<Integer> getPins() {
        return Optional.empty();
    }

    @Override
    public String getReport() {
        return "";
    }
}
