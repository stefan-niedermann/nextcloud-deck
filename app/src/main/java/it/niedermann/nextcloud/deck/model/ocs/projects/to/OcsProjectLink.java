package it.niedermann.nextcloud.deck.model.ocs.projects.to;

public class OcsProjectLink {
    public static final String CARD_RESOURCE_TYPE = "deck-card";
    public static final String BOARD_RESOURCE_TYPE = "deck";
    private String resourceType = CARD_RESOURCE_TYPE;
    /** the remote card- or boardId  */
    private String resourceId;

    public OcsProjectLink(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
