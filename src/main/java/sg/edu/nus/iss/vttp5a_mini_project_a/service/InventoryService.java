package sg.edu.nus.iss.vttp5a_mini_project_a.service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.edu.nus.iss.vttp5a_mini_project_a.model.Item;
import sg.edu.nus.iss.vttp5a_mini_project_a.model.Recipe;
import sg.edu.nus.iss.vttp5a_mini_project_a.repository.MapRepo;

@Service
public class InventoryService {
    
    @Autowired
    MapRepo userRepo;

    public void saveInventory(String userId, List<Item> inventory) {
        userRepo.putValue(userId, "inventory", inventory);
    }

    @SuppressWarnings("unchecked")
    public List<Item> getInventory(String userId) {
        return (List<Item>) userRepo.getValue(userId, "inventory");
    }

    public void saveRecipes(String userId, List<Recipe> recipes) {
        userRepo.putValue(userId, "recipes", recipes);
    }

    @SuppressWarnings("unchecked")
    public List<Recipe> getRecipes(String userId) {
        return (List<Recipe>) userRepo.getValue(userId, "recipes");
    }

    public void incrementUserPoints(String userId, String category) {
        String mapKey = "points:" + category;
        Integer points = (Integer) userRepo.getValue(userId, mapKey);
        if (points == null) {
            points = 0;
        }
        points = points + 1;

        System.out.println("Current points: " + category + ": " + points);
        userRepo.putValue(userId, mapKey, points);
        System.out.println("Set raw value for " + mapKey + ": " + points);
    }

    public Integer getUserPoints(String userId, String category) {
        String mapKey = "points:" + category;
        Integer points = (Integer) userRepo.getValue(userId, mapKey);
        System.out.println("Retrieved points for " + mapKey + ": " + points);
        if (points != null) {
            return points;
        }
        return 0;
    }

    public void resetPoints(String userId, String category) {
        String mapKey = "points:" + category;
        userRepo.putValue(userId, mapKey, 0);
    }

    public void incrementExpiryPointsOnceDaily(String userId) {
        String lastUpdatedKey = "lastUpdated:expired";
        String lastUpdatedDate = (String) userRepo.getValue(userId, lastUpdatedKey);

        String today = LocalDate.now().toString();
        if (lastUpdatedDate == null || !lastUpdatedDate.equals(today)) {
            incrementUserPoints(userId, "expired");
            userRepo.putValue(userId, lastUpdatedKey, today);
        }
    }

    public void incrementLoginPointsOnceDaily(String userId) {
        String lastUpdatedKey = "lastUpdated:daily";
        String lastUpdatedDate = (String) userRepo.getValue(userId, lastUpdatedKey);
        System.out.println("Last updated date for daily points: " + lastUpdatedDate);

        String today = LocalDate.now().toString();
        if (lastUpdatedDate == null || !lastUpdatedDate.equals(today)) {
            incrementUserPoints(userId, "daily");
            userRepo.putValue(userId, lastUpdatedKey, today);
            System.out.println("Incrementing daily points for user: " + userId);
        }
    }

    public Boolean isNewWeek(String lastResetDate) {
        if (lastResetDate == null) {
            return true;
        }
        LocalDate lastReset = LocalDate.parse(lastResetDate);
        LocalDate now = LocalDate.now();

        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int lastWeek = lastReset.get(weekFields.weekOfWeekBasedYear());
        int currWeek = now.get(weekFields.weekOfWeekBasedYear());

        Boolean diffWeek = currWeek != lastWeek;
        Boolean diffYear = now.getYear() != lastReset.getYear();
        return (diffWeek || diffYear);
    }

    public void resetPointsWeekly(String userId) {
        String lastResetDate = (String) userRepo.getValue(userId, "lastReset");
        if (isNewWeek(lastResetDate) || lastResetDate == null) {
            resetPoints(userId, "expired");
            resetPoints(userId, "daily");
            resetPoints(userId, "recipes");

            String today = LocalDate.now().toString();
            userRepo.putValue(userId, "lastReset", today);
        }
    }

    public String getRandomTip() {
        List<String> images = new ArrayList<>();
        String image1 = "Donation.png";
        images.add(image1);
        String image2 = "Food Storage Chart.png";
        images.add(image2);
        String image3 = "Food Storage Tips.png";
        images.add(image3);

        int randomIndex = new Random().nextInt(images.size());
        String randomTip = images.get(randomIndex);
        return randomTip;
    }
}
