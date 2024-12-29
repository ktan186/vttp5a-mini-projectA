package sg.edu.nus.iss.vttp5a_mini_project_a.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import sg.edu.nus.iss.vttp5a_mini_project_a.model.Ingredients;
import sg.edu.nus.iss.vttp5a_mini_project_a.model.Recipe;

@Service
public class RecipeRestService {
    
    @Value("${recipe.api.key}")
    private String apiKey;

    private String IGD_API_URL = "https://api.spoonacular.com/recipes/findByIngredients?ingredients=";

    private String RCP_BULK_API_URL = "https://api.spoonacular.com/recipes/informationBulk?ids=";

    private String RCP_API_URL = "https://api.spoonacular.com/recipes/";

    RestTemplate restTemplate = new RestTemplate();

    public List<Ingredients> findByIngredients(List<String> ingredients, Integer number) {
        List<Ingredients> recipeIdeas = new ArrayList<>();
        List<Integer> recipeIds = new ArrayList<>();
        try {
            System.out.println(number);
            String ingredientList = String.join(",", ingredients);
            String ingredientUrl = IGD_API_URL + ingredientList + "&number=" + number + "&ranking=1&apiKey=" + apiKey;
            System.out.println("Calling Spoonacular API: " + ingredientUrl);

            // Use ResponseEntity to handle HTTP status codes
            ResponseEntity<String> response = restTemplate.getForEntity(ingredientUrl, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                String ingredientData = response.getBody();
                JsonReader jReader = Json.createReader(new StringReader(ingredientData));
                JsonArray jArray = jReader.readArray();

                for (int i = 0; i < jArray.size(); i ++) {
                    JsonObject recipeData = jArray.getJsonObject(i);
                    // Add recipe ID for validation
                    recipeIds.add(recipeData.getInt("id"));

                    Ingredients recipe = new Ingredients();
                    recipe.setId(recipeData.getInt("id"));
                    recipe.setTitle(recipeData.getString("title"));
                    recipe.setImage(recipeData.getString("image"));
                    recipe.setUsedIngredientCount(recipeData.getInt("usedIngredientCount"));
                    recipe.setMissedIngredientCount(recipeData.getInt("missedIngredientCount"));

                    List<String> usedIngredients = new ArrayList<>();
                    JsonArray usedArray = recipeData.getJsonArray("usedIngredients");
                    for (int j = 0; j < usedArray.size(); j ++) {
                        JsonObject usedIngredient = usedArray.getJsonObject(j);
                        String ingredient = usedIngredient.getString("original");
                        usedIngredients.add(ingredient);
                    }
                    recipe.setUsedIngredients(usedIngredients);

                    List<String> missedIngredients = new ArrayList<>();
                    JsonArray missedArray = recipeData.getJsonArray("missedIngredients");
                    for (int k = 0; k < missedArray.size(); k ++) {
                        JsonObject missedIngredient = missedArray.getJsonObject(k);
                        String ingredient = missedIngredient.getString("original");
                        missedIngredients.add(ingredient);
                    }
                    recipe.setMissedIngredients(missedIngredients);
                    recipeIdeas.add(recipe);
                }
            } else if (response.getStatusCode().value() == 402) {
                throw new RuntimeException("API limit reached. Please try again later.");
            } else {
                throw new RuntimeException("Error from Spoonacular API: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error calling Spoonacular API: " + e.getMessage());
            e.printStackTrace();
            throw e; 
        }

        // Validate recipes in bulk, ensure that detailed recipe can be found
        // Some recipes exist in the findbyingredients api, but cannot be found in the findbyid api
        List<Integer> validatedRecipes = getValidatedRecipes(recipeIds);
        System.out.println(validatedRecipes);

        // Filter out invalid recipes
        recipeIdeas = recipeIdeas.stream().filter(r -> validatedRecipes.contains(r.getId())).collect(Collectors.toList());
        System.out.println(recipeIdeas);

        return recipeIdeas;
    }
    
    public Recipe getDetailedRecipe(String id) {
        try {
            String recipeUrl = RCP_API_URL + id + "/information?apiKey=" + apiKey;
            System.out.println("Calling Recipe URL: " + recipeUrl);
            
            ResponseEntity<String> response = restTemplate.getForEntity(recipeUrl, String.class);
            String recipeData = response.getBody();
    
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonReader jReader = Json.createReader(new StringReader(recipeData));
                JsonObject jObject = jReader.readObject();
    
                Recipe r = new Recipe();
                r.setVegetarian(getSafeJsonBoolean(jObject, "vegetarian"));
                r.setVegan(getSafeJsonBoolean(jObject, "vegan"));
                r.setGlutenFree(getSafeJsonBoolean(jObject, "glutenFree"));
                r.setDairyFree(getSafeJsonBoolean(jObject, "dairyFree"));
                r.setPreparationMinutes(jObject.isNull("preparationMinutes") ? null : jObject.getInt("preparationMinutes"));
                r.setCookingMinutes(jObject.isNull("cookingMinutes") ? null : jObject.getInt("cookingMinutes"));
    
                // Parse extendedIngredients
                List<String> ingredients = new ArrayList<>();
                JsonArray jArray = jObject.getJsonArray("extendedIngredients");
                if (jArray != null) {
                    for (int i = 0; i < jArray.size(); i++) {
                        JsonObject ingredientObject = jArray.getJsonObject(i);
                        ingredients.add(getSafeJsonString(ingredientObject, "original"));
                    }
                }
                r.setExtendedIngredients(ingredients);
    
                r.setId(jObject.getInt("id"));
                r.setTitle(getSafeJsonString(jObject, "title"));
                r.setReadyInMinutes(jObject.isNull("readyInMinutes") ? null : jObject.getInt("readyInMinutes"));
                r.setServings(jObject.isNull("servings") ? null : jObject.getInt("servings"));
                r.setSourceUrl(getSafeJsonString(jObject, "sourceUrl"));
                r.setImage(getSafeJsonString(jObject, "image"));
                r.setSummary(getSafeJsonString(jObject, "summary"));
    
                // Parse cuisines
                List<String> cuisines = parseJsonArrayToList(jObject, "cuisines");
                r.setCuisines(cuisines);
    
                // Parse dishTypes
                List<String> dishTypes = parseJsonArrayToList(jObject, "dishTypes");
                r.setDishTypes(dishTypes);
    
                // Parse diets
                List<String> diets = parseJsonArrayToList(jObject, "diets");
                r.setDiets(diets);
    
                // Parse occasions
                List<String> occasions = parseJsonArrayToList(jObject, "occasions");
                r.setOccasions(occasions);
    
                r.setInstructions(getSafeJsonString(jObject, "instructions"));
    
                // Parse analyzedInstructions
                List<String> steps = new ArrayList<>();
                JsonArray analyzedInstructions = jObject.getJsonArray("analyzedInstructions");
                if (analyzedInstructions != null && !analyzedInstructions.isEmpty()) {
                    JsonObject ai = analyzedInstructions.getJsonObject(0);
                    JsonArray stepArray = ai.getJsonArray("steps");
                    if (stepArray != null) {
                        for (int i = 0; i < stepArray.size(); i++) {
                            JsonObject stepObject = stepArray.getJsonObject(i);
                            steps.add(getSafeJsonString(stepObject, "step"));
                        }
                    }
                }
                r.setStep(steps);
    
                // Add nutrition and taste URLs
                r.setNutrition("https://api.spoonacular.com/recipes/" + id + "/nutritionLabel?apiKey=" + apiKey);
                r.setTaste("https://api.spoonacular.com/recipes/" + id + "/tasteWidget.png?apiKey=" + apiKey);
    
                return r;
            } else if (response.getStatusCode().value() == 402) {
                throw new RuntimeException("API limit reached. Please try again later.");
            } else {
                throw new RuntimeException("Error from Spoonacular API: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error calling Spoonacular API: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private String getSafeJsonString(JsonObject jsonObject, String key) {
        if (jsonObject.containsKey(key) && !jsonObject.isNull(key)) {
            JsonValue value = jsonObject.get(key);
            if (value instanceof JsonString) {
                return ((JsonString) value).getString();
            }
        }
        return null;
    }

    private Boolean getSafeJsonBoolean(JsonObject jsonObject, String key) {
        if (jsonObject.containsKey(key) && !jsonObject.isNull(key)) {
            return jsonObject.getBoolean(key);
        }
        return null;
    }

    private List<String> parseJsonArrayToList(JsonObject jsonObject, String key) {
        List<String> list = new ArrayList<>();
        if (jsonObject.containsKey(key) && !jsonObject.isNull(key)) {
            JsonArray jsonArray = jsonObject.getJsonArray(key);
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonValue value = jsonArray.get(i);
                if (value instanceof JsonString) {
                    list.add(((JsonString) value).getString());
                }
            }
        }
        return list;
    }

    public List<Integer> getValidatedRecipes(List<Integer> recipeIds) {
        try {
            String bulkUrl = RCP_BULK_API_URL + 
                            String.join(",", recipeIds.stream().map(String::valueOf).toArray(String[]::new)) +
                            "&apiKey=" + apiKey;

            ResponseEntity<String> response = restTemplate.getForEntity(bulkUrl, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                String bulkData = response.getBody();
                JsonReader jReader = Json.createReader(new StringReader(bulkData));
                JsonArray jArray = jReader.readArray();

                // List<Recipe> validatedRecipes = new ArrayList<>();
                List<Integer> validatedRecipes = new ArrayList<>();

                for (int i = 0; i < jArray.size(); i++) {
                    JsonObject recipeData = jArray.getJsonObject(i);

                    Integer recipeId = recipeData.getInt("id");
                    validatedRecipes.add(recipeId);
                }
                return validatedRecipes;
            } else if (response.getStatusCode().value() == 402) {
                throw new RuntimeException("API limit reached. Please try again later.");
            } else {
                throw new RuntimeException("Error from Spoonacular API: " + response.getStatusCode());
            }
         } catch (Exception e) {
            System.err.println("Error calling Spoonacular API: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        
    }
    
}
