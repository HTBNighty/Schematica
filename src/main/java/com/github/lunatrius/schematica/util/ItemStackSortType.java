package com.github.lunatrius.schematica.util;

import com.github.lunatrius.schematica.client.util.BlockList;
import com.github.lunatrius.schematica.reference.Reference;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum ItemStackSortType {
    NAME_ASC("name", "\u2191", (final BlockList.WrappedItemStack wrappedItemStackA, final BlockList.WrappedItemStack wrappedItemStackB) -> {
        final String nameA = wrappedItemStackA.getItemStackDisplayName();
        final String nameB = wrappedItemStackB.getItemStackDisplayName();

        return nameA.compareTo(nameB);
    }),
    NAME_DESC("name", "\u2193", (final BlockList.WrappedItemStack wrappedItemStackA, final BlockList.WrappedItemStack wrappedItemStackB) -> {
        final String nameA = wrappedItemStackA.getItemStackDisplayName();
        final String nameB = wrappedItemStackB.getItemStackDisplayName();

        return nameB.compareTo(nameA);
    }),

    SIZE_ASC("amount", "\u2191", (final BlockList.WrappedItemStack wrappedItemStackA, final BlockList.WrappedItemStack wrappedItemStackB) -> wrappedItemStackA.total - wrappedItemStackB.total),
    SIZE_DESC("amount", "\u2193", (final BlockList.WrappedItemStack wrappedItemStackA, final BlockList.WrappedItemStack wrappedItemStackB) -> wrappedItemStackB.total - wrappedItemStackA.total),

    REMAINING_ASC("remaining", "\u2191", (final BlockList.WrappedItemStack wrappedItemStackA, final BlockList.WrappedItemStack wrappedItemStackB) -> (wrappedItemStackA.total - wrappedItemStackA.placed) - wrappedItemStackB.total),
    REMAINING_DESC("remaining", "\u2193", (final BlockList.WrappedItemStack wrappedItemStackA, final BlockList.WrappedItemStack wrappedItemStackB) -> (wrappedItemStackB.total - wrappedItemStackB.placed) - wrappedItemStackA.total);

    private final Comparator<BlockList.WrappedItemStack> comparator;

    public final String label;
    public final String glyph;

    private ItemStackSortType(final String label, final String glyph, final Comparator<BlockList.WrappedItemStack> comparator) {
        this.label = label;
        this.glyph = glyph;
        this.comparator = comparator;
    }

    public void sort(final List<BlockList.WrappedItemStack> blockList) {
        try {
            Collections.sort(blockList, this.comparator);
        } catch (final Exception e) {
            Reference.logger.error("Could not sort the block list!", e);
        }
    }

    public ItemStackSortType next() {
        final ItemStackSortType[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    public static ItemStackSortType fromString(final String name) {
        try {
            return valueOf(name);
        } catch (final Exception ignored) {
        }

        return NAME_ASC;
    }
}
