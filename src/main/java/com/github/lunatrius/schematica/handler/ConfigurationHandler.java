package com.github.lunatrius.schematica.handler;

import com.github.lunatrius.schematica.Schematica;
import com.github.lunatrius.schematica.reference.Names;
import com.github.lunatrius.schematica.reference.Reference;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class ConfigurationHandler {
    public static final ConfigurationHandler INSTANCE = new ConfigurationHandler();

    public static final String VERSION = "1";

    public static Configuration configuration;

    public static final boolean DUMP_BLOCK_LIST_DEFAULT = false;
    public static final boolean SHOW_DEBUG_INFO_DEFAULT = true;
    public static final boolean ENABLE_ALPHA_DEFAULT = false;
    public static final double ALPHA_DEFAULT = 1.0;
    public static final boolean HIGHLIGHT_DEFAULT = true;
    public static final boolean HIGHLIGHT_AIR_DEFAULT = true;
    public static final double BLOCK_DELTA_DEFAULT = 0.005;
    public static final int RENDER_DISTANCE_DEFAULT = 8;

    public static final String EXTRA_BLOCK_DEFAULT = "0x1FBF00BF";
    public static final String WRONG_BLOCK_DEFAULT = "0x1FFF0000";
    public static final String WRONG_META_DEFAULT = "0x1FBF5F00";
    public static final String MISSING_BLOCK_DEFAULT = "0x1F00BFFF";
    public static final String IN_INVENTORY_DEFAULT = "0x3F00FFC8";
    public static final String PLACEABLE_DEFAULT = "0x4A00FF00";
    public static final String OPTIMAL_DEFAULT = "0x3FD883FC";
    public static final String OPTIMAL_PLACEABLE_DEFAULT = "0x4A9D00E0";

    public static final int PLACE_SPEED_DEFAULT = 0;
    public static final int BREAK_PAUSE_DEFAULT = 25;
    public static final int SWAP_DELAY_DEFAULT = 5;
    public static final int TIMEOUT_DEFAULT = 10;
    public static final int PLACE_DISTANCE_DEFAULT = 5;
    public static final boolean PLACE_INSTANTLY_DEFAULT = false;
    public static final boolean DESTROY_BLOCKS_DEFAULT = false;
    public static final boolean DESTROY_INSTANTLY_DEFAULT = false;
    public static final boolean PLACE_ADJACENT_DEFAULT = true;
    public static final boolean PRINT_NOOBLINE_DEFAULT = false;
    public static final boolean NO_GHOST_BLOCKS_DEFAULT = false;
    public static final boolean PREDICT_PLACE_DEFAULT = false;
    public static final boolean LIQUID_PLACE_DEFAULT = true;
    public static final boolean[] SWAP_SLOTS_DEFAULT = new boolean[] {
            false, false, false, false, false, true, true, true, true
    };
    public static final float NUKER_RANGE_DEFAULT = 5.0f;
    public static final int NUKER_TIMEOUT_DEFAULT = 1000;
    public static final boolean NUKER_FLATTEN_DEFAULT = false;
    public static final float NUKER_MINE_SPEED_DEFAULT = 0.2f;
    public static final String NUKER_MODE_DEFAULT = NukerMode.BLOCKS.name();
    public static final String SCHEMATIC_DIRECTORY_STR = "./schematics";
    public static final File SCHEMATIC_DIRECTORY_DEFAULT = new File(Schematica.proxy.getDataDirectory(), SCHEMATIC_DIRECTORY_STR);
    public static final String[] EXTRA_AIR_BLOCKS_DEFAULT = {};
    public static final String SORT_TYPE_DEFAULT = "";
    public static final boolean PRINTER_ENABLED_DEFAULT = true;
    public static final boolean SAVE_ENABLED_DEFAULT = true;
    public static final boolean LOAD_ENABLED_DEFAULT = true;
    public static final int PLAYER_QUOTA_KILOBYTES_DEFAULT = 8192;
    public static final int INVENTORY_CALCULATOR_RANGE_DEFAULT = 5;
    public static final boolean AUTO_ALIGN_DEFAULT = false;

    public static boolean dumpBlockList = DUMP_BLOCK_LIST_DEFAULT;
    public static boolean showDebugInfo = SHOW_DEBUG_INFO_DEFAULT;
    public static boolean enableAlpha = ENABLE_ALPHA_DEFAULT;
    public static float alpha = (float) ALPHA_DEFAULT;
    public static boolean highlight = HIGHLIGHT_DEFAULT;
    public static boolean highlightAir = HIGHLIGHT_AIR_DEFAULT;
    public static double blockDelta = BLOCK_DELTA_DEFAULT;
    public static int renderDistance = RENDER_DISTANCE_DEFAULT;
    public static String extraBlockColor = EXTRA_BLOCK_DEFAULT;
    public static String wrongBlockColor = WRONG_BLOCK_DEFAULT;
    public static String wrongMetaColor = WRONG_META_DEFAULT;
    public static String missingBlockColor = MISSING_BLOCK_DEFAULT;
    public static String inInvColor = IN_INVENTORY_DEFAULT;
    public static String placeableBlockColor = PLACEABLE_DEFAULT;
    public static String optimalBlockColor = OPTIMAL_DEFAULT;
    public static String optimalPlaceableColor = OPTIMAL_PLACEABLE_DEFAULT;
    public static int placeSpeed = PLACE_SPEED_DEFAULT;
    public static int breakPause = BREAK_PAUSE_DEFAULT;
    public static int swapDelay = SWAP_DELAY_DEFAULT;
    public static int timeout = TIMEOUT_DEFAULT;
    public static int placeDistance = PLACE_DISTANCE_DEFAULT;
    public static boolean placeInstantly = PLACE_INSTANTLY_DEFAULT;
    public static boolean destroyBlocks = DESTROY_BLOCKS_DEFAULT;
    public static boolean destroyInstantly = DESTROY_INSTANTLY_DEFAULT;
    public static boolean placeAdjacent = PLACE_ADJACENT_DEFAULT;
    public static boolean printNoobline = PRINT_NOOBLINE_DEFAULT;
    public static boolean noGhostBlocks = NO_GHOST_BLOCKS_DEFAULT;
    public static boolean predictPlace = PREDICT_PLACE_DEFAULT;
    public static boolean liquidPlace = LIQUID_PLACE_DEFAULT;
    public static boolean[] swapSlots = Arrays.copyOf(SWAP_SLOTS_DEFAULT, SWAP_SLOTS_DEFAULT.length);
    public static final Queue<Integer> swapSlotsQueue = new ArrayDeque<Integer>();
    public static float nukerRange = NUKER_RANGE_DEFAULT;
    public static int nukerTimeout = NUKER_TIMEOUT_DEFAULT;
    public static boolean nukerFlatten = NUKER_FLATTEN_DEFAULT;
    public static float nukerMineSpeed = NUKER_MINE_SPEED_DEFAULT;
    public static String nukerMode = NUKER_MODE_DEFAULT;

    public enum NukerMode {
        BLOCKS, AIR, BOTH;
    }

    public static String[] nukerModeValues = {NukerMode.BLOCKS.name(), NukerMode.AIR.name(), NukerMode.BOTH.name()};
    public static String[] nukerModeValuesDisplay = nukerModeValues; // TODO
    public static File schematicDirectory = SCHEMATIC_DIRECTORY_DEFAULT;
    public static String[] extraAirBlocks = Arrays.copyOf(EXTRA_AIR_BLOCKS_DEFAULT, EXTRA_AIR_BLOCKS_DEFAULT.length);
    public static String sortType = SORT_TYPE_DEFAULT;
    public static boolean printerEnabled = PRINTER_ENABLED_DEFAULT;
    public static boolean saveEnabled = SAVE_ENABLED_DEFAULT;
    public static boolean loadEnabled = LOAD_ENABLED_DEFAULT;
    public static int playerQuotaKilobytes = PLAYER_QUOTA_KILOBYTES_DEFAULT;
    public static int inventoryCalculatorRange = INVENTORY_CALCULATOR_RANGE_DEFAULT;
    public static boolean autoAlign = AUTO_ALIGN_DEFAULT;

    public static Property propDumpBlockList = null;
    public static Property propShowDebugInfo = null;
    public static Property propEnableAlpha = null;
    public static Property propAlpha = null;
    public static Property propHighlight = null;
    public static Property propHighlightAir = null;
    public static Property propBlockDelta = null;
    public static Property propRenderDistance = null;
    public static Property propExtraBlockColor = null;
    public static Property propWrongBlockColor = null;
    public static Property propWrongMetaColor = null;
    public static Property propMissingBlockColor = null;
    public static Property propInInvColor = null;
    public static Property propPlaceableBlockColor = null;
    public static Property propOptimalBlockColor = null;
    public static Property propOptimalPlaceableColor = null;
    public static Property propPlaceSpeed = null;
    public static Property propBreakPause = null;
    public static Property propSwapDelay = null;
    public static Property propTimeout = null;
    public static Property propPlaceDistance = null;
    public static Property propPlaceInstantly = null;
    public static Property propDestroyBlocks = null;
    public static Property propDestroyInstantly = null;
    public static Property propPlaceAdjacent = null;
    public static Property propPrintNoobline = null;
    public static Property propNoGhostBlocks = null;
    public static Property propPredictPlace = null;
    public static Property propLiquidPlace = null;
    public static Property[] propSwapSlots = new Property[SWAP_SLOTS_DEFAULT.length];
    public static Property propNukerRange = null;
    public static Property propNukerTimeout = null;
    public static Property propNukerFlatten = null;
    public static Property propNukerMineSpeed = null;
    public static Property propNukerMode = null;
    public static Property propSchematicDirectory = null;
    public static Property propExtraAirBlocks = null;
    public static Property propSortType = null;
    public static Property propPrinterEnabled = null;
    public static Property propSaveEnabled = null;
    public static Property propLoadEnabled = null;
    public static Property propPlayerQuotaKilobytes = null;
    public static Property propInventoryCalcRange = null;
    public static Property propAutoAlign = null;

    private static final Set<Block> extraAirBlockList = new HashSet<Block>();

    public static void init(final File configFile) {
        if (configuration == null) {
            configuration = new Configuration(configFile, VERSION);
            loadConfiguration();
        }
    }

    public static void loadConfiguration() {
        loadConfigurationDebug();
        loadConfigurationRender();
        loadConfigurationPrinter();
        loadConfigurationSwapSlots();
        loadConfigurationNuker();
        loadConfigurationGeneral();
        loadConfigurationServer();

        Schematica.proxy.createFolders();

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    private static void loadConfigurationDebug() {
        propDumpBlockList = configuration.get(Names.Config.Category.DEBUG, Names.Config.DUMP_BLOCK_LIST, DUMP_BLOCK_LIST_DEFAULT, Names.Config.DUMP_BLOCK_LIST_DESC);
        propDumpBlockList.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.DUMP_BLOCK_LIST);
        propDumpBlockList.requiresMcRestart();
        dumpBlockList = propDumpBlockList.getBoolean(DUMP_BLOCK_LIST_DEFAULT);

        propShowDebugInfo = configuration.get(Names.Config.Category.DEBUG, Names.Config.SHOW_DEBUG_INFO, SHOW_DEBUG_INFO_DEFAULT, Names.Config.SHOW_DEBUG_INFO_DESC);
        propShowDebugInfo.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.SHOW_DEBUG_INFO);
        showDebugInfo = propShowDebugInfo.getBoolean(SHOW_DEBUG_INFO_DEFAULT);
    }

    private static void loadConfigurationRender() {
        propEnableAlpha = configuration.get(Names.Config.Category.RENDER, Names.Config.ALPHA_ENABLED, ENABLE_ALPHA_DEFAULT, Names.Config.ALPHA_ENABLED_DESC);
        propEnableAlpha.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.ALPHA_ENABLED);
        enableAlpha = propEnableAlpha.getBoolean(ENABLE_ALPHA_DEFAULT);

        propAlpha = configuration.get(Names.Config.Category.RENDER, Names.Config.ALPHA, ALPHA_DEFAULT, Names.Config.ALPHA_DESC, 0.0, 1.0);
        propAlpha.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.ALPHA);
        alpha = (float) propAlpha.getDouble(ALPHA_DEFAULT);

        propHighlight = configuration.get(Names.Config.Category.RENDER, Names.Config.HIGHLIGHT, HIGHLIGHT_DEFAULT, Names.Config.HIGHLIGHT_DESC);
        propHighlight.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.HIGHLIGHT);
        highlight = propHighlight.getBoolean(HIGHLIGHT_DEFAULT);

        propHighlightAir = configuration.get(Names.Config.Category.RENDER, Names.Config.HIGHLIGHT_AIR, HIGHLIGHT_AIR_DEFAULT, Names.Config.HIGHLIGHT_AIR_DESC);
        propHighlightAir.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.HIGHLIGHT_AIR);
        highlightAir = propHighlightAir.getBoolean(HIGHLIGHT_AIR_DEFAULT);

        propBlockDelta = configuration.get(Names.Config.Category.RENDER, Names.Config.BLOCK_DELTA, BLOCK_DELTA_DEFAULT, Names.Config.BLOCK_DELTA_DESC, 0.0, 0.2);
        propBlockDelta.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.BLOCK_DELTA);
        blockDelta = propBlockDelta.getDouble(BLOCK_DELTA_DEFAULT);

        propRenderDistance = configuration.get(Names.Config.Category.RENDER, Names.Config.RENDER_DISTANCE, RENDER_DISTANCE_DEFAULT, Names.Config.RENDER_DISTANCE_DESC, 2, 16);
        propRenderDistance.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.RENDER_DISTANCE);
        renderDistance = propRenderDistance.getInt(RENDER_DISTANCE_DEFAULT);

        propExtraBlockColor = configuration.get(Names.Config.Category.RENDER, Names.Config.EXTRA_BLOCK, EXTRA_BLOCK_DEFAULT, Names.Config.EXTRA_BLOCK_DESC, Property.Type.COLOR);
        propExtraBlockColor.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.EXTRA_BLOCK);
        extraBlockColor = propExtraBlockColor.getString();

        propWrongBlockColor = configuration.get(Names.Config.Category.RENDER, Names.Config.WRONG_BLOCK, WRONG_BLOCK_DEFAULT, Names.Config.WRONG_BLOCK_DESC, Property.Type.COLOR);
        propWrongBlockColor.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.WRONG_BLOCK);
        wrongBlockColor = propWrongBlockColor.getString();

        propWrongMetaColor = configuration.get(Names.Config.Category.RENDER, Names.Config.WRONG_META, WRONG_META_DEFAULT, Names.Config.WRONG_META_DESC, Property.Type.COLOR);
        propWrongMetaColor.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.WRONG_META);
        wrongMetaColor = propWrongMetaColor.getString();

        propMissingBlockColor = configuration.get(Names.Config.Category.RENDER, Names.Config.MISSING_BLOCK, MISSING_BLOCK_DEFAULT, Names.Config.MISSING_BLOCK_DESC, Property.Type.COLOR);
        propMissingBlockColor.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.MISSING_BLOCK);
        missingBlockColor = propMissingBlockColor.getString();

        propInInvColor = configuration.get(Names.Config.Category.RENDER, Names.Config.IN_INVENTORY, IN_INVENTORY_DEFAULT, Names.Config.IN_INVENTORY_DESC, Property.Type.COLOR);
        propInInvColor.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.IN_INVENTORY);
        inInvColor = propInInvColor.getString();

        propPlaceableBlockColor = configuration.get(Names.Config.Category.RENDER, Names.Config.PLACEABLE, PLACEABLE_DEFAULT, Names.Config.PLACEABLE_DESC, Property.Type.COLOR);
        propPlaceableBlockColor.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.PLACEABLE);
        placeableBlockColor = propPlaceableBlockColor.getString();

        propOptimalBlockColor = configuration.get(Names.Config.Category.RENDER, Names.Config.OPTIMAL, OPTIMAL_DEFAULT, Names.Config.OPTIMAL_DESC, Property.Type.COLOR);
        propOptimalBlockColor.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.OPTIMAL);
        optimalBlockColor = propOptimalBlockColor.getString();

        propOptimalPlaceableColor = configuration.get(Names.Config.Category.RENDER, Names.Config.OPTIMAL_PLACEABLE, OPTIMAL_PLACEABLE_DEFAULT, Names.Config.OPTIMAL_PLACEABLE_DESC, Property.Type.COLOR);
        propOptimalPlaceableColor.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.OPTIMAL_PLACEABLE);
        optimalPlaceableColor = propOptimalPlaceableColor.getString();
    }

    private static void loadConfigurationPrinter() {
        propPlaceSpeed = configuration.get(Names.Config.Category.PRINTER, Names.Config.PLACE_SPEED, PLACE_SPEED_DEFAULT, Names.Config.PLACE_SPEED_DESC);
        propPlaceSpeed.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.PLACE_SPEED);
        placeSpeed = propPlaceSpeed.getInt(PLACE_SPEED_DEFAULT);

        propBreakPause = configuration.get(Names.Config.Category.PRINTER, Names.Config.BREAK_PAUSE, BREAK_PAUSE_DEFAULT, Names.Config.BREAK_PAUSE_DESC);
        propBreakPause.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.BREAK_PAUSE);
        breakPause = propBreakPause.getInt(BREAK_PAUSE_DEFAULT);

        propSwapDelay = configuration.get(Names.Config.Category.PRINTER, Names.Config.SWAP_DELAY, SWAP_DELAY_DEFAULT, Names.Config.SWAP_DELAY_DESC);
        propSwapDelay.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.SWAP_DELAY);
        swapDelay = propSwapDelay.getInt(SWAP_DELAY_DEFAULT);

        propTimeout = configuration.get(Names.Config.Category.PRINTER, Names.Config.TIMEOUT, TIMEOUT_DEFAULT, Names.Config.TIMEOUT_DESC, 0, 100);
        propTimeout.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.TIMEOUT);
        timeout = propTimeout.getInt(TIMEOUT_DEFAULT);

        propPlaceDistance = configuration.get(Names.Config.Category.PRINTER, Names.Config.PLACE_DISTANCE, PLACE_DISTANCE_DEFAULT, Names.Config.PLACE_DISTANCE_DESC, 1, 10);
        propPlaceDistance.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.PLACE_DISTANCE);
        placeDistance = propPlaceDistance.getInt(PLACE_DISTANCE_DEFAULT);

        propPlaceInstantly = configuration.get(Names.Config.Category.PRINTER, Names.Config.PLACE_INSTANTLY, PLACE_INSTANTLY_DEFAULT, Names.Config.PLACE_INSTANTLY_DESC);
        propPlaceInstantly.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.PLACE_INSTANTLY);
        placeInstantly = propPlaceInstantly.getBoolean(PLACE_INSTANTLY_DEFAULT);

        propDestroyBlocks = configuration.get(Names.Config.Category.PRINTER, Names.Config.DESTROY_BLOCKS, DESTROY_BLOCKS_DEFAULT, Names.Config.DESTROY_BLOCKS_DESC);
        propDestroyBlocks.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.DESTROY_BLOCKS);
        destroyBlocks = propDestroyBlocks.getBoolean(DESTROY_BLOCKS_DEFAULT);

        propDestroyInstantly = configuration.get(Names.Config.Category.PRINTER, Names.Config.DESTROY_INSTANTLY, DESTROY_INSTANTLY_DEFAULT, Names.Config.DESTROY_INSTANTLY_DESC);
        propDestroyInstantly.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.DESTROY_INSTANTLY);
        destroyInstantly = propDestroyInstantly.getBoolean(DESTROY_INSTANTLY_DEFAULT);

        propPlaceAdjacent = configuration.get(Names.Config.Category.PRINTER, Names.Config.PLACE_ADJACENT, PLACE_ADJACENT_DEFAULT, Names.Config.PLACE_ADJACENT_DESC);
        propPlaceAdjacent.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.PLACE_ADJACENT);
        placeAdjacent = propPlaceAdjacent.getBoolean(PLACE_ADJACENT_DEFAULT);

        propPrintNoobline = configuration.get(Names.Config.Category.PRINTER, Names.Config.PRINT_NOOBLINE, PRINT_NOOBLINE_DEFAULT, Names.Config.PRINT_NOOBLINE_DESC);
        propPrintNoobline.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.PRINT_NOOBLINE);
        printNoobline = propPrintNoobline.getBoolean(PRINT_NOOBLINE_DEFAULT);

        propNoGhostBlocks = configuration.get(Names.Config.Category.PRINTER, Names.Config.NO_GHOST_BLOCKS, NO_GHOST_BLOCKS_DEFAULT, Names.Config.NO_GHOST_BLOCKS_DESC);
        propNoGhostBlocks.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.NO_GHOST_BLOCKS);
        noGhostBlocks = propNoGhostBlocks.getBoolean(NO_GHOST_BLOCKS_DEFAULT);

        propPredictPlace = configuration.get(Names.Config.Category.PRINTER, Names.Config.PREDICT_PLACE, PREDICT_PLACE_DEFAULT, Names.Config.PREDICT_PLACE_DESC);
        propPredictPlace.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.PREDICT_PLACE);
        predictPlace = propPredictPlace.getBoolean(PREDICT_PLACE_DEFAULT);

        propLiquidPlace = configuration.get(Names.Config.Category.PRINTER, Names.Config.LIQUID_PLACE, LIQUID_PLACE_DEFAULT, Names.Config.LIQUID_PLACE_DESC);
        propLiquidPlace.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.LIQUID_PLACE);
        liquidPlace = propLiquidPlace.getBoolean();
    }

    private static void loadConfigurationNuker () {
        propNukerRange = configuration.get(Names.Config.Category.NUKER, Names.Config.NUKER_RANGE, NUKER_RANGE_DEFAULT, Names.Config.NUKER_RANGE_DESC);
        propNukerRange.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.NUKER_RANGE);
        nukerRange = (float) propNukerRange.getDouble(NUKER_RANGE_DEFAULT);

        propNukerTimeout = configuration.get(Names.Config.Category.NUKER, Names.Config.NUKER_TIMEOUT, NUKER_TIMEOUT_DEFAULT, Names.Config.NUKER_TIMEOUT_DESC);
        propNukerTimeout.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.NUKER_TIMEOUT);
        nukerTimeout = propNukerTimeout.getInt(NUKER_TIMEOUT_DEFAULT);

        propNukerFlatten = configuration.get(Names.Config.Category.NUKER, Names.Config.NUKER_FLATTEN, NUKER_FLATTEN_DEFAULT, Names.Config.NUKER_FLATTEN_DESC);
        propNukerFlatten.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.NUKER_FLATTEN);
        nukerFlatten = propNukerFlatten.getBoolean(NUKER_FLATTEN_DEFAULT);

        propNukerMineSpeed = configuration.get(Names.Config.Category.NUKER, Names.Config.NUKER_MINE_SPEED, NUKER_MINE_SPEED_DEFAULT, Names.Config.NUKER_MINE_SPEED_DESC);
        propNukerMineSpeed.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.NUKER_MINE_SPEED);
        nukerMineSpeed = (float) propNukerMineSpeed.getDouble(NUKER_MINE_SPEED_DEFAULT);

        propNukerMode = configuration.get(Names.Config.Category.NUKER, Names.Config.NUKER_MODE, NUKER_MODE_DEFAULT, Names.Config.NUKER_MODE_DESC, nukerModeValues, nukerModeValuesDisplay); // TODO: Make this work with locales
        propNukerMode.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.NUKER_MODE);
        nukerMode = propNukerMode.getString();
    }

    private static void loadConfigurationSwapSlots() {
        swapSlotsQueue.clear();
        for (int i = 0; i < SWAP_SLOTS_DEFAULT.length; i++) {
            propSwapSlots[i] = configuration.get(Names.Config.Category.PRINTER_SWAPSLOTS, Names.Config.SWAP_SLOT + i, SWAP_SLOTS_DEFAULT[i], Names.Config.SWAP_SLOT_DESC);
            propSwapSlots[i].setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.SWAP_SLOT + i);
            swapSlots[i] = propSwapSlots[i].getBoolean(SWAP_SLOTS_DEFAULT[i]);

            if (swapSlots[i]) {
                swapSlotsQueue.offer(i);
            }
        }
    }

    private static void loadConfigurationGeneral() {
        propSchematicDirectory = configuration.get(Names.Config.Category.GENERAL, Names.Config.SCHEMATIC_DIRECTORY, SCHEMATIC_DIRECTORY_STR, Names.Config.SCHEMATIC_DIRECTORY_DESC);
        propSchematicDirectory.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.SCHEMATIC_DIRECTORY);
        schematicDirectory = getSchematicDirectoryFile(propSchematicDirectory.getString());

        propExtraAirBlocks = configuration.get(Names.Config.Category.GENERAL, Names.Config.EXTRA_AIR_BLOCKS, EXTRA_AIR_BLOCKS_DEFAULT, Names.Config.EXTRA_AIR_BLOCKS_DESC);
        propExtraAirBlocks.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.EXTRA_AIR_BLOCKS);
        extraAirBlocks = propExtraAirBlocks.getStringList();

        propSortType = configuration.get(Names.Config.Category.GENERAL, Names.Config.SORT_TYPE, SORT_TYPE_DEFAULT, Names.Config.SORT_TYPE_DESC);
        propSortType.setShowInGui(false);
        sortType = propSortType.getString();

        propInventoryCalcRange = configuration.get(Names.Config.Category.GENERAL, Names.Config.INV_CALC_RANGE, INVENTORY_CALCULATOR_RANGE_DEFAULT, Names.Config.INV_CALC_RANGE_DESC, 0, 25);
        propInventoryCalcRange.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.INV_CALC_RANGE);
        inventoryCalculatorRange = propInventoryCalcRange.getInt();

        propAutoAlign = configuration.get(Names.Config.Category.GENERAL, Names.Config.AUTO_ALIGN, AUTO_ALIGN_DEFAULT, Names.Config.AUTO_ALIGN_DESC);
        propAutoAlign.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.AUTO_ALIGN);
        autoAlign = propAutoAlign.getBoolean();

        normalizeSchematicPath();
        populateExtraAirBlocks();
    }

    private static File getSchematicDirectoryFile(String path) {
        if (path.startsWith(".")) {
            return Schematica.proxy.getDirectory(path);
        }

        return new File(path);
    }

    private static void normalizeSchematicPath() {
        try {
            schematicDirectory = schematicDirectory.getCanonicalFile();
            final String schematicPath = schematicDirectory.getAbsolutePath();
            final String dataPath = Schematica.proxy.getDataDirectory().getAbsolutePath();
            final String newSchematicPath = mergePaths(schematicPath, dataPath);
            propSchematicDirectory.set(newSchematicPath);
            Reference.logger.debug("Schematic path: {}", schematicPath);
            Reference.logger.debug("Data path: {}", dataPath);
            Reference.logger.debug("New schematic path: {}", newSchematicPath);
        } catch (final IOException e) {
            Reference.logger.warn("Could not canonize path!", e);
        }
    }

    private static String mergePaths(final String schematicPath, final String dataPath) {
        final String newPath;
        if (schematicPath.startsWith(dataPath)) {
            newPath = "." + schematicPath.substring(dataPath.length());
        } else {
            newPath = schematicPath;
        }

        return newPath.replace("\\", "/");
    }

    private static void populateExtraAirBlocks() {
        extraAirBlockList.clear();
        for (final String name : extraAirBlocks) {
            final Block block = Block.REGISTRY.getObject(new ResourceLocation(name));
            if (block != Blocks.AIR) {
                extraAirBlockList.add(block);
            }
        }
    }

    private static void loadConfigurationServer() {
        propPrinterEnabled = configuration.get(Names.Config.Category.SERVER, Names.Config.PRINTER_ENABLED, PRINTER_ENABLED_DEFAULT, Names.Config.PRINTER_ENABLED_DESC);
        propPrinterEnabled.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.PRINTER_ENABLED);
        printerEnabled = propPrinterEnabled.getBoolean(PRINTER_ENABLED_DEFAULT);

        propSaveEnabled = configuration.get(Names.Config.Category.SERVER, Names.Config.SAVE_ENABLED, SAVE_ENABLED_DEFAULT, Names.Config.SAVE_ENABLED_DESC);
        propSaveEnabled.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.SAVE_ENABLED);
        saveEnabled = propSaveEnabled.getBoolean(SAVE_ENABLED_DEFAULT);

        propLoadEnabled = configuration.get(Names.Config.Category.SERVER, Names.Config.LOAD_ENABLED, LOAD_ENABLED_DEFAULT, Names.Config.LOAD_ENABLED_DESC);
        propLoadEnabled.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.LOAD_ENABLED);
        loadEnabled = propLoadEnabled.getBoolean(LOAD_ENABLED_DEFAULT);

        propPlayerQuotaKilobytes = configuration.get(Names.Config.Category.SERVER, Names.Config.PLAYER_QUOTA_KILOBYTES, PLAYER_QUOTA_KILOBYTES_DEFAULT, Names.Config.PLAYER_QUOTA_KILOBYTES_DESC);
        propPlayerQuotaKilobytes.setLanguageKey(Names.Config.LANG_PREFIX + "." + Names.Config.PLAYER_QUOTA_KILOBYTES);
        playerQuotaKilobytes = propPlayerQuotaKilobytes.getInt(PLAYER_QUOTA_KILOBYTES_DEFAULT);
    }

    private ConfigurationHandler() {}

    @SubscribeEvent
    public void onConfigurationChangedEvent(final ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equalsIgnoreCase(Reference.MODID)) {
            loadConfiguration();
        }
    }

    public static boolean isExtraAirBlock(final Block block) {
        return extraAirBlockList.contains(block);
    }
}
