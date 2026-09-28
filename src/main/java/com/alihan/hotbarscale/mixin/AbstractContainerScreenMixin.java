package com.alihan.hotbarscale.mixin;

import com.alihan.hotbarscale.HotbarScaleConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.Slot;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

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

    private double hotbarScale$logicalMouseX(double mouseX) {
        AbstractContainerScreen<?> screen =
                (AbstractContainerScreen<?>) (Object) this;

        float scale = hotbarScale$getScale();

        if (scale == 1.0f) {
            return mouseX;
        }

        double centerX = screen.width / 2.0;
        return centerX + (mouseX - centerX) / scale;
    }

    private double hotbarScale$logicalMouseY(double mouseY) {
        AbstractContainerScreen<?> screen =
                (AbstractContainerScreen<?>) (Object) this;

        float scale = hotbarScale$getScale();

        if (scale == 1.0f) {
            return mouseY;
        }

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
     * Масштабируем фон GUI:
     * текстура контейнера, модель игрока и остальные элементы,
     * которые рисуются через renderBackground/renderBg.
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
     * Масштабируем слоты, предметы, подписи и остальные элементы
     * контейнерного GUI.
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
     * Minecraft проверяет слот по логическим координатам.
     * После масштабирования физический курсор нужно преобразовать
     * обратно в исходную систему координат GUI.
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
        AbstractContainerScreen<?> screen =
                (AbstractContainerScreen<?>) (Object) this;

        double logicalMouseX = hotbarScale$logicalMouseX(mouseX);
        double logicalMouseY = hotbarScale$logicalMouseY(mouseY);

        for (Slot slot : screen.getMenu().slots) {
            if (!slot.isActive()) {
                continue;
            }

            double slotX = screen.getGuiLeft() + slot.x;
            double slotY = screen.getGuiTop() + slot.y;

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
     * Проверка "кликнул ли игрок за пределами GUI".
     * Нужна для корректной работы кликов после масштабирования.
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

    /*
     * Скролл тоже должен учитывать масштаб GUI.
     */
    @ModifyVariable(
            method = "mouseScrolled",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private double hotbarScale$scrollMouseX(double mouseX) {
        return hotbarScale$logicalMouseX(mouseX);
    }

    @ModifyVariable(
            method = "mouseScrolled",
            at = @At("HEAD"),
            ordinal = 1,
            argsOnly = true
    )
    private double hotbarScale$scrollMouseY(double mouseY) {
        return hotbarScale$logicalMouseY(mouseY);
    }

    /*
     * Предмет, который держим курсором, тоже должен находиться
     * точно под физическим курсором.
     */
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
}