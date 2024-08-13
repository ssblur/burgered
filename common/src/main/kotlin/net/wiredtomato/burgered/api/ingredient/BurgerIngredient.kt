package net.wiredtomato.burgered.api.ingredient

import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.wiredtomato.burgered.api.Burger
import net.wiredtomato.burgered.api.StatusEffectEntry
import net.wiredtomato.burgered.api.event.LivingEntityEvents
import net.wiredtomato.burgered.api.rendering.IngredientRenderSettings

interface BurgerIngredient : ItemLike, LivingEntityEvents.EatCallback {
    fun canBePutOn(stack: ItemStack, burger: Burger): Boolean
    fun saturation(): Int
    fun overSaturation(): Double
    fun statusEffects(): List<StatusEffectEntry>
    fun renderSettings(): IngredientRenderSettings
}
