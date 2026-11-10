package guru.springframework.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private Integer prepTime;
    @Enumerated(EnumType.STRING)
    private TimeUnit prepTimeUnit = TimeUnit.MINUTES;
    private Integer cookTime;
    @Enumerated(EnumType.STRING)
    private TimeUnit cookTimeUnit = TimeUnit.MINUTES;
    private Integer servings;
    private String source;
    private String url;

    @Lob
    private String directions;

    @Enumerated(value = EnumType.STRING)
    private Difficulty difficulty;

    @Lob
    private Byte[] image;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "notes_id")
    @ToString.Exclude
    private Notes notes;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "recipe")
    @ToString.Exclude
    private Set<Ingredient> ingredients = new HashSet<>();

    @ManyToMany
    @ToString.Exclude
    @JoinTable(name = "recipe_category",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    public void setNotes(Notes notes) {
        notes.setRecipe(this);
        this.notes = notes;
    }

    public void addIngredient(Ingredient ingredient) {
        ingredient.setRecipe(this);
        ingredients.add(ingredient);
    }

    public TimeUnit getPrepTimeUnit() {
        return prepTimeUnit == null ? TimeUnit.MINUTES : prepTimeUnit;
    }

    public TimeUnit getCookTimeUnit() {
        return cookTimeUnit == null ? TimeUnit.MINUTES : cookTimeUnit;
    }
}
