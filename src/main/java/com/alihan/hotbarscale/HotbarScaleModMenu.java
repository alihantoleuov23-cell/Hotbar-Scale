package com.alihan.hotbarscale;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class HotbarScaleModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new SimpleConfigScreen(parent);
    }

    private static class SimpleConfigScreen extends Screen {

        private final Screen parent;

        protected SimpleConfigScreen(Screen parent) {
            super(Component.literal("Hotbar Scale"));
            this.parent = parent;
        }

        @Override
        public void onClose() {
            if (this.minecraft != null) {
                this.minecraft.setScreen(parent);
            }
        }
    }
}