package it.niedermann.android.markdown.simplemde;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import it.niedermann.android.markdown.MarkdownEditor;

public class SimpleMDEMarkdownEditor extends WebView implements MarkdownEditor {

    private boolean pageFinished = false;
    @NonNull
    private Consumer<String> listener = s -> {
    };
    private String textToSetOnPageFinished;
    private boolean enabledStateOnPageFinished = true;

    @SuppressLint("SetJavaScriptEnabled")
    public SimpleMDEMarkdownEditor(@NonNull Context context) {
        super(context);
        init();
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface", "AddJavascriptInterface"})
    public SimpleMDEMarkdownEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public SimpleMDEMarkdownEditor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        getSettings().setJavaScriptEnabled(true);
        addJavascriptInterface(this, "Android");
        loadUrl("file:///android_asset/web/simplemde/index.html");
        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pageFinished = true;
                setText(textToSetOnPageFinished);
                setEnabled(enabledStateOnPageFinished);
            }
        });
    }

    @Override
    public void setText(String textToSetOnPageFinished) {
        if (pageFinished) {
            String newText = this.textToSetOnPageFinished == null ? "" : this.textToSetOnPageFinished.replace("`", "\\`");
            evaluateJavascript("setText(`" + newText + "`);", null);
        } else {
            this.textToSetOnPageFinished = textToSetOnPageFinished;
        }
    }

    @Override
    public void setTextChangedListener(@NonNull Consumer<String> listener) {
        this.listener = listener;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (pageFinished) {
            evaluateJavascript("setEnabled(" + this.enabledStateOnPageFinished + ");", null);
        } else {
            this.enabledStateOnPageFinished = enabled;
        }
    }

    @JavascriptInterface
    public void onTextChanged(String newText) {
        this.listener.accept(newText);
    }
}