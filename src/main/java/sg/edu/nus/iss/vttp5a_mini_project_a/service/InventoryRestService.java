package sg.edu.nus.iss.vttp5a_mini_project_a.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import sg.edu.nus.iss.vttp5a_mini_project_a.model.Ingredients;
import sg.edu.nus.iss.vttp5a_mini_project_a.model.Recipe;

@Service
public class InventoryRestService {
    
    @Value("${api.base.url}")
    private String apiBaseUrl;

    public List<Ingredients> getRecipeIdeas(List<String> ingredientNames, Integer number) {
        RestTemplate restTemplate = new RestTemplate();
        String ingredientQuery = String.join(",", ingredientNames);
        String recipeIdeaUrl = apiBaseUrl + "/api/recipes/suggest?ingredients=" + ingredientQuery + "&number=" + number;
        
        try {
            ResponseEntity<List<Ingredients>> response = restTemplate.exchange(recipeIdeaUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<Ingredients>>() {});

            if (response.getStatusCode() == HttpStatus.OK) {
                List<Ingredients> recipes = response.getBody();
                return recipes;
            } else if (response.getStatusCode() == HttpStatusCode.valueOf(402)) {
                throw new RuntimeException("API limit reached. Please try again later.");
            } else {
                System.err.println("API call failed with status: " + response.getStatusCode());
                throw new RuntimeException("Error from Recipe API: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error calling Recipe API: " + e.getMessage());
            throw e; 
        }
    }

    public Recipe getRecipeInfo(String id) {
        RestTemplate restTemplate = new RestTemplate();
        String recipeInfoUrl = apiBaseUrl + "/api/recipes/detailed?id=" + id;

        try {
            ResponseEntity<Recipe> response = restTemplate.exchange(recipeInfoUrl, HttpMethod.GET,null, new ParameterizedTypeReference<Recipe>() {});

            if (response.getStatusCode() == HttpStatus.OK) {
                Recipe recipe = response.getBody();
                return recipe;
            } else if (response.getStatusCode() == HttpStatusCode.valueOf(402)) {
                throw new RuntimeException("API limit reached. Please try again later.");
            } else {
                System.err.println("API call failed with status: " + response.getStatusCode());
                throw new RuntimeException("Error from Recipe API: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error calling Recipe API: " + e.getMessage());
            throw e;
        }
    }
}
