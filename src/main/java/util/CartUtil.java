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
     * Get the total price of selected items in the cart.
     * 
     * @return The total price of selected items
     */
    public double getSelectedTotalPrice() {
        double total = 0;
        for (CartItem item : items) {
            if (item.isSelected()) {
                total += item.getPrice();
            }
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
     * Get the number of selected items in the cart.
     * 
     * @return The number of selected items
     */
    public int getSelectedItemCount() {
        int count = 0;
        for (CartItem item : items) {
            if (item.isSelected()) {
                count++;
            }
        }
        return count;
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
     * Check if there are no selected items in the cart.
     * 
     * @return true if no items are selected, false otherwise
     */
    public boolean isNothingSelected() {
        for (CartItem item : items) {
            if (item.isSelected()) {
                return false;
            }
        }
        return true;
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

    /**
     * Select or deselect a course in the cart.
     * 
     * @param courseId The ID of the course to select/deselect
     * @param selected true to select, false to deselect
     * @return true if the course was found and updated, false otherwise
     */
    public boolean setItemSelected(int courseId, boolean selected) {
        for (CartItem item : items) {
            if (item.getCourse().getCourseID() == courseId) {
                item.setSelected(selected);
                return true;
            }
        }
        return false;
    }

    /**
     * Select all items in the cart.
     */
    public void selectAll() {
        for (CartItem item : items) {
            item.setSelected(true);
        }
    }

    /**
     * Deselect all items in the cart.
     */
    public void deselectAll() {
        for (CartItem item : items) {
            item.setSelected(false);
        }
    }

    /**
     * Get list of selected items in the cart.
     * 
     * @return List of selected cart items
     */
    public List<CartItem> getSelectedItems() {
        List<CartItem> selectedItems = new ArrayList<>();
        for (CartItem item : items) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }
}