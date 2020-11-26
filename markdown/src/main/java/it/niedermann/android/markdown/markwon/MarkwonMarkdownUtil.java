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
import io.noties.markwon.syntax.Prism4jTheme;
import io.noties.markwon.syntax.Prism4jThemeDefault;
import io.noties.markwon.syntax.SyntaxHighlightPlugin;
import io.noties.prism4j.Prism4j;
import io.noties.prism4j.annotations.PrismBundle;
import it.niedermann.android.markdown.markwon.plugins.NextcloudMentionsPlugin;

@RestrictTo(value = RestrictTo.Scope.LIBRARY)
@PrismBundle(
        include = {
                "c", "clike", "clojure", "cpp", "csharp", "css", "dart", "git", "go", "groovy", "java", "javascript", "json",
                "kotlin", "latex", "makefile", "markdown", "markup", "python", "scala", "sql", "swift", "yaml"
        },
        grammarLocatorClassName = ".MarkwonGrammarLocator"
)
public class MarkwonMarkdownUtil {

    private MarkwonMarkdownUtil() {
        // Util class
    }

    public static Markwon.Builder initMarkwon(@NonNull Context context) {
        final Prism4j prism4j = new Prism4j(new MarkwonGrammarLocator());
        final Prism4jTheme prism4jTheme = Prism4jThemeDefault.create();
        return Markwon.builder(context)
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(TaskListPlugin.create(context))
                .usePlugin(ImagesPlugin.create())
                .usePlugin(GlideImagesPlugin.create(context))
                .usePlugin(SimpleExtPlugin.create())
                .usePlugin(MarkwonInlineParserPlugin.create())
                .usePlugin(LinkifyPlugin.create())
                .usePlugin(SyntaxHighlightPlugin.create(prism4j, prism4jTheme));
    }

    public static Markwon.Builder initMarkwon(@NonNull Context context, @NonNull Map<String, String> mentions) {
        return initMarkwon(context)
                .usePlugin(NextcloudMentionsPlugin.create(context, mentions));
    }
}
