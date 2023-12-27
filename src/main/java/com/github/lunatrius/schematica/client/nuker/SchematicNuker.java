package com.github.lunatrius.schematica.client.nuker;

import static com.github.lunatrius.schematica.handler.ConfigurationHandler.*;

import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.*;

// TODO: We might want to add an overlay color to indicate a block being nukered

public class SchematicNuker {
    public static final Comparator<BlockPos> BLOCKPOS_DIST_COMPARATOR = Comparator.comparingDouble(o -> o.distanceSqToCenter(Minecraft.getMinecraft().player.posX, Minecraft.getMinecraft().player.posY, Minecraft.getMinecraft().player.posZ));

    private static boolean isNuking = false;

    private static Minecraft mc = Minecraft.getMinecraft();
    private static Map<BlockPos, Long> attemptedBreaks = new HashMap<>();

    public static void doNuker () {
        SchematicWorld schem = ClientProxy.schematic;
        if (schem == null) {
            return;
        }

        EntityPlayerSP player = mc.player;

        AxisAlignedBB bb = new AxisAlignedBB(
                    (int) player.posX - nukerRange,
                    (int) (nukerFlatten ? player.posY : player.posY - nukerRange),
                    (int) player.posZ - nukerRange,
                    (int) player.posX + nukerRange,
                    (int) player.posY + nukerRange,
                    (int) player.posZ + nukerRange);

        // TODO: Is it whl or lhw?
        bb = bb.intersect(new AxisAlignedBB(schem.position, schem.position.add(schem.getWidth(), schem.getHeight(), schem.getLength())));

        List<BlockPos> blocks = getClosestBlocksInBox(bb, nukerRange);

        for (BlockPos pos : blocks) {
            IBlockState state = mc.world.getBlockState(pos);
            Block block = state.getBlock();

            if (shouldNuke(pos)) {
                if (block != Blocks.AIR && !(block instanceof BlockLiquid)) {
                    if (block.getPlayerRelativeBlockHardness(state, player, mc.world, pos) >= nukerMineSpeed) {
                        if (pos.distanceSqToCenter(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) <= nukerRange * nukerRange) {
                            if (!attemptedBreaks.containsKey(pos)) {
                                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.NORTH));
                                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.NORTH));

                                attemptedBreaks.put(pos, System.currentTimeMillis());
                            }
                        }
                    }
                }
            }
        }

        List<BlockPos> toRemove = new ArrayList<>();
        for (BlockPos pos : attemptedBreaks.keySet()) {
            if (System.currentTimeMillis() - attemptedBreaks.get(pos) >= nukerTimeout) {
                toRemove.add(pos);
            }
        }

        for (BlockPos pos : toRemove) {
            attemptedBreaks.remove(pos);
        }
    }

    private static boolean shouldNuke (BlockPos pos) {
        SchematicWorld schem = ClientProxy.schematic;

        IBlockState schemState = schem.getBlockState(pos.add(-schem.position.x, -schem.position.y, -schem.position.z));
        IBlockState mcState = mc.world.getBlockState(pos);

        // TODO: Add an option to allow players to keep blocks of the same Block but different State
        if (schemState == mcState) {
            return false;
        }

        if (nukerMode.equals(NukerMode.BLOCKS.name())) {
            return !isSchemAirBlock(schemState);
        } else if (nukerMode.equals(NukerMode.AIR.name())) {
            return isSchemAirBlock(schemState);
        } else {
            return true; // Do we not support cave air?
        }
    }

    public static boolean toggle () {
        attemptedBreaks.clear();
        mc = Minecraft.getMinecraft();

        isNuking = !isNuking;
        return isNuking;
    }

    // TODO: All methods below this line should be in their own util files

    /**
     * @param radius The radius of the sphere. -1 if all blocks in the bb should be included, regardless of if they are
     *               in the sphere.
     * @return A list of {@link BlockPos}' in <code>bb</code> intersecting with the sphere of radius <code>radius</code>
     *         centered on the middle of the player at eye height, sorted by ascending distance to the player.
     */
    public static List<BlockPos> getClosestBlocksInBox (AxisAlignedBB bb, double radius) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        List<BlockPos> ret = new ArrayList<>();

        for (BlockPos pos : getAllInBB(bb)) {
            if (radius == -1 || pos.distanceSqToCenter(player.posX, player.posY + player.eyeHeight, player.posZ) <= radius * radius) {
                ret.add(new BlockPos(pos));
            }
        }

        ret.sort(BLOCKPOS_DIST_COMPARATOR);
        return ret;
    }

    public static Iterable<BlockPos> getAllInBB (AxisAlignedBB bb) {
        return BlockPos.getAllInBox((int) bb.minX, (int) bb.minY, (int) bb.minZ, (int) bb.maxX, (int) bb.maxY, (int) bb.maxZ);
    }

    // TODO: This is copied from InvCalc, make sure to move that too when the aforementioned util file is made
    private static boolean isSchemAirBlock (IBlockState state) {
        return ConfigurationHandler.isExtraAirBlock(state.getBlock()) || state.getBlock() == Blocks.AIR;
    }
}
