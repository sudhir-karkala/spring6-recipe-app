package guru.springframework.commands;

import guru.springframework.domain.Difficulty;
import guru.springframework.domain.TimeUnit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class RecipeCommand {
    private Long id;
    private String description;
    private Integer prepTime;
    private TimeUnit prepTimeUnit = TimeUnit.MINUTES;
    private Integer cookTime;
    private TimeUnit cookTimeUnit = TimeUnit.MINUTES;
    private Integer servings;
    private String source;
    private String url;
    private String directions;
    private Difficulty difficulty;
    private NotesCommand notes = new NotesCommand();
    private Set<IngredientCommand> ingredients = new HashSet<>();
    private Set<CategoryCommand> categories = new HashSet<>();
    private Set<Long> categoryIds = new HashSet<>();
}
