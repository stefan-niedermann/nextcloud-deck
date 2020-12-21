package it.niedermann.nextcloud.deck.model.ocs.projects.to;

public class OcsProjectNameUpdate {
    private String collectionName;

    public OcsProjectNameUpdate(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }
}
