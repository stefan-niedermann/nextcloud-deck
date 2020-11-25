package it.niedermann.android.markdown.markwon;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import java.util.Map;

import io.noties.markwon.Markwon;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.image.glide.GlideImagesPlugin;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;
import io.noties.markwon.simple.ext.SimpleExtPlugin;
import it.niedermann.android.markdown.markwon.plugins.NextcloudMentionsPlugin;

/**
 * Created by stefan on 07.12.16.
 */
@RestrictTo(value = RestrictTo.Scope.LIBRARY)
public class MarkwonMarkdownUtil {

    private MarkwonMarkdownUtil() {
    }

    public static Markwon.Builder initMarkwon(@NonNull Context context) {
        return Markwon.builder(context)
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(TaskListPlugin.create(context))
                .usePlugin(ImagesPlugin.create())
                .usePlugin(GlideImagesPlugin.create(context))
                .usePlugin(SimpleExtPlugin.create())
                .usePlugin(MarkwonInlineParserPlugin.create())
                .usePlugin(LinkifyPlugin.create());
    }

    public static Markwon.Builder initMarkwon(@NonNull Context context, @NonNull Map<String, String> mentions) {
        return initMarkwon(context)
                .usePlugin(NextcloudMentionsPlugin.create(context, mentions));
    }
}
