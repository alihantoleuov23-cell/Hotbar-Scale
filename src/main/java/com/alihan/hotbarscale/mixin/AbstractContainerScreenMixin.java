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

    @Inject(
            method = "renderContents",
            at = @At("HEAD")
    )
    private void hotbarScale$begin(
            GuiGraphics context,
            int mouseX,
            int mouseY,
            float delta,
            CallbackInfo ci
    ) {
        AbstractContainerScreen<?> screen =
                (AbstractContainerScreen<?>) (Object) this;

        float scale;

        if (screen instanceof InventoryScreen) {
            scale =
                    HotbarScaleConfig.getInventoryScale()
                            / 100.0f;

        } else if (screen instanceof CraftingScreen) {
            scale =
                    HotbarScaleConfig.getCraftingScale()
                            / 100.0f;

        } else {
            scale =
                    HotbarScaleConfig.getContainerScale()
                            / 100.0f;
        }

        Matrix3x2fStack matrices = context.pose();

        float centerX = context.guiWidth() / 2.0f;
        float centerY = context.guiHeight() / 2.0f;

        matrices.pushMatrix();

        matrices.translate(centerX, centerY);
        matrices.scale(scale, scale);
        matrices.translate(-centerX, -centerY);
    }

    @Inject(
            method = "renderContents",
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