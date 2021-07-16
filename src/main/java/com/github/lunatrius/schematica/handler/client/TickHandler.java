package com.github.lunatrius.schematica.handler.client;

import com.github.lunatrius.schematica.Schematica;
import com.github.lunatrius.schematica.client.printer.SchematicPrinter;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.github.lunatrius.schematica.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.Sys;

public class TickHandler {
    public static final TickHandler INSTANCE = new TickHandler();

    private final Minecraft minecraft = Minecraft.getMinecraft();

    private long lastPlaceTime = 0;
    private long lastBreakTime = 0;

    private TickHandler() {}

    @SubscribeEvent
    public void onClientConnect(final FMLNetworkEvent.ClientConnectedToServerEvent event) {
        /* TODO: is this still needed?
        Reference.logger.info("Scheduling client settings reset.");
        ClientProxy.isPendingReset = true;
        */
    }

    @SubscribeEvent
    public void onClientDisconnect(final FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Reference.logger.info("Scheduling client settings reset.");
        ClientProxy.isPendingReset = true;
    }

    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        if (this.minecraft.isGamePaused() || event.phase != TickEvent.Phase.END) {
            return;
        }

        if (ClientProxy.isPendingReset) {
            Schematica.proxy.resetSettings();
            ClientProxy.isPendingReset = false;
            Reference.logger.info("Client settings have been reset.");
        }
    }

    /** Called from {@link com.github.lunatrius.schematica.mixin.mixins.MixinEntityPlayerSP} */
    // TODO: Get an actual event system working
    public void onUpdateWalkingPlayer () {
        final SchematicPrinter printer = SchematicPrinter.INSTANCE;

        if (minecraft.isGamePaused() || !printer.isEnabled() || printer.forceDisable || !printer.isPrinting()
        || ((System.nanoTime() - lastBreakTime) / 1000000L) < ConfigurationHandler.breakPause) {
            return;
        }

        final WorldClient world = this.minecraft.world;
        final EntityPlayerSP player = this.minecraft.player;
        final SchematicWorld schematic = ClientProxy.schematic;
        if (world != null && player != null && schematic != null && schematic.isRendering) {
            this.minecraft.profiler.startSection("printer");

            // Make sure the time passed since the last place is greater than 1 second divided by the blocks placed per second
            if ((ConfigurationHandler.placeSpeed == 0) || (System.nanoTime() - lastPlaceTime >= 1000000000L / ConfigurationHandler.placeSpeed)) {
                this.lastPlaceTime = System.nanoTime();

                printer.print(world, player);
            }

            this.minecraft.profiler.endSection();
        }
    }

    // TODO: Get an actual event system working
    public void onBreakBlock () {
        lastBreakTime = System.nanoTime();
    }
}
