package net.wiredtomato.burgered.init

import net.wiredtomato.burgered.Burgered
import net.wiredtomato.burgered.api.data.burger.BurgerStackableEatCallback
import net.wiredtomato.burgered.api.registry.RegistryDelegate
import net.wiredtomato.burgered.api.registry.registered
import java.util.function.Supplier

object BurgeredEatEvents {
    val NO_OP by registering("no_op") { BurgerStackableEatCallback { _, _, _, _ -> } }

    fun <T : BurgerStackableEatCallback> registering(name: String, supplier: Supplier<T>): RegistryDelegate<BurgeredEatEvents, T> {
        return registered(BurgeredRegistries.EAT_EVENT.register(Burgered.modLoc(name), supplier))
    }
}