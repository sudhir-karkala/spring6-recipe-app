package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Category;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.CategoryRepository;
import guru.springframework.repositories.RecipeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecipeServiceImplTest {
    private RecipeService recipeService;

    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private CategoryRepository categoryRepository;

    private AutoCloseable autoCloseable;

    @Mock
    private RecipeCommandToRecipe recipeCommandToRecipe;

    @Mock
    private RecipeToRecipeCommand recipeToRecipeCommand;

    @BeforeEach
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        recipeService = new RecipeServiceImpl(
                recipeRepository, categoryRepository, recipeCommandToRecipe, recipeToRecipeCommand);
    }

    @Test
    public void getRecipes() {
        Set<Recipe> recipesData = new HashSet<>();
        Recipe recipe = new Recipe();
        recipesData.add(recipe);
        when(recipeRepository.findAll()).thenReturn(recipesData);
        Set<Recipe> recipes = recipeService.getRecipes();
        assertEquals(recipes.size(), 1);
        verify(recipeRepository, times(1)).findAll();
        verify(recipeRepository, never()).findById(anyLong());
    }

    @Test
    public void getCategories() {
        Category category = new Category();
        when(categoryRepository.findAll()).thenReturn(Set.of(category));

        assertEquals(Set.of(category), recipeService.getCategories());
    }

    @Test
    public void saveRecipeResolvesSelectedCategoriesFromRepository() {
        RecipeCommand command = new RecipeCommand();
        command.setCategoryIds(Set.of(5L));
        Recipe recipe = new Recipe();
        Category category = new Category();
        category.setId(5L);
        RecipeCommand expected = new RecipeCommand();

        when(recipeCommandToRecipe.convert(command)).thenReturn(recipe);
        when(categoryRepository.findById(5L)).thenReturn(Optional.of(category));
        when(recipeRepository.save(recipe)).thenReturn(recipe);
        when(recipeToRecipeCommand.convert(recipe)).thenReturn(expected);

        assertSame(expected, recipeService.saveRecipeCommand(command));

        assertEquals(Set.of(category), recipe.getCategories());
    }

    @Test
    public void getRecipeCommandByIdTest() {
        //given
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        Optional<Recipe> optionalRecipe = Optional.of(recipe);
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId(1L);
        when(recipeRepository.findById(anyLong())).thenReturn(optionalRecipe);
        when(recipeToRecipeCommand.convert(recipe)).thenReturn(recipeCommand);

        //when
        RecipeCommand returnedRecipeCommand = recipeService.findCommandById(1L);

        //then
        assertNotNull(returnedRecipeCommand, "Null RecipeCommand returned");
        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeToRecipeCommand, times(1)).convert(any());
    }

    @Test
    public void getRecipeByIdTest() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        Optional<Recipe> optionalRecipe = Optional.of(recipe);

        when(recipeRepository.findById(anyLong())).thenReturn(optionalRecipe);

        Recipe returnedRecipe = recipeService.findById(1L);

        assertNotNull(returnedRecipe, "Null recipe returned");
        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, never()).findAll();
    }

    @Test
    public void testDeleteRecipeById() {
        //given
        Long id = 1L;

        //when
        recipeService.deleteById(id);
        //no 'when', since method has void return type

        //then
        verify(recipeRepository, times(1)).deleteById(anyLong());
    }

    @AfterEach
    public void tearDown() throws Exception {
        autoCloseable.close();
    }
}