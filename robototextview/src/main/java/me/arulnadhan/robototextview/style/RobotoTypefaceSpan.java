package me.arulnadhan.robototextview.style;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import me.arulnadhan.robototextview.util.RobotoTypefaceUtils;
import me.arulnadhan.robototextview.util.RobotoTypefaceManager;

/**
 * Span for replacing typeface.
 */
public class RobotoTypefaceSpan extends MetricAffectingSpan {

    /**
     * Created typefaces.
     */
    private final Typeface mTypeface;

    /**
     * Constructor to use with default typeface (regular).
     *
     * @param context The Context the span is using in, through which it can
     *                access the current theme, resources, etc.
     */
    public RobotoTypefaceSpan(Context context) {
        this(context, RobotoTypefaceManager.Typeface.ROBOTO_REGULAR);
    }

    /**
     * Constructor to use with typeface id.
     *
     * @param context    The Context the span is using in, through which it can
     *                   access the current theme, resources, etc.
     * @param typefaceId Typeface id ({@link RobotoTypefaceManager.Typeface})
     */
    public RobotoTypefaceSpan(Context context, int typefaceId) {
        mTypeface = RobotoTypefaceManager.obtainTypeface(context, typefaceId);
    }

    /**
     * Constructor to use with set of parameters.
     *
     * @param context    The Context the span is using in, through which it can
     *                   access the current theme, resources, etc.
     * @param fontFamily The value of "fontFamily" attribute ({@link RobotoTypefaceManager.FontFamily})
     * @param textWeight The value of "textWeight" attribute ({@link RobotoTypefaceManager.TextWeight})
     * @param textStyle  The value of "textStyle" attribute ({@link RobotoTypefaceManager.TextStyle})
     */
    public RobotoTypefaceSpan(Context context, int fontFamily, int textWeight, int textStyle) {
        mTypeface = RobotoTypefaceManager.obtainTypeface(context, fontFamily, textWeight, textStyle);
    }

    /**
     * Returns the typeface specified by this span.
     */
    @NonNull
    public Typeface getTypeface() {
        return mTypeface;
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        updateMeasureState(tp);
    }

    @Override
    public void updateMeasureState(TextPaint tp) {
        RobotoTypefaceUtils.setup(tp, mTypeface);
    }
}