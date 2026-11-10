package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;

public interface IngredientService {
    IngredientCommand findByRecipeIdAndIngredientId(Long recipeId, Long ingredientId);

    IngredientCommand createIngredientCommand(Long recipeId, IngredientCommand command);

    IngredientCommand saveIngredientCommand(Long recipeId, Long ingredientId, IngredientCommand command);

    void deleteById(Long recipeId, Long ingredientId);
}
