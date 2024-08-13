package net.wiredtomato.burgered.neoforge.data.gen.provider

import net.minecraft.core.HolderLookup
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.level.block.Block
import net.wiredtomato.burgered.init.BurgeredBlocks

class BurgeredBlockLootTableProvider(
    provider: HolderLookup.Provider
) : BlockLootSubProvider(setOf(), FeatureFlags.DEFAULT_FLAGS, provider) {
    override fun getKnownBlocks(): MutableIterable<Block> {
        return BurgeredBlocks.BLOCKS.map { it.get() }.toMutableList()
    }

    override fun generate() {
        dropSelf(BurgeredBlocks.BURGER_STACKER)
        dropSelf(BurgeredBlocks.GRILL)
    }
}