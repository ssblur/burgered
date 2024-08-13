package net.wiredtomato.burgered.neoforge.data.gen

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.wiredtomato.burgered.Burgered
import net.wiredtomato.burgered.neoforge.data.gen.provider.*

@EventBusSubscriber(modid = Burgered.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object BurgeredDataGen {
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        val output = generator.packOutput
        val fileHelper = event.existingFileHelper

        generator.addProvider(
            event.includeClient(),
            BurgeredBlockStateProvider(output, fileHelper)
        )

        generator.addProvider(
            event.includeClient(),
            BurgeredItemModelProvider(output, fileHelper)
        )

        generator.addProvider(
            event.includeServer(),
            BurgeredEnUsLangProvider(output)
        )

        generator.addProvider(
            event.includeServer(),
            BurgeredRecipeProvider(output, event.lookupProvider)
        )

        generator.addProvider(
            event.includeServer(),
            BurgeredStackablesProvider(output, event.lookupProvider)
        )

        generator.addProvider(
            event.includeServer(),
            BurgeredLootTableProvider(output, event.lookupProvider)
        )

        generator.addProvider(
            event.includeServer(),
            BurgeredBlockTagProvider(output, event.lookupProvider, event.existingFileHelper)
        )
    }
}
