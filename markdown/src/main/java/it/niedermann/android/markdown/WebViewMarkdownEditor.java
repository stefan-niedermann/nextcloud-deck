package it.niedermann.android.markdown;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.util.Consumer;

abstract class WebViewMarkdownEditor extends WebView implements MarkdownEditor {

    protected boolean pageFinished = false;
    protected CharSequence textToSetOnPageFinished;
    protected boolean enabledStateOnPageFinished = true;
    @Nullable
    protected Consumer<String> listener;
    private CharSequence lastText = "";

    public WebViewMarkdownEditor(@NonNull Context context) {
        super(context);
        init();
    }

    public WebViewMarkdownEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WebViewMarkdownEditor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WebViewMarkdownEditor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void init() {
        getSettings().setJavaScriptEnabled(true);
        addJavascriptInterface(this, "Android");
        loadUrl(getUrlToIndex());
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

    abstract protected String getUrlToIndex();

    @Override
    public CharSequence getText() {
        return lastText;
    }

    @Override
    public void setText(CharSequence textToSetOnPageFinished) {
        if (pageFinished) {
            final String escapedText = this.textToSetOnPageFinished == null ? "" : this.textToSetOnPageFinished.toString().replace("`", "\\`");
            evaluateJavascript("setText(`" + escapedText + "`);", null);
        } else {
            this.textToSetOnPageFinished = textToSetOnPageFinished;
        }
    }

    @Override
    public void setTextChangedListener(@Nullable Consumer<String> listener) {
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
        if (this.listener != null) {
            this.listener.accept(newText);
        }
        lastText = newText;
    }
}
