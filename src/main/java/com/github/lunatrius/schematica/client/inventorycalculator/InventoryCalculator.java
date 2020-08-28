package com.github.lunatrius.schematica.client.inventorycalculator;

import com.github.lunatrius.core.util.math.BlockPosHelper;
import com.github.lunatrius.core.util.math.MBlockPos;
import com.github.lunatrius.schematica.api.ISchematic;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.*;

/**
 * Facilitates the calculation, printing, and rendering of the optimal inventory
 * to be taken to a given map.
 *
 * The goal is to find a list of block stacks that will all be used when taken to
 * a map and printed.
 *
 * TODO: Add a gui to help manipulate this
 *
 * @author Old Chum
 * @since 8/26/20
 */
public class InventoryCalculator {
    public static InventoryCalculator INSTANCE = new InventoryCalculator();
    private SchematicWorld schematicWorld = ClientProxy.schematic;

    /**
     * A set of blocks pos that were included when the optimal inventory was calculated,
     * allows for printer and render filtering.
     *
     * If this is null, there is currently no optimal inventory.
     */
    private Set<MBlockPos> countedBlocks = null;

    /**
     * A map from a block state to the amount of that block state needed. The current optimal inventory.
     *
     * If this is null, there is currently no optimal inventory.
     */
    private Map<IBlockState, Integer> optimalInventory = null;

    /**
     * A set of block pos used by getHighestFloodCountBlock() in conjunction with getBlockFloodCount()
     * to make sure that one floodable area is checked only once, not for each block inside of it.
     */
    private Set<MBlockPos> floodCountedBlocks = new HashSet<>();

    public void calculateOptimalInv () {
        this.schematicWorld = ClientProxy.schematic;
        if (schematicWorld == null) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString("\2477[Mapmatica] \247cCannot get inventory of schematic (no schematic is loaded?)"));
            return;
        }

        ISchematic schematic = schematicWorld.getSchematic();

        optimalInventory = new HashMap<>();

        // Resets counted blocks if it is not null and inits it if it is null
        countedBlocks = new HashSet<>();

        int openSlots = 0;
        for (ItemStack stack : Minecraft.getMinecraft().player.inventory.mainInventory) {
            if (stack.getItem() == Items.AIR) {
                openSlots++;
            }
        }

        MBlockPos currentPos = getHighestFloodCountBlock();
        // If the current block is null there are no more blocks to check,
        // Stop when the optimalInventory would fill up the player's inventory
        while (currentPos != null && doesFitInInv(schematic.getBlockState(currentPos), optimalInventory, openSlots)) {
            IBlockState currentSchemState = schematic.getBlockState(currentPos);

            this.floodAdd(currentPos, currentSchemState, openSlots);
            currentPos = getHighestFloodCountBlock();
        }

        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString("\2477[Mapmatica] \n" + getBlockListFromMap(optimalInventory)));
    }

    /**
     * Adapted version of flood fill to add a chunk of blocks to the optimal inventory.
     */
    private void floodAdd(MBlockPos pos, IBlockState targetState, int openSlots) {
        IBlockState schemState = schematicWorld.getSchematic().getBlockState(pos);

        if (schemState != targetState) {
            return;
        }

        IBlockState mcSate = Minecraft.getMinecraft().world.getBlockState(new BlockPos(pos.getX() + this.schematicWorld.position.getX(), pos.getY() + this.schematicWorld.position.getY(), pos.getZ() + this.schematicWorld.position.getZ()));
        if (pos.getZ() >= 1 && !countedBlocks.contains(pos) && mcSate.getBlock() == Blocks.AIR && schematicWorld.isInside(pos)) {
            if (!addBlock(targetState, pos, openSlots)) {
                return;
            }
        } else {
            return;
        }

        floodAdd(pos.offset(EnumFacing.NORTH), targetState, openSlots);
        floodAdd(pos.offset(EnumFacing.SOUTH), targetState, openSlots);
        floodAdd(pos.offset(EnumFacing.EAST), targetState, openSlots);
        floodAdd(pos.offset(EnumFacing.WEST), targetState, openSlots);
        floodAdd(pos.offset(EnumFacing.UP), targetState, openSlots);
        floodAdd(pos.offset(EnumFacing.DOWN), targetState, openSlots);
    }

    /** Gets the flood size of a block */
    private int getBlockFloodCount(MBlockPos pos, IBlockState targetState, Set<MBlockPos> counted) {
        IBlockState schemState = schematicWorld.getSchematic().getBlockState(pos);

        if (schemState != targetState) {
            return counted.size();
        }

        IBlockState mcSate = Minecraft.getMinecraft().world.getBlockState(new BlockPos(pos.getX() + this.schematicWorld.position.getX(), pos.getY() + this.schematicWorld.position.getY(), pos.getZ() + this.schematicWorld.position.getZ()));
        if (pos.getZ() >= 1 && !floodCountedBlocks.contains(pos) && !countedBlocks.contains(pos) && mcSate.getBlock() == Blocks.AIR && schematicWorld.isInside(pos)) {
            floodCountedBlocks.add(pos);
            counted.add(pos);
        } else {
            return counted.size();
        }

        getBlockFloodCount(pos.offset(EnumFacing.NORTH), targetState, counted);
        getBlockFloodCount(pos.offset(EnumFacing.SOUTH), targetState, counted);
        getBlockFloodCount(pos.offset(EnumFacing.EAST), targetState, counted);
        getBlockFloodCount(pos.offset(EnumFacing.WEST), targetState, counted);
        getBlockFloodCount(pos.offset(EnumFacing.UP), targetState, counted);
        getBlockFloodCount(pos.offset(EnumFacing.DOWN), targetState, counted);

        return counted.size();
    }

    /** Gets the placeable block that has the highest flood count */
    private MBlockPos getHighestFloodCountBlock () {
        ISchematic schematic = this.schematicWorld.getSchematic();
        BlockPos.MutableBlockPos mcBlockPos = new BlockPos.MutableBlockPos();
        World mcWorld = Minecraft.getMinecraft().world;

        this.floodCountedBlocks.clear();

        // If no air blocks are ever found, the schematic is completed
        boolean foundAirBlock = false;

        MBlockPos maxPos = null;
        int maxFloodCount = -1;
        for (MBlockPos pos : BlockPosHelper.getAllInBoxXZY(0, 0, 1, schematic.getWidth(), schematic.getHeight(), schematic.getLength())) {
            IBlockState realBlockState = mcWorld.getBlockState(mcBlockPos.setPos(pos.getX() + this.schematicWorld.position.getX(), pos.getY() + this.schematicWorld.position.getY(), pos.getZ() + this.schematicWorld.position.getZ()));

            if (!countedBlocks.contains(pos) && !floodCountedBlocks.contains(pos) && realBlockState.getBlock() == Blocks.AIR && this.schematicWorld.isInside(pos)) {
                foundAirBlock = true;
                for (EnumFacing side : EnumFacing.VALUES) {
                    MBlockPos mcAdjacentPos = new MBlockPos(mcBlockPos.offset(side));
                    MBlockPos schemAdjacentPos = pos.offset(side);

                    IBlockState mcAdjacentState = mcWorld.getBlockState(mcAdjacentPos);
                    Block mcAdjacentBlock = mcAdjacentState.getBlock();

                    if (mcAdjacentBlock.canCollideCheck(mcAdjacentState, false) || countedBlocks.contains(schemAdjacentPos)) {
                        int floodCount = getBlockFloodCount(pos, schematic.getBlockState(pos), new HashSet<>());

                        if (floodCount > maxFloodCount) {
                            maxFloodCount = floodCount;
                            maxPos = new MBlockPos(pos);
                            floodCountedBlocks.add(maxPos);
                            break;
                        }
                    }
                }
            }
        }

        if (maxPos != null) {
            return maxPos;
        } else if (foundAirBlock) {
            System.out.println("[SOUPHACK+ DEBUG ENHANCEMENT SUITE] The map better be empty...");
            return new MBlockPos(0, 0, 1);
        } else {
            return null;
        }
    }

    /** If the given block could fit in the inventory */
    private static boolean doesFitInInv (IBlockState state, Map<IBlockState, Integer> inv, int openSlots) {
        // if the addition of the item will create a new stack in the inventory
        boolean makesNewStack = (inv.getOrDefault(state, 0) % 64 == 0) || !inv.containsKey(state);
        return getMapSpace(inv) + (makesNewStack ? 1 : 0) <= openSlots;
    }

    /** Gets the amount of space a map from the blockstate to the amount of blocks would take up in the inventory */
    private static int getMapSpace (Map<IBlockState, Integer> inv) {
        int space = 0;
        for (IBlockState state : inv.keySet()) {
            space += Math.ceil(inv.get(state) / 64.0);
        }
        return space;
    }

    /** Get a nice string summarizing the blocks and the number of stacks of each block that is needed */
    public static String getBlockListFromMap (Map<IBlockState, Integer> map) {
        StringBuilder ret = new StringBuilder();
        for (IBlockState state : map.keySet()) {
            ret.append("\247a").append(state.getBlock().getPickBlock(state, null, Minecraft.getMinecraft().world, null, Minecraft.getMinecraft().player).getTextComponent().getUnformattedText()).append(": ").append((int) Math.ceil(map.get(state) / 64.0)).append(" stack(s)\n");
        }
        return ret.toString();
    }



    /** Adds a block to countedBlocks and to optimalInventory */
    private boolean addBlock (IBlockState state, MBlockPos pos, int openSlots) {
        if (doesFitInInv(state, optimalInventory, openSlots)) {
            this.countedBlocks.add(pos);
            optimalInventory.put(state, optimalInventory.getOrDefault(state, 0) + 1);
            return true;
        } else {
            return false;
        }
    }

    public Set<MBlockPos> getCountedBlocks() {
        return countedBlocks;
    }

    public void setCountedBlocks(Set<MBlockPos> countedBlocks) {
        this.countedBlocks = countedBlocks;
    }

    public Map<IBlockState, Integer> getOptimalInventory() {
        return optimalInventory;
    }

    public void setOptimalInventory(Map<IBlockState, Integer> optimalInventory) {
        this.optimalInventory = optimalInventory;
    }
}
