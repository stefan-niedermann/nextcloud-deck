package it.niedermann.nextcloud.deck.model.ocs.projects;

import java.util.ArrayList;
import java.util.List;

public class OcsProjectList {
    List<OcsProject>  projects;

    public OcsProjectList() {
        projects = new ArrayList<>();
    }

    public OcsProjectList(List<OcsProject> projects) {
        this.projects = projects;
    }

    public List<OcsProject> getProjects() {
        return projects;
    }

    public void add(OcsProject project) {
        projects.add(project);
    }
}
