package it.niedermann.nextcloud.deck.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import it.niedermann.nextcloud.deck.DeckLog;
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

public class JsonToEntityParser {
    private static SimpleDateFormat formatter = new SimpleDateFormat(GsonConfig.DATE_PATTERN);

    static {
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    protected static <T> T parseJsonObject(JsonObject obj, Class<T> mType) {
        if (mType == FullBoard.class) {
            return (T) parseBoard(obj);
        } else if (mType == FullCard.class) {
            return (T) parseCard(obj);
        } else if (mType == FullStack.class) {
            return (T) parseStack(obj);
        } else if (mType == Label.class) {
            return (T) parseLabel(obj);
        }  else if (mType == Activity.class) {
            return (T) parseActivity(obj);
        }  else if (mType == Capabilities.class) {
            return (T) parseCapabilities(obj);
        } else if (mType == Attachment.class) {
            return (T) parseAttachment(obj);
        } else if (mType == OcsComment.class) {
            return (T) parseOcsComment(obj);
        }
        throw new IllegalArgumentException("unregistered type: " + mType.getCanonicalName());
    }

    private static OcsComment parseOcsComment(JsonObject obj) {
        DeckLog.verbose(obj.toString());
        OcsComment comment = new OcsComment();
        JsonElement data = obj.get("ocs").getAsJsonObject().get("data");
        if (data.isJsonArray()) {
            for (JsonElement deckComment : data.getAsJsonArray()) {
                comment.addComment(parseDeckComment(deckComment));
            }
        } else {
            comment.addComment(parseDeckComment(data));
        }
        return comment;
    }

    private static DeckComment parseDeckComment(JsonElement data) {
        DeckLog.verbose(data.toString());
        JsonObject commentJson = data.getAsJsonObject();
        DeckComment deckComment = new DeckComment();

        deckComment.setId(commentJson.get("id").getAsLong());
        deckComment.setObjectId(commentJson.get("objectId").getAsLong());
        deckComment.setMessage(commentJson.get("message").getAsString());
        deckComment.setActorId(commentJson.get("actorId").getAsString());
        deckComment.setActorDisplayName(commentJson.get("actorDisplayName").getAsString());
        deckComment.setActorType(commentJson.get("actorType").getAsString());
        deckComment.setCreationDateTime(getTimestampFromString(commentJson.get("creationDateTime")));

        JsonElement mentions = commentJson.get("mentions");
        if (mentions != null && mentions.isJsonArray()) {
            for (JsonElement mention : mentions.getAsJsonArray()) {
                deckComment.addMention(parseMention(mention));
            }
        }

        return deckComment;
    }

    private static Mention parseMention(JsonElement mentionJson) {
        Mention mention = new Mention();
        DeckLog.verbose(mentionJson.toString());

        JsonObject mentionObject = mentionJson.getAsJsonObject();
        mention.setMentionId(mentionObject.get("mentionId").getAsString());
        mention.setMentionType(mentionObject.get("mentionType").getAsString());
        mention.setMentionDisplayName(mentionObject.get("mentionDisplayName").getAsString());

        return mention;
    }


    protected static FullBoard parseBoard(JsonObject e) {
        FullBoard fullBoard = new FullBoard();

        DeckLog.verbose(e.toString());
        Board board = new Board();
        board.setTitle(getNullAsEmptyString(e.get("title")));
        board.setColor(getNullAsEmptyString(e.get("color")));
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
            if (assignedUsers.isJsonArray() && assignedUsers.getAsJsonArray().size() > 0){
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
            if (permissionsObject.has("PERMISSION_READ")){
                JsonElement read = permissionsObject.get("PERMISSION_READ");
                fullBoard.getBoard().setPermissionRead(read.getAsBoolean());
            }
            if (permissionsObject.has("PERMISSION_EDIT")){
                JsonElement read = permissionsObject.get("PERMISSION_EDIT");
                fullBoard.getBoard().setPermissionEdit(read.getAsBoolean());
            }
            if (permissionsObject.has("PERMISSION_MANAGE")){
                JsonElement read = permissionsObject.get("PERMISSION_MANAGE");
                fullBoard.getBoard().setPermissionManage(read.getAsBoolean());
            }
            if (permissionsObject.has("PERMISSION_SHARE")){
                JsonElement read = permissionsObject.get("PERMISSION_SHARE");
                fullBoard.getBoard().setPermissionShare(read.getAsBoolean());
            }
        }

        JsonElement owner = e.get("owner");
        if (owner != null) {
            if (owner.isJsonPrimitive()) {//TODO: remove if, let only else!
                DeckLog.verbose("owner is Primitive, skipping");
            } else
                fullBoard.setOwner(parseUser(owner.getAsJsonObject()));
        }
        return fullBoard;
    }

    protected static AccessControl parseAcl(JsonObject aclJson){
        DeckLog.verbose(aclJson.toString());
        AccessControl acl = new AccessControl();

        if (aclJson.has("participant") && !aclJson.get("participant").isJsonNull()) {
            User participant = parseUser(aclJson.get("participant").getAsJsonObject());
            acl.setUser(participant);
            acl.setType(aclJson.get("type").getAsLong());
            acl.setBoardId(aclJson.get("boardId").getAsLong());
            acl.setId(aclJson.get("id").getAsLong());

            acl.setOwner(aclJson.get("owner").getAsBoolean());
            acl.setPermissionEdit(aclJson.get("permissionEdit").getAsBoolean());
            acl.setPermissionManage(aclJson.get("permissionManage").getAsBoolean());
            acl.setPermissionShare(aclJson.get("permissionShare").getAsBoolean());
        }


        return acl;
    }

    protected static FullCard parseCard(JsonObject e) {
        DeckLog.verbose(e.toString());
        FullCard fullCard = new FullCard();
        Card card = new Card();
        fullCard.setCard(card);
        card.setId(e.get("id").getAsLong());
        card.setTitle(getNullAsEmptyString(e.get("title")));
        card.setDescription(getNullAsEmptyString(e.get("description")));
        card.setStackId(e.get("stackId").getAsLong());
        card.setType(getNullAsEmptyString(e.get("type")));
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
                    users.add(parseUser(userJson.get("participant").getAsJsonObject()));
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
            if (owner.isJsonPrimitive()) {//TODO: remove if, let only else!
                DeckLog.verbose("owner is Primitive, skipping");
            } else
                fullCard.setOwner(parseUser(owner.getAsJsonObject()));
        }
        card.setArchived(e.get("archived").getAsBoolean());

        return fullCard;
    }

    protected static Attachment parseAttachment(JsonObject e) {
        DeckLog.verbose(e.toString());
        Attachment a = new Attachment();
        a.setId(e.get("id").getAsLong());
        a.setCardId(e.get("cardId").getAsLong());
        a.setType(e.get("type").getAsString());
        a.setData(e.get("data").getAsString());
        a.setLastModified(getTimestampFromLong(e.get("lastModified")));
        a.setCreatedAt(getTimestampFromLong(e.get("createdAt")));
        a.setCreatedBy(e.get("createdBy").getAsString());
        a.setDeletedAt(getTimestampFromLong(e.get("deletedAt")));
        if (e.has("extendedData") && !e.get("extendedData").isJsonNull()) {
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

        return a;
    }

    protected static User parseUser(JsonObject e) {
        DeckLog.verbose(e.toString());
        User user = new User();
        user.setDisplayname(getNullAsEmptyString(e.get("displayname")));
        user.setPrimaryKey(getNullAsEmptyString(e.get("primaryKey")));
        user.setUid(getNullAsEmptyString(e.get("uid")));
        return user;
    }


    protected static Capabilities parseCapabilities(JsonObject e) {
        DeckLog.verbose(e.toString());
        Capabilities capabilities = new Capabilities();

        if (e.has("ocs")){
            JsonObject ocs = e.getAsJsonObject("ocs");
            if (ocs.has("meta")) {
                int statuscode = ocs.getAsJsonObject("meta").get("statuscode").getAsInt();
                capabilities.setMaintenanceEnabled(statuscode==503);
                if (capabilities.isMaintenanceEnabled()){
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

                String version = "";
                if (data.has("capabilities")) {
                    JsonObject caps = data.getAsJsonObject("capabilities");
                    if (caps.has("deck")) {
                        JsonObject deck = caps.getAsJsonObject("deck");
                        if (deck.has("version")) {
                            version = deck.get("version").getAsString();

                        }
                    }
                    if (caps.has("theming")) {
                        JsonObject theming = caps.getAsJsonObject("theming");
                        capabilities.setColor(theming.get("color").getAsString());
                        capabilities.setTextColor(theming.get("color-text").getAsString());
                    }
                }
                capabilities.setDeckVersion(Version.of(version));
            }
        }
        return capabilities;
    }

    protected static List<Activity> parseActivity(JsonObject e) {
        DeckLog.verbose(e.toString());
        List<Activity> activityList = new ArrayList<>();

        if (e.has("ocs")){
            JsonObject ocs = e.getAsJsonObject("ocs");
            if (ocs.has("data")) {
                JsonArray data = ocs.getAsJsonArray("data");
                for (JsonElement activityJson : data) {
                    Activity activity = new Activity();
                    JsonObject activityObject = activityJson.getAsJsonObject();

                    activity.setId(activityObject.get("activity_id").getAsLong());
                    activity.setType(ActivityType.findByPath(getNullAsEmptyString(activityObject.get( "icon"))).getId());
                    activity.setSubject(getNullAsEmptyString(activityObject.get("subject")));
                    activity.setCardId(activityObject.get("object_id").getAsLong());
                    activity.setLastModified(getTimestampFromString(activityObject.get("datetime")));

                    activityList.add(activity);
                }
            }
        }
        return activityList;
    }

    protected static FullStack parseStack(JsonObject e) {
        DeckLog.verbose(e.toString());
        FullStack fullStack = new FullStack();
        Stack stack = new Stack();
        stack.setTitle(getNullAsEmptyString(e.get("title")));
        stack.setBoardId(e.get("boardId").getAsLong());
        stack.setId(e.get("id").getAsLong());
        stack.setLastModified(getTimestampFromLong(e.get("lastModified")));
        stack.setDeletedAt(getTimestampFromLong(e.get("deletedAt")));
        stack.setOrder(e.get("order").getAsInt());
        if (e.has("cards")) {
            JsonArray cardsJson = e.getAsJsonArray("cards");
            List<Card> cards = new ArrayList<>();
            for (JsonElement cardJson : cardsJson) {
                cards.add(parseCard(cardJson.getAsJsonObject()).getCard());
            }
            fullStack.setCards(cards);
        }
        fullStack.setStack(stack);
        stack.setDeletedAt(getTimestampFromLong(e.get("deletedAt")));
        return fullStack;
    }

    protected static Label parseLabel(JsonObject e) {
        DeckLog.verbose(e.toString());
        Label label = new Label();
        label.setId(e.get("id").getAsLong());
        //todo: last modified!
//        label.setLastModified(get);
        label.setTitle(getNullAsEmptyString(e.get("title")));
        label.setColor(getNullAsEmptyString(e.get("color")));
        return label;
    }

    private static String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }

    private static Date getTimestampFromString(JsonElement jsonElement) {
        if (jsonElement.isJsonNull()) {
            return null;
        } else {
            try {
                return formatter.parse(jsonElement.getAsString());
            } catch (ParseException e) {
                return null;
            }
        }
    }

    private static Date getTimestampFromLong(JsonElement jsonElement) {
        if (jsonElement.isJsonNull()) {
            return null;
        } else {
            return new Date (jsonElement.getAsLong() * 1000);
        }
    }
}
