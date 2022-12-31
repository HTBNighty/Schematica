package com.github.lunatrius.schematica.client.printer;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

/**
 * Track's the last yaw and pitch that has been sent to the server. Used to eliminate desync that causes incorrectly
 * rotated blocks while printing.
 *
 * @author Old Chum
 * @since 12/31/2022
 */
public class PlayerLookTracker {
    public static PlayerLookTracker INSTANCE = new PlayerLookTracker();

    public float rotationYaw = 0;
    public float rotationPitch = 0;

    public void onSendPacket (Packet<?> packet) {
        if (packet instanceof CPacketPlayer.Rotation || packet instanceof CPacketPlayer.PositionRotation) {
            CPacketPlayer cPacketPlayer = (CPacketPlayer) packet;

            this.rotationYaw = cPacketPlayer.getYaw(0);
            this.rotationPitch = cPacketPlayer.getPitch(0);
        }
    }

    public EnumFacing getHorizontalFacing() {
        return EnumFacing.byHorizontalIndex(MathHelper.floor((double)(this.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3);
    }

    public EnumFacing getDirectionFromEntityLiving(BlockPos pos, EntityLivingBase placer) {
        if (Math.abs(placer.posX - (double)((float)pos.getX() + 0.5F)) < 2.0D && Math.abs(placer.posZ - (double)((float)pos.getZ() + 0.5F)) < 2.0D) {
            double d0 = placer.posY + (double)placer.getEyeHeight();

            if (d0 - (double)pos.getY() > 2.0D)
            {
                return EnumFacing.UP;
            }

            if ((double)pos.getY() - d0 > 0.0D)
            {
                return EnumFacing.DOWN;
            }
        }

        return this.getHorizontalFacing().getOpposite();
    }
}
