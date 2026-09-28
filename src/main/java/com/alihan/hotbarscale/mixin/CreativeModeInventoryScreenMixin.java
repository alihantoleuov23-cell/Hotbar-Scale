package com.alihan.hotbarscale.mixin;

import com.alihan.hotbarscale.HotbarScaleConfig;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CreativeModeInventoryScreen.class)
public class CreativeModeInventoryScreenMixin {

    private double hotbarScale$logicalX(double mouseX) {
        CreativeModeInventoryScreen screen =
                (CreativeModeInventoryScreen) (Object) this;

        float scale =
                HotbarScaleConfig.getContainerScale() / 100.0f;

        if (scale == 1.0f) {
            return mouseX;
        }

        double centerX = screen.width / 2.0;

        return centerX + (mouseX - centerX) / scale;
    }

    private double hotbarScale$logicalY(double mouseY) {
        CreativeModeInventoryScreen screen =
                (CreativeModeInventoryScreen) (Object) this;

        float scale =
                HotbarScaleConfig.getContainerScale() / 100.0f;

        if (scale == 1.0f) {
            return mouseY;
        }

        double centerY = screen.height / 2.0;

        return centerY + (mouseY - centerY) / scale;
    }

    /*
     * Колесо мыши.
     */
    @ModifyVariable(
            method = "mouseScrolled",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private double hotbarScale$scrollMouseX(double mouseX) {
        return hotbarScale$logicalX(mouseX);
    }

    @ModifyVariable(
            method = "mouseScrolled",
            at = @At("HEAD"),
            ordinal = 1,
            argsOnly = true
    )
    private double hotbarScale$scrollMouseY(double mouseY) {
        return hotbarScale$logicalY(mouseY);
    }

    /*
     * Проверка попадания в полосу прокрутки.
     *
     * Это позволяет и колесу, и перетаскиванию
     * работать с увеличенным GUI.
     */
    @ModifyVariable(
            method = "insideScrollbar",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private double hotbarScale$scrollbarMouseX(double mouseX) {
        return hotbarScale$logicalX(mouseX);
    }

    @ModifyVariable(
            method = "insideScrollbar",
            at = @At("HEAD"),
            ordinal = 1,
            argsOnly = true
    )
    private double hotbarScale$scrollbarMouseY(double mouseY) {
        return hotbarScale$logicalY(mouseY);
    }

    /*
     * Координаты при перетаскивании полосы.
     */
    @ModifyVariable(
            method = "mouseDragged",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private double hotbarScale$dragMouseX(double mouseX) {
        return hotbarScale$logicalX(mouseX);
    }

    @ModifyVariable(
            method = "mouseDragged",
            at = @At("HEAD"),
            ordinal = 1,
            argsOnly = true
    )
    private double hotbarScale$dragMouseY(double mouseY) {
        return hotbarScale$logicalY(mouseY);
    }
}