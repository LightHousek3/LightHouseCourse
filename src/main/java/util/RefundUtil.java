package util;

import dao.RefundRequestDAO;
import dao.CourseProgressDAO;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import model.Order;
import model.OrderDetail;
import model.CourseProgress;

/**
 * Utility class for handling refund-related operations
 */
public class RefundUtil {

    private static final RefundRequestDAO refundDAO = new RefundRequestDAO();
    private static final CourseProgressDAO progressDAO = new CourseProgressDAO();

    /**
     * Checks if a course is eligible for refund
     * Conditions:
     * 1. Purchased within 7 days
     * 2. Completed less than 20% of the course
     * 3. No pending refund requests
     * 
     * @param orderDate            The date when the course was purchased
     * @param completionPercentage The percentage of course completion
     * @param customerId               The user ID
     * @param courseId             The course ID
     * @return true if eligible for refund, false otherwise
     */
    public static boolean isEligibleForRefund(Date orderDate, double completionPercentage, int customerId, int courseId) {
        // Check if there's already a pending refund request for this course
        if (refundDAO.hasPendingRefundForCourse(customerId, courseId)) {
            return false;
        }

        // Check if there's already an approved refund request for this course
        if (refundDAO.hasApprovedRefundForCourse(customerId, courseId)) {
            return false;
        }

        // Calculate days since purchase
        Date now = new Date();
        long diffInMillies = Math.abs(now.getTime() - orderDate.getTime());
        long daysSincePurchase = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        // Check conditions:
        // 1. Purchased within 7 days
        boolean isWithin7Days = daysSincePurchase <= 7;

        // 2. Completed less than 20% of the course
        boolean isBelow20Percent = completionPercentage < 20;

        // Both conditions must be met
        return isWithin7Days && isBelow20Percent;
    }

    /**
     * Checks if an order is eligible for refund
     * 
     * @param order The order to check
     * @return true if eligible for refund, false otherwise
     */
    public static boolean isEligibleForRefund(Order order) {
        if (order == null) {
            return false;
        }

        // Check purchase time
        Date now = new Date();
        long diffInMillies = Math.abs(now.getTime() - order.getOrderDate().getTime());
        long daysSincePurchase = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        // Check time condition
        boolean isWithinTimeLimit = daysSincePurchase <= 7;

        // Check order status
        boolean hasValidStatus = "completed".equals(order.getStatus()) || "paid".equals(order.getStatus());

        // Check payment method (can add more conditions if needed)
        boolean hasValidPaymentMethod = order.getPaymentMethod() != null && !order.getPaymentMethod().isEmpty();

        return isWithinTimeLimit && hasValidStatus && hasValidPaymentMethod;
    }

    /**
     * Checks if an entire order is eligible for refund
     * All courses must be less than 20% completed and the order must be within 7
     * days
     * 
     * @param order  The order to check
     * @param customerId The ID of the user
     * @return true if the entire order is eligible for refund, false otherwise
     */
    public static boolean isEntireOrderEligibleForRefund(Order order, int customerId) {
        if (order == null || order.getOrderDetails() == null || order.getOrderDetails().isEmpty()) {
            System.out.println("1");
            return false;
        }

        // Check if there's already a pending refund for this order
        if (refundDAO.hasPendingRefundForOrder(customerId, order.getOrderID())) {
                        System.out.println("2");
            return false;
        }

        // Check if there's already an approved refund for this order
        if (refundDAO.hasApprovedRefundForOrder(customerId, order.getOrderID())) {
            return false;
        }

        // Check purchase time
        Date now = new Date();
        long diffInMillies = Math.abs(now.getTime() - order.getOrderDate().getTime());
        long daysSincePurchase = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        // Check time condition (must be within 7 days)
        if (daysSincePurchase > 7) {
            return false;
        }

        // Check order status
        boolean hasValidStatus = "completed".equals(order.getStatus()) || "paid".equals(order.getStatus());
        if (!hasValidStatus) {
            return false;
        }

        // Check each course's progress
        for (OrderDetail detail : order.getOrderDetails()) {
            CourseProgress progress = progressDAO.getByUserAndCourse(customerId, detail.getCourseID());
            BigDecimal percentage = (progress != null) ? progress.getCompletionPercentage() : BigDecimal.ZERO;

            // If any course exceeds the completion limit, the entire order is not eligible
            if (percentage.compareTo(BigDecimal.valueOf(20)) > 0) {
                return false;
            }

            // Check if there's a refund request for this specific course
            if (refundDAO.hasPendingRefundForCourse(customerId, detail.getCourseID()) ||
                    refundDAO.hasApprovedRefundForCourse(customerId, detail.getCourseID())) {
                return false;
            }
        }

        // All conditions passed
        return true;
    }

    /**
     * Checks if a specific course in an order is eligible for refund
     * 
     * @param order    The order containing the course
     * @param courseId The ID of the course
     * @param userId   The ID of the user
     * @return true if the course is eligible for refund, false otherwise
     */
    public static boolean isCourseEligibleForRefund(Order order, int courseId, int customerId) {
        if (order == null) {
            return false;
        }

        // Check if there's already a pending or approved refund for this course
        if (refundDAO.hasPendingRefundForCourse(customerId, courseId) ||
                refundDAO.hasApprovedRefundForCourse(customerId, courseId)) {
            return false;
        }

        // Check purchase time
        Date now = new Date();
        long diffInMillies = Math.abs(now.getTime() - order.getOrderDate().getTime());
        long daysSincePurchase = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        // Check time condition (must be within 7 days)
        if (daysSincePurchase > 7) {
            return false;
        }

        // Check course progress
        CourseProgress progress = progressDAO.getByUserAndCourse(customerId, courseId);
        BigDecimal percentage = (progress != null) ? progress.getCompletionPercentage() : BigDecimal.ZERO;

        // Check if course completion is below the limit
        return percentage.compareTo(BigDecimal.valueOf(20)) < 0;
    }

    /**
     * Calculates the refund amount for an order
     * 
     * @param order The order to calculate refund for
     * @return The refund amount
     */
    public static double calculateRefundAmount(Order order) {
        if (order == null) {
            return 0.0;
        }

        double totalAmount = order.getTotalAmount();
        int refundPercentage = getRefundPercentage(order);

        return (totalAmount * refundPercentage) / 100.0;
    }

    /**
     * Calculates the refund amount for a specific course in an order
     * 
     * @param order    The order containing the course
     * @param courseId The ID of the course
     * @return The refund amount for the course
     */
    public static double calculateCourseRefundAmount(Order order, int courseId) {
        if (order == null || order.getOrderDetails() == null) {
            return 0.0;
        }

        for (OrderDetail detail : order.getOrderDetails()) {
            if (detail.getCourseID() == courseId) {
                int refundPercentage = getRefundPercentage(order);
                return (detail.getPrice() * refundPercentage) / 100.0;
            }
        }

        return 0.0;
    }

    /**
     * Gets the refund percentage for an order
     * 
     * @param order The order to get refund percentage for
     * @return The refund percentage
     */
    public static int getRefundPercentage(Order order) {
        if (order == null) {
            return 0;
        }

        // Get refund percentage from configuration
        int defaultPercentage = 80;

        // Calculate days since purchase
        Date now = new Date();
        long diffInMillies = Math.abs(now.getTime() - order.getOrderDate().getTime());
        long daysSincePurchase = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        // Adjust refund percentage based on days since purchase
        // Example: 100% refund if within 1 day, default percentage if 2-7 days
        if (daysSincePurchase <= 1) {
            return 100;
        }

        return defaultPercentage;
    }
}
