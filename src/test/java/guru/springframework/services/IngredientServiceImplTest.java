package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.repositories.IngredientRepository;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IngredientServiceImplTest {
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private IngredientRepository ingredientRepository;
    @Mock
    private UnitOfMeasureRepository unitOfMeasureRepository;
    @Mock
    private IngredientToIngredientCommand ingredientToIngredientCommand;

    private IngredientService ingredientService;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        ingredientService = new IngredientServiceImpl(
                recipeRepository, ingredientRepository, unitOfMeasureRepository, ingredientToIngredientCommand);
    }

    @Test
    void findsIngredientOnlyWithinRequestedRecipe() {
        Recipe recipe = recipeWithIngredient(1L, 10L);
        IngredientCommand expected = new IngredientCommand();
        expected.setId(10L);
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(ingredientToIngredientCommand.convert(recipe.getIngredients().iterator().next())).thenReturn(expected);

        assertSame(expected, ingredientService.findByRecipeIdAndIngredientId(1L, 10L));

        assertNotNull(recipe.getIngredients().iterator().next().getRecipe());
    }

    @Test
    void rejectsIngredientThatDoesNotBelongToRequestedRecipe() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipeWithIngredient(1L, 10L)));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> ingredientService.findByRecipeIdAndIngredientId(1L, 11L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void updatesIngredientAndUsesPersistedUnitOfMeasure() {
        Recipe recipe = recipeWithIngredient(1L, 10L);
        Ingredient ingredient = recipe.getIngredients().iterator().next();
        UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
        unitOfMeasure.setId(3L);
        IngredientCommand command = new IngredientCommand();
        command.setDescription("Flour");
        command.setAmount(new BigDecimal("2"));
        UnitOfMeasureCommand uomCommand = new UnitOfMeasureCommand();
        uomCommand.setId(3L);
        command.setUom(uomCommand);
        IngredientCommand expected = new IngredientCommand();

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(unitOfMeasureRepository.findById(3L)).thenReturn(Optional.of(unitOfMeasure));
        when(ingredientToIngredientCommand.convert(ingredient)).thenReturn(expected);

        assertSame(expected, ingredientService.saveIngredientCommand(1L, 10L, command));

        assertEquals("Flour", ingredient.getDescription());
        assertEquals(new BigDecimal("2"), ingredient.getAmount());
        assertSame(unitOfMeasure, ingredient.getUom());
        verify(recipeRepository).save(recipe);
    }

    @Test
    void createsIngredientOnRequestedRecipe() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
        unitOfMeasure.setId(3L);
        IngredientCommand command = new IngredientCommand();
        command.setDescription("Flour");
        command.setAmount(new BigDecimal("2"));
        UnitOfMeasureCommand uomCommand = new UnitOfMeasureCommand();
        uomCommand.setId(3L);
        command.setUom(uomCommand);
        IngredientCommand expected = new IngredientCommand();
        expected.setId(10L);

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(unitOfMeasureRepository.findById(3L)).thenReturn(Optional.of(unitOfMeasure));
        when(ingredientRepository.save(any(Ingredient.class))).thenAnswer(invocation -> {
            Ingredient ingredient = invocation.getArgument(0);
            ingredient.setId(10L);
            return ingredient;
        });
        when(ingredientToIngredientCommand.convert(any(Ingredient.class))).thenReturn(expected);

        assertSame(expected, ingredientService.createIngredientCommand(1L, command));

        Ingredient savedIngredient = recipe.getIngredients().iterator().next();
        assertEquals("Flour", savedIngredient.getDescription());
        assertEquals(new BigDecimal("2"), savedIngredient.getAmount());
        assertSame(unitOfMeasure, savedIngredient.getUom());
        assertSame(recipe, savedIngredient.getRecipe());
    }

    @Test
    void deletesIngredientFromItsRecipe() {
        Recipe recipe = recipeWithIngredient(1L, 10L);
        Ingredient ingredient = recipe.getIngredients().iterator().next();
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        ingredientService.deleteById(1L, 10L);

        assertTrue(recipe.getIngredients().isEmpty());
        assertNull(ingredient.getRecipe());
        verify(ingredientRepository).delete(ingredient);
    }

    private Recipe recipeWithIngredient(Long recipeId, Long ingredientId) {
        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientId);
        ingredient.setRecipe(recipe);
        recipe.getIngredients().add(ingredient);
        return recipe;
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }
}
