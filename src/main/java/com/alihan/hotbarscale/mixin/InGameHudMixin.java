package com.alihan.hotbarscale.mixin;

import com.alihan.hotbarscale.HotbarScaleClient;
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

    @Inject(
            method = "renderItemHotbar",
            at = @At("HEAD")
    )
    private void hotbarScale$begin(
            GuiGraphics context,
            DeltaTracker deltaTracker,
            CallbackInfo ci
    ) {
        Matrix3x2fStack matrices = context.pose();

        float scale = HotbarScaleClient.getScale();

        float centerX = context.guiWidth() / 2.0f;
        float bottomY = context.guiHeight();

        matrices.pushMatrix();

        matrices.translate(centerX, bottomY);
        matrices.scale(scale, scale);
        matrices.translate(-centerX, -bottomY);
    }

    @Inject(
            method = "renderItemHotbar",
            at = @At("TAIL")
    )
    private void hotbarScale$end(
            GuiGraphics context,
            DeltaTracker deltaTracker,
            CallbackInfo ci
    ) {
        context.pose().popMatrix();
    }
}