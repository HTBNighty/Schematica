package com.github.lunatrius.schematica.mixin.mixins;

import com.github.lunatrius.schematica.client.inventorycalculator.InventoryCalculator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO: Events :-(
@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {
    @Shadow
    Minecraft mc;

    @Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(F)V"))
    public void updateCameraAndRender (float partialTicks, long nanoTime, CallbackInfo callback) {
        InventoryCalculator.onRender2d();
    }
}
