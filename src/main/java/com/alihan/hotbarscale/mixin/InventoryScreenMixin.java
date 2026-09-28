package com.alihan.hotbarscale.mixin;

import com.alihan.hotbarscale.HotbarScaleConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {

    @Inject(
            method = "render",
            at = @At("HEAD")
    )
    private void hotbarScale$begin(
            GuiGraphics context,
            int mouseX,
            int mouseY,
            float delta,
            CallbackInfo ci
    ) {
        Matrix3x2fStack matrices = context.pose();

        float scale =
                HotbarScaleConfig.getInventoryScale() / 100.0f;

        float centerX = context.guiWidth() / 2.0f;
        float centerY = context.guiHeight() / 2.0f;

        matrices.pushMatrix();

        matrices.translate(centerX, centerY);
        matrices.scale(scale, scale);
        matrices.translate(-centerX, -centerY);
    }

    @Inject(
            method = "render",
            at = @At("TAIL")
    )
    private void hotbarScale$end(
            GuiGraphics context,
            int mouseX,
            int mouseY,
            float delta,
            CallbackInfo ci
    ) {
        context.pose().popMatrix();
    }
}