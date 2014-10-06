package io.doist.material.widget.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import io.doist.material.R;


public class Roboto {
    public static final String TAG = Roboto.class.getSimpleName();

    private static final String REGULAR_FAMILY = "sans-serif"; // Styles: italic, bold.
    private static final String LIGHT_FAMILY = "sans-serif-light"; // Styles: italic.
    private static final String MEDIUM_FAMILY = "sans-serif-medium"; // Styles: italic.

    private static final String CONDENSED_FAMILY = "sans-serif-condensed"; // Styles: italic, bold.
    private static final String CONDENSED_LIGHT_FAMILY = "sans-serif-condensed-light"; // Styles: italic.

    public static void apply(TextView textView, Context context, AttributeSet attrs, int defStyle) {
        // Skip applying altogether if it's rendering in the layout editor.
        if (textView.isInEditMode()) {
            return;
        }

        // Extract the font family and text style.
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextAppearance, defStyle, 0);
        String fontFamily = a.getString(R.styleable.TextAppearance_android_fontFamily);
        int textStyle = a.getInt(R.styleable.TextAppearance_android_textStyle, Typeface.NORMAL);
        a.recycle();

        // Grab the appropriate variant and apply it.
        Typeface typeface = RobotoTypeface.get(context, fontFamily, textStyle);
        if (typeface == null) {
            typeface = Typeface.create(fontFamily, textStyle);
        }
        textView.setTypeface(typeface, textStyle);
    }

    private static class RobotoTypeface {
        private static RobotoTypefaceForStyle regularStyle =
                new RobotoTypefaceForStyle(
                        "fonts/Roboto-Regular.ttf",
                        "fonts/Roboto-Bold.ttf",
                        "fonts/Roboto-Italic.ttf",
                        "fonts/Roboto-BoldItalic.ttf");
        private static RobotoTypefaceForStyle lightStyle =
                new RobotoTypefaceForStyle(
                        "fonts/Roboto-Light.ttf",
                        /* Android will fake the bold. See TextView#setTypeface(Typeface, int). */
                        "fonts/Roboto-Light.ttf",
                        "fonts/Roboto-LightItalic.ttf",
                        /* Android will fake the bold and italic. See TextView#setTypeface(Typeface, int). */
                        "fonts/Roboto-Light.ttf");
        private static RobotoTypefaceForStyle mediumStyle =
                new RobotoTypefaceForStyle(
                        "fonts/Roboto-Medium.ttf",
                        /* Android will fake the bold. See TextView#setTypeface(Typeface, int). */
                        "fonts/Roboto-Medium.ttf",
                        "fonts/Roboto-MediumItalic.ttf",
                        /* Android will fake the bold and italic. See TextView#setTypeface(Typeface, int). */
                        "fonts/Roboto-Medium.ttf");
        private static RobotoTypefaceForStyle condensedStyle =
                new RobotoTypefaceForStyle(
                        "fonts/RobotoCondensed-Regular.ttf",
                        "fonts/RobotoCondensed-Bold.ttf",
                        "fonts/RobotoCondensed-Italic.ttf",
                        "fonts/RobotoCondensed-BoldItalic.ttf");
        private static RobotoTypefaceForStyle condensedLightStyle =
                new RobotoTypefaceForStyle(
                        "fonts/RobotoCondensed-Light.ttf",
                        /* Android will fake the bold. See TextView#setTypeface(Typeface, int). */
                        "fonts/RobotoCondensed-Light.ttf",
                        "fonts/RobotoCondensed-LightItalic.ttf",
                        /* Android will fake the bold and italic. See TextView#setTypeface(Typeface, int). */
                        "fonts/RobotoCondensed-Light.ttf");

        public static Typeface get(Context context, String family, int style) {
            try {
                if (family == null || REGULAR_FAMILY.equals(family)) {
                    return regularStyle.get(context, style);
                } else if (LIGHT_FAMILY.equals(family)) {
                    return lightStyle.get(context, style);
                } else if (MEDIUM_FAMILY.equals(family)) {
                    return mediumStyle.get(context, style);
                } else if (CONDENSED_FAMILY.equals(family)) {
                    return condensedStyle.get(context, style);
                } else if (CONDENSED_LIGHT_FAMILY.equals(family)) {
                    return condensedLightStyle.get(context, style);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to load Roboto font (family: " + family + ", style: " + style + ")", e);
            }

            return null;
        }

        private static class RobotoTypefaceForStyle {
            private final String normalPath;
            private final String boldPath;
            private final String italicPath;
            private final String boldItalicPath;

            private volatile Typeface normalStyledTypeface;
            private volatile Typeface boldStyledTypeface;
            private volatile Typeface italicStyledTypeface;
            private volatile Typeface boldItalicTypeface;

            private RobotoTypefaceForStyle(String normalPath, String boldPath, String italicPath,
                                           String boldItalicPath) {
                this.normalPath = normalPath;
                this.boldPath = boldPath;
                this.italicPath = italicPath;
                this.boldItalicPath = boldItalicPath;
            }

            public Typeface get(Context context, int style) {
                switch (style) {
                    case Typeface.NORMAL:
                        if (normalStyledTypeface == null) {
                            synchronized (normalPath) {
                                if (normalStyledTypeface == null) {
                                    normalStyledTypeface = Typeface.createFromAsset(context.getAssets(), normalPath);
                                }
                            }
                        }
                        return normalStyledTypeface;

                    case Typeface.BOLD:
                        if (boldPath.equals(normalPath)) {
                            boldStyledTypeface = get(context, Typeface.NORMAL);
                        } else if (boldStyledTypeface == null) {
                            synchronized (boldPath) {
                                if (boldStyledTypeface == null) {
                                    boldStyledTypeface = Typeface.createFromAsset(context.getAssets(), boldPath);
                                }
                            }
                        }
                        return boldStyledTypeface;

                    case Typeface.ITALIC:
                        if (italicPath.equals(normalPath)) {
                            italicStyledTypeface = get(context, Typeface.NORMAL);
                        } else if (italicStyledTypeface == null) {
                            synchronized (italicPath) {
                                if (italicStyledTypeface == null) {
                                    italicStyledTypeface = Typeface.createFromAsset(context.getAssets(), italicPath);
                                }
                            }
                        }
                        return italicStyledTypeface;

                    case Typeface.BOLD_ITALIC:
                        if (boldItalicPath.equals(normalPath)) {
                            boldItalicTypeface = get(context, Typeface.NORMAL);
                        } else if (boldItalicTypeface == null) {
                            synchronized (boldItalicPath) {
                                if (boldItalicTypeface == null) {
                                    boldItalicTypeface = Typeface.createFromAsset(context.getAssets(), boldItalicPath);
                                }
                            }
                        }

                        return boldItalicTypeface;

                    default:
                        return null;
                }
            }
        }
    }
}
