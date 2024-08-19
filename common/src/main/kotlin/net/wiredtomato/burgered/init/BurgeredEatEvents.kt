package net.wiredtomato.burgered.init

import dev.architectury.registry.registries.DeferredRegister
import net.wiredtomato.burgered.Burgered
import net.wiredtomato.burgered.api.data.burger.BurgerStackableEatCallback
import net.wiredtomato.burgered.api.registry.RegistryDelegate
import net.wiredtomato.burgered.api.registry.registered
import java.util.function.Supplier

object BurgeredEatEvents {
    val EAT_EVENTS = DeferredRegister.create(Burgered.MOD_ID, BurgeredRegistries.Keys.EAT_EVENT)
    val NO_OP by registering("no_op") { BurgerStackableEatCallback { _, _, _, _ -> } }

    fun <T : BurgerStackableEatCallback> registering(name: String, supplier: Supplier<T>): RegistryDelegate<BurgeredEatEvents, T> {
        return registered(EAT_EVENTS.register(Burgered.modLoc(name), supplier))
    }
}