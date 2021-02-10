package com.github.lunatrius.schematica.client.inventorycalculator;

import com.github.lunatrius.core.client.gui.GuiHelper;
import com.github.lunatrius.core.entity.EntityHelper;
import com.github.lunatrius.core.util.math.BlockPosHelper;
import com.github.lunatrius.core.util.math.MBlockPos;
import com.github.lunatrius.schematica.api.ISchematic;
import com.github.lunatrius.schematica.client.gui.inventorycalc.GuiInventoryCalculator;
import com.github.lunatrius.schematica.client.printer.SchematicPrinter;
import com.github.lunatrius.schematica.client.util.BlockList;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.github.lunatrius.schematica.reference.Names;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Facilitates the calculation, printing, and rendering of the optimal inventory
 * to be taken to a given map.
 *
 * The goal is to find a list of block stacks that will all be used when a player takes
 * them to a map and prints them.
 *
 * @author Old Chum
 * @since 8/26/20
 */
public class InventoryCalculator {
    public static InventoryCalculator INSTANCE = new InventoryCalculator();
    private SchematicWorld schematicWorld = ClientProxy.schematic;

    /** The thread that is currently calculating the inventory, null if not calculating. */
    private Thread thread = null;

    /**
     * A set of blocks pos that were included when the optimal inventory was calculated,
     * allows for printer and render filtering.
     *
     * If this is null, there is currently no optimal inventory.
     */
    private Set<MBlockPos> optimalBlocks = null;

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
        this.thread = new Thread(() -> {
            SchematicPrinter printer = SchematicPrinter.INSTANCE;
            printer.forceDisable = true; // Disable printer to prevent crash

            this.schematicWorld = ClientProxy.schematic;
            if (schematicWorld == null) {
                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString("\2477[Mapmatica] \247cCannot get inventory of schematic (no schematic is loaded?)"));
                return;
            }

            ISchematic schematic = schematicWorld.getSchematic();

            optimalInventory = new HashMap<>();

            // Resets counted blocks if it is not null and inits it if it is null
            optimalBlocks = new HashSet<>();

            int openSlots = 0;
            for (ItemStack stack : Minecraft.getMinecraft().player.inventory.mainInventory) {
                if (stack.getItem() == Items.AIR) {
                    openSlots++;
                }
            }

            int range = ConfigurationHandler.inventoryCalculatorRange;
            MBlockPos currentPos = getHighestFloodCountBlock(range, null);

            // If the current block is null there are no more blocks to check,
            // Stop when the optimalInventory would fill up the player's inventory
            while (currentPos != null /*&& doesFitInInv(schematic.getBlockState(currentPos), optimalInventory, openSlots)*/) {
                Set<IBlockState> targets = new HashSet<>();
                Set<IBlockState> whitelist = new HashSet<>();

                // If the current pos fits the inventory we can use the range to determine the targets
                if (doesFitInInv(schematic.getBlockState(currentPos), optimalInventory, openSlots)) {
                    for (MBlockPos targetPos : BlockPosHelper.getAllInBoxXZY(currentPos.x + range, currentPos.y + range, currentPos.z + range, currentPos.x - range, currentPos.y - range, currentPos.z - range)) {
                        if (schematicWorld.isInside(targetPos)) {
                            targets.add(schematic.getBlockState(targetPos));
                        }
                    }

                } else { // If the current block doesnt fit the inventory, we can use the incomplete stacks to determine the targets
                    boolean hasIncompleteStack = false;
                    for (IBlockState state : optimalInventory.keySet()) {
                        if (optimalInventory.get(state) % 64 != 0) {
                            if (!canPlace(state)) {
                                continue;
                            }

                            targets.add(state); // We dont need to worry about the number of blocks for targets
                            whitelist.add(state);
                            hasIncompleteStack = true;
                        }
                    }

                    if (!hasIncompleteStack) {
                        break;
                    }
                }

                this.floodAdd(currentPos, targets, openSlots);
                currentPos = getHighestFloodCountBlock(range, whitelist.isEmpty() ? null : whitelist);
            }

            printer.forceDisable = false; // Re-enable printer
            InventoryCalculator.INSTANCE.thread = null; // ¯\_(ツ  )_/¯
        });
        this.thread.setName("InvCalc Thread");
        this.thread.start();
    }

    /**
     * Adapted version of flood fill to add a chunk of blocks to the optimal inventory.
     */
    private void floodAdd(MBlockPos pos, Set<IBlockState> targets, int openSlots) {
        Queue<MBlockPos> queue = new LinkedList<>();
        queue.add(pos);

        while (!queue.isEmpty()) {
            MBlockPos current = queue.remove();

            IBlockState schemState = schematicWorld.getSchematic().getBlockState(current);
            IBlockState mcSate = Minecraft.getMinecraft().world.getBlockState(new BlockPos(current.getX() + this.schematicWorld.position.getX(), current.getY() + this.schematicWorld.position.getY(), current.getZ() + this.schematicWorld.position.getZ()));

            if (current.getZ() >= 1 && !optimalBlocks.contains(current) && (ConfigurationHandler.isExtraAirBlock(mcSate.getBlock()) || mcSate.getBlock() == Blocks.AIR) && schematicWorld.isInside(current) && targets.contains(schemState)) {
                if (addBlock(schemState, current, openSlots)) {
                   queue.add(current.offset(EnumFacing.NORTH));
                   queue.add(current.offset(EnumFacing.SOUTH));
                   queue.add(current.offset(EnumFacing.EAST));
                   queue.add(current.offset(EnumFacing.WEST));
                   queue.add(current.offset(EnumFacing.UP));
                   queue.add(current.offset(EnumFacing.DOWN));
               }
            }
        }
    }

    /** Gets the flood size of a block */
    private int getBlockFloodCount(MBlockPos pos, Set<IBlockState> targets) {
        Queue<MBlockPos> queue = new LinkedList<>();
        queue.add(pos);

        // Used to check if a block has already been counted
        // Flood fill would normally do this by setting the color of the pixel
        Set<MBlockPos> counted = new HashSet<>();

        while (!queue.isEmpty()) {
            MBlockPos current = queue.remove();

            IBlockState schemState = schematicWorld.getSchematic().getBlockState(current);
            IBlockState mcSate = Minecraft.getMinecraft().world.getBlockState(new BlockPos(current.getX() + this.schematicWorld.position.getX(), current.getY() + this.schematicWorld.position.getY(), current.getZ() + this.schematicWorld.position.getZ()));

            if (current.getZ() >= 1 && !counted.contains(current) && !optimalBlocks.contains(current) && (ConfigurationHandler.isExtraAirBlock(mcSate.getBlock()) || mcSate.getBlock() == Blocks.AIR) && schematicWorld.isInside(current) && targets.contains(schemState)) {
                floodCountedBlocks.add(current);
                counted.add(current);

                queue.add(current.offset(EnumFacing.NORTH));
                queue.add(current.offset(EnumFacing.SOUTH));
                queue.add(current.offset(EnumFacing.EAST));
                queue.add(current.offset(EnumFacing.WEST));
                queue.add(current.offset(EnumFacing.UP));
                queue.add(current.offset(EnumFacing.DOWN));
            }
        }

        return counted.size();
    }

    /** Gets the placeable block that has the highest flood count */
    private MBlockPos getHighestFloodCountBlock (int range, @Nullable Set<IBlockState> whitelist) {
        ISchematic schematic = this.schematicWorld.getSchematic();
        BlockPos.MutableBlockPos mcBlockPos = new BlockPos.MutableBlockPos();
        World mcWorld = Minecraft.getMinecraft().world;

        this.floodCountedBlocks.clear();

        MBlockPos maxPos = null;
        int maxFloodCount = -1;
        for (MBlockPos pos : BlockPosHelper.getAllInBoxXZY(0, 0, 1, schematic.getWidth(), schematic.getHeight(), schematic.getLength())) {
            // If the whitelist doesnt contain this block we can continue to the next one
            if (whitelist != null && !whitelist.contains(schematic.getBlockState(pos))) {
                continue;
            }

            IBlockState realBlockState = mcWorld.getBlockState(mcBlockPos.setPos(pos.getX() + this.schematicWorld.position.getX(), pos.getY() + this.schematicWorld.position.getY(), pos.getZ() + this.schematicWorld.position.getZ()));

            if (!optimalBlocks.contains(pos) && !floodCountedBlocks.contains(pos) && realBlockState.getBlock() == Blocks.AIR && this.schematicWorld.isInside(pos)) {
                Set<IBlockState> targets = new HashSet<>();
                targets.add(schematic.getBlockState(pos));

                // Find block to place off of
                boolean foundSolidAdj = false;
                for (EnumFacing side : EnumFacing.VALUES) {
                    IBlockState mcAdjacentState = mcWorld.getBlockState(mcBlockPos.offset(side));
                    MBlockPos schemAdjacentPos = pos.offset(side);

                    if (mcAdjacentState.getBlock().canCollideCheck(mcAdjacentState, false) || optimalBlocks.contains(schemAdjacentPos)) {
                        foundSolidAdj = true;
                    }
                }

                // Get targets
                for (MBlockPos targetPos : BlockPosHelper.getAllInBoxXZY(pos.x + range, pos.y + range, pos.z + range, pos.x - range, pos.y - range, pos.z - range)) {
                    if (schematicWorld.isInside(targetPos)) {
                        if (whitelist == null || whitelist.contains(schematic.getBlockState(targetPos))) {
                            targets.add(schematic.getBlockState(new MBlockPos(targetPos)));
                        }
                    }
                }

                if (foundSolidAdj) {
                    int floodCount = getBlockFloodCount(new MBlockPos(pos), targets);
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
        } else {
            return null;
        }
    }

    /** Tells if a blockstate has an instance in the schematic and if that instance can be placed */
    private boolean canPlace (IBlockState state) {
        ISchematic schematic = this.schematicWorld.getSchematic();
        BlockPos.MutableBlockPos mcBlockPos = new BlockPos.MutableBlockPos();

        for (MBlockPos pos : BlockPosHelper.getAllInBoxXZY(0, 0, 1, schematic.getWidth(), schematic.getHeight(), schematic.getLength())) {
            mcBlockPos.setPos(pos.getX() + this.schematicWorld.position.getX(), pos.getY() + this.schematicWorld.position.getY(), pos.getZ() + this.schematicWorld.position.getZ());
            IBlockState schemState = schematic.getBlockState(pos);

            if (schemState == state) {
                for (EnumFacing side : EnumFacing.VALUES) {
                    IBlockState realAdjacent = schematic.getBlockState(mcBlockPos.offset(side));
                    MBlockPos adjacentPos = pos.offset(side);

                    if (realAdjacent.getBlock().canCollideCheck(realAdjacent, false) || optimalBlocks.contains(adjacentPos)) {
                        return true;
                    }
                }
            }
        }

        return false;
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
            this.optimalBlocks.add(pos);
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
                BlockList.WrappedItemStack wrappedItemStack = new BlockList.WrappedItemStack(state.getBlock().getPickBlock(state, null, Minecraft.getMinecraft().world, null, Minecraft.getMinecraft().player), 0, this.optimalInventory.get(state));

                if (Minecraft.getMinecraft().player.capabilities.isCreativeMode) {
                    wrappedItemStack.inventory = -1;
                } else {
                    wrappedItemStack.inventory = EntityHelper.getItemCountInInventory(Minecraft.getMinecraft().player.inventory, wrappedItemStack.itemStack.getItem(), wrappedItemStack.itemStack.getItemDamage());
                }

                ret.add(wrappedItemStack);
            }
        }

        return ret;
    }

    public static void onRender2d () {
        BlockList.WrappedItemStack currentStack = null;

        if (ClientProxy.schematic != null) {
            for (BlockList.WrappedItemStack stack : GuiInventoryCalculator.INSTANCE.getSortType().sort(InventoryCalculator.INSTANCE.getWrappedItemStacks())) {
                if (stack.inventory < stack.total) {
                    currentStack = stack;
                    break;
                }
            }

            // TODO: Support for material list

            if (currentStack != null && Minecraft.getMinecraft().currentScreen == null) {
                // Draw stack in the middle of the screen, im not adding options for where this is drawn.
                ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
                drawStack(currentStack, sr.getScaledWidth() / 2 - 215 / 2, 1);
            }
        }
    }

    private static void drawStack (BlockList.WrappedItemStack wrappedItemStack, final int x, final int y) {
        Minecraft minecraft = Minecraft.getMinecraft();
        final ItemStack itemStack = wrappedItemStack.itemStack;

        final String itemName = wrappedItemStack.getItemStackDisplayName();
        final String amount = wrappedItemStack.getFormattedAmount();
        final String amountMissing = wrappedItemStack.getFormattedAmountMissing(I18n.format(Names.Gui.Control.MATERIAL_AVAILABLE), I18n.format(Names.Gui.Control.MATERIAL_AMOUNT));

        GuiHelper.drawItemStackWithSlot(minecraft.renderEngine, itemStack, x, y);

        minecraft.fontRenderer.drawStringWithShadow(itemName, x + 24, y + 6, 0xFFFFFF);
        minecraft.fontRenderer.drawStringWithShadow(amount, x + 215 - minecraft.fontRenderer.getStringWidth(amount), y + 1, 0xFFFFFF);
        minecraft.fontRenderer.drawStringWithShadow(amountMissing, x + 215 - minecraft.fontRenderer.getStringWidth(amountMissing), y + 11, 0xFFFFFF);
    }

    public Set<MBlockPos> getOptimalBlocks() {
        return optimalBlocks;
    }

    public void setOptimalBlocks(Set<MBlockPos> optimalBlocks) {
        this.optimalBlocks = optimalBlocks;
    }

    public Map<IBlockState, Integer> getOptimalInventory() {
        return optimalInventory;
    }

    public void setOptimalInventory(Map<IBlockState, Integer> optimalInventory) {
        this.optimalInventory = optimalInventory;
    }

    public Thread getThread() {
        return thread;
    }

    public boolean isCalculating () {
        return this.thread != null;
    }

    public void stopCalculating () {
        this.thread.interrupt();
        this.thread = null;
    }
}
