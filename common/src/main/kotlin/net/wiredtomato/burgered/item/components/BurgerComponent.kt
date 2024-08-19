package net.wiredtomato.burgered.item.components

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipProvider
import net.minecraft.world.level.Level
import net.wiredtomato.burgered.api.Burger
import net.wiredtomato.burgered.api.StatusEffectEntry
import net.wiredtomato.burgered.api.ingredient.BurgerIngredient
import net.wiredtomato.burgered.api.ingredient.BurgerIngredientInstance
import net.wiredtomato.burgered.api.ingredient.BurgerIngredientInstance.Companion.defaultInstance
import net.wiredtomato.burgered.api.ingredient.BurgerIngredientInstance.Companion.instanceFromStack
import net.wiredtomato.burgered.api.ingredient.ingredient
import net.wiredtomato.burgered.data.text.CommonText
import net.wiredtomato.burgered.init.BurgeredDataComponents
import net.wiredtomato.burgered.init.BurgeredItems
import net.wiredtomato.burgered.item.components.IngredientQualityComponent.Companion.quality
import net.wiredtomato.burgered.util.createGroupsBy
import net.wiredtomato.burgered.util.withPatch
import java.text.DecimalFormat
import java.util.function.Consumer

data class BurgerComponent(
    private val burgerIngredients: List<BurgerIngredientInstance> = listOf(),
    private val burgerSloppiness: Double = 0.0,
    private var dirty: Boolean = true
) : Burger, TooltipProvider {

    override fun addToTooltip(
        context: Item.TooltipContext,
        appender: Consumer<Component>,
        flag: TooltipFlag
    ) {
        val format = DecimalFormat("#.##")
        appender.accept(Component.translatable(CommonText.SLOPPINESS, format.format(burgerSloppiness)))
        appender.accept(Component.empty())

        appender.accept(Component.translatable(CommonText.INGREDIENTS))
        burgerIngredients.reversed().createGroupsBy { it.ingredient to it.stack().quality() }.forEach { group ->
            appender.accept(
                Component.literal("${group.count}x ")
                    .append(group.value.ingredient.asItem().getName(group.value.stack()))
                    .append(" (")
                    .append(Component.translatable(group.value.stack().quality().translationKey))
                    .append(")")
            )
        }
    }

    override fun ingredients(): List<BurgerIngredientInstance> = burgerIngredients

    override fun saturation(): Int {
        return burgerIngredients.map { (it.saturation() * it.quality.multiplier).toInt() }.reduce { acc, d -> acc + d }
    }

    override fun overSaturation(): Double {
        return burgerIngredients.map { it.overSaturation() * it.quality.multiplier }.reduce { acc, d -> acc + d }
    }

    override fun statusEffects(): List<StatusEffectEntry> {
        return ingredients().map { it.statusEffects() }.flatten()
    }

    override fun eatTime(): Float {
        return (ingredients().size / 2f).coerceAtMost(2f)
    }

    override fun sloppiness(): Double = burgerSloppiness
    override fun onEat(entity: LivingEntity, world: Level, stack: ItemStack, component: FoodProperties) {
        ingredients().forEach { it.ingredient.onEat(entity, world, stack, component) }
    }

    fun isDirty() = dirty

    companion object : Burger.Modifier<BurgerComponent> {
        val CODEC: Codec<BurgerComponent> = RecordCodecBuilder.create { builder ->
            builder.group(
                BurgerIngredientInstance.CODEC.listOf().fieldOf("ingredients").forGetter(BurgerComponent::burgerIngredients),
                Codec.DOUBLE.fieldOf("sloppiness").orElse(0.0).forGetter(BurgerComponent::burgerSloppiness),
                Codec.BOOL.fieldOf("dirty").orElse(true).forGetter(BurgerComponent::dirty)
            ).apply(builder, ::BurgerComponent)
        }

        val DEFAULT by lazy {
            BurgerComponent(
                listOf(
                    BurgeredItems.BOTTOM_BUN.defaultInstance(),
                    BurgeredItems.BEEF_PATTY.defaultInstance(),
                    BurgeredItems.CHEESE_SLICE.defaultInstance(),
                    BurgeredItems.TOP_BUN.defaultInstance()
                )
            )
        }

        fun fromItem(ingredient: Item): BurgerIngredient {
            return ingredient.ingredient() ?: BurgeredItems.BOTTOM_BUN
        }

        override fun appendIngredient(burger: BurgerComponent, stack: ItemStack, ingredientStack: ItemStack, ingredient: BurgerIngredient): Component? {
            val ingredients = burger.ingredients().toMutableList()
            if (ingredients.size >= 2048) return Component.translatable(CommonText.BURGER_MAX_SIZE)

            val result = if (ingredient.canBePutOn(ingredientStack, burger, stack)) {
                ingredients.add(ingredient.instanceFromStack(ingredientStack))
                null
            } else Component.translatable(CommonText.CANT_BE_PUT_ON_BURGER, ingredient.asItem().getName(ingredientStack))

            stack.set(BurgeredDataComponents.BURGER, BurgerComponent(ingredients, burger.sloppiness(), true))

            return result
        }

        override fun removeIngredient(burger: BurgerComponent, stack: ItemStack, ingredientStack: ItemStack, ingredient: BurgerIngredient) {
            val ingredients = burger.ingredients().toMutableList()
            ingredients.remove(ingredient.instanceFromStack(stack))
            stack.set(BurgeredDataComponents.BURGER, BurgerComponent(ingredients, burger.sloppiness(), true))
        }

        override fun removeLastIngredient(burger: BurgerComponent, stack: ItemStack) {
            val ingredients = burger.ingredients().toMutableList()
            if (ingredients.isEmpty()) return
            ingredients.removeLast()
            stack.set(BurgeredDataComponents.BURGER, BurgerComponent(ingredients, burger.sloppiness(), true))
        }

        override fun setSloppiness(burger: BurgerComponent, stack: ItemStack, sloppiness: Double) {
            stack.set(BurgeredDataComponents.BURGER, BurgerComponent(burger.burgerIngredients.toMutableList(), sloppiness, true))
        }

        fun Pair<BurgerIngredient, DataComponentPatch>.stack(): ItemStack = first.asItem().defaultInstance.withPatch(second)
    }
}
