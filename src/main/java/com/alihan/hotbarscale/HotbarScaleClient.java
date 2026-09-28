package com.alihan.hotbarscale;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class HotbarScaleClient implements ClientModInitializer {

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
        return HotbarScaleConfig.getHotbarScale() / 100.0f;
    }

    @Override
    public void onInitializeClient() {

        HotbarScaleConfig.load();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            while (DECREASE_SIZE.consumeClick()) {

                int current =
                        HotbarScaleConfig.getHotbarScale();

                if (current > HotbarScaleConfig.MIN_SCALE) {

                    HotbarScaleConfig.setHotbarScale(
                            Math.max(
                                    HotbarScaleConfig.MIN_SCALE,
                                    current - 5
                            )
                    );

                    HotbarScaleConfig.save();
                    showCurrentSize(client);
                }
            }

            while (INCREASE_SIZE.consumeClick()) {

                int current =
                        HotbarScaleConfig.getHotbarScale();

                if (current < HotbarScaleConfig.MAX_SCALE) {

                    HotbarScaleConfig.setHotbarScale(
                            Math.min(
                                    HotbarScaleConfig.MAX_SCALE,
                                    current + 5
                            )
                    );

                    HotbarScaleConfig.save();
                    showCurrentSize(client);
                }
            }
        });
    }

    private static void showCurrentSize(
            net.minecraft.client.Minecraft client
    ) {
        if (client.player != null) {

            client.player.displayClientMessage(
                    Component.literal(
                            "Hotbar: "
                                    + HotbarScaleConfig.getHotbarScale()
                                    + "%"
                    ),
                    true
            );
        }
    }
}