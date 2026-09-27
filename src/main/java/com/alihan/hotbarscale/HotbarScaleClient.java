package com.alihan.hotbarscale;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class HotbarScaleClient implements ClientModInitializer {

    private static final float[] SCALES = {
            0.33f,
            0.66f,
            1.00f,
            1.33f,
            1.66f,
            2.00f
    };

    private static int scaleIndex = 2;

    private static final KeyMapping DECREASE_SIZE =
            KeyBindingHelper.registerKeyBinding(
                    new KeyMapping(
                            "key.hotbar-scale.decrease",
                            InputConstants.Type.KEYSYM,
                            GLFW.GLFW_KEY_O,
                            KeyMapping.Category.MISC
                    )
            );

    private static final KeyMapping INCREASE_SIZE =
            KeyBindingHelper.registerKeyBinding(
                    new KeyMapping(
                            "key.hotbar-scale.increase",
                            InputConstants.Type.KEYSYM,
                            GLFW.GLFW_KEY_P,
                            KeyMapping.Category.MISC
                    )
            );

    public static float getScale() {
        return SCALES[scaleIndex];
    }

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            while (DECREASE_SIZE.consumeClick()) {
                if (scaleIndex > 0) {
                    scaleIndex--;
                    showCurrentSize(client);
                }
            }

            while (INCREASE_SIZE.consumeClick()) {
                if (scaleIndex < SCALES.length - 1) {
                    scaleIndex++;
                    showCurrentSize(client);
                }
            }
        });
    }

    private static void showCurrentSize(
            net.minecraft.client.Minecraft client) {

        if (client.player != null) {
            int percent = Math.round(getScale() * 100.0f);

            client.player.displayClientMessage(
                    Component.literal(
                            "Hotbar: " + (scaleIndex + 1)
                                    + "/6 (" + percent + "%)"
                    ),
                    true
            );
        }
    }
}