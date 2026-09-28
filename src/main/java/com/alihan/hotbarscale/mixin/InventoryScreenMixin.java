package com.alihan.hotbarscale.mixin;

import com.alihan.hotbarscale.HotbarScaleConfig;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {

    /*
     * При масштабировании GUI модель игрока тоже попадала
     * под общий scale и становилась слишком большой.
     *
     * Поэтому уменьшаем её исходный размер во столько же раз,
     * во сколько увеличен GUI.
     *
     * Например:
     * GUI = 150%
     * размер модели = 100 / 150 = 66.7%
     *
     * После общего GUI scale:
     * 66.7% × 150% ≈ 100%
     */
    @ModifyVariable(
            method = "renderEntityInInventoryFollowsMouse",
            at = @At("HEAD"),
            ordinal = 4,
            argsOnly = true
    )
    private static int hotbarScale$playerPreviewSize(int size) {
        float scale =
                HotbarScaleConfig.getInventoryScale() / 100.0f;

        if (scale <= 0.0f) {
            return size;
        }

        return Math.max(
                1,
                Math.round(size / scale)
        );
    }
}