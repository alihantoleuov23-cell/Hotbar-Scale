package com.alihan.hotbarscale.mixin;

import com.alihan.hotbarscale.HotbarScaleConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.Slot;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {

    @Shadow
    protected int leftPos;

    @Shadow
    protected int topPos;

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

    private double hotbarScale$logicalMouseX(double mouseX) {
        float scale = hotbarScale$getScale();

        if (scale == 1.0f) {
            return mouseX;
        }

        AbstractContainerScreen<?> screen =
                (AbstractContainerScreen<?>) (Object) this;

        double centerX = screen.width / 2.0;

        return centerX + (mouseX - centerX) / scale;
    }

    private double hotbarScale$logicalMouseY(double mouseY) {
        float scale = hotbarScale$getScale();

        if (scale == 1.0f) {
            return mouseY;
        }

        AbstractContainerScreen<?> screen =
                (AbstractContainerScreen<?>) (Object) this;

        double centerY = screen.height / 2.0;

        return centerY + (mouseY - centerY) / scale;
    }

    private void hotbarScale$push(GuiGraphics graphics) {
        float scale = hotbarScale$getScale();

        if (scale == 1.0f) {
            return;
        }

        Matrix3x2fStack matrices = graphics.pose();

        float centerX = graphics.guiWidth() / 2.0f;
        float centerY = graphics.guiHeight() / 2.0f;

        matrices.pushMatrix();

        matrices.translate(centerX, centerY);
        matrices.scale(scale, scale);
        matrices.translate(-centerX, -centerY);
    }

    private void hotbarScale$pop(GuiGraphics graphics) {
        if (hotbarScale$getScale() != 1.0f) {
            graphics.pose().popMatrix();
        }
    }

    /*
     * Фон GUI.
     */
    @Inject(
            method = "renderBackground",
            at = @At("HEAD")
    )
    private void hotbarScale$beginBackground(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float delta,
            CallbackInfo ci
    ) {
        hotbarScale$push(graphics);
    }

    @Inject(
            method = "renderBackground",
            at = @At("TAIL")
    )
    private void hotbarScale$endBackground(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float delta,
            CallbackInfo ci
    ) {
        hotbarScale$pop(graphics);
    }

    /*
     * Слоты, предметы, текст и остальное содержимое.
     */
    @Inject(
            method = "renderContents",
            at = @At("HEAD")
    )
    private void hotbarScale$beginContents(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float delta,
            CallbackInfo ci
    ) {
        hotbarScale$push(graphics);
    }

    @Inject(
            method = "renderContents",
            at = @At("TAIL")
    )
    private void hotbarScale$endContents(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float delta,
            CallbackInfo ci
    ) {
        hotbarScale$pop(graphics);
    }

    /*
     * Предмет, который находится под курсором.
     */
    @Inject(
            method = "renderCarriedItem",
            at = @At("HEAD")
    )
    private void hotbarScale$beginCarriedItem(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            CallbackInfo ci
    ) {
        hotbarScale$push(graphics);
    }

    @Inject(
            method = "renderCarriedItem",
            at = @At("TAIL")
    )
    private void hotbarScale$endCarriedItem(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            CallbackInfo ci
    ) {
        hotbarScale$pop(graphics);
    }

    @ModifyVariable(
            method = "renderCarriedItem",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private int hotbarScale$carriedMouseX(int mouseX) {
        return (int) Math.round(
                hotbarScale$logicalMouseX(mouseX)
        );
    }

    @ModifyVariable(
            method = "renderCarriedItem",
            at = @At("HEAD"),
            ordinal = 1,
            argsOnly = true
    )
    private int hotbarScale$carriedMouseY(int mouseY) {
        return (int) Math.round(
                hotbarScale$logicalMouseY(mouseY)
        );
    }

    /*
     * Анимация возврата предмета.
     */
    @Inject(
            method = "renderSnapbackItem",
            at = @At("HEAD")
    )
    private void hotbarScale$beginSnapback(
            GuiGraphics graphics,
            CallbackInfo ci
    ) {
        hotbarScale$push(graphics);
    }

    @Inject(
            method = "renderSnapbackItem",
            at = @At("TAIL")
    )
    private void hotbarScale$endSnapback(
            GuiGraphics graphics,
            CallbackInfo ci
    ) {
        hotbarScale$pop(graphics);
    }

    /*
     * Определение слота под физическим курсором.
     */
    @Inject(
            method = "getHoveredSlot",
            at = @At("HEAD"),
            cancellable = true
    )
    private void hotbarScale$getHoveredSlot(
            double mouseX,
            double mouseY,
            CallbackInfoReturnable<Slot> cir
    ) {
        double logicalMouseX =
                hotbarScale$logicalMouseX(mouseX);

        double logicalMouseY =
                hotbarScale$logicalMouseY(mouseY);

        AbstractContainerScreen<?> screen =
                (AbstractContainerScreen<?>) (Object) this;

        for (Slot slot : screen.getMenu().slots) {
            if (!slot.isActive()) {
                continue;
            }

            double slotX = this.leftPos + slot.x;
            double slotY = this.topPos + slot.y;

            if (logicalMouseX >= slotX
                    && logicalMouseX < slotX + 16.0
                    && logicalMouseY >= slotY
                    && logicalMouseY < slotY + 16.0) {

                cir.setReturnValue(slot);
                return;
            }
        }

        cir.setReturnValue(null);
    }

    /*
     * Клик за пределами GUI.
     */
    @ModifyVariable(
            method = "hasClickedOutside",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private double hotbarScale$outsideMouseX(double mouseX) {
        return hotbarScale$logicalMouseX(mouseX);
    }

    @ModifyVariable(
            method = "hasClickedOutside",
            at = @At("HEAD"),
            ordinal = 1,
            argsOnly = true
    )
    private double hotbarScale$outsideMouseY(double mouseY) {
        return hotbarScale$logicalMouseY(mouseY);
    }
}