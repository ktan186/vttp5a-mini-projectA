package sg.edu.nus.iss.vttp5a_mini_project_a.model;

import java.io.Serializable;
import java.util.List;

public class Recipe implements Serializable{
    
    private Boolean vegetarian;
    private Boolean vegan;
    private Boolean glutenFree;
    private Boolean dairyFree;

    private Integer preparationMinutes;
    private Integer cookingMinutes;

    private List<String> extendedIngredients;

    private Integer id;
    private String title;
    private Integer readyInMinutes;
    private Integer servings;
    private String sourceUrl;
    private String image;
    private String summary;

    private List<String> cuisines;
    private List<String> dishTypes;
    private List<String> diets;
    private List<String> occasions;

    private String instructions;
    private List<String> step;

    private String nutrition;
    private String taste;


    public Recipe() {
    }
    public Recipe(Boolean vegetarian, Boolean vegan, Boolean glutenFree, Boolean dairyFree, Integer preparationMinutes,
            Integer cookingMinutes, List<String> extendedIngredients, Integer id, String title, Integer readyInMinutes,
            Integer servings, String sourceUrl, String image, String summary, List<String> cuisines,
            List<String> dishTypes, List<String> diets, List<String> occasions, String instructions, List<String> step,
            String nutrition, String taste) {
        this.vegetarian = vegetarian;
        this.vegan = vegan;
        this.glutenFree = glutenFree;
        this.dairyFree = dairyFree;
        this.preparationMinutes = preparationMinutes;
        this.cookingMinutes = cookingMinutes;
        this.extendedIngredients = extendedIngredients;
        this.id = id;
        this.title = title;
        this.readyInMinutes = readyInMinutes;
        this.servings = servings;
        this.sourceUrl = sourceUrl;
        this.image = image;
        this.summary = summary;
        this.cuisines = cuisines;
        this.dishTypes = dishTypes;
        this.diets = diets;
        this.occasions = occasions;
        this.instructions = instructions;
        this.step = step;
        this.nutrition = nutrition;
        this.taste = taste;
    }
    public Boolean getVegetarian() {
        return vegetarian;
    }
    public void setVegetarian(Boolean vegetarian) {
        this.vegetarian = vegetarian;
    }
    public Boolean getVegan() {
        return vegan;
    }
    public void setVegan(Boolean vegan) {
        this.vegan = vegan;
    }
    public Boolean getGlutenFree() {
        return glutenFree;
    }
    public void setGlutenFree(Boolean glutenFree) {
        this.glutenFree = glutenFree;
    }
    public Boolean getDairyFree() {
        return dairyFree;
    }
    public void setDairyFree(Boolean dairyFree) {
        this.dairyFree = dairyFree;
    }
    public Integer getPreparationMinutes() {
        return preparationMinutes;
    }
    public void setPreparationMinutes(Integer preparationMinutes) {
        this.preparationMinutes = preparationMinutes;
    }
    public Integer getCookingMinutes() {
        return cookingMinutes;
    }
    public void setCookingMinutes(Integer cookingMinutes) {
        this.cookingMinutes = cookingMinutes;
    }
    public List<String> getExtendedIngredients() {
        return extendedIngredients;
    }
    public void setExtendedIngredients(List<String> extendedIngredients) {
        this.extendedIngredients = extendedIngredients;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Integer getReadyInMinutes() {
        return readyInMinutes;
    }
    public void setReadyInMinutes(Integer readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }
    public Integer getServings() {
        return servings;
    }
    public void setServings(Integer servings) {
        this.servings = servings;
    }
    public String getSourceUrl() {
        return sourceUrl;
    }
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public List<String> getCuisines() {
        return cuisines;
    }
    public void setCuisines(List<String> cuisines) {
        this.cuisines = cuisines;
    }
    public List<String> getDishTypes() {
        return dishTypes;
    }
    public void setDishTypes(List<String> dishTypes) {
        this.dishTypes = dishTypes;
    }
    public List<String> getDiets() {
        return diets;
    }
    public void setDiets(List<String> diets) {
        this.diets = diets;
    }
    public List<String> getOccasions() {
        return occasions;
    }
    public void setOccasions(List<String> occasions) {
        this.occasions = occasions;
    }
    public String getInstructions() {
        return instructions;
    }
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    public List<String> getStep() {
        return step;
    }
    public void setStep(List<String> step) {
        this.step = step;
    }
    public String getNutrition() {
        return nutrition;
    }
    public void setNutrition(String nutrition) {
        this.nutrition = nutrition;
    }
    public String getTaste() {
        return taste;
    }
    public void setTaste(String taste) {
        this.taste = taste;
    }
    @Override
    public String toString() {
        return "Recipe [vegetarian=" + vegetarian + ", vegan=" + vegan + ", glutenFree=" + glutenFree + ", dairyFree="
                + dairyFree + ", preparationMinutes=" + preparationMinutes + ", cookingMinutes=" + cookingMinutes
                + ", extendedIngredients=" + extendedIngredients + ", id=" + id + ", title=" + title
                + ", readyInMinutes=" + readyInMinutes + ", servings=" + servings + ", sourceUrl=" + sourceUrl
                + ", image=" + image + ", summary=" + summary + ", cuisines=" + cuisines + ", dishTypes=" + dishTypes
                + ", diets=" + diets + ", occasions=" + occasions + ", instructions=" + instructions + ", step=" + step
                + ", nutrition=" + nutrition + ", taste=" + taste + "]";
    }

    

}
