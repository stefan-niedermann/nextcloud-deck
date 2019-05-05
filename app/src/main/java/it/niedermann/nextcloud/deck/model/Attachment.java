package it.niedermann.nextcloud.deck.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;

import java.util.Date;

import it.niedermann.nextcloud.deck.model.interfaces.AbstractRemoteEntity;

@Entity(inheritSuperIndices = true,
    foreignKeys = {
        @ForeignKey(
            entity = Card.class,
            parentColumns = "localId",
            childColumns = "cardId",
            onDelete = ForeignKey.CASCADE
        )
    }
)
public class Attachment extends AbstractRemoteEntity {

    private long cardId;
    private String type;
    private String data;
    private Date createdAt;
    private String createdBy;
    private Date deletedAt;
    private long filesize;
    private String mimetype;
    private String dirname;
    private String basename;
    private String extension;
    private String filename;

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public long getFilesize() {
        return filesize;
    }

    public void setFilesize(long filesize) {
        this.filesize = filesize;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getDirname() {
        return dirname;
    }

    public void setDirname(String dirname) {
        this.dirname = dirname;
    }

    public String getBasename() {
        return basename;
    }

    public void setBasename(String basename) {
        this.basename = basename;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "cardId=" + cardId +
                ", type='" + type + '\'' +
                ", data='" + data + '\'' +
                ", createdAt=" + createdAt +
                ", createdBy='" + createdBy + '\'' +
                ", deletedAt=" + deletedAt +
                ", filesize=" + filesize +
                ", mimetype='" + mimetype + '\'' +
                ", dirname='" + dirname + '\'' +
                ", basename='" + basename + '\'' +
                ", extension='" + extension + '\'' +
                ", filename='" + filename + '\'' +
                ", localId=" + localId +
                ", accountId=" + accountId +
                ", id=" + id +
                ", status=" + status +
                ", lastModified=" + lastModified +
                ", lastModifiedLocal=" + lastModifiedLocal +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attachment that = (Attachment) o;

        if (cardId != that.cardId) return false;
        if (filesize != that.filesize) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (data != null ? !data.equals(that.data) : that.data != null) return false;
        if (createdAt != null ? !createdAt.equals(that.createdAt) : that.createdAt != null)
            return false;
        if (createdBy != null ? !createdBy.equals(that.createdBy) : that.createdBy != null)
            return false;
        if (deletedAt != null ? !deletedAt.equals(that.deletedAt) : that.deletedAt != null)
            return false;
        if (mimetype != null ? !mimetype.equals(that.mimetype) : that.mimetype != null)
            return false;
        if (dirname != null ? !dirname.equals(that.dirname) : that.dirname != null) return false;
        if (basename != null ? !basename.equals(that.basename) : that.basename != null)
            return false;
        if (extension != null ? !extension.equals(that.extension) : that.extension != null)
            return false;
        return filename != null ? filename.equals(that.filename) : that.filename == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (cardId ^ (cardId >>> 32));
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (deletedAt != null ? deletedAt.hashCode() : 0);
        result = 31 * result + (int) (filesize ^ (filesize >>> 32));
        result = 31 * result + (mimetype != null ? mimetype.hashCode() : 0);
        result = 31 * result + (dirname != null ? dirname.hashCode() : 0);
        result = 31 * result + (basename != null ? basename.hashCode() : 0);
        result = 31 * result + (extension != null ? extension.hashCode() : 0);
        result = 31 * result + (filename != null ? filename.hashCode() : 0);
        return result;
    }
}