package com.github.lunatrius.schematica.client.printer;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.server.SPacketConfirmTransaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manages printer's inventory management in order to eliminate client server inventory desync.
 * This works by not actually moving items on the client until we get the SPacketConfirmTransaction
 * back from the server so that we know that the item is safe to move on our client. This should eliminate
 * all misplaced blocks.
 *
 * @author Old Chum
 * @since 02/18/2021
 */
public class SlotManager {
    public static SlotManager INSTANCE = new SlotManager();

    /**
     * A list of ClickWindow packets that have been sent by this manager.
     * Used to track which packets should be removed when we receive a confirm transaction.
     */
    List<CPacketClickWindow> transactionList = new ArrayList<>();

    /**
     * Stores which slots in the hotbar the printer can currently use.
     * If a slot is false in this array it means that we are waiting for the confirm transaction from the server for that slot.
     */
    // TODO: Update this to a list of times and periodically reset them to prevent hotbar lockout
    private boolean[] usableSlots = new boolean[]{true, true, true, true, true, true, true, true, true};

    /** Stores the inv slots that are currently pending so that printer does not try to move the same item multiple times */
    private List<Integer> blockedInvSlots = new ArrayList<>();

    /** Sends the window click packet without executing the swap on the client and adds the packet to the transactionList */
    public ItemStack swapSlots(int windowId, int slotId, int mouseButton, ClickType type, EntityPlayer player) {
        short short1 = player.openContainer.getNextTransactionID(player.inventory);

        // We can use ItemStack.EMPTY because the stack does not need to be checked when the click type is SWAP
        CPacketClickWindow clickPacket = new CPacketClickWindow(windowId, slotId, mouseButton, type, ItemStack.EMPTY, short1);

        Minecraft.getMinecraft().getConnection().sendPacket(clickPacket);
        this.transactionList.add(clickPacket);
        this.blockedInvSlots.add(slotId);
        updateUsableSlots();

        return ItemStack.EMPTY;
    }

    /**
     * Updates the transactionList to ensure that it only holds pending transactions.
     */
    public void onReceivePacket(Packet<?> packet) {
        if (packet instanceof SPacketConfirmTransaction) {
            SPacketConfirmTransaction sPacket = (SPacketConfirmTransaction) packet;
            CPacketClickWindow clickPacket = null;

            // Find the packet with the same id as the confirmation
            for (CPacketClickWindow cPacketClickWindow : transactionList) {
                if (cPacketClickWindow.getActionNumber() == sPacket.getActionNumber()) {
                    clickPacket = cPacketClickWindow;
                    break;
                }
            }

            // If clickPacket is null we know that the incoming transaction id is not a swap
            if (clickPacket != null) {
                // Delete all packets that were added before this packet (if any) because the packets should come in order
                // If they don't come in order we have to assume that the packets that were sent before are missing
                boolean removedTransaction = false;
                while (!removedTransaction) {
                    if (this.transactionList.get(0) == clickPacket) {
                        removedTransaction = true;
                    }

                    this.blockedInvSlots.remove(Integer.valueOf(this.transactionList.get(0).getSlotId()));
                    this.transactionList.remove(0);
                }

                updateUsableSlots();
                Minecraft.getMinecraft().player.openContainer.slotClick(clickPacket.getSlotId(), clickPacket.getUsedButton(), clickPacket.getClickType(), Minecraft.getMinecraft().player);
            }
        }
    }

    /** Searches through the current transactionList for which slots are currently waiting for a slot change */
    private void updateUsableSlots () {
        // Reset the usable slots
        Arrays.fill(usableSlots, true);

        // Search through the pending transaction list and update the hotbar slots
        for (CPacketClickWindow packet : this.transactionList) {
            // Hotbar slots are stored as 0-8 in CPacketClickWindow
            if (packet.getUsedButton() >= 0 && packet.getUsedButton() <= 8) {
                this.usableSlots[packet.getUsedButton()] = false;
            }
        }

        System.out.println("Update Usable Slots: " + Arrays.toString(this.usableSlots));
    }

    public boolean[] getUsableSlots() {
        return usableSlots;
    }

    public List<Integer> getBlockedInvSlots() {
        return blockedInvSlots;
    }
}
