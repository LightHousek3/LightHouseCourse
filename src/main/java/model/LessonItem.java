/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 * Model class for LessonItems
 *
 * @author DangPH - CE180896
 */
public class LessonItem {

    private int lessonItemID;
    private int lessonID;
    private int orderIndex;
    private String itemType; // 'video', 'material', 'quiz'
    private int itemID;

    // Additional information
    private Object item; // This can be Video, Material, or Quiz object

    public LessonItem() {
    }

    public LessonItem(int lessonID, int orderIndex, String itemType, int itemID) {
        this.lessonID = lessonID;
        this.orderIndex = orderIndex;
        this.itemType = itemType;
        this.itemID = itemID;
    }

    public LessonItem(int lessonItemID, int lessonID, int orderIndex, String itemType, int itemID) {
        this.lessonItemID = lessonItemID;
        this.lessonID = lessonID;
        this.orderIndex = orderIndex;
        this.itemType = itemType;
        this.itemID = itemID;
    }

    /**
     * @return the lessonItemID
     */
    public int getLessonItemID() {
        return lessonItemID;
    }

    /**
     * @param lessonItemID the lessonItemID to set
     */
    public void setLessonItemID(int lessonItemID) {
        this.lessonItemID = lessonItemID;
    }

    /**
     * @return the lessonID
     */
    public int getLessonID() {
        return lessonID;
    }

    /**
     * @param lessonID the lessonID to set
     */
    public void setLessonID(int lessonID) {
        this.lessonID = lessonID;
    }

    /**
     * @return the orderIndex
     */
    public int getOrderIndex() {
        return orderIndex;
    }

    /**
     * @param orderIndex the orderIndex to set
     */
    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    /**
     * @return the itemType
     */
    public String getItemType() {
        return itemType;
    }

    /**
     * @param itemType the itemType to set
     */
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    /**
     * @return the itemID
     */
    public int getItemID() {
        return itemID;
    }

    /**
     * @param itemID the itemID to set
     */
    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    /**
     * @return the associated item object (Video, Material, or Quiz)
     */
    public Object getItem() {
        return item;
    }

    /**
     * @param item the item to set
     */
    public void setItem(Object item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "LessonItem{" + "lessonItemID=" + lessonItemID + ", lessonID=" + lessonID + ", orderIndex=" + orderIndex + ", itemType=" + itemType + ", itemID=" + itemID + ", item=" + item + '}';
    }

}
