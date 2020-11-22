package it.niedermann.android.markdown;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import static androidx.lifecycle.Transformations.distinctUntilChanged;

@RestrictTo(value = RestrictTo.Scope.LIBRARY)
public abstract class AbstractWebViewMarkdownEditor extends WebView implements MarkdownEditor {

    private final MutableLiveData<CharSequence> lastText$ = new MutableLiveData<>();
    protected boolean pageFinished = false;
    protected CharSequence textToSetOnPageFinished;
    protected boolean enabledStateOnPageFinished = true;

    public AbstractWebViewMarkdownEditor(@NonNull Context context) {
        super(context);
        init();
    }

    public AbstractWebViewMarkdownEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AbstractWebViewMarkdownEditor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
                setMarkdownString(textToSetOnPageFinished);
                setEnabled(enabledStateOnPageFinished);
            }
        });
    }

    abstract protected String getUrlToIndex();

    @Override
    public void setMarkdownString(CharSequence textToSetOnPageFinished) {
        if (pageFinished) {
            final String escapedText = this.textToSetOnPageFinished == null ? "" : this.textToSetOnPageFinished.toString().replace("`", "\\`");
            evaluateJavascript("setText(`" + escapedText + "`);", null);
            lastText$.setValue(this.textToSetOnPageFinished);
        } else {
            this.textToSetOnPageFinished = textToSetOnPageFinished;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (pageFinished) {
            evaluateJavascript("setEnabled(" + this.enabledStateOnPageFinished + ");", null);
        } else {
            this.enabledStateOnPageFinished = enabled;
        }
    }

    @Override
    public LiveData<CharSequence> getMarkdownString() {
        return distinctUntilChanged(lastText$);
    }

    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    @JavascriptInterface
    public void onTextChanged(String newText) {
        lastText$.setValue(newText);
    }
}
