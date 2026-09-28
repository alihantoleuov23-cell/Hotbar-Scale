package com.alihan.hotbarscale.mixin;

import com.alihan.hotbarscale.HotbarScaleConfig;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {

    private double hotbarScale$logicalX(double mouseX) {
        float scale =
                HotbarScaleConfig.getInventoryScale() / 100.0f;

        if (scale == 1.0f) {
            return mouseX;
        }

        RecipeBookComponent<?> component =
                (RecipeBookComponent<?>) (Object) this;

        /*
         * Внутренние координаты книги рецептов
         * возвращаем из физического масштаба обратно
         * в обычную систему Minecraft.
         */
        double centerX =
                component.getScreenWidth() / 2.0;

        return centerX + (mouseX - centerX) / scale;
    }

    private double hotbarScale$logicalY(double mouseY) {
        float scale =
                HotbarScaleConfig.getInventoryScale() / 100.0f;

        if (scale == 1.0f) {
            return mouseY;
        }

        RecipeBookComponent<?> component =
                (RecipeBookComponent<?>) (Object) this;

        double centerY =
                component.getScreenHeight() / 2.0;

        return centerY + (mouseY - centerY) / scale;
    }

    @ModifyVariable(
            method = "mouseClicked",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private double hotbarScale$clickX(double mouseX) {
        return hotbarScale$logicalX(mouseX);
    }

    @ModifyVariable(
            method = "mouseClicked",
            at = @At("HEAD"),
            ordinal = 1,
            argsOnly = true
    )
    private double hotbarScale$clickY(double mouseY) {
        return hotbarScale$logicalY(mouseY);
    }

    @ModifyVariable(
            method = "hasClickedOutside",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private double hotbarScale$outsideX(double mouseX) {
        return hotbarScale$logicalX(mouseX);
    }

    @ModifyVariable(
            method = "hasClickedOutside",
            at = @At("HEAD"),
            ordinal = 1,
            argsOnly = true
    )
    private double hotbarScale$outsideY(double mouseY) {
        return hotbarScale$logicalY(mouseY);
    }
}