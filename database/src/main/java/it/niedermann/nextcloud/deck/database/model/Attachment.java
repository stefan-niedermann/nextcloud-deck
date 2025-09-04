package it.niedermann.nextcloud.deck.database.model;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import it.niedermann.nextcloud.deck.database.model.enums.EAttachmentType;
import it.niedermann.nextcloud.deck.database.model.interfaces.AbstractRemoteEntity;

@Entity(inheritSuperIndices = true,
        indices = {@Index("cardId")},
        foreignKeys = {
                @ForeignKey(
                        entity = Card.class,
                        parentColumns = "localId",
                        childColumns = "cardId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Account.class,
                        parentColumns = "id",
                        childColumns = "accountId", onDelete = ForeignKey.CASCADE
                )
        }
)
public class Attachment extends AbstractRemoteEntity implements Comparable<Attachment>, Serializable {

    private long cardId;
    // TODO use EAttachmentType
    private EAttachmentType type = EAttachmentType.DECK_FILE;
    private String data;
    private Instant createdAt;
    private String createdBy;
    private Instant deletedAt;
    private long filesize;
    private String mimetype;
    private String dirname;
    private String basename;
    private String extension;
    private String filename;
    private String localPath;
    @Nullable
    private Long fileId;

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public EAttachmentType getType() {
        return type;
    }

    public void setType(EAttachmentType type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
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

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    @Nullable
    public Long getFileId() {
        return this.fileId;
    }

    public void setFileId(@Nullable Long fileId) {
        this.fileId = fileId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Attachment that = (Attachment) o;

        if (cardId != that.cardId) return false;
        if (filesize != that.filesize) return false;
        if (!Objects.equals(type, that.type)) return false;
        if (!Objects.equals(data, that.data)) return false;
        if (!Objects.equals(createdAt, that.createdAt))
            return false;
        if (!Objects.equals(createdBy, that.createdBy))
            return false;
        if (!Objects.equals(deletedAt, that.deletedAt))
            return false;
        if (!Objects.equals(mimetype, that.mimetype))
            return false;
        if (!Objects.equals(dirname, that.dirname)) return false;
        if (!Objects.equals(basename, that.basename))
            return false;
        if (!Objects.equals(extension, that.extension))
            return false;
        if (!Objects.equals(filename, that.filename))
            return false;
        return Objects.equals(localPath, that.localPath);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (cardId ^ (cardId >>> 32));
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
        result = 31 * result + (localPath != null ? localPath.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Attachment other) {
        // DESC order
        long res = other.getModificationTimeForComparsion() - getModificationTimeForComparsion();
        if (res == 0) {
            return longToComparsionResult(other.getCreationTimeForComparsion() - getCreationTimeForComparsion());
        }
        return longToComparsionResult(res);
    }

    private static int longToComparsionResult(long diff) {
        if (diff > 0) {
            return 1;
        } else if (diff < 0) {
            return -1;
        }
        return 0;
    }

    public long getModificationTimeForComparsion() {
        if (lastModifiedLocal != null) {
            return lastModifiedLocal.toEpochMilli();
        }
        if (lastModified != null) {
            return lastModified.toEpochMilli();
        }
        return Instant.now().toEpochMilli();
    }

    public long getCreationTimeForComparsion() {
        if (createdAt != null) {
            return createdAt.toEpochMilli();
        }
        return Instant.now().toEpochMilli();
    }
}