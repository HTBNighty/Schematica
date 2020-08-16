package com.github.lunatrius.schematica.mixin.mixins;

import com.github.lunatrius.schematica.handler.client.TickHandler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class MixinBlock {
    @Inject(method = "removedByPlayer", at = @At("HEAD"), cancellable = true, remap = false)
    public void removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest, CallbackInfoReturnable<Boolean> cb) {
        if (player == Minecraft.getMinecraft().player) {
            TickHandler.INSTANCE.onBreakBlock(); // TODO: Get an actual event system working
        }
    }
}
