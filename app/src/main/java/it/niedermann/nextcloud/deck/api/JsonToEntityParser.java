package it.niedermann.nextcloud.deck.api;

import android.graphics.Color;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import it.niedermann.android.util.ColorUtil;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.exceptions.DeckException;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.enums.ActivityType;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.model.ocs.Version;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.Mention;
import it.niedermann.nextcloud.deck.model.ocs.comment.OcsComment;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProject;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectList;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectResource;
import it.niedermann.nextcloud.deck.model.ocs.user.GroupMemberUIDs;
import it.niedermann.nextcloud.deck.model.ocs.user.OcsUser;
import it.niedermann.nextcloud.deck.model.ocs.user.OcsUserList;

import static it.niedermann.nextcloud.deck.exceptions.DeckException.Hint.CAPABILITIES_VERSION_NOT_PARSABLE;
import static it.niedermann.nextcloud.deck.exceptions.TraceableException.makeTraceableIfFails;

public class JsonToEntityParser {

    @SuppressWarnings("unchecked")
    protected static <T> T parseJsonObject(JsonObject obj, Class<T> mType) {
        if (mType == FullBoard.class) {
            return (T) parseBoard(obj);
        } else if (mType == FullCard.class) {
            return (T) parseCard(obj);
        } else if (mType == FullStack.class) {
            return (T) parseStack(obj);
        } else if (mType == Label.class) {
            return (T) parseLabel(obj);
        } else if (mType == Activity.class) {
            return (T) parseActivity(obj);
        } else if (mType == Capabilities.class) {
            return (T) parseCapabilities(obj);
        } else if (mType == OcsUserList.class) {
            return (T) parseOcsUserList(obj);
        } else if (mType == OcsUser.class) {
            return (T) parseSingleOcsUser(obj);
        } else if (mType == Attachment.class) {
            return (T) parseAttachment(obj);
        } else if (mType == OcsComment.class) {
            return (T) parseOcsComment(obj);
        } else if (mType == GroupMemberUIDs.class) {
            return (T) parseGroupMemberUIDs(obj);
        } else if (mType == OcsProjectList.class) {
            return (T) parseOcsProjectList(obj);
        }
        throw new IllegalArgumentException("unregistered type: " + mType.getCanonicalName());
    }

    private static GroupMemberUIDs parseGroupMemberUIDs(JsonObject obj) {
        DeckLog.verbose(obj.toString());
        GroupMemberUIDs uids = new GroupMemberUIDs();
        makeTraceableIfFails(() -> {
            JsonElement data = obj.get("ocs").getAsJsonObject().get("data");
            if (!data.isJsonNull() && data.getAsJsonObject().has("users")) {
                JsonElement users = data.getAsJsonObject().get("users");
                if (!users.isJsonNull() && users.isJsonArray()) {
                    for (JsonElement userElement : users.getAsJsonArray()) {
                        uids.add(userElement.getAsString());
                    }
                }
            }

        }, obj);
        return uids;
    }

    private static OcsUserList parseOcsUserList(JsonObject obj) {
        DeckLog.verbose(obj.toString());
        OcsUserList ocsUserList = new OcsUserList();
        makeTraceableIfFails(() -> {
            JsonElement data = obj.get("ocs").getAsJsonObject().get("data");
            if (!data.isJsonNull() && data.getAsJsonObject().has("users")) {
                JsonElement users = data.getAsJsonObject().get("users");
                if (!users.isJsonNull() && users.isJsonArray()) {
                    for (JsonElement userElement : users.getAsJsonArray()) {
                        JsonObject singleUserElement = userElement.getAsJsonObject();
                        OcsUser user = new OcsUser();
                        user.setDisplayName(singleUserElement.get("label").getAsString());
                        user.setId(
                                singleUserElement.get("value").getAsJsonObject()
                                        .get("shareWith").getAsString()
                        );
                        ocsUserList.addUser(user);
                    }
                }
            }

        }, obj);
        return ocsUserList;
    }

    private static OcsUser parseSingleOcsUser(JsonObject obj) {
        DeckLog.verbose(obj.toString());
        OcsUser ocsUser = new OcsUser();
        makeTraceableIfFails(() -> {
            JsonElement data = obj.get("ocs").getAsJsonObject().get("data");
            if (!data.isJsonNull()) {
                JsonObject user = data.getAsJsonObject();
                if (user.has("id")) {
                    ocsUser.setId(user.get("id").getAsString());
                }
                if (user.has("displayname")) {
                    ocsUser.setDisplayName(user.get("displayname").getAsString());
                }
            }

        }, obj);
        return ocsUser;
    }

    private static OcsProjectList parseOcsProjectList(JsonObject obj) {
        DeckLog.verbose(obj.toString());
        OcsProjectList projectList = new OcsProjectList();
        makeTraceableIfFails(() -> {
            JsonElement data = obj.get("ocs").getAsJsonObject().get("data");
            if (!data.isJsonNull() && data.isJsonArray()) {
                JsonArray projectJsonArray = data.getAsJsonArray();
                for (JsonElement jsonArrayElement : projectJsonArray) {
                    if (jsonArrayElement.isJsonObject()) {
                        JsonObject jsonObject = jsonArrayElement.getAsJsonObject();
                        OcsProject project = new OcsProject();
                        project.setId(jsonObject.get("id").getAsLong());
                        project.setName(getNullAsEmptyString(jsonObject.get("name")));
                        project.setResources(new ArrayList<>());
                        JsonElement jsonResources = jsonObject.get("resources");
                        if (jsonResources != null && jsonResources.isJsonArray()) {
                            JsonArray resourcesArray = jsonResources.getAsJsonArray();
                            for (JsonElement resourceElement : resourcesArray) {
                                if (resourceElement.isJsonObject()) {
                                    OcsProjectResource resource = parseOcsProjectResource(resourceElement.getAsJsonObject());
                                    resource.setProjectId(project.getId());
                                    project.getResources().add(resource);
                                }
                            }
                        }
                        projectList.add(project);
                    }
                }
            }

        }, obj);
        return projectList;
    }

    private static OcsProjectResource parseOcsProjectResource(JsonObject obj) {
        DeckLog.verbose(obj.toString());
        OcsProjectResource resource = new OcsProjectResource();
        makeTraceableIfFails(() -> {
            if (obj.has("id")) {
                String idString = obj.get("id").getAsString();
                if (idString != null && idString.trim().length() > 0) {
                    if (idString.matches("[0-9]+")) {
                        resource.setId(Long.parseLong(idString.trim()));
                    } else {
                        resource.setIdString(idString);
                    }
                }
            }
            if (obj.has("type")) {
                resource.setType(getNullAsEmptyString(obj.get("type")));
            }
            if (obj.has("name")) {
                resource.setName(getNullAsEmptyString(obj.get("name")));
            }
            if (obj.has("link")) {
                resource.setLink(getNullAsEmptyString(obj.get("link")));
            }
            if (obj.has("iconUrl")) {
                resource.setIconUrl(getNullAsEmptyString(obj.get("iconUrl")));
            }
            if (obj.has("path")) {
                resource.setPath(obj.get("path").getAsString());
            }
            if (obj.has("mimetype")) {
                resource.setMimetype(obj.get("mimetype").getAsString());
            }
            if (obj.has("preview-available")) {
                resource.setPreviewAvailable(obj.get("preview-available").getAsBoolean());
            } else {
                resource.setPreviewAvailable(false);
            }

        }, obj);
        return resource;
    }

    private static OcsComment parseOcsComment(JsonObject obj) {
        DeckLog.verbose(obj.toString());
        OcsComment comment = new OcsComment();
        makeTraceableIfFails(() -> {
            JsonElement data = obj.get("ocs").getAsJsonObject().get("data");
            if (data.isJsonArray()) {
                for (JsonElement deckComment : data.getAsJsonArray()) {
                    comment.addComment(parseDeckComment(deckComment));
                }
            } else {
                comment.addComment(parseDeckComment(data));
            }
        }, obj);
        return comment;
    }

    private static DeckComment parseDeckComment(JsonElement data) {
        DeckLog.verbose(data.toString());
        DeckComment deckComment = new DeckComment();

        makeTraceableIfFails(() -> {
            JsonObject commentJson = data.getAsJsonObject();

            deckComment.setId(commentJson.get("id").getAsLong());
            deckComment.setObjectId(commentJson.get("objectId").getAsLong());
            deckComment.setMessage(commentJson.get("message").getAsString());
            deckComment.setActorId(commentJson.get("actorId").getAsString());
            deckComment.setActorDisplayName(commentJson.get("actorDisplayName").getAsString());
            deckComment.setActorType(commentJson.get("actorType").getAsString());
            deckComment.setCreationDateTime(getTimestampFromString(commentJson.get("creationDateTime")));

            if (commentJson.has("replyTo")) {
                JsonObject replyTo = commentJson.get("replyTo").getAsJsonObject();
                deckComment.setParentId(replyTo.get("id").getAsLong());
            }

            JsonElement mentions = commentJson.get("mentions");
            if (mentions != null && mentions.isJsonArray()) {
                for (JsonElement mention : mentions.getAsJsonArray()) {
                    deckComment.addMention(parseMention(mention));
                }
            }
        }, data);

        return deckComment;
    }

    private static Mention parseMention(JsonElement mentionJson) {
        Mention mention = new Mention();
        DeckLog.verbose(mentionJson.toString());


        makeTraceableIfFails(() -> {
            JsonObject mentionObject = mentionJson.getAsJsonObject();
            mention.setMentionId(mentionObject.get("mentionId").getAsString());
            mention.setMentionType(mentionObject.get("mentionType").getAsString());
            mention.setMentionDisplayName(mentionObject.get("mentionDisplayName").getAsString());
        }, mentionJson);

        return mention;
    }


    protected static FullBoard parseBoard(JsonObject e) {
        FullBoard fullBoard = new FullBoard();

        DeckLog.verbose(e.toString());
        Board board = new Board();

        makeTraceableIfFails(() -> {
            board.setTitle(getNullAsEmptyString(e.get("title")));
            board.setColor(getNullAsEmptyString(e.get("color")));
            board.setEtag(getNullAsNull(e.get("ETag")));
            board.setArchived(e.get("archived").getAsBoolean());

            board.setLastModified(getTimestampFromLong(e.get("lastModified")));
            board.setDeletedAt(getTimestampFromLong(e.get("deletedAt")));
            board.setId(e.get("id").getAsLong());
            fullBoard.setBoard(board);

            if (e.has("labels") && !e.get("labels").isJsonNull()) {
                JsonArray labelsJson = e.getAsJsonArray("labels");
                List<Label> labels = new ArrayList<>();
                for (JsonElement labelJson : labelsJson) {
                    labels.add(parseLabel(labelJson.getAsJsonObject()));
                }
                fullBoard.setLabels(labels);
            }

            if (e.has("stacks") && !e.get("stacks").isJsonNull()) {
                JsonArray stacksJson = e.getAsJsonArray("stacks");
                List<Stack> stacks = new ArrayList<>();
                for (JsonElement stackJson : stacksJson) {
                    stacks.add(parseStack(stackJson.getAsJsonObject()).getStack());
                }
                fullBoard.setStacks(stacks);
            }

            if (e.has("acl") && !e.get("acl").isJsonNull()) {
                JsonElement assignedUsers = e.get("acl");
                if (assignedUsers.isJsonArray() && assignedUsers.getAsJsonArray().size() > 0) {
                    JsonArray assignedUsersArray = assignedUsers.getAsJsonArray();

                    List<AccessControl> acl = new ArrayList<>();
                    for (JsonElement aclElement : assignedUsersArray) {
                        acl.add(parseAcl(aclElement.getAsJsonObject()));
                    }
                    fullBoard.setParticipants(acl);
                }

            }

            if (e.has("permissions")) {
                JsonElement permissions = e.get("permissions");
                JsonObject permissionsObject = permissions.getAsJsonObject();
                if (permissionsObject.has("PERMISSION_READ")) {
                    JsonElement read = permissionsObject.get("PERMISSION_READ");
                    fullBoard.getBoard().setPermissionRead(read.getAsBoolean());
                }
                if (permissionsObject.has("PERMISSION_EDIT")) {
                    JsonElement read = permissionsObject.get("PERMISSION_EDIT");
                    fullBoard.getBoard().setPermissionEdit(read.getAsBoolean());
                }
                if (permissionsObject.has("PERMISSION_MANAGE")) {
                    JsonElement read = permissionsObject.get("PERMISSION_MANAGE");
                    fullBoard.getBoard().setPermissionManage(read.getAsBoolean());
                }
                if (permissionsObject.has("PERMISSION_SHARE")) {
                    JsonElement read = permissionsObject.get("PERMISSION_SHARE");
                    fullBoard.getBoard().setPermissionShare(read.getAsBoolean());
                }
            }

            if (e.has("owner")) {
                fullBoard.setOwner(parseUser(e.get("owner")));
            }
            if (e.has("users")) {
                JsonElement users = e.get("users");
                if (users != null && !users.isJsonNull() && users.isJsonArray()) {
                    JsonArray usersArray = users.getAsJsonArray();
                    List<User> usersList = new ArrayList<>();
                    for (JsonElement userJson : usersArray) {
                        usersList.add(parseUser(userJson));
                    }
                    fullBoard.setUsers(usersList);
                }
            }
        }, e);
        return fullBoard;
    }

    protected static AccessControl parseAcl(JsonObject aclJson) {
        DeckLog.verbose(aclJson.toString());
        AccessControl acl = new AccessControl();

        if (aclJson.has("participant") && !aclJson.get("participant").isJsonNull()) {
            makeTraceableIfFails(() -> {
                User participant = parseUser(aclJson.get("participant"));
                acl.setUser(participant);
                acl.setType(aclJson.get("type").getAsLong());
                acl.setBoardId(aclJson.get("boardId").getAsLong());
                acl.setId(aclJson.get("id").getAsLong());

                acl.setOwner(aclJson.get("owner").getAsBoolean());
                acl.setPermissionEdit(aclJson.get("permissionEdit").getAsBoolean());
                acl.setPermissionManage(aclJson.get("permissionManage").getAsBoolean());
                acl.setPermissionShare(aclJson.get("permissionShare").getAsBoolean());
            }, aclJson);
        }


        return acl;
    }

    protected static FullCard parseCard(JsonObject e) {
        DeckLog.verbose(e.toString());
        FullCard fullCard = new FullCard();
        Card card = new Card();
        fullCard.setCard(card);
        makeTraceableIfFails(() -> {
            card.setId(e.get("id").getAsLong());
            card.setTitle(getNullAsEmptyString(e.get("title")));
            card.setDescription(getNullAsEmptyString(e.get("description")));
            card.setStackId(e.get("stackId").getAsLong());
            card.setType(getNullAsEmptyString(e.get("type")));
            card.setEtag(getNullAsNull(e.get("ETag")));
            card.setLastModified(getTimestampFromLong(e.get("lastModified")));
            card.setCreatedAt(getTimestampFromLong(e.get("createdAt")));
            card.setDeletedAt(getTimestampFromLong(e.get("deletedAt")));
            if (e.has("labels") && !e.get("labels").isJsonNull()) {
                JsonArray labelsJson = e.getAsJsonArray("labels");
                List<Label> labels = new ArrayList<>();
                for (JsonElement labelJson : labelsJson) {
                    labels.add(parseLabel(labelJson.getAsJsonObject()));
                }
                fullCard.setLabels(labels);
            }

            if (e.has("assignedUsers") && !e.get("assignedUsers").isJsonNull()) {
                JsonArray assignedUsers = e.getAsJsonArray("assignedUsers");

                List<User> users = new ArrayList<>();
                for (JsonElement assignedUser : assignedUsers) {
                    JsonObject userJson = assignedUser.getAsJsonObject();
                    if (userJson.has("participant") && !userJson.get("participant").isJsonNull()) {
                        users.add(parseUser(userJson.get("participant")));
                    }
                }
                fullCard.setAssignedUsers(users);
            }
            if (e.has("attachments") && !e.get("attachments").isJsonNull()) {
                JsonArray attachmentsJson = e.getAsJsonArray("attachments");

                List<Attachment> attachments = new ArrayList<>();
                for (JsonElement att : attachmentsJson) {
                    JsonObject attachmentJson = att.getAsJsonObject();
                    attachments.add(parseAttachment(attachmentJson));
                }
                fullCard.setAttachments(attachments);
            }

            if (e.has("attachmentCount") && !e.get("attachmentCount").isJsonNull()) {
                card.setAttachmentCount(e.get("attachmentCount").getAsInt());
            }

            card.setOrder(e.get("order").getAsInt());
            card.setOverdue(e.get("overdue").getAsInt());
            card.setDueDate(getTimestampFromString(e.get("duedate")));
            card.setCommentsUnread(e.get("commentsUnread").getAsInt());
            JsonElement owner = e.get("owner");
            if (owner != null) {
                fullCard.setOwner(parseUser(owner));
            }
            card.setArchived(e.get("archived").getAsBoolean());
        }, e);

        return fullCard;
    }

    protected static Attachment parseAttachment(JsonObject e) {
        DeckLog.verbose(e.toString());
        Attachment a = new Attachment();
        makeTraceableIfFails(() -> {
            a.setId(e.get("id").getAsLong());
            a.setCardId(e.get("cardId").getAsLong());
            a.setType(e.get("type").getAsString());
            a.setEtag(getNullAsNull(e.get("ETag")));
            a.setData(e.get("data").getAsString());
            a.setLastModified(getTimestampFromLong(e.get("lastModified")));
            a.setCreatedAt(getTimestampFromLong(e.get("createdAt")));
            a.setCreatedBy(e.get("createdBy").getAsString());
            a.setDeletedAt(getTimestampFromLong(e.get("deletedAt")));
            if (e.has("extendedData") && !e.get("extendedData").isJsonNull() && e.get("extendedData").isJsonObject()) {
                JsonObject extendedData = e.getAsJsonObject("extendedData").getAsJsonObject();
                a.setFilesize(extendedData.get("filesize").getAsLong());
                a.setMimetype(extendedData.get("mimetype").getAsString());
                if (extendedData.has("info") && !extendedData.get("info").isJsonNull()) {
                    JsonObject info = extendedData.getAsJsonObject("info").getAsJsonObject();
                    a.setDirname(info.get("dirname").getAsString());
                    a.setBasename(info.get("basename").getAsString());
                    if (info.has("extension")) {
                        a.setExtension(info.get("extension").getAsString());
                    }
                    a.setFilename(info.get("filename").getAsString());
                }
            }
        }, e);

        return a;
    }

    protected static User parseUser(JsonElement userElement) {
        DeckLog.verbose(userElement.toString());
        if (userElement.isJsonNull()) {
            return null;
        }
        User user = new User();
        makeTraceableIfFails(() -> {

            if (userElement.isJsonPrimitive()) {
                String uid = userElement.getAsString();
                user.setDisplayname(uid);
                user.setPrimaryKey(uid);
                user.setUid(uid);
            } else {
                JsonObject userJson = userElement.getAsJsonObject();
                user.setDisplayname(getNullAsEmptyString(userJson.get("displayname")));
                user.setPrimaryKey(getNullAsEmptyString(userJson.get("primaryKey")));
                user.setUid(getNullAsEmptyString(userJson.get("uid")));
            }

        }, userElement);
        return user;
    }


    protected static Capabilities parseCapabilities(JsonObject e) {
        DeckLog.verbose(e.toString());
        Capabilities capabilities = new Capabilities();
        // not traceable, we need the original DeckException for error handling
        if (e.has("ocs")) {
            JsonObject ocs = e.getAsJsonObject("ocs");
            if (ocs.has("meta")) {
                int statuscode = ocs.getAsJsonObject("meta").get("statuscode").getAsInt();
                capabilities.setMaintenanceEnabled(statuscode == 503);
                if (capabilities.isMaintenanceEnabled()) {
                    // Abort, since there is nothing more to read.
                    return capabilities;
                }
            }
            if (ocs.has("data")) {
                JsonObject data = ocs.getAsJsonObject("data");
                if (data.has("version")) {
                    JsonObject version = data.getAsJsonObject("version");
                    Version v = Version.of(version.get("string").getAsString());
                    capabilities.setNextcloudVersion(v);
                }

                String version = null;
                if (data.has("capabilities")) {
                    JsonObject caps = data.getAsJsonObject("capabilities");
                    if (caps.has("deck")) {
                        JsonObject deck = caps.getAsJsonObject("deck");
                        if (deck.has("version")) {
                            version = deck.get("version").getAsString();
                            if (version == null || version.trim().length() < 1) {
                                throw new DeckException(CAPABILITIES_VERSION_NOT_PARSABLE,
                                        "capabilities endpoint returned an invalid version string: \"" + version + "\"");
                            }
                        } else {
                            throw new DeckException(CAPABILITIES_VERSION_NOT_PARSABLE,
                                    "deck version node is missing in capabilities endpoint! deck-node: " + deck.getAsString());
                        }
                    } else {
                        throw new DeckException(CAPABILITIES_VERSION_NOT_PARSABLE,
                                "deck node is missing in capabilities endpoint!");
                    }
                    if (caps.has("theming")) {
                        JsonObject theming = caps.getAsJsonObject("theming");
                        capabilities.setColor(getColorAsInt(theming, "color"));
                        capabilities.setTextColor(getColorAsInt(theming, "color-text"));
                    }
                }
                capabilities.setDeckVersion(Version.of(version));
            }
        }
        return capabilities;
    }

    private static int getColorAsInt(JsonObject element, String field) {
        String rawString = getNullAsEmptyString(element.get(field));
        try {
            if (!rawString.trim().isEmpty()) {
                String colorAsString = ColorUtil.INSTANCE.formatColorToParsableHexString(rawString);
                return Color.parseColor(colorAsString);
            }
        } catch (Exception e) {
            // Do mostly nothing, return default value
        }
        return Color.GRAY;
    }

    protected static FullStack parseStack(JsonObject e) {
        DeckLog.verbose(e.toString());
        FullStack fullStack = new FullStack();
        Stack stack = new Stack();
        fullStack.setStack(stack);
        makeTraceableIfFails(() -> {
            stack.setTitle(getNullAsEmptyString(e.get("title")));
            stack.setBoardId(e.get("boardId").getAsLong());
            stack.setId(e.get("id").getAsLong());
            stack.setEtag(getNullAsNull(e.get("ETag")));
            stack.setLastModified(getTimestampFromLong(e.get("lastModified")));
            stack.setDeletedAt(getTimestampFromLong(e.get("deletedAt")));
            if (e.has("order") && !e.get("order").isJsonNull()) {
                stack.setOrder(e.get("order").getAsInt());
            } else {
                stack.setOrder(0);
            }
            if (e.has("cards")) {
                JsonArray cardsJson = e.getAsJsonArray("cards");
                List<Card> cards = new ArrayList<>();
                for (JsonElement cardJson : cardsJson) {
                    cards.add(parseCard(cardJson.getAsJsonObject()).getCard());
                }
                fullStack.setCards(cards);
            }
            stack.setDeletedAt(getTimestampFromLong(e.get("deletedAt")));
        }, e);
        return fullStack;
    }

    protected static List<Activity> parseActivity(JsonObject e) {
        DeckLog.verbose(e.toString());
        List<Activity> activityList = new ArrayList<>();

        makeTraceableIfFails(() -> {
            if (e.has("ocs")) {
                JsonObject ocs = e.getAsJsonObject("ocs");
                if (ocs.has("data")) {
                    JsonArray data = ocs.getAsJsonArray("data");
                    for (JsonElement activityJson : data) {
                        Activity activity = new Activity();
                        JsonObject activityObject = activityJson.getAsJsonObject();

                        activity.setId(activityObject.get("activity_id").getAsLong());
                        activity.setType(ActivityType.findByPath(getNullAsEmptyString(activityObject.get("icon"))).getId());
                        activity.setSubject(getNullAsEmptyString(activityObject.get("subject")));
                        activity.setCardId(activityObject.get("object_id").getAsLong());
                        activity.setEtag(getNullAsNull(e.get("ETag")));
                        activity.setLastModified(getTimestampFromString(activityObject.get("datetime")));

                        activityList.add(activity);
                    }
                }
            }
        }, e);
        return activityList;
    }

    protected static Label parseLabel(JsonObject e) {
        DeckLog.verbose(e.toString());
        Label label = new Label();
        makeTraceableIfFails(() -> {
            label.setId(e.get("id").getAsLong());
            //todo: last modified!
//          label.setLastModified(get);
            label.setTitle(getNullAsEmptyString(e.get("title")));
            label.setEtag(getNullAsNull(e.get("ETag")));
            label.setColor(getColorAsInt(e, "color"));
        }, e);
        return label;
    }

    private static String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement == null || jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }

    private static String getNullAsNull(JsonElement jsonElement) {
        return jsonElement == null || jsonElement.isJsonNull() ? null : jsonElement.getAsString();
    }

    private static Instant getTimestampFromString(JsonElement jsonElement) {
        if (jsonElement.isJsonNull()) {
            return null;
        } else {
            String dateAsString = jsonElement.getAsString();
            return ZonedDateTime.from(DateTimeFormatter.ISO_DATE_TIME.parse(dateAsString)).toInstant();
        }
    }

    private static Instant getTimestampFromLong(JsonElement jsonElement) {
        if (jsonElement.isJsonNull()) {
            return null;
        } else {
            return Instant.ofEpochMilli(jsonElement.getAsLong() * 1000);
        }
    }
}
