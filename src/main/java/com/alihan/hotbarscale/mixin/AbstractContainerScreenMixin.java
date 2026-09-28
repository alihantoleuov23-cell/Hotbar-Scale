package com.alihan.hotbarscale.mixin;

import com.alihan.hotbarscale.HotbarScaleConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
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

    private double hotbarScale$transformX(double x) {
        float scale = hotbarScale$getScale();

        if (scale == 1.0f) {
            return x;
        }

        AbstractContainerScreen<?> screen =
                (AbstractContainerScreen<?>) (Object) this;

        double centerX = screen.width / 2.0;
        return centerX + (x - centerX) / scale;
    }

    private double hotbarScale$transformY(double y) {
        float scale = hotbarScale$getScale();

        if (scale == 1.0f) {
            return y;
        }

        AbstractContainerScreen<?> screen =
                (AbstractContainerScreen<?>) (Object) this;

        double centerY = screen.height / 2.0;
        return centerY + (y - centerY) / scale;
    }

    @Inject(
            method = "renderBg",
            at = @At("HEAD")
    )
    private void hotbarScale$beginBackground(
            GuiGraphics context,
            float delta,
            int mouseX,
            int mouseY,
            CallbackInfo ci
    ) {
        Matrix3x2fStack matrices = context.pose();

        float scale = hotbarScale$getScale();

        float centerX = context.guiWidth() / 2.0f;
        float centerY = context.guiHeight() / 2.0f;

        matrices.pushMatrix();
        matrices.translate(centerX, centerY);
        matrices.scale(scale, scale);
        matrices.translate(-centerX, -centerY);
    }

    @Inject(
            method = "renderBg",
            at = @At("TAIL")
    )
    private void hotbarScale$endBackground(
            GuiGraphics context,
            float delta,
            int mouseX,
            int mouseY,
            CallbackInfo ci
    ) {
        context.pose().popMatrix();
    }

    @Inject(
            method = "renderContents",
            at = @At("HEAD")
    )
    private void hotbarScale$beginContents(
            GuiGraphics context,
            int mouseX,
            int mouseY,
            float delta,
            CallbackInfo ci
    ) {
        Matrix3x2fStack matrices = context.pose();

        float scale = hotbarScale$getScale();

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
    private void hotbarScale$endContents(
            GuiGraphics context,
            int mouseX,
            int mouseY,
            float delta,
            CallbackInfo ci
    ) {
        context.pose().popMatrix();
    }

    @ModifyVariable(
            method = "mouseClicked",
            at = @At("HEAD"),
            argsOnly = true
    )
    private MouseButtonEvent hotbarScale$mouseClicked(
            MouseButtonEvent event
    ) {
        return new MouseButtonEvent(
                hotbarScale$transformX(event.x()),
                hotbarScale$transformY(event.y()),
                event.buttonInfo()
        );
    }

    @ModifyVariable(
            method = "mouseReleased",
            at = @At("HEAD"),
            argsOnly = true
    )
    private MouseButtonEvent hotbarScale$mouseReleased(
            MouseButtonEvent event
    ) {
        return new MouseButtonEvent(
                hotbarScale$transformX(event.x()),
                hotbarScale$transformY(event.y()),
                event.buttonInfo()
        );
    }

    @ModifyVariable(
            method = "mouseDragged",
            at = @At("HEAD"),
            argsOnly = true
    )
    private MouseButtonEvent hotbarScale$mouseDragged(
            MouseButtonEvent event
    ) {
        return new MouseButtonEvent(
                hotbarScale$transformX(event.x()),
                hotbarScale$transformY(event.y()),
                event.buttonInfo()
        );
    }

    @ModifyVariable(
            method = "mouseDragged",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private double hotbarScale$dragOffsetX(double value) {
        return value / hotbarScale$getScale();
    }

    @ModifyVariable(
            method = "mouseDragged",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 1
    )
    private double hotbarScale$dragOffsetY(double value) {
        return value / hotbarScale$getScale();
    }

    @ModifyVariable(
            method = "mouseScrolled",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private double hotbarScale$scrollX(double value) {
        return hotbarScale$transformX(value);
    }

    @ModifyVariable(
            method = "mouseScrolled",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 1
    )
    private double hotbarScale$scrollY(double value) {
        return hotbarScale$transformY(value);
    }
}