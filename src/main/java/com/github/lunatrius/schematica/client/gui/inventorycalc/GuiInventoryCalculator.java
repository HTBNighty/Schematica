package com.github.lunatrius.schematica.client.gui.inventorycalc;

import com.github.lunatrius.core.client.gui.GuiScreenBase;
import com.github.lunatrius.schematica.client.inventorycalculator.InventoryCalculator;
import com.github.lunatrius.schematica.client.util.BlockList;
import com.github.lunatrius.schematica.reference.Names;
import com.github.lunatrius.schematica.util.ItemStackSortType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.util.List;

public class GuiInventoryCalculator extends GuiScreenBase {
    public static GuiInventoryCalculator INSTANCE = new GuiInventoryCalculator(null);

    private GuiInventoryCaclulatorSlot guiInventoryCalculatorSlot;

    private GuiButton btnGen = null;
    private GuiButton btnStop = null;
    private GuiButton btnDone = null;

    private ItemStackSortType sortType = ItemStackSortType.SIZE_DESC;

    protected List<BlockList.WrappedItemStack> blockList;

    private boolean checkCalc = false;

    public GuiInventoryCalculator(final GuiScreen guiScreen) {
        super(guiScreen);

        blockList = InventoryCalculator.INSTANCE.getWrappedItemStacks();
        this.sortType.sort(blockList);
    }

    @Override
    public void initGui() {
        int id = 0;

        this.btnGen = new GuiButton(++id, this.width / 2 - 50, this.height - 30, 100, 20, I18n.format(Names.Gui.Control.GENERATE));
        this.buttonList.add(this.btnGen);
        this.btnGen.enabled = !InventoryCalculator.INSTANCE.isCalculating();

        this.btnStop = new GuiButton(++id, this.width / 2 - 154, this.height - 30, 100, 20, I18n.format(Names.Gui.Control.STOP));
        this.buttonList.add(this.btnStop);

        this.btnDone = new GuiButton(++id, this.width / 2 + 54, this.height - 30, 100, 20, I18n.format(Names.Gui.DONE));
        this.buttonList.add(this.btnDone);

        this.guiInventoryCalculatorSlot = new GuiInventoryCaclulatorSlot(this);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.guiInventoryCalculatorSlot.handleMouseInput();
    }

    @Override
    protected void actionPerformed(final GuiButton guiButton) {
        if (guiButton.enabled) {
            if (guiButton.id == this.btnGen.id) {
                InventoryCalculator.INSTANCE.calculateOptimalInv();
                this.btnGen.enabled = false;
            } else if (guiButton.id == this.btnDone.id) {
                this.mc.displayGuiScreen(null);
            } else if (guiButton.id == this.btnStop.id) {
                if (InventoryCalculator.INSTANCE.isCalculating()) {
                    InventoryCalculator.INSTANCE.stopCalculating();
                }

                InventoryCalculator.INSTANCE.setOptimalBlocks(null);
                InventoryCalculator.INSTANCE.setOptimalInventory(null);
                this.blockList.clear();
                this.btnGen.enabled = true;
            } else {
                this.guiInventoryCalculatorSlot.actionPerformed(guiButton);
            }
        }
    }

    @Override
    public void renderToolTip(final ItemStack stack, final int x, final int y) {
        super.renderToolTip(stack, x, y);
    }

    @Override
    public void drawScreen(final int x, final int y, final float partialTicks) {
        this.guiInventoryCalculatorSlot.drawScreen(x, y, partialTicks);
        super.drawScreen(x, y, partialTicks);

        if (InventoryCalculator.INSTANCE.isCalculating()) {
            this.checkCalc = true;
            FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

            String loading = "Loading";
            switch ((int) ((System.currentTimeMillis() / 1000) % 4)) { // Cycle "Loading"->"Loading."->"Loading.."->"Loading..." 1 stage per second
                case 2:
                    loading += ".";
                case 1:
                    loading += ".";
                case 0:
                    loading += ".";
            }

            fr.drawStringWithShadow(loading, 3, 3, 0xFFFFFFFF);
        } else if (this.checkCalc) {
            this.onFinishCalculating();
            this.checkCalc = false;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    /**
     * Called when calculator is done and the gui is open
     * if the gui is closed this will be handled by its initialization.
     */
    public void onFinishCalculating () {
        this.blockList = InventoryCalculator.INSTANCE.getWrappedItemStacks();
        this.sortType.sort(this.blockList);

        this.btnGen.enabled = true;
    }

    public List<BlockList.WrappedItemStack> getBlockList() {
        return blockList;
    }

    public ItemStackSortType getSortType() {
        return sortType;
    }
}
