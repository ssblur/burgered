package net.wiredtomato.burgered.item.components

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
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
import net.wiredtomato.burgered.api.ingredient.ingredient
import net.wiredtomato.burgered.data.text.CommonText
import net.wiredtomato.burgered.init.BurgeredDataComponents
import net.wiredtomato.burgered.init.BurgeredItems
import net.wiredtomato.burgered.util.group
import java.text.DecimalFormat
import java.util.function.Consumer

data class BurgerComponent(
    private val burgerIngredients: List<BurgerIngredient> = listOf(),
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
        burgerIngredients.reversed().group().forEach { group ->
            appender.accept(Component.literal("${group.count}x ").append(group.value.asItem().getName(group.value.asItem().defaultInstance)))
        }
    }

    override fun ingredients(): List<BurgerIngredient> = burgerIngredients

    override fun saturation(): Int {
        return burgerIngredients.map { it.saturation() }.reduce { acc, d -> acc + d }
    }

    override fun overSaturation(): Double {
        return burgerIngredients.map { it.overSaturation() }.reduce { acc, d -> acc + d }
    }

    override fun statusEffects(): List<StatusEffectEntry> {
        return ingredients().map { it.statusEffects() }.flatten()
    }

    override fun eatTime(): Float {
        return (ingredients().size / 2f).coerceAtMost(2f)
    }

    override fun sloppiness(): Double = burgerSloppiness
    override fun onEat(entity: LivingEntity, world: Level, stack: ItemStack, component: FoodProperties) {
        ingredients().forEach { it.onEat(entity, world, stack, component) }
    }

    fun isDirty() = dirty

    companion object : Burger.Modifier<BurgerComponent> {
        val INGREDIENT_PAIR_CODEC: Codec<BurgerIngredient> = BuiltInRegistries.ITEM.byNameCodec().xmap({ fromItem(it) }, { it.asItem() })
        val CODEC: Codec<BurgerComponent> = RecordCodecBuilder.create { builder ->
            builder.group(
                INGREDIENT_PAIR_CODEC.listOf().fieldOf("ingredients").forGetter(BurgerComponent::burgerIngredients),
                Codec.DOUBLE.fieldOf("sloppiness").orElse(0.0).forGetter(BurgerComponent::burgerSloppiness),
                Codec.BOOL.fieldOf("dirty").orElse(true).forGetter(BurgerComponent::dirty)
            ).apply(builder, ::BurgerComponent)
        }

        val DEFAULT by lazy {
            BurgerComponent(
                listOf(
                    BurgeredItems.BOTTOM_BUN,
                    BurgeredItems.BEEF_PATTY,
                    BurgeredItems.CHEESE_SLICE,
                    BurgeredItems.TOP_BUN
                )
            )
        }

        fun fromItem(ingredient: Item): BurgerIngredient {
            return ingredient.ingredient() ?: throw IllegalStateException("Non ingredient item found: $ingredient")
        }

        override fun appendIngredient(burger: BurgerComponent, stack: ItemStack, ingredientStack: ItemStack, ingredient: BurgerIngredient): Component? {
            val ingredients = burger.ingredients().toMutableList()
            if (ingredients.size >= 2048) return Component.translatable(CommonText.BURGER_MAX_SIZE)

            val result = if (ingredient.canBePutOn(stack, burger)) {
                ingredients.add(ingredient)
                null
            } else Component.translatable(CommonText.CANT_BE_PUT_ON_BURGER, ingredient.asItem().getName(ingredientStack))

            stack.set(BurgeredDataComponents.BURGER, BurgerComponent(ingredients, burger.sloppiness(), true))

            return result
        }

        override fun removeIngredient(burger: BurgerComponent, stack: ItemStack, ingredientStack: ItemStack, ingredient: BurgerIngredient) {
            val ingredients = burger.ingredients().toMutableList()
            ingredients.remove(ingredient)
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
    }
}
