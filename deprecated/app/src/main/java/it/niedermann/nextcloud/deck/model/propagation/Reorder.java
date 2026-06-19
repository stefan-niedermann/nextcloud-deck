package it.niedermann.nextcloud.deck.model.propagation;

public class Reorder {
    Integer order;
    Integer stackId;

    public Reorder(Integer order, Integer stackId) {
        this.order = order;
        this.stackId = stackId;
    }

    public Reorder() {
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getStackId() {
        return stackId;
    }

    public void setStackId(Integer stackId) {
        this.stackId = stackId;
    }
}
