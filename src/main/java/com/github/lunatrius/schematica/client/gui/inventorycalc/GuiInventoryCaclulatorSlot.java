package com.github.lunatrius.schematica.client.gui.inventorycalc;

import com.github.lunatrius.core.client.gui.GuiHelper;
import com.github.lunatrius.schematica.client.gui.control.GuiSchematicMaterials;
import com.github.lunatrius.schematica.client.util.BlockList;
import com.github.lunatrius.schematica.reference.Names;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public class GuiInventoryCaclulatorSlot extends GuiSlot {
    private final Minecraft minecraft = Minecraft.getMinecraft();

    private final GuiInventoryCalculator guiInventoryCalculator;

    private final String strMaterialAvailable = I18n.format(Names.Gui.Control.MATERIAL_AVAILABLE);
    private final String strMaterialMissing = I18n.format(Names.Gui.Control.MATERIAL_MISSING);

    protected int selectedIndex = -1;

    public GuiInventoryCaclulatorSlot(final GuiInventoryCalculator parent) {
        super(Minecraft.getMinecraft(), parent.width, parent.height, 16, parent.height - 34, 24);
        this.guiInventoryCalculator = parent;
        this.selectedIndex = -1;
    }

    @Override
    protected int getSize() {
        return this.guiInventoryCalculator.blockList.size();
    }

    @Override
    protected void elementClicked(final int index, final boolean par2, final int par3, final int par4) {
        this.selectedIndex = index;
    }

    @Override
    protected boolean isSelected(final int index) {
        return index == this.selectedIndex;
    }

    @Override
    protected void drawBackground() {
    }

    @Override
    protected void drawContainerBackground(final Tessellator tessellator) {
    }

    @Override
    protected int getScrollBarX() {
        return this.width / 2 + getListWidth() / 2 + 2;
    }

    @Override
    protected void drawSlot(final int index, final int x, final int y, final int par4, final int mouseX, final int mouseY, final float partialTicks) {
        final BlockList.WrappedItemStack wrappedItemStack = this.guiInventoryCalculator.blockList.get(index);
        final ItemStack itemStack = wrappedItemStack.itemStack;

        final String itemName = wrappedItemStack.getItemStackDisplayName();
        final String amount = wrappedItemStack.getFormattedAmount();
        final String amountMissing = wrappedItemStack.getFormattedAmountMissing(strMaterialAvailable, strMaterialMissing);

        GuiHelper.drawItemStackWithSlot(this.minecraft.renderEngine, itemStack, x, y);

        this.guiInventoryCalculator.drawString(this.minecraft.fontRenderer, itemName, x + 24, y + 6, 0xFFFFFF);
        this.guiInventoryCalculator.drawString(this.minecraft.fontRenderer, amount, x + 215 - this.minecraft.fontRenderer.getStringWidth(amount), y + 1, 0xFFFFFF);
        this.guiInventoryCalculator.drawString(this.minecraft.fontRenderer, amountMissing, x + 215 - this.minecraft.fontRenderer.getStringWidth(amountMissing), y + 11, 0xFFFFFF);

        if (mouseX > x && mouseY > y && mouseX <= x + 18 && mouseY <= y + 18) {
            this.guiInventoryCalculator.renderToolTip(itemStack, mouseX, mouseY);
            GlStateManager.disableLighting();
        }
    }
}
