package com.retrytech.vilo.customview.socialview;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.PatternsCompat;

import com.retrytech.vilo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to help implement {@link SocialView} on any {@link TextView}-based class.
 * This class itself is not a {@link View}.
 */
public final class SocialViewHelper implements SocialView {
    private static final int FLAG_HASHTAG = 1;
    private static final int FLAG_MENTION = 2;
    private static final int FLAG_HYPERLINK = 4;

    @NonNull
    private final TextView view;
    @Nullable
    private final MovementMethod initialMovementMethod;

    @Nullable
    private Pattern hashtagPattern;
    @Nullable
    private Pattern mentionPattern;
    @Nullable
    private Pattern hyperlinkPattern;
    private int flags;
    @NonNull
    private ColorStateList hashtagColors;
    @NonNull
    private ColorStateList mentionColors;
    @NonNull
    private ColorStateList hyperlinkColors;
    @Nullable
    private OnClickListener hashtagClickListener;
    @Nullable
    private OnClickListener mentionClickListener;
    @Nullable
    private OnClickListener hyperlinkClickListener;
    @Nullable
    private OnChangedListener hashtagChangedListener;
    @Nullable
    private OnChangedListener mentionChangedListener;
    private boolean hashtagEditing;
    private boolean mentionEditing;

    @SuppressWarnings("FieldCanBeLocal")
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (count > 0 && start > 0) {
                final char c = s.charAt(start - 1);
                switch (c) {
                    case '#':
                        hashtagEditing = true;
                        mentionEditing = false;
                        break;
                    case '@':
                        hashtagEditing = false;
                        mentionEditing = true;
                        break;
                    default:
                        if (!Character.isLetterOrDigit(c)) {
                            hashtagEditing = false;
                            mentionEditing = false;
                        } else if (hashtagChangedListener != null && hashtagEditing) {
                            hashtagChangedListener.onChanged(SocialViewHelper.this, s.subSequence(
                                    indexOfPreviousNonLetterDigit(s, start - 1) + 1, start
                            ));
                        } else if (mentionChangedListener != null && mentionEditing) {
                            mentionChangedListener.onChanged(SocialViewHelper.this, s.subSequence(
                                    indexOfPreviousNonLetterDigit(s, start - 1) + 1, start
                            ));
                        }
                        break;
                }
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // triggered when text is added
            if (s.length() == 0) {
                return;
            }
            recolorize();
            if (start < s.length()) {
                final int index = start + count - 1;
                if (index < 0) {
                    return;
                }
                switch (s.charAt(index)) {
                    case '#':
                        hashtagEditing = true;
                        mentionEditing = false;
                        break;
                    case '@':
                        hashtagEditing = false;
                        mentionEditing = true;
                        break;
                    default:
                        if (!Character.isLetterOrDigit(s.charAt(start))) {
                            hashtagEditing = false;
                            mentionEditing = false;
                        } else if (hashtagChangedListener != null && hashtagEditing) {
                            hashtagChangedListener.onChanged(SocialViewHelper.this, s.subSequence(
                                    indexOfPreviousNonLetterDigit(s, start) + 1, start + count
                            ));
                        } else if (mentionChangedListener != null && mentionEditing) {
                            mentionChangedListener.onChanged(SocialViewHelper.this, s.subSequence(
                                    indexOfPreviousNonLetterDigit(s, start) + 1, start + count
                            ));
                        }
                        break;
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.i("", "");
        }
    };

    /**
     * Configuring {@link SocialView} into given view.
     *
     * @param view  TextView to install SocialView into.
     * @param attrs The attributes from the View's constructor.
     */
    public SocialViewHelper(@NonNull TextView view, @Nullable AttributeSet attrs) {
        this.view = view;
        this.initialMovementMethod = view.getMovementMethod();

        view.addTextChangedListener(textWatcher);
        view.setText(view.getText(), TextView.BufferType.SPANNABLE);
        final TypedArray a = view.getContext().obtainStyledAttributes(
                attrs, R.styleable.SocialView, R.attr.socialViewStyle, R.style.Widget_SocialView
        );
        flags = a.getInteger(R.styleable.SocialView_socialFlags, FLAG_HASHTAG | FLAG_MENTION | FLAG_HYPERLINK);
        hashtagColors = Objects.requireNonNull(a.getColorStateList(R.styleable.SocialView_hashtagColor));
        mentionColors = Objects.requireNonNull(a.getColorStateList(R.styleable.SocialView_mentionColor));
        hyperlinkColors = Objects.requireNonNull(a.getColorStateList(R.styleable.SocialView_hyperlinkColor));
        a.recycle();
        recolorize();
    }

    private static int indexOfPreviousNonLetterDigit(CharSequence text, int end) {
        for (int i = end; i > 0; i--) {
            if (!Character.isLetterOrDigit(text.charAt(i))) {
                return i;
            }
        }
        return 0;
    }

    //
    private static void spanAll(Spannable spannable, Pattern pattern, Supplier<CharacterStyle> styleSupplier) {
        final Matcher matcher = pattern.matcher(spannable);
        while (matcher.find()) {
            final int start = matcher.start();
            final int end = matcher.end();
            Object span = new Object();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                span = styleSupplier.get();
            }

            spannable.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (span instanceof SocialClickableSpan) {
                ((SocialClickableSpan) span).text = spannable.subSequence(start, end);
            }
        }
    }

    private void spanAll(Spannable spannable, Pattern pattern, Object span) {
        final Matcher matcher = pattern.matcher(spannable);
        while (matcher.find()) {
            final int start = matcher.start();
            final int end = matcher.end();
            spannable.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (span instanceof SocialClickableSpan) {
                ((SocialClickableSpan) span).text = spannable.subSequence(start, end);
            }
        }
    }

    private static List<String> listOf(CharSequence text, Pattern pattern, boolean isHyperlink) {
        final List<String> list = new ArrayList<>();
        final Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            list.add(matcher.group(!isHyperlink
                    ? 1 // remove hashtag and mention symbol
                    : 0));
        }
        return list;
    }

    @NonNull
    @Override
    public Pattern getHashtagPattern() {
        return hashtagPattern != null ? hashtagPattern : Pattern.compile("#(\\w+)");
    }

    @Override
    public void setHashtagPattern(@Nullable Pattern pattern) {
        if (hashtagPattern != pattern) {
            hashtagPattern = pattern;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                recolorize();
            }
        }
    }

    @NonNull
    @Override
    public Pattern getMentionPattern() {
        return mentionPattern != null ? mentionPattern : Pattern.compile("@(\\w+)");
    }

    @Override
    public void setMentionPattern(@Nullable Pattern pattern) {
        if (mentionPattern != null) {
            mentionPattern = pattern;
            recolorize();
        }
    }

    @NonNull
    @Override
    public Pattern getHyperlinkPattern() {
        return hyperlinkPattern != null ? hyperlinkPattern : PatternsCompat.WEB_URL;
    }

    @Override
    public void setHyperlinkPattern(@Nullable Pattern pattern) {
        if (hyperlinkPattern != null) {
            hyperlinkPattern = pattern;
            recolorize();
        }
    }

    @Override
    public boolean isHashtagEnabled() {
        return (flags | FLAG_HASHTAG) == flags;
    }

    @Override
    public void setHashtagEnabled(boolean enabled) {
        if (enabled != isHashtagEnabled()) {
            flags = enabled ? flags | FLAG_HASHTAG : flags & (~FLAG_HASHTAG);
            recolorize();
        }
    }

    @Override
    public boolean isMentionEnabled() {
        return (flags | FLAG_MENTION) == flags;
    }

    @Override
    public void setMentionEnabled(boolean enabled) {
        if (enabled != isMentionEnabled()) {
            flags = enabled ? flags | FLAG_MENTION : flags & (~FLAG_MENTION);
            recolorize();
        }
    }

    @Override
    public boolean isHyperlinkEnabled() {
        return (flags | FLAG_HYPERLINK) == flags;
    }

    @Override
    public void setHyperlinkEnabled(boolean enabled) {
        if (enabled != isHyperlinkEnabled()) {
            flags = enabled ? flags | FLAG_HYPERLINK : flags & (~FLAG_HYPERLINK);
            recolorize();
        }
    }

    @NonNull
    @Override
    public ColorStateList getHashtagColors() {
        return hashtagColors;
    }

    @Override
    public void setHashtagColors(@NonNull ColorStateList colors) {
        hashtagColors = colors;
        recolorize();
    }

    @NonNull
    @Override
    public ColorStateList getMentionColors() {
        return mentionColors;
    }

    @Override
    public void setMentionColors(@NonNull ColorStateList colors) {
        mentionColors = colors;
        recolorize();
    }

    @NonNull
    @Override
    public ColorStateList getHyperlinkColors() {
        return hyperlinkColors;
    }

    @Override
    public void setHyperlinkColors(@NonNull ColorStateList colors) {
        hyperlinkColors = colors;
        recolorize();
    }

    @Override
    public int getHashtagColor() {
        return getHashtagColors().getDefaultColor();
    }

    @Override
    public void setHashtagColor(int color) {
        setHashtagColors(ColorStateList.valueOf(color));
    }

    @Override
    public int getMentionColor() {
        return getMentionColors().getDefaultColor();
    }

    @Override
    public void setMentionColor(int color) {
        setMentionColors(ColorStateList.valueOf(color));
    }

    @Override
    public int getHyperlinkColor() {
        return getHyperlinkColors().getDefaultColor();
    }

    @Override
    public void setHyperlinkColor(int color) {
        setHyperlinkColors(ColorStateList.valueOf(color));
    }

    @Override
    public void setOnHashtagClickListener(@Nullable OnClickListener listener) {
        ensureMovementMethod(listener);
        hashtagClickListener = listener;
        recolorize();
    }

    @Override
    public void setOnMentionClickListener(@Nullable OnClickListener listener) {
        ensureMovementMethod(listener);
        mentionClickListener = listener;
        recolorize();
    }

    @Override
    public void setOnHyperlinkClickListener(@Nullable OnClickListener listener) {
        ensureMovementMethod(listener);
        hyperlinkClickListener = listener;
        recolorize();
    }

    @Override
    public void setHashtagTextChangedListener(@Nullable OnChangedListener listener) {
        hashtagChangedListener = listener;
    }

    @Override
    public void setMentionTextChangedListener(@Nullable OnChangedListener listener) {
        mentionChangedListener = listener;
    }

    @NonNull
    @Override
    public List<String> getHashtags() {
        return listOf(view.getText(), getHashtagPattern(), false);
    }

    @NonNull
    @Override
    public List<String> getMentions() {
        return listOf(view.getText(), getMentionPattern(), false);
    }

    @NonNull
    @Override
    public List<String> getHyperlinks() {
        return listOf(view.getText(), getHyperlinkPattern(), true);
    }

    private void ensureMovementMethod(Object listener) {
        if (listener == null) {
            view.setMovementMethod(initialMovementMethod);
        } else if (!(view.getMovementMethod() instanceof LinkMovementMethod)) {
            view.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private void recolorize() {
        final CharSequence text = view.getText();
        if (!(text instanceof Spannable)) {
            throw new IllegalStateException("Attached text is not a Spannable," +
                    "add TextView.BufferType.SPANNABLE when setting text to this TextView.");
        }
        final Spannable spannable = (Spannable) text;
        for (final Object span : spannable.getSpans(0, text.length(), CharacterStyle.class)) {
            spannable.removeSpan(span);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (isHashtagEnabled()) {
                spanAll(spannable, getHashtagPattern(), () -> hashtagClickListener != null
                        ? new SocialClickableSpan(hashtagClickListener, false)
                        : new ForegroundColorSpan(hashtagColors.getDefaultColor())
                );
            }
            if (isMentionEnabled()) {
                spanAll(spannable, getMentionPattern(), () -> mentionClickListener != null
                        ? new SocialClickableSpan(mentionClickListener, false)
                        : new ForegroundColorSpan(mentionColors.getDefaultColor())
                );
            }
            if (isHyperlinkEnabled()) {
                spanAll(spannable, getHyperlinkPattern(), () -> hyperlinkClickListener != null
                        ? new SocialClickableSpan(hyperlinkClickListener, true)
                        : new SocialURLSpan(text, hyperlinkColors)
                );
            }
        } else {
            if (isHashtagEnabled()) {
                spanAll(spannable, getHashtagPattern(), hashtagClickListener != null
                        ? new SocialClickableSpan(hashtagClickListener, false)
                        : new ForegroundColorSpan(hashtagColors.getDefaultColor())
                );
            }
            if (isMentionEnabled()) {
                spanAll(spannable, getMentionPattern(), mentionClickListener != null
                        ? new SocialClickableSpan(mentionClickListener, false)
                        : new ForegroundColorSpan(mentionColors.getDefaultColor())
                );
            }

            //
            if (isHyperlinkEnabled()) {
                spanAll(spannable, getHyperlinkPattern(), hyperlinkClickListener != null ? new SocialClickableSpan(hyperlinkClickListener, true)
                        : new SocialURLSpan(text, hyperlinkColors));
            }
        }

    }

    /**
     * Default {@link CharacterStyle} for <b>hyperlinks</b>.
     */
    private static class SocialURLSpan extends URLSpan {
        private final int color;

        private SocialURLSpan(CharSequence url, ColorStateList colors) {
            super(url.toString());
            this.color = colors.getDefaultColor();
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            ds.setColor(color);
            ds.setUnderlineText(true);
        }
    }

    /**
     * {@link CharacterStyle} that will be used for <b>hashtags</b>, <b>mentions</b>, and/or <b>hyperlinks</b>
     * when {@link } are activated.
     */
    private class SocialClickableSpan extends ClickableSpan {
        private final OnClickListener listener;
        private final boolean isHyperlink;
        private CharSequence text;

        private SocialClickableSpan(OnClickListener listener, boolean isHyperlink) {
            this.listener = listener;
            this.isHyperlink = isHyperlink;
        }

        @Override
        public void onClick(@NonNull View widget) {
            if (!(widget instanceof SocialView)) {
                throw new IllegalStateException("Clicked widget is not an instance of SocialView.");
            }
            listener.onClick((SocialView) widget, !isHyperlink ? text.subSequence(1, text.length()) : text);
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            ds.setColor(ContextCompat.getColor(view.getContext(), R.color.white));

            ds.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.popins_semi_bold));
            ds.setUnderlineText(isHyperlink);
        }
    }
}