package it.niedermann.nextcloud.deck.model.propagation;

public class Reorder {
    Integer order;
    Integer newStackId;

    public Reorder(Integer order, Integer newStackId) {
        this.order = order;
        this.newStackId = newStackId;
    }

    public Reorder() {
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getNewStackId() {
        return newStackId;
    }

    public void setNewStackId(Integer newStackId) {
        this.newStackId = newStackId;
    }
}
