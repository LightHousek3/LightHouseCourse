package util;

import java.util.ArrayList;
import java.util.List;
import model.CartItem;
import model.Course;

/**
 * Represents a user's shopping cart.
 */
public class CartUtil {
    private List<CartItem> items;
    
    public CartUtil() {
        this.items = new ArrayList<>();
    }
    
    public List<CartItem> getItems() {
        return items;
    }
    
    public void setItems(List<CartItem> items) {
        this.items = items;
    }
    
    /**
     * Add a course to the cart.
     * 
     * @param course The course to add
     * @return true if added successfully, false if already exists
     */
    public boolean addItem(Course course) {
        // Check if course already exists in cart
        for (CartItem item : items) {
            if (item.getCourse().getCourseID() == course.getCourseID()) {
                return false; // Course already in cart
            }
        }
        
        CartItem newItem = new CartItem(course, course.getPrice());
        items.add(newItem);
        return true;
    }
    
    /**
     * Remove a course from the cart.
     * 
     * @param courseId The ID of the course to remove
     * @return true if removed successfully, false if not found
     */
    public boolean removeItem(int courseId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getCourse().getCourseID() == courseId) {
                items.remove(i);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get the total price of all items in the cart.
     * 
     * @return The total price
     */
    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getPrice();
        }
        return total;
    }
    
    /**
     * Get the number of items in the cart.
     * 
     * @return The number of items
     */
    public int getItemCount() {
        return items.size();
    }
    
    /**
     * Check if the cart is empty.
     * 
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    /**
     * Clear all items from the cart.
     */
    public void clear() {
        items.clear();
    }
    
    /**
     * Check if a course is in the cart.
     * 
     * @param courseId The ID of the course to check
     * @return true if in cart, false otherwise
     */
    public boolean containsCourse(int courseId) {
        for (CartItem item : items) {
            if (item.getCourse().getCourseID() == courseId) {
                return true;
            }
        }
        return false;
    }
} 