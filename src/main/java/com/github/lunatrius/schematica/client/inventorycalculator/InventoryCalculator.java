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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

        MBlockPos currentPos = getAnyValidBlock();
        // If the current block is null there are no more blocks to check,
        // Stop when the optimalInventory would fill up the player's inventory
        while (currentPos != null && doesFitInInv(schematic.getBlockState(currentPos), optimalInventory, openSlots)) {
            IBlockState currentSchemState = schematic.getBlockState(currentPos);

            // The position of an adjacent non matching block
            MBlockPos backupPos = null;
            // The position of an adjacent matching block
            MBlockPos prioPos = null;
            for (EnumFacing side : EnumFacing.VALUES) {
                MBlockPos adjacentBlockPos = currentPos.offset(side);
                IBlockState adjacentShemState = schematic.getBlockState(adjacentBlockPos);

                IBlockState adjacentMCState = Minecraft.getMinecraft().world.getBlockState(new BlockPos(adjacentBlockPos.getX() + this.schematicWorld.position.getX(), adjacentBlockPos.getY() + this.schematicWorld.position.getY(), adjacentBlockPos.getZ() + this.schematicWorld.position.getZ()));

                if (!countedBlocks.contains(adjacentBlockPos) && this.schematicWorld.isInside(adjacentBlockPos)) {
                    if (adjacentShemState == currentSchemState && adjacentMCState.getBlock() == Blocks.AIR) {
                        prioPos = adjacentBlockPos;
                        break;
                    } else if (backupPos == null) {
                        backupPos = adjacentBlockPos;
                    }
                }
            }

            if (prioPos != null && addBlock(schematic.getBlockState(prioPos), prioPos, openSlots)) {
                currentPos = prioPos;

            } else if (backupPos != null && addBlock(schematic.getBlockState(backupPos), backupPos, openSlots)) {
                currentPos = backupPos;

            } else { // find any other block if a dead end is found
                currentPos = getAnyValidBlock();
                if (currentPos != null) {
                    addBlock(schematic.getBlockState(currentPos), currentPos, openSlots);
                } else {
                    break;
                }
            }
        }

        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString("\2477[Mapmatica] \n" + getBlockListFromMap(optimalInventory)));
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

    /**
     * Gets a block in the schematic that can be placed off of and that has not been counted,
     * if none exist return 0, 0, 1, (one block below the noob line)
     * if the entire schematic is built, return null
     */
    private MBlockPos getAnyValidBlock () {
        ISchematic schematic = this.schematicWorld.getSchematic();
        BlockPos.MutableBlockPos realBlockPos = new BlockPos.MutableBlockPos();
        World mcWorld = Minecraft.getMinecraft().world;
        // If no air blocks are ever found, the schematic is completed
        boolean foundAirBlock = false;

        for (MBlockPos pos : BlockPosHelper.getAllInBoxXZY(0, 0, 0, schematic.getWidth(), schematic.getHeight(), schematic.getLength() - 1)) {
            IBlockState realBlockState = mcWorld.getBlockState(realBlockPos.setPos(pos.getX() + this.schematicWorld.position.getX(), pos.getY() + this.schematicWorld.position.getY(), pos.getZ() + this.schematicWorld.position.getZ()));

            if (!countedBlocks.contains(pos) && realBlockState.getBlock() == Blocks.AIR && this.schematicWorld.isInside(pos)) {
                foundAirBlock = true;
                for (EnumFacing side : EnumFacing.VALUES) {
                    MBlockPos realAdjacentPos = new MBlockPos(realBlockPos.offset(side));
                    MBlockPos schemAdjacentPos = pos.offset(side);

                    IBlockState adjacentState = mcWorld.getBlockState(realAdjacentPos);
                    Block adjacentBlock = adjacentState.getBlock();

                    if (adjacentBlock.canCollideCheck(adjacentState, false) || countedBlocks.contains(schemAdjacentPos)) {
                        return pos;
                    }
                }
            }
        }

        return foundAirBlock ? new MBlockPos(0, 0, 1) : null;
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
