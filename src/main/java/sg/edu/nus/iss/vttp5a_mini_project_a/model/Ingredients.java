package sg.edu.nus.iss.vttp5a_mini_project_a.model;

import java.util.List;

public class Ingredients {
    
    private Integer id;
    private String title;
    private String image;
    private Integer usedIngredientCount;
    private Integer missedIngredientCount;

    private List<String> usedIngredients;
    private List<String> missedIngredients;


    public Ingredients() {
    }

    public Ingredients(Integer id, String title, String image, Integer usedIngredientCount,
            Integer missedIngredientCount, List<String> usedIngredients, List<String> missedIngredients) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.usedIngredientCount = usedIngredientCount;
        this.missedIngredientCount = missedIngredientCount;
        this.usedIngredients = usedIngredients;
        this.missedIngredients = missedIngredients;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getUsedIngredientCount() {
        return usedIngredientCount;
    }

    public void setUsedIngredientCount(Integer usedIngredientCount) {
        this.usedIngredientCount = usedIngredientCount;
    }

    public Integer getMissedIngredientCount() {
        return missedIngredientCount;
    }

    public void setMissedIngredientCount(Integer missedIngredientCount) {
        this.missedIngredientCount = missedIngredientCount;
    }

    public List<String> getUsedIngredients() {
        return usedIngredients;
    }

    public void setUsedIngredients(List<String> usedIngredients) {
        this.usedIngredients = usedIngredients;
    }

    public List<String> getMissedIngredients() {
        return missedIngredients;
    }

    public void setMissedIngredients(List<String> missedIngredients) {
        this.missedIngredients = missedIngredients;
    }
    
    

}
