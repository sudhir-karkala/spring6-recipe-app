package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.repositories.IngredientRepository;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class IngredientServiceImpl implements IngredientService {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final IngredientToIngredientCommand ingredientToIngredientCommand;

    public IngredientServiceImpl(RecipeRepository recipeRepository,
                                 IngredientRepository ingredientRepository,
                                 UnitOfMeasureRepository unitOfMeasureRepository,
                                 IngredientToIngredientCommand ingredientToIngredientCommand) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
    }

    @Override
    @Transactional(readOnly = true)
    public IngredientCommand findByRecipeIdAndIngredientId(Long recipeId, Long ingredientId) {
        Recipe recipe = findRecipe(recipeId);
        return recipe.getIngredients()
                .stream()
                .filter(ingredient -> ingredientId.equals(ingredient.getId()))
                .findFirst()
                .map(ingredientToIngredientCommand::convert)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found"));
    }

    @Override
    @Transactional
    public IngredientCommand createIngredientCommand(Long recipeId, IngredientCommand command) {
        Recipe recipe = findRecipe(recipeId);
        UnitOfMeasure unitOfMeasure = findUnitOfMeasure(command);

        Ingredient ingredient = new Ingredient();
        ingredient.setDescription(command.getDescription());
        ingredient.setAmount(command.getAmount());
        ingredient.setUom(unitOfMeasure);
        recipe.addIngredient(ingredient);

        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        return ingredientToIngredientCommand.convert(savedIngredient);
    }

    @Override
    @Transactional
    public IngredientCommand saveIngredientCommand(Long recipeId, Long ingredientId, IngredientCommand command) {
        Recipe recipe = findRecipe(recipeId);
        Ingredient ingredient = findIngredient(recipe, ingredientId);
        UnitOfMeasure unitOfMeasure = findUnitOfMeasure(command);

        ingredient.setDescription(command.getDescription());
        ingredient.setAmount(command.getAmount());
        ingredient.setUom(unitOfMeasure);

        recipeRepository.save(recipe);
        return ingredientToIngredientCommand.convert(ingredient);
    }

    private UnitOfMeasure findUnitOfMeasure(IngredientCommand command) {
        if (command.getUom() == null || command.getUom().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unit of measure is required");
        }
        return unitOfMeasureRepository.findById(command.getUom().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unit of measure not found"));
    }

    @Override
    @Transactional
    public void deleteById(Long recipeId, Long ingredientId) {
        Recipe recipe = findRecipe(recipeId);
        Ingredient ingredient = findIngredient(recipe, ingredientId);

        recipe.getIngredients().removeIf(candidate -> ingredientId.equals(candidate.getId()));
        ingredient.setRecipe(null);
        ingredientRepository.delete(ingredient);
    }

    private Recipe findRecipe(Long recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));
    }

    private Ingredient findIngredient(Recipe recipe, Long ingredientId) {
        return recipe.getIngredients()
                .stream()
                .filter(ingredient -> ingredientId.equals(ingredient.getId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found"));
    }
}
