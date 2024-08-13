package net.wiredtomato.burgered.api

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.wiredtomato.burgered.api.event.LivingEntityEvents
import net.wiredtomato.burgered.api.ingredient.BurgerIngredient

interface Burger : LivingEntityEvents.EatCallback {
    fun ingredients(): List<BurgerIngredient>
    fun saturation(): Int
    fun overSaturation(): Double
    fun statusEffects(): List<StatusEffectEntry>
    fun eatTime(): Float
    fun sloppiness(): Double

    interface Modifier<T : Burger> {
        fun setSloppiness(burger: T, stack: ItemStack, sloppiness: Double)
        fun appendIngredient(burger: T, stack: ItemStack, ingredientStack: ItemStack, ingredient: BurgerIngredient): Component?
        fun removeIngredient(burger: T, stack: ItemStack, ingredientStack: ItemStack, ingredient: BurgerIngredient)
        fun removeLastIngredient(burger: T, stack: ItemStack)
    }
}
