package com.github.lunatrius.schematica.mixin.mixins;

import com.github.lunatrius.schematica.client.printer.PlayerLookTracker;
import com.github.lunatrius.schematica.client.printer.SlotManager;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Old Chum
 * @since 02/18/2021
 */
@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager {
    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void channelRead0PRE(ChannelHandlerContext p_channelRead0_1_, Packet<?> p_channelRead0_2_, CallbackInfo callback) {
        SlotManager.INSTANCE.onReceivePacket(p_channelRead0_2_);
    }

    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    private void sendPacket(Packet<?> packetIn, CallbackInfo callback) {
        PlayerLookTracker.INSTANCE.onSendPacket(packetIn);
    }
}
