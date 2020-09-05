package com.github.lunatrius.schematica.client.inventorycalculator;

import com.github.lunatrius.core.util.math.BlockPosHelper;
import com.github.lunatrius.core.util.math.MBlockPos;
import com.github.lunatrius.schematica.api.ISchematic;
import com.github.lunatrius.schematica.client.util.BlockList;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.handler.ConfigurationHandler;
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
 *
 * TODO: Multi-threading?
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

        int range = ConfigurationHandler.inventoryCalculatorRange;
        MBlockPos currentPos = getHighestFloodCountBlock(range);
        // If the current block is null there are no more blocks to check,
        // Stop when the optimalInventory would fill up the player's inventory
        while (currentPos != null && doesFitInInv(schematic.getBlockState(currentPos), optimalInventory, openSlots)) {

            Map<IBlockState, Integer> targets = new HashMap<>();
            for (MBlockPos targetPos : BlockPosHelper.getAllInBoxXZY(currentPos.x + range, currentPos.y + range, currentPos.z + range, currentPos.x - range, currentPos.y - range, currentPos.z - range)) {
                if (schematicWorld.isInside(targetPos)) {
                    targets.put(schematic.getBlockState(targetPos), targets.getOrDefault(schematic.getBlockState(targetPos), 1));
                }
            }

            this.floodAdd(currentPos, targets, openSlots);
            currentPos = getHighestFloodCountBlock(range);
        }
    }

    private String blockPosToString (BlockPos pos) {
        if (pos == null) {
            return "null";
        }

        return String.format("(%d, %d, %d)", pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Adapted version of flood fill to add a chunk of blocks to the optimal inventory.
     */
    private void floodAdd(MBlockPos pos, Map<IBlockState, Integer> targetStates, int openSlots) {
        IBlockState schemState = schematicWorld.getSchematic().getBlockState(pos);

        if (!targetStates.containsKey(schemState)) {
            return;
        }

        IBlockState mcSate = Minecraft.getMinecraft().world.getBlockState(new BlockPos(pos.getX() + this.schematicWorld.position.getX(), pos.getY() + this.schematicWorld.position.getY(), pos.getZ() + this.schematicWorld.position.getZ()));
        if ((ConfigurationHandler.printNoobline || !(pos.getZ() >= 1)) && !countedBlocks.contains(pos) && mcSate.getBlock() == Blocks.AIR && schematicWorld.isInside(pos)) {
            if (!addBlock(schemState, pos, openSlots)) {
                return;
            }
        } else {
            return;
        }

        List<EnumFacing> facings = new ArrayList<>();
        Collections.addAll(facings, EnumFacing.VALUES);

        facings.sort((o1, o2) -> {
            ISchematic schematic = schematicWorld.getSchematic();
            IBlockState state1 = schematic.getBlockState(pos.offset(o1));
            IBlockState state2 = schematic.getBlockState(pos.offset(o2));

            if (targetStates.containsKey(state1) && targetStates.containsKey(state2)) {
                int diff = targetStates.get(state1) - targetStates.get(state2);
                return Integer.compare(diff, 0);
            } else if (state1 == schemState) {
                return 1;
            } else if (state2 == schemState) {
                return -1;
            } else {
                return 0;
            }
        });

        for (EnumFacing side : facings) {
            floodAdd(pos.offset(side), targetStates, openSlots);
        }
    }

    /** Gets the flood size of a block */
    private int getBlockFloodCount(MBlockPos pos, Map<IBlockState, Integer> targetStates, Set<MBlockPos> counted) {
        IBlockState schemState = schematicWorld.getSchematic().getBlockState(pos);

        if (!targetStates.containsKey(schemState)) {
            return counted.size();
        }

        IBlockState mcSate = Minecraft.getMinecraft().world.getBlockState(new BlockPos(pos.getX() + this.schematicWorld.position.getX(), pos.getY() + this.schematicWorld.position.getY(), pos.getZ() + this.schematicWorld.position.getZ()));
        if (pos.getZ() >= 1 && !floodCountedBlocks.contains(pos) && !countedBlocks.contains(pos) && mcSate.getBlock() == Blocks.AIR && schematicWorld.isInside(pos)) {
            floodCountedBlocks.add(pos);
            counted.add(pos);
        } else {
            return counted.size();
        }

        getBlockFloodCount(pos.offset(EnumFacing.NORTH), targetStates, counted);
        getBlockFloodCount(pos.offset(EnumFacing.SOUTH), targetStates, counted);
        getBlockFloodCount(pos.offset(EnumFacing.EAST), targetStates, counted);
        getBlockFloodCount(pos.offset(EnumFacing.WEST), targetStates, counted);
        getBlockFloodCount(pos.offset(EnumFacing.UP), targetStates, counted);
        getBlockFloodCount(pos.offset(EnumFacing.DOWN), targetStates, counted);

        return counted.size();
    }

    /** Gets the placeable block that has the highest flood count */
    private MBlockPos getHighestFloodCountBlock (int range) {
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

                Map<IBlockState, Integer> targets = new HashMap<>();
                targets.put(schematic.getBlockState(pos), 1);

                // Find block to place off of
                boolean foundSolidAdj = false;
                for (EnumFacing side : EnumFacing.VALUES) {
                    IBlockState mcAdjacentState = mcWorld.getBlockState(mcBlockPos.offset(side));
                    MBlockPos schemAdjacentPos = pos.offset(side);

                    if (mcAdjacentState.getBlock().canCollideCheck(mcAdjacentState, false) || countedBlocks.contains(schemAdjacentPos)) {
                        foundSolidAdj = true;
                    }
                }

                // Get targets
                for (MBlockPos targetPos : BlockPosHelper.getAllInBoxXZY(pos.x + range, pos.y + range, pos.z + range, pos.x - range, pos.y - range, pos.z - range)) {
                    if (schematicWorld.isInside(targetPos)) {
                        targets.put(schematic.getBlockState(new MBlockPos(targetPos)), targets.getOrDefault(schematic.getBlockState(new MBlockPos(targetPos)), 1));
                    }
                }

                if (foundSolidAdj) {
                    int floodCount = getBlockFloodCount(new MBlockPos(pos), targets, new HashSet<>());
                    if (floodCount > maxFloodCount) {
                        maxFloodCount = floodCount;
                        maxPos = new MBlockPos(pos);
                        floodCountedBlocks.add(new MBlockPos(pos));
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

    public List<BlockList.WrappedItemStack> getWrappedItemStacks () {
        List<BlockList.WrappedItemStack> ret = new ArrayList<>();

        if (this.optimalInventory != null) {
            for (IBlockState state : this.optimalInventory.keySet()) {
                ret.add(new BlockList.WrappedItemStack(state.getBlock().getPickBlock(state, null, Minecraft.getMinecraft().world, null, Minecraft.getMinecraft().player), 0, this.optimalInventory.get(state)));
            }
        }

        return ret;
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
