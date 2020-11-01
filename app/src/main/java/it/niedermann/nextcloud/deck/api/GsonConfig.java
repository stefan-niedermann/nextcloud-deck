package it.niedermann.nextcloud.deck.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.List;

import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.model.ocs.comment.OcsComment;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectList;
import it.niedermann.nextcloud.deck.model.ocs.user.GroupMemberUIDs;
import it.niedermann.nextcloud.deck.model.ocs.user.OcsUser;
import it.niedermann.nextcloud.deck.model.ocs.user.OcsUserList;

/**
 * Created by david on 27.06.17.
 */

public class GsonConfig {

    public static final String DATE_PATTERN = "yyyy-MM-dd'T'hh:mm:ssZ";

    private static final Gson INSTANCE;

    static {
        Type boardList = new TypeToken<List<FullBoard>>() {}.getType();
        Type board = new TypeToken<FullBoard>() {}.getType();
        Type cardList = new TypeToken<FullCard>() {}.getType();
        Type card = new TypeToken<FullCard>() {}.getType();
        Type labelList = new TypeToken<Label>() {}.getType();
        Type label = new TypeToken<Label>() {}.getType();
        Type stackList = new TypeToken<List<FullStack>>() {}.getType();
        Type stack = new TypeToken<FullStack>() {}.getType();
        Type capabilities = new TypeToken<Capabilities>() {}.getType();
        Type ocsUserList = new TypeToken<OcsUserList>() {}.getType();
        Type ocsUser = new TypeToken<OcsUser>() {}.getType();
        Type activity = new TypeToken<Activity>() {}.getType();
        Type activityList = new TypeToken<List<Activity>>() {}.getType();
        Type attachment = new TypeToken<Attachment>() {}.getType();
        Type attachmentList = new TypeToken<List<Attachment>>() {}.getType();
        Type comment = new TypeToken<OcsComment>() {}.getType();
        Type projectList = new TypeToken<OcsProjectList>() {}.getType();
        Type groupMembers = new TypeToken<GroupMemberUIDs>() {}.getType();

        INSTANCE = new GsonBuilder()
                .setDateFormat(DATE_PATTERN)
                .setLenient()
                .registerTypeAdapter(Instant.class,     new GsonUTCInstantAdapter())
                .registerTypeAdapter(boardList,         new NextcloudArrayDeserializer<>("boards", FullBoard.class))
                .registerTypeAdapter(board,             new NextcloudDeserializer<>("board", FullBoard.class))
                .registerTypeAdapter(cardList,          new NextcloudArrayDeserializer<>("cards", FullCard.class))
                .registerTypeAdapter(card,              new NextcloudDeserializer<>("card", FullCard.class))
                .registerTypeAdapter(labelList,         new NextcloudArrayDeserializer<>("labels", Label.class))
                .registerTypeAdapter(label,             new NextcloudDeserializer<>("label", Label.class))
                .registerTypeAdapter(stackList,         new NextcloudArrayDeserializer<>("stacks", FullStack.class))
                .registerTypeAdapter(stack,             new NextcloudDeserializer<>("stack", FullStack.class))
                .registerTypeAdapter(capabilities,      new NextcloudDeserializer<>("capability", Capabilities.class))
                .registerTypeAdapter(ocsUserList,       new NextcloudDeserializer<>("ocsUserList", OcsUserList.class))
                .registerTypeAdapter(ocsUser,           new NextcloudDeserializer<>("ocsUser", OcsUser.class))
                .registerTypeAdapter(activity,          new NextcloudDeserializer<>("activity", Activity.class))
                .registerTypeAdapter(activityList,      new NextcloudDeserializer<>("activityList", Activity.class))
                .registerTypeAdapter(attachmentList,    new NextcloudArrayDeserializer<>("attachments", Attachment.class))
                .registerTypeAdapter(attachment,        new NextcloudDeserializer<>("attachment", Attachment.class))
                .registerTypeAdapter(comment,           new NextcloudDeserializer<>("comment", OcsComment.class))
                .registerTypeAdapter(projectList,       new NextcloudDeserializer<>("projectList", OcsProjectList.class))
                .registerTypeAdapter(groupMembers,      new NextcloudDeserializer<>("groupMembers", GroupMemberUIDs.class))
                .create();
    }

    public static Gson getGson() {
        return INSTANCE;
    }

}
