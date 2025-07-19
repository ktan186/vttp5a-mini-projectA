# 🥦 Food Inventory System - Reducing Food Waste, One Item at a Time

## 📌 Overview

This is a **Food Inventory Management System** developed as a mini project to promote food sustainability by helping users reduce food wastage. Users can track their food inventory, get recipe suggestions based on their items, and earn badges for positive actions like logging in daily and avoiding expired items.

---

## 🚀 Features

### 🔐 User Authentication
- Landing page is the login form
- User ID: 1–30 characters  
- Password: Minimum 5 characters  
- Account creation with password confirmation
- Server-side validation for matching passwords and unique user ID
- Redirect to success page after registration

### 📦 Inventory Management
- View all inventory items sorted by expiry date
- Expired items highlighted in light pink
- Inline update of item quantity
- Add, update, and delete food items
- Duplicate item handling on add
- Filters for **category** and **storage type**
- Recipe suggestions update based on active filters
- Rotating tip image (3 images rotate each page refresh)

### 🍽️ Recipe Suggestions
- Dynamically suggested based on current inventory
- Save recipes for later
- Validation to prevent duplicate saves
- Error/success messages displayed
- View detailed recipe or visit source URL

### 📚 Saved Recipes
- View all saved recipes
- Unsave recipes
- Navigate to detailed view or source

### 🏆 Weekly Badges System
- Earn points by:
  - Logging in daily
  - Having no expired items
  - Saving recipes
- Get a badge every 5 points
- Progress resets every week

---

## 🛠️ Technologies Used

- **Backend**: Spring Boot (Java)
- **Frontend**: Thymeleaf templating engine
- **Styling**: Bootstrap 5
- **Database**: Redis
- **Recipe Integration**: External APIs (Spoonacular)

---
