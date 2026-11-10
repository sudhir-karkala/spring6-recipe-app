package guru.springframework.controllers;

import guru.springframework.domain.Ingredient;
import guru.springframework.repositories.IngredientRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class IngredientControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private UnitOfMeasureRepository unitOfMeasureRepository;

    @Test
    void rendersUpdatesAndDeletesRecipeIngredient() throws Exception {
        Ingredient ingredient = ingredientRepository.findAll().iterator().next();
        Long recipeId = ingredient.getRecipe().getId();
        Long ingredientId = ingredient.getId();
        Long unitOfMeasureId = unitOfMeasureRepository.findAll().iterator().next().getId();

        mockMvc.perform(get("/recipe/{recipeId}/ingredients", recipeId))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(ingredient.getDescription())));

        mockMvc.perform(get("/recipe/{recipeId}/ingredient/{ingredientId}/show", recipeId, ingredientId))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(ingredient.getDescription())));

        mockMvc.perform(get("/recipe/{recipeId}/ingredient/{ingredientId}/update", recipeId, ingredientId))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Edit Ingredient")));

        mockMvc.perform(get("/recipe/{recipeId}/ingredient/new", recipeId))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Add Ingredient")));

        mockMvc.perform(post("/recipe/{recipeId}/ingredient", recipeId)
                        .param("description", "New integration-test ingredient")
                        .param("amount", "1.5")
                        .param("uom.id", unitOfMeasureId.toString()))
                .andExpect(status().is3xxRedirection());

        Ingredient createdIngredient = findIngredient("New integration-test ingredient");
        assertEquals(recipeId, createdIngredient.getRecipe().getId());

        mockMvc.perform(post("/recipe/{recipeId}/ingredient/{ingredientId}", recipeId, ingredientId)
                        .param("description", "Updated ingredient")
                        .param("amount", "2")
                        .param("uom.id", unitOfMeasureId.toString()))
                .andExpect(status().is3xxRedirection());

        Ingredient updatedIngredient = ingredientRepository.findById(ingredientId).orElseThrow();
        assertEquals("Updated ingredient", updatedIngredient.getDescription());

        mockMvc.perform(post("/recipe/{recipeId}/ingredient/{ingredientId}/delete", recipeId, ingredientId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recipe/" + recipeId + "/ingredients"));

        assertFalse(ingredientRepository.findById(ingredientId).isPresent());
        assertTrue(ingredientRepository.findAll().iterator().hasNext());
        ingredientRepository.delete(createdIngredient);
    }

    private Ingredient findIngredient(String description) {
        return java.util.stream.StreamSupport.stream(ingredientRepository.findAll().spliterator(), false)
                .filter(ingredient -> description.equals(ingredient.getDescription()))
                .findFirst()
                .orElseThrow();
    }
}
