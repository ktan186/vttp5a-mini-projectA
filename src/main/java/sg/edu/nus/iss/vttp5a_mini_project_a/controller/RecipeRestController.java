package sg.edu.nus.iss.vttp5a_mini_project_a.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sg.edu.nus.iss.vttp5a_mini_project_a.model.Ingredients;
import sg.edu.nus.iss.vttp5a_mini_project_a.model.Recipe;
import sg.edu.nus.iss.vttp5a_mini_project_a.service.RecipeRestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/recipes")
public class RecipeRestController {
    
    @Autowired
    RecipeRestService recipeRestService;

    @GetMapping("/suggest")
    public ResponseEntity<?> suggestRecipes(@RequestParam List<String> ingredients, @RequestParam Integer number) {
        // System.out.println("Received ingredients: " + ingredients);
        // System.out.println("Number of recipes requested: " + number);
        try {
            List<Ingredients> recipes = recipeRestService.findByIngredients(ingredients, number);
            if (recipes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No recipes found for the given ingredients.");
            }
            return ResponseEntity.ok(recipes);
        } catch (Exception e) {
            if (e.getMessage().contains("API limit reached")) {
                return ResponseEntity.status(402).body("API limit reached. Please try again tomorrow.");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }
    
    @GetMapping("/detailed")
    public ResponseEntity<?> getFullRecipe(@RequestParam String id) {
        try {
            Recipe recipe = recipeRestService.getDetailedRecipe(id);
            if (recipe == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No recipe found for the given ID.");
            }
            return ResponseEntity.ok(recipe);
        } catch (Exception e) {
            if (e.getMessage().contains("API limit reached")) {
                return ResponseEntity.status(402).body("API limit reached. Please try again tomorrow.");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }
    
}
