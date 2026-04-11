package com.hbm.ntm.common.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.ntm.HbmNtmMod;
import com.hbm.ntm.common.machine.ConfigurableMachineRegistry;
import com.hbm.ntm.common.machine.IConfigurableMachine;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.minecraftforge.fml.loading.FMLPaths;

public final class MachineDynConfig {
    private static final Gson GSON = new Gson();
    private static final String[] INFO_LINES = new String[]{
        "Unlike other JSON configs, this one does not use a variable amount of options (like recipes), rather all config options are fixed.",
        "This means that there is no distinction between template and used config, you can simply edit this file and it will use the new values.",
        "If you wish to reset one or multiple values to default, simply delete them, the file is re-created every time the game starts (but changed values persist!).",
        "How this works in detail:",
        "- Machines have default values on init",
        "- The config system will try to read the config file. It will replace the default values where applicable, and keep them when an option is missing.",
        "- The config system will then use the full set of values - configured or default if missing - and re-create the config file to include any missing entries.",
        "This final step also means that any custom non-config values added to the JSON, while not causing errors, will be deleted when the config is re-created.",
        "It also means that should an update add more values to an existing machines, those will be retroactively added to the config using the default value."
    };

    private MachineDynConfig() {
    }

    public static void initialize() {
        MachineConfigBootstrap.ensureRegistered();
        final Path baseDir = FMLPaths.CONFIGDIR.get().resolve("hbmConfig");
        final Path file = baseDir.resolve("hbmMachines.json");

        try {
            Files.createDirectories(baseDir);
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to create machine config directory " + baseDir, exception);
        }

        final List<IConfigurableMachine> machines = ConfigurableMachineRegistry.createInstances();

        try {
            if (Files.exists(file)) {
                final JsonObject json = GSON.fromJson(new FileReader(file.toFile()), JsonObject.class);
                if (json != null) {
                    for (final IConfigurableMachine machine : machines) {
                        try {
                            final JsonElement element = json.get(machine.getConfigName());
                            final JsonObject object = element != null && element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
                            machine.readIfPresent(object);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }

            try (JsonWriter writer = new JsonWriter(new FileWriter(file.toFile()))) {
                writer.setIndent("  ");
                writer.beginObject();

                writer.name("info").beginArray();
                for (final String line : INFO_LINES) {
                    writer.value(line);
                }
                writer.endArray();

                for (final IConfigurableMachine machine : machines) {
                    try {
                        writer.name(machine.getConfigName()).beginObject();
                        machine.writeConfig(writer);
                        writer.endObject();
                    } catch (Exception ignored) {
                    }
                }

                writer.endObject();
            }
        } catch (Exception exception) {
            HbmNtmMod.LOGGER.warn("Failed to initialize machine dynamic config", exception);
        }
    }
}

