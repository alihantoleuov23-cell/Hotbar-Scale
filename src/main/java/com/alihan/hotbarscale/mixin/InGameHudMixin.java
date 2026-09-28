package com.alihan.hotbarscale.mixin;

import com.alihan.hotbarscale.HotbarScaleConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class InGameHudMixin {

    private float hotbarScale$getScale() {
        return HotbarScaleConfig.getHotbarScale() / 100.0f;
    }

    @Inject(
            method = "renderHotbarAndDecorations",
            at = @At("HEAD")
    )
    private void hotbarScale$beginHudScale(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo ci
    ) {
        float scale = hotbarScale$getScale();

        if (scale == 1.0f) {
            return;
        }

        Matrix3x2fStack matrices = graphics.pose();

        /*
         * Точка масштабирования находится по центру хотбара.
         *
         * Благодаря этому:
         * - сам хотбар остаётся на привычном месте;
         * - сердца масштабируются вместе с ним;
         * - голод масштабируется вместе с ним;
         * - XP масштабируется вместе с ним.
         */
        float centerX = graphics.guiWidth() / 2.0f;
        float centerY = graphics.guiHeight() - 11.0f;

        matrices.pushMatrix();

        matrices.translate(centerX, centerY);
        matrices.scale(scale, scale);
        matrices.translate(-centerX, -centerY);
    }

    @Inject(
            method = "renderHotbarAndDecorations",
            at = @At("TAIL")
    )
    private void hotbarScale$endHudScale(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo ci
    ) {
        if (hotbarScale$getScale() != 1.0f) {
            graphics.pose().popMatrix();
        }
    }
}