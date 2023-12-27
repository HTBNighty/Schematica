package com.github.lunatrius.schematica.reference;

@SuppressWarnings("HardCodedStringLiteral")
public final class Names {
    public static final class Config {
        public static final class Category {
            public static final String DEBUG = "debug";
            public static final String RENDER = "render";
            public static final String PRINTER = "printer";
            public static final String PRINTER_SWAPSLOTS = "printer.swapslots";
            public static final String NUKER = "nuker";
            public static final String GENERAL = "general";
            public static final String SERVER = "server";
        }

        public static final String DUMP_BLOCK_LIST = "dumpBlockList";
        public static final String DUMP_BLOCK_LIST_DESC = "Dump all block states on startup.";
        public static final String SHOW_DEBUG_INFO = "showDebugInfo";
        public static final String SHOW_DEBUG_INFO_DESC = "Display extra information on the debug screen (F3).";

        public static final String ALPHA_ENABLED = "alphaEnabled";
        public static final String ALPHA_ENABLED_DESC = "Enable transparent textures.";
        public static final String ALPHA = "alpha";
        public static final String ALPHA_DESC = "Alpha value used when rendering the schematic (1.0 = opaque, 0.5 = half transparent, 0.0 = transparent).";
        public static final String HIGHLIGHT = "highlight";
        public static final String HIGHLIGHT_DESC = "Highlight invalid placed blocks and to be placed blocks.";
        public static final String HIGHLIGHT_AIR = "highlightAir";
        public static final String HIGHLIGHT_AIR_DESC = "Highlight blocks that should be air.";
        public static final String BLOCK_DELTA = "blockDelta";
        public static final String BLOCK_DELTA_DESC = "Delta value used for highlighting (if you experience z-fighting increase this).";
        public static final String RENDER_DISTANCE = "renderDistance";
        public static final String RENDER_DISTANCE_DESC = "Schematic render distance.";
        public static final String EXTRA_BLOCK = "extraBlockColor";
        public static final String EXTRA_BLOCK_DESC = "A block that is present in the world but not the schematic";
        public static final String WRONG_BLOCK = "wrongBlockColor";
        public static final String WRONG_BLOCK_DESC = "A mismatch between the block in the world and the schematic";
        public static final String WRONG_META = "wrongMetaColor";
        public static final String WRONG_META_DESC = "A mismatch between the metadata for the block in the world and the schematic";
        public static final String MISSING_BLOCK = "missingBlockColor";
        public static final String MISSING_BLOCK_DESC = "A block that is present in the schematic but not in the world";
        public static final String IN_INVENTORY = "inInvColor";
        public static final String IN_INVENTORY_DESC = "A block that is in the player's inventory";
        public static final String PLACEABLE = "placeableBlockColor";
        public static final String PLACEABLE_DESC = "A block that is placeable and in the player's inventory";
        public static final String OPTIMAL = "optimalBlockColor";
        public static final String OPTIMAL_DESC = "A block that is in the current optimal inventory";
        public static final String OPTIMAL_PLACEABLE = "optimalPlaceableColor";
        public static final String OPTIMAL_PLACEABLE_DESC = "A block that is in the current optimal inventory and is placeable and is in the players inventory";

        public static final String PLACE_SPEED = "placeSpeed";
        public static final String PLACE_SPEED_DESC = "How fast to place blocks (in bps). 0 for unlimited.";
        public static final String BREAK_PAUSE = "breakPause";
        public static final String BREAK_PAUSE_DESC = "How long in ms printer should stop for after breaking a block";
        public static final String SWAP_DELAY = "swapDelay";
        public static final String SWAP_DELAY_DESC = "How long in ms printer should pause after moving an item to the hotbar";
        public static final String TIMEOUT = "timeout";
        public static final String TIMEOUT_DESC = "Timeout before re-trying failed blocks.";
        public static final String PLACE_DISTANCE = "placeDistance";
        public static final String PLACE_DISTANCE_DESC = "Maximum placement distance.";
        public static final String PLACE_INSTANTLY = "placeInstantly";
        public static final String PLACE_INSTANTLY_DESC = "Place all blocks that can be placed in one tick.";
        public static final String DESTROY_BLOCKS = "destroyBlocks";
        public static final String DESTROY_BLOCKS_DESC = "The printer will destroy blocks (creative mode only).";
        public static final String DESTROY_INSTANTLY = "destroyInstantly";
        public static final String DESTROY_INSTANTLY_DESC = "Destroy all blocks that can be destroyed in one tick.";
        public static final String PLACE_ADJACENT = "placeAdjacent";
        public static final String PLACE_ADJACENT_DESC = "Place blocks only if there is an adjacent block next to them.";
        public static final String PRINT_NOOBLINE = "printNoobline";
        public static final String PRINT_NOOBLINE_DESC = "If printer should place blocks on the noobline.";
        public static final String NO_GHOST_BLOCKS = "noGhostBlocks";
        public static final String NO_GHOST_BLOCKS_DESC = "If the client should wait for a response from the server before changing a block.";
        public static final String PREDICT_PLACE = "predictPlace";
        public static final String PREDICT_PLACE_DESC = "Speeds up printing by attempting to place blocks on blocks that have not been confirmed to have been placed successfully (Only works when NoGhostBlocks is enabled)";
        public static final String LIQUID_PLACE = "liquidPlace";
        public static final String LIQUID_PLACE_DESC = "Attempts to place blocks on top of liquid. Disabled in single player, only works on some servers. Might get you banned also.";
        public static final String SWAP_SLOT = "swapSlot";
        public static final String SWAP_SLOT_DESC = "Allow the printer to use this hotbar slot.";

        public static final String NUKER_RANGE = "nukerRange";
        public static final String NUKER_RANGE_DESC = "How far nuker will mine.";
        public static final String NUKER_TIMEOUT = "nukerTimeout";
        public static final String NUKER_TIMEOUT_DESC = "How long to wait (in ms) until trying to break a specific block again.";
        public static final String NUKER_FLATTEN = "nukerFlatten";
        public static final String NUKER_FLATTEN_DESC = "If nuker should mine below your feet.";
        public static final String NUKER_MINE_SPEED = "nukerMineSpeed";
        public static final String NUKER_MINE_SPEED_DESC = "How fast you should be able to mine a block for nuker to attempt to mine it (0-1, 0 for unminable, 1 for instant mine).";
        public static final String NUKER_MODE = "nukerMode";
        public static final String NUKER_MODE_DESC = "If nuker should try to mine a block depending on the type of block in the schematic.";

        public static final String SCHEMATIC_DIRECTORY = "schematicDirectory";
        public static final String SCHEMATIC_DIRECTORY_DESC = "Schematic directory.";
        public static final String EXTRA_AIR_BLOCKS = "extraAirBlocks";
        public static final String EXTRA_AIR_BLOCKS_DESC = "Extra blocks to consider as air for the schematic renderer.";
        public static final String SORT_TYPE = "sortType";
        public static final String SORT_TYPE_DESC = "Default sort type for the material list.";
        public static final String INV_CALC_RANGE = "inventoryCalculatorRange";
        public static final String INV_CALC_RANGE_DESC = "The range at which inventory calculator will check for matching blocks.";
        public static final String AUTO_ALIGN = "autoAlign";
        public static final String AUTO_ALIGN_DESC = "If the schematic should be aligned with the world's map boundaries when brought to the player.";

        public static final String PRINTER_ENABLED = "printerEnabled";
        public static final String PRINTER_ENABLED_DESC = "Allow players to use the printer.";
        public static final String SAVE_ENABLED = "saveEnabled";
        public static final String SAVE_ENABLED_DESC = "Allow players to save schematics.";
        public static final String LOAD_ENABLED = "loadEnabled";
        public static final String LOAD_ENABLED_DESC = "Allow players to load schematics.";

        public static final String PLAYER_QUOTA_KILOBYTES = "playerQuotaKilobytes";
        public static final String PLAYER_QUOTA_KILOBYTES_DESC = "Amount of storage provided per-player for schematics on the server.";

        public static final String LANG_PREFIX = Reference.MODID + ".config";
    }

    public static final class Command {
        public static final class Save {
            public static final class Message {
                public static final String USAGE = "schematica.command.save.usage";
                public static final String PLAYERS_ONLY = "schematica.command.save.playersOnly";
                public static final String SAVE_STARTED = "schematica.command.save.started";
                public static final String SAVE_SUCCESSFUL = "schematica.command.save.saveSucceeded";
                public static final String SAVE_FAILED = "schematica.command.save.saveFailed";
                public static final String QUOTA_EXCEEDED = "schematica.command.save.quotaExceeded";
                public static final String PLAYER_SCHEMATIC_DIR_UNAVAILABLE = "schematica.command.save.playerSchematicDirUnavailable";
                public static final String UNKNOWN_FORMAT = "schematica.command.save.unknownFormat";
            }

            public static final String NAME = "schematicaSave";
        }

        public static final class List {
            public static final class Message {
                public static final String USAGE = "schematica.command.list.usage";
                public static final String LIST_NOT_AVAILABLE = "schematica.command.list.notAvailable";
                public static final String REMOVE = "schematica.command.list.remove";
                public static final String DOWNLOAD = "schematica.command.list.download";
                public static final String PAGE_HEADER = "schematica.command.list.header";
                public static final String NO_SUCH_PAGE = "schematica.command.list.noSuchPage";
                public static final String NO_SCHEMATICS = "schematica.command.list.noSchematics";
            }

            public static final String NAME = "schematicaList";
        }

        public static final class Remove {
            public static final class Message {
                public static final String USAGE = "schematica.command.remove.usage";
                public static final String PLAYERS_ONLY = "schematica.command.save.playersOnly";
                public static final String SCHEMATIC_REMOVED = "schematica.command.remove.schematicRemoved";
                public static final String SCHEMATIC_NOT_FOUND = "schematica.command.remove.schematicNotFound";
                public static final String ARE_YOU_SURE_START = "schematica.command.remove.areYouSure";
                public static final String YES = "gui.yes";
            }

            public static final String NAME = "schematicaRemove";
        }

        public static final class Download {
            public static final class Message {
                public static final String USAGE = "schematica.command.download.usage";
                public static final String PLAYERS_ONLY = "schematica.command.save.playersOnly";
                public static final String DOWNLOAD_STARTED = "schematica.command.download.started";
                public static final String DOWNLOAD_SUCCEEDED = "schematica.command.download.downloadSucceeded";
                public static final String DOWNLOAD_FAILED = "schematica.command.download.downloadFail";
            }

            public static final String NAME = "schematicaDownload";
        }

        public static final class Replace {
            public static final class Message {
                public static final String USAGE = "schematica.command.replace.usage";
                public static final String NO_SCHEMATIC = "schematica.command.replace.noSchematic";
                public static final String SUCCESS = "schematica.command.replace.success";
            }

            public static final String NAME = "schematicaReplace";
        }
    }

    public static final class Messages {
        public static final String TOGGLE_PRINTER = "schematica.message.togglePrinter";
        public static final String TOGGLE_PREDICT = "schematica.message.togglePredict";
        public static final String TOGGLE_NUKER = "schematica.message.toggleNuker";

        public static final String INVALID_BLOCK = "schematica.message.invalidBlock";
        public static final String INVALID_PROPERTY = "schematica.message.invalidProperty";
        public static final String INVALID_PROPERTY_FOR_BLOCK = "schematica.message.invalidPropertyForBlock";
    }

    public static final class Gui {
        public static final class Load {
            public static final String TITLE = "schematica.gui.title";
            public static final String FOLDER_INFO = "schematica.gui.folderInfo";
            public static final String OPEN_FOLDER = "schematica.gui.openFolder";
            public static final String NO_SCHEMATIC = "schematica.gui.noschematic";
        }

        public static final class Save {
            public static final String POINT_RED = "schematica.gui.point.red";
            public static final String POINT_BLUE = "schematica.gui.point.blue";
            public static final String SAVE = "schematica.gui.save";
            public static final String SAVE_SELECTION = "schematica.gui.saveselection";
            public static final String FORMAT = "schematica.gui.format";
        }

        public static final class Control {
            public static final String MOVE_SCHEMATIC = "schematica.gui.moveschematic";
            public static final String MATERIALS = "schematica.gui.materials";
            public static final String PRINTER = "schematica.gui.printer";
            public static final String OPERATIONS = "schematica.gui.operations";

            public static final String UNLOAD = "schematica.gui.unload";
            public static final String MODE_ALL = "schematica.gui.all";
            public static final String MODE_LAYERS = "schematica.gui.layers";
            public static final String MODE_BELOW = "schematica.gui.below";
            public static final String HIDE = "schematica.gui.hide";
            public static final String SHOW = "schematica.gui.show";
            public static final String MOVE_HERE = "schematica.gui.movehere";
            public static final String FLIP = "schematica.gui.flip";
            public static final String ROTATE = "schematica.gui.rotate";
            public static final String TRANSFORM_PREFIX = "schematica.gui.";

            public static final String MATERIAL_NAME = "schematica.gui.materialname";
            public static final String MATERIAL_AMOUNT = "schematica.gui.materialamount";
            public static final String MATERIAL_AVAILABLE = "schematica.gui.materialavailable";
            public static final String MATERIAL_MISSING = "schematica.gui.materialmissing";

            public static final String SORT_PREFIX = "schematica.gui.material";
            public static final String DUMP = "schematica.gui.materialdump";

            public static final String STOP = "schematica.gui.stop";
            public static final String GENERATE = "schematica.gui.generate";
        }

        public static final String X = "schematica.gui.x";
        public static final String Y = "schematica.gui.y";
        public static final String Z = "schematica.gui.z";
        public static final String ON = "schematica.gui.on";
        public static final String OFF = "schematica.gui.off";
        public static final String DONE = "schematica.gui.done";
    }

    public static final class ModId {
        public static final String MINECRAFT = "minecraft";
    }

    public static final class Keys {
        public static final String CATEGORY = "schematica.key.category";
        public static final String LOAD = "schematica.key.load";
        public static final String SAVE = "schematica.key.save";
        public static final String CONTROL = "schematica.key.control";
        public static final String LAYER_INC = "schematica.key.layerInc";
        public static final String LAYER_DEC = "schematica.key.layerDec";
        public static final String LAYER_TOGGLE = "schematica.key.layerToggle";
        public static final String RENDER_TOGGLE = "schematica.key.renderToggle";
        public static final String PRINTER_TOGGLE = "schematica.key.printerToggle";
        public static final String NUKER_TOGGLE = "schematica.key.nukerToggle";
        public static final String MOVE_HERE = "schematica.key.moveHere";
        public static final String PICK_BLOCK = "schematica.key.pickBlock";
        public static final String GET_INV = "schematica.key.inventoryCalculator";
        public static final String PREDICT_TOGGLE = "schematica.key.predictToggle";
        public static final String MATERIAL_LIST = "schematica.key.materialList";
    }

    public static final class NBT {
        public static final String ROOT = "Schematic";

        public static final String MATERIALS = "Materials";
        public static final String FORMAT_CLASSIC = "Classic";
        public static final String FORMAT_ALPHA = "Alpha";
        public static final String FORMAT_STRUCTURE = "Structure";

        public static final String ICON = "Icon";
        public static final String BLOCKS = "Blocks";
        public static final String DATA = "Data";
        public static final String ADD_BLOCKS = "AddBlocks";
        public static final String ADD_BLOCKS_SCHEMATICA = "Add";
        public static final String WIDTH = "Width";
        public static final String LENGTH = "Length";
        public static final String HEIGHT = "Height";
        public static final String MAPPING_SCHEMATICA = "SchematicaMapping";
        public static final String TILE_ENTITIES = "TileEntities";
        public static final String ENTITIES = "Entities";
        public static final String EXTENDED_METADATA = "ExtendedMetadata";
    }

    public static final class Formats {
        public static final String CLASSIC = "schematica.format.classic";
        public static final String ALPHA = "schematica.format.alpha";
        public static final String STRUCTURE = "schematica.format.structure";
        public static final String INVALID = "schematica.format.invalid";
    }

    public static final class Extensions {
        public static final String SCHEMATIC = ".schematic";
        public static final String STRUCTURE = ".nbt";
    }
}
