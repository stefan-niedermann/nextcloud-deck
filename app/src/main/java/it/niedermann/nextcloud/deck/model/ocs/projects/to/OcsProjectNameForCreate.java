package it.niedermann.nextcloud.deck.model.ocs.projects.to;

public class OcsProjectNameForCreate {
    private String name;

    public OcsProjectNameForCreate(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
