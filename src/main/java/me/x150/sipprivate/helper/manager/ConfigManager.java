package me.x150.sipprivate.helper.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.config.SettingBase;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleRegistry;
import me.x150.sipprivate.helper.event.EventType;
import me.x150.sipprivate.helper.event.Events;
import me.x150.sipprivate.helper.event.events.base.NonCancellableEvent;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

public class ConfigManager {

    static final List<Module> toBeEnabled = new ArrayList<>();
    static final File CONFIG_FILE;
    public static boolean loaded = false;
    public static boolean enabled = false;

    static {
        CONFIG_FILE = new File(CoffeeClientMain.BASE, "config.sip");
    }

    /**
     * Compresses a byte array using GZIP Deflate
     *
     * @param in The input
     * @return The compressed output
     * @throws Exception If something goes wrong
     */
    public static byte[] compress(byte[] in) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (DeflaterOutputStream dos = new DeflaterOutputStream(os)) {
            dos.write(in);
        }
        return os.toByteArray();
    }

    /**
     * Decompressed a byte array using GZIP Inflate
     *
     * @param in The compressed data
     * @return The decompressed date
     * @throws Exception If something goes wrong
     */
    public static byte[] decompress(byte[] in) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (InflaterOutputStream ios = new InflaterOutputStream(os)) {
            ios.write(in);
        }

        return os.toByteArray();
    }

    /**
     * Saves the current state of the client to the file
     */
    public static void saveState() {
        if (!loaded || !enabled) {
            System.out.println("Not saving config because we didn't load it yet");
            return;
        }
        System.out.println("Saving state");
        JsonObject base = new JsonObject();
        JsonArray enabled = new JsonArray();
        JsonArray config = new JsonArray();
        for (Module module : ModuleRegistry.getModules()) {
            if (module.isEnabled()) {
                enabled.add(module.getName());
            }
            JsonObject currentConfig = new JsonObject();
            currentConfig.addProperty("name", module.getName());
            JsonArray pairs = new JsonArray();
            for (SettingBase<?> dynamicValue : module.config.getSettings()) {
                JsonObject jesus = new JsonObject();
                jesus.addProperty("key", dynamicValue.getName());
                jesus.addProperty("value", dynamicValue.getValue() + "");
                pairs.add(jesus);
            }
            currentConfig.add("pairs", pairs);
            config.add(currentConfig);
        }
        base.add("enabled", enabled);
        base.add("config", config);
        try {
            FileUtils.writeByteArrayToFile(CONFIG_FILE, compress(base.toString().getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to save config!");
        }
        Events.fireEvent(EventType.CONFIG_SAVE, new NonCancellableEvent());
    }

    /**
     * Loads the state we saved earlier from the file
     */
    public static void loadState() {
        if (loaded) {
            return;
        }
        loaded = true;
        try {
            if (!CONFIG_FILE.isFile()) {
                //noinspection ResultOfMethodCallIgnored
                CONFIG_FILE.delete();
            }
            if (!CONFIG_FILE.exists()) {
                return;
            }
            byte[] retrieved = FileUtils.readFileToByteArray(CONFIG_FILE);
            String decompressed = new String(decompress(retrieved));
            JsonObject config = JsonParser.parseString(decompressed).getAsJsonObject();
            if (config.has("config") && config.get("config").isJsonArray()) {
                JsonArray configArray = config.get("config").getAsJsonArray();
                for (JsonElement jsonElement : configArray) {
                    if (jsonElement.isJsonObject()) {
                        JsonObject jsonObj = jsonElement.getAsJsonObject();
                        String name = jsonObj.get("name").getAsString();
                        Module j = ModuleRegistry.getByName(name);
                        if (j == null) {
                            continue;
                        }
                        if (jsonObj.has("pairs") && jsonObj.get("pairs").isJsonArray()) {
                            JsonArray pairs = jsonObj.get("pairs").getAsJsonArray();
                            for (JsonElement pair : pairs) {
                                JsonObject jo = pair.getAsJsonObject();
                                String key = jo.get("key").getAsString();
                                String value = jo.get("value").getAsString();
                                SettingBase<?> val = j.config.get(key);
                                if (val != null) {
                                    val.accept(value);
                                }
                            }
                        }
                    }
                }
            }

            if (config.has("enabled") && config.get("enabled").isJsonArray()) {
                for (JsonElement enabled : config.get("enabled").getAsJsonArray()) {
                    String name = enabled.getAsString();
                    Module m = ModuleRegistry.getByName(name);
                    if (m != null) {
                        toBeEnabled.add(m);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Enables all modules to be enabled, when we are in game
     */
    public static void enableModules() {
        if (enabled) {
            return;
        }
        enabled = true;
        for (Module module : toBeEnabled) {
            module.setEnabled(true);
        }
    }

}
