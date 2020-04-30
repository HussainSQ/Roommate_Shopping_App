package edu.uga.cs.finalproject;

public class GroceryItem {
    private String title;
    private String description;
    private int priority;
    private String groceryList;

    public GroceryItem(){
    }

    public GroceryItem(String title, String description, int priority, String groceryList){
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.groceryList = groceryList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setGroceryList(String groceryList) { this.groceryList = groceryList; }

    public String getGroceryList() {
        return groceryList;
    }

}
