package sg.edu.nus.iss.vttp5a_mini_project_a.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class Item implements Serializable {
    
    private String id;

    @NotEmpty(message = "Item name is required")
    @Size(min = 1, max = 150, message = "Name should have between 1 and 150 characters")
    private String name;

    @NotNull(message = "Item quantity is required")
    @Positive(message = "Item quantity must be greater than 0")
    private int quantity;

    @NotNull(message = "Expiration date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent(message = "Expiration date must be in the future")
    private Date expiryDate;

    @NotEmpty(message = "Category is required")
    private String category;

    @NotEmpty(message = "Storage location is required")
    private String storage;

    private String notes;

    public Item() {
    }

    public Item(String id,
            @NotEmpty(message = "Item name is required") @Size(min = 1, max = 150, message = "Name should have between 1 and 150 characters") String name,
            @NotNull(message = "Item quantity is required") @Positive(message = "Item quantity must be greater than 0") int quantity,
            @NotNull(message = "Expiration date is required") @FutureOrPresent(message = "Expiration date must be in the present or future") Date expiryDate,
            @NotEmpty(message = "Category is required") String category,
            @NotEmpty(message = "Storage location is required") String storage, String notes) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.category = category;
        this.storage = storage;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
