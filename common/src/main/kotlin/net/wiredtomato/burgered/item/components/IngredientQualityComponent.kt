package net.wiredtomato.burgered.item.components

import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.component.DataComponents
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.ItemStack
import net.wiredtomato.burgered.api.ingredient.BurgerIngredient
import net.wiredtomato.burgered.api.ingredient.BurgerIngredientInstance.Companion.instanceFromStack
import net.wiredtomato.burgered.api.ingredient.IngredientQuality
import net.wiredtomato.burgered.init.BurgeredDataComponents
import java.util.Optional
import kotlin.math.roundToInt

data class IngredientQualityComponent(
    val quality: IngredientQuality
) {
    companion object {
        val CODEC = RecordCodecBuilder.create { builder ->
            builder.group(
                IngredientQuality.CODEC.fieldOf("quality").forGetter(IngredientQualityComponent::quality)
            ).apply(builder, ::IngredientQualityComponent)
        }

        fun ItemStack.defaultQuality(): IngredientQuality {
            val item = this.item
            return if (item is BurgerIngredient) item.defaultQuality() else IngredientQuality.NORMAL
        }

        fun ItemStack.quality(): IngredientQuality {
            val item = this.item
            if (item !is BurgerIngredient) return IngredientQuality.NORMAL

            return get(BurgeredDataComponents.QUALITY)?.quality ?: item.defaultQuality()
        }

        fun ItemStack.withQuality(quality: IngredientQuality): ItemStack {
            val item = this.item
            if (item !is BurgerIngredient) return this
            val instance = item.instanceFromStack(this)
            val foodComponent = FoodProperties(
                (item.saturation(instance) * quality.multiplier).roundToInt(),
                (item.overSaturation(instance) * quality.multiplier).toFloat(),
                item.statusEffects(instance).isNotEmpty(),
                0.5f,
                Optional.empty(),
                item.statusEffects(instance)
            )

            set(BurgeredDataComponents.QUALITY, IngredientQualityComponent(quality))
            set(DataComponents.FOOD, foodComponent)

            return this
        }
    }
}