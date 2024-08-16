package net.wiredtomato.burgered.api.ingredient

import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.wiredtomato.burgered.api.Burger
import net.wiredtomato.burgered.api.StatusEffectEntry
import net.wiredtomato.burgered.api.event.LivingEntityEvents
import net.wiredtomato.burgered.api.rendering.IngredientRenderSettings

interface BurgerIngredient : ItemLike, LivingEntityEvents.EatCallback {
    fun canBePutOn(ingredientStack: ItemStack, burger: Burger, burgerStack: ItemStack): Boolean
    fun saturation(instance: BurgerIngredientInstance): Int
    fun overSaturation(instance: BurgerIngredientInstance): Double
    fun statusEffects(instance: BurgerIngredientInstance): List<StatusEffectEntry>
    fun renderSettings(instance: BurgerIngredientInstance): IngredientRenderSettings
    fun defaultQuality(): IngredientQuality = IngredientQuality.NORMAL
}
