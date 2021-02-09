package com.github.lunatrius.schematica.mixin.mixins;

import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Old Chum
 * @since 02/08/2021
 */
@Mixin(World.class)
public abstract class MixinWorld {
    @Inject(method = "setBlockState", at = @At("HEAD"), cancellable = true)
    public void setBlockStatePRE(BlockPos pos, IBlockState newState, int flags, CallbackInfoReturnable<Boolean> callback) {
        if (!Minecraft.getMinecraft().isSingleplayer()) {
            if (ConfigurationHandler.noGhostBlocks) {
                if (flags != 3) {
                    callback.cancel();
                }
            }
        }
    }
}
