package net.wiredtomato.burgered.init

import dev.architectury.registry.registries.Registrar
import dev.architectury.registry.registries.RegistrarManager
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.wiredtomato.burgered.Burgered
import net.wiredtomato.burgered.api.data.burger.BurgerStackableEatCallback

object BurgeredRegistries {
    val MANAGER by lazy { RegistrarManager.get(Burgered.MOD_ID) }
    val EAT_EVENT: Registrar<BurgerStackableEatCallback> = MANAGER.builder(Burgered.modLoc("eat_events"), *arrayOf<BurgerStackableEatCallback>()).build()

    object Keys {
        val EAT_EVENT: ResourceKey<Registry<BurgerStackableEatCallback>> = ResourceKey.createRegistryKey(Burgered.modLoc("eat_events"))
    }
}
