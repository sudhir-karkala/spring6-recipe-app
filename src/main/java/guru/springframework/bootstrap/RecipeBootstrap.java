package guru.springframework.bootstrap;

import guru.springframework.domain.*;
import guru.springframework.repositories.CategoryRepository;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public class RecipeBootstrap implements ApplicationListener<ContextRefreshedEvent> {
    private final CategoryRepository categoryRepository;
    private final RecipeRepository recipeRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;

    public RecipeBootstrap(CategoryRepository categoryRepository, RecipeRepository recipeRepository, UnitOfMeasureRepository unitOfMeasureRepository) {
        this.categoryRepository = categoryRepository;
        this.recipeRepository = recipeRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        recipeRepository.saveAll(loadRecipes());
        log.debug("Loading bootstrap data");
    }

    private List<Recipe> loadRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(guacRecipe());
        recipes.add(veganSpinachRecipe());
        return recipes;
    }

    private Recipe guacRecipe() {
        Recipe guacRecipe = new Recipe();
        guacRecipe.setDescription("Perfect Guacamole");
        guacRecipe.setPrepTime(10);
        guacRecipe.setCookTime(0);
        guacRecipe.setDifficulty(Difficulty.EASY);
        guacRecipe.setUrl("https://www.simplyrecipes.com/recipes/perfect_guacamole/#ixzz4jvoun5ws");
        guacRecipe.setDirections("1. Cut avocado, remove flesh: Cut the avocados in half. Remove seed. Score the inside of the avocado with a blunt knife and scoop out the flesh with a spoon" +
                "\n" +
                "2. Mash with a fork: Using a fork, roughly mash the avocado. (Don't overdo it! The guacamole should be a little chunky.)" +
                "\n" +
                "3. Add salt, lime juice, and the rest: Sprinkle with salt and lime (or lemon) juice. The acid in the lime juice will provide some balance to the richness of the avocado and will help delay the avocados from turning brown.\n" +
                "Add the chopped onion, cilantro, black pepper, and chiles. Chili peppers vary individually in their hotness. So, start with a half of one chili pepper and add to the guacamole to your desired degree of hotness.\n" +
                "Remember that much of this is done to taste because of the variability in the fresh ingredients. Start with this recipe and adjust to your taste.\n" +
                "4. Cover with plastic and chill to store: Place plastic wrap on the surface of the guacamole cover it and to prevent air reaching it. (The oxygen in the air causes oxidation which will turn the guacamole brown.) Refrigerate until ready to serve.\n" +
                "Chilling tomatoes hurts their flavor, so if you want to add chopped tomato to your guacamole, add it just before serving.");

        Notes guacNotes = new Notes();
        guacNotes.setRecipeNotes("For a very quick guacamole just take a 1/4 cup of salsa and mix it in with your mashed avocados.\n" +
                "Feel free to experiment! One classic Mexican guacamole has pomegranate seeds and chunks of peaches in it (a Diana Kennedy favorite). Try guacamole with added pineapple, mango, or strawberries.\n" +
                "The simplest version of guacamole is just mashed avocados with salt. Don't let the lack of availability of other ingredients stop you from making guacamole.\n" +
                "To extend a limited supply of avocados, add either sour cream or cottage cheese to your guacamole dip. Purists may be horrified, but so what? It tastes great.\n" +
                "\n" +
                "Read more: http://www.simplyrecipes.com/recipes/perfect_guacamole/#ixzz4jvoun5ws");

        guacRecipe.setNotes(guacNotes);

        guacNotes.setRecipe(guacRecipe);
        //very redundant - could add helper method, and make this simpler
        guacRecipe.setIngredients(Set.of(
                createIngredient("ripe avocados", new BigDecimal(2), getUom("Each"), guacRecipe),
                createIngredient("Kosher salt", new BigDecimal(".5"), getUom("Teaspoon"), guacRecipe),
                createIngredient("fresh lime juice or lemon juice", new BigDecimal(2), getUom("Tablespoon"), guacRecipe),
                createIngredient("minced red onion or thinly sliced green onion", new BigDecimal(2), getUom("Tablespoon"), guacRecipe),
                createIngredient("serrano chiles, stems and seeds removed, minced", new BigDecimal(2), getUom("Each"), guacRecipe),
                createIngredient("Cilantro", new BigDecimal(2), getUom("Tablespoon"), guacRecipe),
                createIngredient("freshly grated black pepper", new BigDecimal(2), getUom("Dash"), guacRecipe),
                createIngredient("ripe tomato, seeds and pulp removed, chopped", new BigDecimal(".5"), getUom("Each"), guacRecipe)
        ));

        guacRecipe.getCategories().add(getCategory("Mexican"));

        guacRecipe.setServings(4);
        guacRecipe.setSource("Simply Recipes");

        return guacRecipe;
    }

    private Recipe veganSpinachRecipe() {
        //Yummy Tacos
        Recipe veganSpinachRecipe = new Recipe();
        veganSpinachRecipe.setDescription("Vegan Creamed Spinach");
        veganSpinachRecipe.setCookTime(8);
        veganSpinachRecipe.setPrepTime(20);
        veganSpinachRecipe.setUrl("https://www.simplyrecipes.com/vegan-creamed-spinach-recipe-6748375");
        veganSpinachRecipe.setDifficulty(Difficulty.MODERATE);

        veganSpinachRecipe.setDirections("1. Soak the cashews:\n" +
                "In a small bowl, pour the oat milk and water over the cashews, ensuring they are submerged. Soak for at least 1 hour or up to 12 hours (refrigerate if soaking for longer than an hour or so).\n" +
                "2. Make the sauce:\n" +
                "Transfer the soaked cashews and soaking liquid to a blender along with 2 tablespoons lemon juice and the salt, black pepper, and nutmeg (if using). Purée until smooth.\n" +
                "3. Wash and wilt the spinach:\n" +
                "Wash the spinach and drain in a colander. Don’t spin dry (you want some water clinging to the spinach).\n" +
                "Heat a large skillet over medium-high heat. Add half the spinach and use tongs to turn it in the pan until wilted. This will take just a minute or two. Transfer to a bowl. Repeat with remaining spinach.\n" +
                "4. Squeeze and chop the spinach:\n" +
                "When cool enough to handle, take up handfuls of spinach and squeeze out as much of the liquid as possible. Be aggressive here.\n" +
                "Set the squeezed-out spinach on a cutting board and roughly chop.\n" +
                "5. Sauté the shallots, garlic, and spinach:\n" +
                "Heat the olive oil in the same large skillet over medium-high heat. Add the shallots and sauté until tender and translucent, about 5 minutes. Add the garlic and sauté for 30 seconds, making sure it doesn’t brown. Add the spinach and stir just until warm.\n" +
                "6. Add the sauce:\n" +
                "Pour the cashew cream sauce over the spinach and stir well to blend. Taste and add more lemon juice and/or salt as desired. Serve warm.\n" +
                "Store leftover creamed spinach in an airtight container in the fridge for up to 3 days.");
        Notes veganSpinachNotes = new Notes();
        veganSpinachNotes.setRecipeNotes("You'll get the best results if you use fresh spinach sold in bunches, which means washing and stemming the greens. If you need a good shortcut, use frozen spinach instead.\n" +
                "You can easily double or even triple the recipe. You will have to cook the spinach in several batches and may find you don't need all the cream sauce. Save any extra to enjoy the next day tossed with pasta and a shower of vegan Parmesan cheese. Consider it vegan alfredo!\n" +
                "\n" +
                "Read more: https://www.simplyrecipes.com/vegan-creamed-spinach-recipe-6748375");

        veganSpinachRecipe.setNotes(veganSpinachNotes);

        veganSpinachNotes.setRecipe(veganSpinachRecipe);
        veganSpinachRecipe.setIngredients(Set.of(
                createIngredient("Oat milk (or unsweetened nut milk)", new BigDecimal(1), getUom("Cup"), veganSpinachRecipe),
                createIngredient("Water", new BigDecimal(.25), getUom("Cup"), veganSpinachRecipe),
                createIngredient("Raw, unsalted cashews", new BigDecimal(1), getUom("Cup"), veganSpinachRecipe),
                createIngredient("Lemon juice", new BigDecimal(3), getUom("Tablespoon"), veganSpinachRecipe),
                createIngredient("Kosher salt", new BigDecimal("1"), getUom("Teaspoon"), veganSpinachRecipe),
                createIngredient("Freshly ground black pepper", new BigDecimal(.5), getUom("Teaspoon"), veganSpinachRecipe),
                createIngredient("Ground nutmeg", new BigDecimal(1), getUom("Pinch"), veganSpinachRecipe),
                createIngredient("Fresh spinach", new BigDecimal(2.5), getUom("Pound"), veganSpinachRecipe),
                createIngredient("Extra-virgin olive oil", new BigDecimal(1), getUom("Tablespoon"), veganSpinachRecipe),
                createIngredient("Diced shallot", new BigDecimal(".5"), getUom("Cup"), veganSpinachRecipe),
                createIngredient("Clove garlic, minced", new BigDecimal(1), getUom("Piece"), veganSpinachRecipe)
                ));

        veganSpinachRecipe.getCategories().add(getCategory("Vegan"));

        veganSpinachRecipe.setServings(4);
        veganSpinachRecipe.setSource("Simply Recipes");

        return veganSpinachRecipe;
    }

    private Ingredient createIngredient(String description, BigDecimal amount, UnitOfMeasure uom, Recipe recipe) {
        Ingredient ingredient = new Ingredient();
        ingredient.setDescription(description);
        ingredient.setAmount(amount);
        ingredient.setUom(uom);
        ingredient.setRecipe(recipe);
        return ingredient;
    }

    private UnitOfMeasure getUom(String description) {
        Optional<UnitOfMeasure> uomOptional = unitOfMeasureRepository.findByDescription(description);

        if(uomOptional.isPresent()){
            return uomOptional.get();
        }
        throw new RuntimeException("Expected UOM Not Found");
    }

    private Category getCategory(String description) {
        Optional<Category> categoryOptional = categoryRepository.findByDescription(description);
        if (categoryOptional.isPresent()) {
            return categoryOptional.get();
        }
        throw new RuntimeException("Expected Category not found");
    }
}
