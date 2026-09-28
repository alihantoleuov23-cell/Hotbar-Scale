package com.alihan.hotbarscale;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class HotbarScaleModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {

        return parent -> {

            HotbarScaleConfig.load();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(
                            Component.literal("Hotbar Scale")
                    );

            ConfigEntryBuilder entries =
                    builder.entryBuilder();

            ConfigCategory category =
                    builder.getOrCreateCategory(
                            Component.literal("Размеры GUI")
                    );

            var hotbar = entries.startIntSlider(
                    Component.literal("Размер хотбара"),
                    HotbarScaleConfig.getHotbarScale(),
                    HotbarScaleConfig.MIN_SCALE,
                    HotbarScaleConfig.MAX_SCALE
            )
            .setDefaultValue(100)
            .setTooltip(
                    Component.literal(
                            "Размер хотбара: 33% - 200%"
                    )
            )
            .build();

            var inventory = entries.startIntSlider(
                    Component.literal("Размер инвентаря"),
                    HotbarScaleConfig.getInventoryScale(),
                    HotbarScaleConfig.MIN_SCALE,
                    HotbarScaleConfig.MAX_SCALE
            )
            .setDefaultValue(100)
            .setTooltip(
                    Component.literal(
                            "Размер интерфейса инвентаря"
                    )
            )
            .build();

            var container = entries.startIntSlider(
                    Component.literal("Размер контейнеров"),
                    HotbarScaleConfig.getContainerScale(),
                    HotbarScaleConfig.MIN_SCALE,
                    HotbarScaleConfig.MAX_SCALE
            )
            .setDefaultValue(100)
            .setTooltip(
                    Component.literal(
                            "Размер сундуков и других контейнеров"
                    )
            )
            .build();

            var crafting = entries.startIntSlider(
                    Component.literal("Размер крафта"),
                    HotbarScaleConfig.getCraftingScale(),
                    HotbarScaleConfig.MIN_SCALE,
                    HotbarScaleConfig.MAX_SCALE
            )
            .setDefaultValue(100)
            .setTooltip(
                    Component.literal(
                            "Размер интерфейса крафта"
                    )
            )
            .build();

            category.addEntry(hotbar);
            category.addEntry(inventory);
            category.addEntry(container);
            category.addEntry(crafting);

            builder.setSavingRunnable(() -> {

                HotbarScaleConfig.setHotbarScale(
                        hotbar.getValue()
                );

                HotbarScaleConfig.setInventoryScale(
                        inventory.getValue()
                );

                HotbarScaleConfig.setContainerScale(
                        container.getValue()
                );

                HotbarScaleConfig.setCraftingScale(
                        crafting.getValue()
                );

                HotbarScaleConfig.save();
            });

            return builder.build();
        };
    }
}