package guru.springframework.controllers;

import guru.springframework.domain.Category;
import guru.springframework.repositories.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class RecipeFormIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void newRecipeFormDisplaysAvailableCategories() throws Exception {
        MvcResult result = mockMvc.perform(get("/recipe/new")).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertContains(result, "Mexican");
        assertContains(result, "name=\"categoryIds\"");
    }

    @Test
    void recipeListLinksToNewRecipeForm() throws Exception {
        MvcResult result = mockMvc.perform(get("/recipes")).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertContains(result, "New Recipe");
        assertContains(result, "href=\"/recipe/new\"");
    }

    @Test
    void savesSelectedCategoriesAndMarksThemOnEditForm() throws Exception {
        Category category = categoryRepository.findByDescription("Mexican").orElseThrow();

        MvcResult saveResult = mockMvc.perform(post("/recipe")
                        .param("description", "Category selection test")
                        .param("categoryIds", category.getId().toString())
                        .param("prepTime", "2")
                        .param("prepTimeUnit", "HOURS")
                        .param("cookTime", "30")
                        .param("cookTimeUnit", "MINUTES"))
                .andReturn();

        assertEquals(302, saveResult.getResponse().getStatus());
        Long recipeId = jdbcTemplate.queryForObject(
                "select id from recipe where description = ?", Long.class, "Category selection test");
        Integer associations = jdbcTemplate.queryForObject(
                "select count(*) from recipe_category where recipe_id = ? and category_id = ?",
                Integer.class, recipeId, category.getId());
        assertTrue(associations != null && associations > 0);
        assertEquals("HOURS", jdbcTemplate.queryForObject(
                "select prep_time_unit from recipe where id = ?", String.class, recipeId));
        assertEquals("MINUTES", jdbcTemplate.queryForObject(
                "select cook_time_unit from recipe where id = ?", String.class, recipeId));

        MvcResult updateResult = mockMvc.perform(get("/recipe/{id}/update", recipeId)).andReturn();
        assertEquals(200, updateResult.getResponse().getStatus());
        assertContains(updateResult, "value=\"" + category.getId() + "\" checked");
        assertContains(updateResult, "value=\"HOURS\" selected");

        MvcResult showResult = mockMvc.perform(get("/recipe/{id}/show", recipeId)).andReturn();
        assertEquals(200, showResult.getResponse().getStatus());
        assertContains(showResult, "2 Hours");
        assertContains(showResult, "30 Minutes");
    }

    private void assertContains(MvcResult result, String expected) throws Exception {
        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains(expected), "Expected rendered HTML to contain: " + expected + "\n" + content);
    }
}
