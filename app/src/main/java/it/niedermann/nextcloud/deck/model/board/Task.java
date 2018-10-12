package it.niedermann.nextcloud.deck.model.board;

import java.io.Serializable;
import java.time.LocalDate;

public class Task implements Serializable {

    private String title;
    private String description;
    private int stackId;
    private String type;
    private LocalDate lastModified;
    private LocalDate createdAt;
    private String labels;
    private String assignedUsers;
    private String attachments;
    private int attachmentCount;
    private String owner;
    private String primaryKey;
    private String uid;
    private String displayName;
    private String order;
    private boolean archived;
    private String dueDate;
    private long id;
    private String overdue;
    private long remoteId;

    public Task(String title, long id, long remoteId) {
        this.title = title;
        this.id = id;
        this.remoteId = remoteId;
    }
}
