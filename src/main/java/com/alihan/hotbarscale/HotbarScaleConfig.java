package com.alihan.hotbarscale;

import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public final class HotbarScaleConfig {

    public static final int MIN_SCALE = 33;
    public static final int MAX_SCALE = 200;

    private static final int DEFAULT_SCALE = 100;

    private static int hotbarScale = DEFAULT_SCALE;
    private static int inventoryScale = DEFAULT_SCALE;
    private static int containerScale = DEFAULT_SCALE;
    private static int craftingScale = DEFAULT_SCALE;

    private HotbarScaleConfig() {
    }

    private static File getConfigFile() {
        File configDirectory = new File(
                Minecraft.getInstance().gameDirectory,
                "config"
        );

        if (!configDirectory.exists()) {
            configDirectory.mkdirs();
        }

        return new File(
                configDirectory,
                "hotbar-scale.properties"
        );
    }

    public static void load() {
        File file = getConfigFile();

        if (!file.exists()) {
            save();
            return;
        }

        Properties properties = new Properties();

        try (FileInputStream input =
                     new FileInputStream(file)) {

            properties.load(input);

            hotbarScale = clamp(parseInt(
                    properties.getProperty("hotbar_scale"),
                    DEFAULT_SCALE
            ));

            inventoryScale = clamp(parseInt(
                    properties.getProperty("inventory_scale"),
                    DEFAULT_SCALE
            ));

            containerScale = clamp(parseInt(
                    properties.getProperty("container_scale"),
                    DEFAULT_SCALE
            ));

            craftingScale = clamp(parseInt(
                    properties.getProperty("crafting_scale"),
                    DEFAULT_SCALE
            ));

        } catch (IOException ignored) {
            resetToDefaults();
        }
    }

    public static void save() {
        File file = getConfigFile();

        Properties properties = new Properties();

        properties.setProperty(
                "hotbar_scale",
                String.valueOf(hotbarScale)
        );

        properties.setProperty(
                "inventory_scale",
                String.valueOf(inventoryScale)
        );

        properties.setProperty(
                "container_scale",
                String.valueOf(containerScale)
        );

        properties.setProperty(
                "crafting_scale",
                String.valueOf(craftingScale)
        );

        try (FileOutputStream output =
                     new FileOutputStream(file)) {

            properties.store(
                    output,
                    "Hotbar Scale configuration"
            );

        } catch (IOException ignored) {
        }
    }

    private static int parseInt(
            String value,
            int fallback
    ) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private static int clamp(int value) {
        return Math.max(
                MIN_SCALE,
                Math.min(MAX_SCALE, value)
        );
    }

    public static int getHotbarScale() {
        return hotbarScale;
    }

    public static void setHotbarScale(int value) {
        hotbarScale = clamp(value);
    }

    public static int getInventoryScale() {
        return inventoryScale;
    }

    public static void setInventoryScale(int value) {
        inventoryScale = clamp(value);
    }

    public static int getContainerScale() {
        return containerScale;
    }

    public static void setContainerScale(int value) {
        containerScale = clamp(value);
    }

    public static int getCraftingScale() {
        return craftingScale;
    }

    public static void setCraftingScale(int value) {
        craftingScale = clamp(value);
    }

    public static void resetToDefaults() {
        hotbarScale = DEFAULT_SCALE;
        inventoryScale = DEFAULT_SCALE;
        containerScale = DEFAULT_SCALE;
        craftingScale = DEFAULT_SCALE;
    }
}