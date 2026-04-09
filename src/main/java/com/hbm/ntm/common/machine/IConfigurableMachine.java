package com.hbm.ntm.common.machine;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

public interface IConfigurableMachine {
    String getConfigName();

    void readIfPresent(JsonObject object);

    void writeConfig(JsonWriter writer) throws IOException;

    static boolean grab(final JsonObject object, final String name, final boolean defaultValue) {
        return object.has(name) ? object.get(name).getAsBoolean() : defaultValue;
    }

    static int grab(final JsonObject object, final String name, final int defaultValue) {
        return object.has(name) ? object.get(name).getAsInt() : defaultValue;
    }

    static long grab(final JsonObject object, final String name, final long defaultValue) {
        return object.has(name) ? object.get(name).getAsLong() : defaultValue;
    }

    static double grab(final JsonObject object, final String name, final double defaultValue) {
        return object.has(name) ? object.get(name).getAsDouble() : defaultValue;
    }
}
