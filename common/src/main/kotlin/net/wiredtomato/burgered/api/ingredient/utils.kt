package net.wiredtomato.burgered.api.ingredient

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.wiredtomato.burgered.api.Burger
import net.wiredtomato.burgered.api.StatusEffectEntry
import net.wiredtomato.burgered.api.data.burger.BurgerStackables
import net.wiredtomato.burgered.api.rendering.IngredientRenderSettings
import org.joml.Vector3d

fun Item.ingredient(): BurgerIngredient? {
    if (this is BurgerIngredient) return this

    if (BurgerStackables.map { it.item }.contains(this)) {
        val stackable = BurgerStackables.find { it.item == this }!!
        return object : BurgerIngredient {
            override fun canBePutOn(stack: ItemStack, burger: Burger): Boolean = true
            override fun saturation(): Int = stackable.hunger
            override fun overSaturation(): Double = stackable.saturation.toDouble()
            override fun statusEffects(): List<StatusEffectEntry> = stackable.statusEffects
            override fun renderSettings(): IngredientRenderSettings = IngredientRenderSettings.ItemModel2d(Vector3d(0.5), Vector3d())
            override fun asItem(): Item = this@ingredient
            override fun onEat(entity: LivingEntity, world: Level, stack: ItemStack, component: FoodProperties) { }
        }
    }

    return null
}
