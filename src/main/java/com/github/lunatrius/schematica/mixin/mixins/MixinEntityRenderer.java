package com.github.lunatrius.schematica.mixin.mixins;

import com.github.lunatrius.core.client.gui.GuiHelper;
import com.github.lunatrius.schematica.client.gui.inventorycalc.GuiInventoryCalculator;
import com.github.lunatrius.schematica.client.inventorycalculator.InventoryCalculator;
import com.github.lunatrius.schematica.client.util.BlockList;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.github.lunatrius.schematica.reference.Names;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO: Events :-(
@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {
    @Shadow
    Minecraft mc;

    @Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(F)V"))
    public void updateCameraAndRender (float partialTicks, long nanoTime, CallbackInfo callback) {
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
}
