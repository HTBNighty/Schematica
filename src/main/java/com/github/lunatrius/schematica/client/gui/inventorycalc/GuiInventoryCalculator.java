package com.github.lunatrius.schematica.client.gui.inventorycalc;

import com.github.lunatrius.core.client.gui.GuiScreenBase;
import com.github.lunatrius.schematica.client.inventorycalculator.InventoryCalculator;
import com.github.lunatrius.schematica.client.util.BlockList;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.github.lunatrius.schematica.reference.Names;
import com.github.lunatrius.schematica.util.ItemStackSortType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.util.List;

public class GuiInventoryCalculator extends GuiScreenBase {
    private GuiInventoryCaclulatorSlot guiInventoryCalculatorSlot;

    private GuiButton btnGen = null;
    private GuiButton btnStop = null;
    private GuiButton btnDone = null;

    private ItemStackSortType sortType = ItemStackSortType.SIZE_DESC;

    protected List<BlockList.WrappedItemStack> blockList;

    public GuiInventoryCalculator(final GuiScreen guiScreen) {
        super(guiScreen);
        final Minecraft minecraft = Minecraft.getMinecraft();
        final SchematicWorld schematic = ClientProxy.schematic;
        this.blockList = InventoryCalculator.INSTANCE.getWrappedItemStacks();
        this.sortType.sort(this.blockList);
    }

    @Override
    public void initGui() {
        int id = 0;

        this.btnGen = new GuiButton(++id, this.width / 2 - 50, this.height - 30, 100, 20, I18n.format(Names.Gui.Control.GENERATE));
        this.buttonList.add(this.btnGen);

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
                this.blockList = InventoryCalculator.INSTANCE.getWrappedItemStacks();
                this.sortType.sort(this.blockList);
            } else if (guiButton.id == this.btnDone.id) {
                this.mc.displayGuiScreen(null);
            } else if (guiButton.id == this.btnStop.id) {
                InventoryCalculator.INSTANCE.setCountedBlocks(null);
                InventoryCalculator.INSTANCE.setOptimalInventory(null);
                this.blockList.clear();
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
    }
}
