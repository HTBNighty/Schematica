package com.github.lunatrius.schematica.mixin.mixins;

import com.github.lunatrius.schematica.handler.client.TickHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP {
    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    public void onUpdateWalkingPlayerPRE (CallbackInfo callback) {
        TickHandler.INSTANCE.onUpdateWalkingPlayer(); // TODO: Get an actual event system working
    }
}
