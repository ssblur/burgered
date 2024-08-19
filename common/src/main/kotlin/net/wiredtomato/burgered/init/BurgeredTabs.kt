package net.wiredtomato.burgered.init

import dev.architectury.registry.CreativeTabRegistry.create
import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.wiredtomato.burgered.Burgered
import net.wiredtomato.burgered.api.registry.RegistryDelegate
import net.wiredtomato.burgered.api.registry.registered
import java.util.function.Consumer

object BurgeredTabs {
    val TABS = DeferredRegister.create(Burgered.MOD_ID, Registries.CREATIVE_MODE_TAB)

    val BURGERED_TAB by registering("burgered_tab") { builder ->
        builder
            .title(Component.translatable("itemGroup.burgered.burgered_tab"))
            .icon { BurgeredItems.BURGER.defaultInstance }
            .displayItems { _, entries ->
                BurgeredItems.ITEMS.forEach {
                    entries.accept(it.get())
                }
            }
    }


    fun registering(
        name: String, tabSupplier: Consumer<CreativeModeTab.Builder>
    ): RegistryDelegate<BurgeredTabs, CreativeModeTab> {
        return registered(TABS.register(Burgered.modLoc(name)) { create(tabSupplier) })
    }
}