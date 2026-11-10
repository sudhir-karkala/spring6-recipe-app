package guru.springframework.controllers;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.RecipeCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.services.IngredientService;
import guru.springframework.services.RecipeService;
import guru.springframework.services.UnitOfMeasureService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class IngredientControllerTest {
    private AutoCloseable autoCloseable;
    private IngredientController ingredientController;
    @Mock
    private RecipeService recipeService;
    @Mock
    private IngredientService ingredientService;
    @Mock
    private UnitOfMeasureService unitOfMeasureService;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() throws Exception {
        autoCloseable = MockitoAnnotations.openMocks(this);
        ingredientController = new IngredientController(recipeService, ingredientService, unitOfMeasureService);
        mockMvc = MockMvcBuilders.standaloneSetup(ingredientController).build();
    }

    @Test
    public void testListIngredients() throws Exception {
        //given
        RecipeCommand recipeCommand = new RecipeCommand();
        when(recipeService.findCommandById(anyLong())).thenReturn(recipeCommand);

        //when
        mockMvc.perform(get("/recipe/1/ingredients"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/list"))
                .andExpect(model().attributeExists("recipe"));

        //then
        verify(recipeService, times(1)).findCommandById(anyLong());
    }

    @Test
    public void testShowIngredient() throws Exception {
        IngredientCommand ingredient = new IngredientCommand();
        ingredient.setId(2L);
        when(ingredientService.findByRecipeIdAndIngredientId(1L, 2L)).thenReturn(ingredient);

        mockMvc.perform(get("/recipe/1/ingredient/2/show"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/show"))
                .andExpect(model().attribute("ingredient", ingredient))
                .andExpect(model().attribute("recipeId", 1L));
    }

    @Test
    public void testUpdateIngredient() throws Exception {
        IngredientCommand ingredient = new IngredientCommand();
        ingredient.setId(2L);
        List<UnitOfMeasureCommand> uoms = List.of(new UnitOfMeasureCommand());
        when(ingredientService.findByRecipeIdAndIngredientId(1L, 2L)).thenReturn(ingredient);
        when(unitOfMeasureService.listAllUoms()).thenReturn(uoms);

        mockMvc.perform(get("/recipe/1/ingredient/2/update"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/ingredientform"))
                .andExpect(model().attribute("ingredient", ingredient))
                .andExpect(model().attribute("uomList", uoms))
                .andExpect(model().attributeExists("formAction"));
    }

    @Test
    public void testNewIngredient() throws Exception {
        List<UnitOfMeasureCommand> uoms = List.of(new UnitOfMeasureCommand());
        when(recipeService.findCommandById(1L)).thenReturn(new RecipeCommand());
        when(unitOfMeasureService.listAllUoms()).thenReturn(uoms);

        mockMvc.perform(get("/recipe/1/ingredient/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/ingredientform"))
                .andExpect(model().attributeExists("ingredient", "uomList", "formAction"));
    }

    @Test
    public void testSaveIngredient() throws Exception {
        when(ingredientService.saveIngredientCommand(eq(1L), eq(2L), any(IngredientCommand.class)))
                .thenAnswer(invocation -> {
                    IngredientCommand command = invocation.getArgument(2);
                    command.setId(2L);
                    return command;
                });
        mockMvc.perform(post("/recipe/1/ingredient/2")
                        .param("description", "Flour")
                        .param("amount", "2")
                        .param("uom.id", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/recipe/1/ingredient/2/show"));

        verify(ingredientService).saveIngredientCommand(eq(1L), eq(2L), any(IngredientCommand.class));
    }

    @Test
    public void testCreateIngredient() throws Exception {
        IngredientCommand savedCommand = new IngredientCommand();
        savedCommand.setId(3L);
        when(ingredientService.createIngredientCommand(eq(1L), any(IngredientCommand.class)))
                .thenReturn(savedCommand);

        mockMvc.perform(post("/recipe/1/ingredient")
                        .param("description", "Flour")
                        .param("amount", "2")
                        .param("uom.id", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/recipe/1/ingredient/3/show"));

        verify(ingredientService).createIngredientCommand(eq(1L), any(IngredientCommand.class));
    }

    @Test
    public void testDeleteIngredient() throws Exception {
        mockMvc.perform(post("/recipe/1/ingredient/2/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/recipe/1/ingredients"));

        verify(ingredientService).deleteById(1L, 2L);
    }

    @AfterEach
    public void tearDown() throws Exception {
        autoCloseable.close();
    }
}