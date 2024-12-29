package sg.edu.nus.iss.vttp5a_mini_project_a.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.edu.nus.iss.vttp5a_mini_project_a.model.Ingredients;
import sg.edu.nus.iss.vttp5a_mini_project_a.model.Item;
import sg.edu.nus.iss.vttp5a_mini_project_a.model.Login;
import sg.edu.nus.iss.vttp5a_mini_project_a.model.Recipe;
import sg.edu.nus.iss.vttp5a_mini_project_a.service.InventoryRestService;
import sg.edu.nus.iss.vttp5a_mini_project_a.service.InventoryService;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    InventoryService inventoryService;

    @Autowired
    InventoryRestService inventoryRestService;
    
    @GetMapping("")
    public String getInventory(@RequestParam(required = false) String categoryFilter, @RequestParam(required = false) String storageFilter, HttpSession session, Model model) {
        Login user = (Login) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        model.addAttribute("user", user.getUserId());
        // Reset user point every week
        inventoryService.resetPointsWeekly(user.getUserId());
        // Increment user points for daily login (once a day)
        inventoryService.incrementLoginPointsOnceDaily(user.getUserId());
        
        List<Item> inventory = new ArrayList<>();
        inventory = inventoryService.getInventory(user.getUserId());

        String randomTip = inventoryService.getRandomTip();
        model.addAttribute("randomTip", randomTip);

        if (inventory != null) {
            // Apply category filter
            if (categoryFilter != null && !categoryFilter.isEmpty()) {
                inventory = inventory.stream().filter(i -> i.getCategory().equals(categoryFilter)).collect(Collectors.toList());
                model.addAttribute("categoryFilter", categoryFilter);
            }
            // Apply storage filter
            if (storageFilter != null && !storageFilter.isEmpty()) {
                inventory = inventory.stream().filter(i -> i.getStorage().equals(storageFilter)).collect(Collectors.toList());
                model.addAttribute("storageFilter", storageFilter);
            }
            // Sort items according to expiry date
            inventory = inventory.stream().sorted(Comparator.comparing(Item::getExpiryDate)).collect(Collectors.toList());

            model.addAttribute("items", inventory);
        
            // Increment points if user does not have expired items in inventory (once a day)
            boolean hasExpired = inventory.stream().anyMatch(i -> i.getExpiryDate().toInstant().isBefore(Instant.now()));
            if (!hasExpired) {
                inventoryService.incrementExpiryPointsOnceDaily(user.getUserId());
            } 

            // Use inventory to get recipe suggestions from api
            List<String> ingredientNames = inventory.stream().map(Item::getName).collect(Collectors.toList());
            try {
                List<Ingredients> recipes = inventoryRestService.getRecipeIdeas(ingredientNames, 3);
                model.addAttribute("recipes", recipes);
                return "inventorylist";
            } catch (Exception e) {
                model.addAttribute("error", "Error calling Recipe API. Please try again later.");
                return "inventorylist";
            }
        }
        
        model.addAttribute("error", "Inventory is empty. Add items to inventory to view recipe suggestions.");
        return "inventorylist";
        
    }

    @GetMapping("/add")
    public String addItem(HttpSession session, Model model) {
        Login user = (Login) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        Item item = new Item();
        model.addAttribute("item", item);
        return "additem";
    }

    @PostMapping("/add")
    public String postAddItem(@Valid @ModelAttribute("item") Item item, BindingResult result, HttpSession session, Model model, @RequestParam(value = "duplicate", defaultValue = "false") boolean duplicate) {
        if (result.hasErrors()) {
            return "additem";
        }

        Login user = (Login) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }

        List<Item> inventory = inventoryService.getInventory(user.getUserId());
        if (inventory == null) {
            inventory = new ArrayList<>();
        }
        // Check if user has item with same name in inventory
        List<Item> duplicateItems = inventory.stream().filter(existingItem -> existingItem.getName().equalsIgnoreCase(item.getName())).collect(Collectors.toList());
        
        // Shows duplicate items and ask user if they want to update existing items when they click submit the 1st time
        // Allow user to save duplicate item if they click submit a 2nd time
        if (!duplicate && !duplicateItems.isEmpty()) {
            if (item.getId() == null || item.getId().isEmpty()) {
                model.addAttribute("duplicateItems", duplicateItems);
                model.addAttribute("error", "Inventory already contains item, would you like to update item instead?");
                model.addAttribute("item", item);
                return "additem";
            }
        }
        // Set unique id for item
        if (item.getId() == null || item.getId().isEmpty()) {
            item.setId(UUID.randomUUID().toString());
        }
        inventory.add(item);
        inventoryService.saveInventory(user.getUserId(), inventory);

        return "redirect:/inventory";
    }
    
    @GetMapping("/delete/{item-id}")
    public String deleteItem(@PathVariable("item-id") String itemId, HttpSession session) {
        Login user = (Login) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }

        List<Item> inventory = inventoryService.getInventory(user.getUserId());
        inventory.removeIf(i -> i.getId().equals(itemId));

        inventoryService.saveInventory(user.getUserId(), inventory);
        return "redirect:/inventory";
    }

    @GetMapping("/update/{item-id}")
    public String updateItem(@PathVariable("item-id") String itemId, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Login user = (Login) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        
        List<Item> inventory = inventoryService.getInventory(user.getUserId());
        Item item = inventory.stream().filter(i -> i.getId().equals(itemId)).findFirst().orElse(null);

        if (item == null) {
            redirectAttributes.addFlashAttribute("error", "Item not found.");
            // model.addAttribute("error", "Item not found.");
            return "redirect:/inventory";
        }
        model.addAttribute("item", item);
        return "updateitem";
    }

    // To update the entire item
    @PostMapping("/update")
    public String postUpdateItem(@Valid @ModelAttribute("item") Item item, BindingResult result, HttpSession session, Model model) {
        if (result.hasErrors()) {
            return "updateitem";
        }

        Login user = (Login) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }

        List<Item> inventory = inventoryService.getInventory(user.getUserId());
        inventory.removeIf(i -> i.getId().equals(item.getId()));
        inventory.add(item);
        inventoryService.saveInventory(user.getUserId(), inventory);
        return "redirect:/inventory";
    }
    
    // To update item quantity from the inventorylist page
    @PostMapping("/update/quantity/{item-id}")
    public String updateItemQuantity(@PathVariable("item-id") String itemId, @RequestParam("quantity") int quantity, HttpSession session) {
        Login user = (Login) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        
        List<Item> inventory = inventoryService.getInventory(user.getUserId());
        inventory.stream().filter(i -> i.getId().equals(itemId)).findFirst().ifPresent(item -> item.setQuantity(item.getQuantity() + quantity));
        inventoryService.saveInventory(user.getUserId(), inventory);
        return "redirect:/inventory";
    }

    @GetMapping("/suggestions")
    public String getRecipeSuggestions(HttpSession session, Model model) {
        Login user = (Login) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        model.addAttribute("user", user.getUserId());

        List<Item> inventory = new ArrayList<>();
        inventory = inventoryService.getInventory(user.getUserId());

        if (inventory != null) {
            List<String> ingredientNames = inventory.stream().map(Item::getName).collect(Collectors.toList());
            try {
                List<Ingredients> recipes = inventoryRestService.getRecipeIdeas(ingredientNames, 15);
                model.addAttribute("recipes", recipes);
                return "recipesuggestion";
            } catch (Exception e) {
                model.addAttribute("error", "Error calling Recipe API. Please try again later.");
                return "recipesuggestion";
            }
        }
        
        model.addAttribute("error", "Inventory is empty. Add items to inventory to view recipe suggestions.");
        return "recipesuggestion";
    }
    
    
    @GetMapping("/detailedRecipe/{id}")
    public String getDetailedRecipe(@PathVariable("id") String id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Login user = (Login) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        model.addAttribute("user", user.getUserId());

        try {
            Recipe recipe = inventoryRestService.getRecipeInfo(id);
            // System.out.println(recipe.toString());
            model.addAttribute("recipe", recipe);
            return "detailedrecipe";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error calling Recipe API. Please try again later.");
            // model.addAttribute("error", "Error calling Recipe API. Please try again later.");
            return "redirect:/inventory/suggestions";
        }
    }
    
    @PostMapping("/saveRecipe")
    public String saveRecipe(@RequestParam("recipeId") String recipeId, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Login user = (Login) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        String userId = user.getUserId();

        try {
            Recipe recipe = inventoryRestService.getRecipeInfo(recipeId);
            List<Recipe> savedRecipes = inventoryService.getRecipes(userId);

            if (savedRecipes == null) {
                savedRecipes = new ArrayList<>();
            }

            if (recipe != null) {
                if (savedRecipes.stream().noneMatch(r -> r.getId().toString().equals(recipeId))) {
                    savedRecipes.add(recipe);
                    inventoryService.saveRecipes(userId, savedRecipes);
                    redirectAttributes.addFlashAttribute("success", "Recipe saved successfully!");
                    // model.addAttribute("success", "Recipe saved successfully!");
                    inventoryService.incrementUserPoints(userId, "recipes");
                } else {
                    redirectAttributes.addFlashAttribute("error", "Recipe is already saved.");
                    // model.addAttribute("error", "Recipe is already saved.");
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to save recipe.");
                // model.addAttribute("error", "Failed to save recipe.");
            }
            return "redirect:/inventory/suggestions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to save recipe.");
            return "redirect:/inventory/suggestions";
        }
        
    }
    
    @GetMapping("/savedRecipes")
    public String getSavedRecipes(HttpSession session, Model model) {
        Login user = (Login) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        model.addAttribute("user", user.getUserId());

        String userId = user.getUserId();
        List<Recipe> savedRecipes = inventoryService.getRecipes(userId);
        if (savedRecipes == null) {
            savedRecipes = new ArrayList<>();
        }

        model.addAttribute("recipes", savedRecipes);
        return "savedrecipes";
    }
    
    @GetMapping("/savedRecipe/{id}")
    public String getSavedDetailedRecipe(@PathVariable("id") String id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Login user = (Login) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        model.addAttribute("user", user.getUserId());

        List<Recipe> savedRecipes = inventoryService.getRecipes(user.getUserId());
        Recipe recipe = savedRecipes.stream().filter(r -> r.getId().toString().equals(id)).findFirst().get();
        if (recipe == null) {
            System.err.println("Recipe not found for id: " + id);
            redirectAttributes.addFlashAttribute("error", "Recipe not found.");
            // model.addAttribute("error", "Recipe not found.");
            return "redirect:/inventory/savedRecipes";
        }
        // System.out.println(recipe.toString());
        model.addAttribute("recipe", recipe);
        return "saveddetailedrecipe";
    }

    @PostMapping("/unsaveRecipe")
    public String unsaveRecipe(@RequestParam("recipeId") String recipeId, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Login user = (Login) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }

        String userId = user.getUserId();
        List<Recipe> savedRecipes = inventoryService.getRecipes(userId);

        if (savedRecipes != null) {
            savedRecipes.removeIf(recipe -> recipe.getId().toString().equals(recipeId));
            inventoryService.saveRecipes(userId, savedRecipes);
            redirectAttributes.addFlashAttribute("success", "Recipe unsaved successfully!");
            // model.addAttribute("success", "Recipe unsaved successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "No saved recipes found.");
            // model.addAttribute("error", "No saved recipes found.");
        }
        return "redirect:/inventory/savedRecipes";
    }

    @GetMapping("/badges")
    public String getBadges(HttpSession session, Model model) {
        Login user = (Login) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        model.addAttribute("user", user.getUserId());

        int badgeThreshold = 5;
        Integer ePoints = inventoryService.getUserPoints(user.getUserId(), "expired");
        Integer rPoints = inventoryService.getUserPoints(user.getUserId(), "recipes");
        Integer dPoints = inventoryService.getUserPoints(user.getUserId(), "daily");

        int eProgress = (ePoints != null) ? Math.min((ePoints * 100) / badgeThreshold, 100) : 0;
        int rProgress = (rPoints != null) ? Math.min((rPoints * 100) / badgeThreshold, 100) : 0;
        int dProgress = (dPoints != null) ? Math.min((dPoints * 100) / badgeThreshold, 100) : 0;

        List<String> badges = new ArrayList<>();
        if (eProgress == 100) {
            badges.add("Inventory Master");
        }
        if (rProgress == 100) {
            badges.add("Recipe Saver");
        }
        if (dProgress == 100) {
            badges.add("Daily Logger");
        }
        // System.out.println("Daily Points: " + dPoints);
        // System.out.println("Daily Progress: " + dProgress);
        model.addAttribute("badges", badges);
        model.addAttribute("eProgress", eProgress);
        model.addAttribute("rProgress", rProgress);
        model.addAttribute("dProgress", dProgress);

        return "badges";
    }
    

}
