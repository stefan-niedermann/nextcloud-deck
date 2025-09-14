package it.niedermann.nextcloud.deck.database.entity.full;

import androidx.annotation.NonNull;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.deck.database.entity.ocs.projects.JoinCardWithProject;
import it.niedermann.nextcloud.deck.database.entity.ocs.projects.OcsProject;
import it.niedermann.nextcloud.deck.database.entity.ocs.projects.full.OcsProjectWithResources;

public class FullCardWithProjects extends FullCard {


    @NonNull
    @Relation(entity = OcsProject.class, parentColumn = "localId", entityColumn = "localId",
            associateBy = @Junction(value = JoinCardWithProject.class, parentColumn = "cardId", entityColumn = "projectId"))

    private List<OcsProjectWithResources> projects = new ArrayList<>();

    public FullCardWithProjects() {
        super();
    }

    public FullCardWithProjects(FullCardWithProjects fullCard) {
        super(fullCard);
        this.projects = copyList(fullCard.getProjects());
    }

    @NonNull
    public List<OcsProjectWithResources> getProjects() {
        return projects;
    }

    public void setProjects(@NonNull List<OcsProjectWithResources> projects) {
        this.projects = projects;
    }

    @NonNull
    @Override
    public String toString() {
        return "FullCard{" +
                "card=" + card +
                ", labels=" + labels +
                ", assignedUsers=" + assignedUsers +
                ", owner=" + owner +
                ", attachments=" + attachments +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FullCardWithProjects fullCard = (FullCardWithProjects) o;

        if (!Objects.equals(card, fullCard.card)) return false;
        if (!Objects.equals(labels, fullCard.labels))
            return false;
        if (!Objects.equals(assignedUsers, fullCard.assignedUsers))
            return false;
        if (!Objects.equals(owner, fullCard.owner)) return false;
        if (!Objects.equals(attachments, fullCard.attachments))
            return false;
        return Objects.equals(commentIDs, fullCard.commentIDs);
    }

    @Override
    public int hashCode() {
        int result = (isAttachmentsSorted ? 1 : 0);
        result = 31 * result + (card != null ? card.hashCode() : 0);
        result = 31 * result + (labels != null ? labels.hashCode() : 0);
        result = 31 * result + (assignedUsers != null ? assignedUsers.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (attachments != null ? attachments.hashCode() : 0);
        result = 31 * result + (commentIDs != null ? commentIDs.hashCode() : 0);
        return result;
    }
}
