package com.alihan.hotbarscale.mixin;

import com.alihan.hotbarscale.HotbarScaleConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {

    private float hotbarScale$getScale() {
        AbstractContainerScreen<?> screen =
                (AbstractContainerScreen<?>) (Object) this;

        if (screen instanceof InventoryScreen) {
            return HotbarScaleConfig.getInventoryScale() / 100.0f;
        }

        if (screen instanceof CraftingScreen) {
            return HotbarScaleConfig.getCraftingScale() / 100.0f;
        }

        return HotbarScaleConfig.getContainerScale() / 100.0f;
    }

    @Inject(
            method = "render",
            at = @At("HEAD")
    )
    private void hotbarScale$beginRender(
            GuiGraphics context,
            int mouseX,
            int mouseY,
            float delta,
            CallbackInfo ci
    ) {
        Matrix3x2fStack matrices = context.pose();

        float scale = hotbarScale$getScale();

        if (scale == 1.0f) {
            return;
        }

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
    private void hotbarScale$endRender(
            GuiGraphics context,
            int mouseX,
            int mouseY,
            float delta,
            CallbackInfo ci
    ) {
        float scale = hotbarScale$getScale();

        if (scale == 1.0f) {
            return;
        }

        context.pose().popMatrix();
    }
}