package net.wiredtomato.burgered.recipe

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.AbstractCookingRecipe
import net.minecraft.world.item.crafting.CookingBookCategory
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer
import net.wiredtomato.burgered.init.BurgeredRecipes

class GrillingRecipe(
    group: String,
    category: CookingBookCategory,
    val ingredient: Ingredient,
    val transform: ItemStack,
    val result: ItemStack,
    experience: Float,
    cookTime: Int
) : AbstractCookingRecipe(BurgeredRecipes.GRILLING, group, category, ingredient, result, experience, cookTime) {
    override fun getSerializer(): RecipeSerializer<*> = SERIALIZER

    companion object {
        val SERIALIZER = GrillingRecipeSerializer()
    }
}
