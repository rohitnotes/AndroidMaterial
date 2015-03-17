package io.doist.material.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import io.doist.material.R;
import io.doist.material.dialog.util.WindowCallbackWrapper;

/**
 * AlertDialog that enables the use of a custom layout as the dialog's
 * content view, in order to backport material-styled dialogs.
 * <p>
 * When its content view is set, it will replace the custom ids of its views by
 * the internal ids used by the framework's AlertController.
 */
public class MaterialAlertDialog extends AlertDialog {
    protected MaterialAlertDialog(Context context) {
        super(context);
    }

    protected MaterialAlertDialog(Context context, int theme) {
        super(context, theme);
    }

    protected MaterialAlertDialog(Context context, boolean cancelable,
                                  OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder extends AlertDialog.Builder {
        private boolean mCancelable = true;

        public Builder(Context context) {
            super(context);
        }

        public Builder(Context context, int theme) {
            super(context, theme);
        }

        @Override
        public AlertDialog.Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return super.setCancelable(cancelable);
        }

        @NonNull
        @Override
        public AlertDialog create() {
            AlertDialog dialog = super.create();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                // On pre-L androids, intercept the content view creation
                // and manipulate its view ids so that the framework can work with them.
                interceptAndConfigureContentView(dialog);
                // Enforce consistent behavior of cancelling on outside touch.
                // Some devices (Samsung, at least), removed this behavior from their dialogs.
                if (mCancelable) {
                    dialog.setCanceledOnTouchOutside(true);
                }
            }
            return dialog;
        }

        private void interceptAndConfigureContentView(AlertDialog dialog) {
            final Window window = dialog.getWindow();
            dialog.getWindow().setCallback(new WindowCallbackWrapper(window.getCallback()) {
                @Override
                public void onContentChanged() {
                    // This is called when the dialog content view is set
                    // and before the AlertController starts styling it.

                    // Call onContentChanged in the original callback (the dialog).
                    super.onContentChanged();

                    // We swap the custom ids from the views in our custom dialog layout,
                    // by the internal ids used by android AlertController.
                    // That way, the native AlertDialog can work with the new layout.
                    ViewGroup decorView = (ViewGroup) window.getDecorView();
                    swapCustomIdsByInternalIds(getContext(), decorView);

                    // Restore original callback. We no longer need to intercept callbacks.
                    window.setCallback(mCallback);
                }

                private void swapCustomIdsByInternalIds(Context context, View decorView) {
                    int[] ids = new int[] {
                            R.id.parentPanel, R.id.topPanel, R.id.title_template, R.id.alertTitle, R.id.contentPanel,
                            R.id.scrollView, R.id.textSpacerNoButtons, R.id.customPanel, R.id.buttonPanel};
                    String[] idNames = new String[] {
                            "parentPanel", "topPanel", "title_template", "alertTitle", "contentPanel",
                            "scrollView", "textSpacerNoButtons", "customPanel", "buttonPanel"};

                    for (int i = 0; i < ids.length; i++) {
                        int id = ids[i];
                        View view = decorView.findViewById(id);
                        if (view != null) {
                            int androidId = context.getResources().getIdentifier(idNames[i], "id", "android");
                            if (androidId != 0) {
                                view.setId(androidId);
                            }
                        }
                    }
                }
            });
        }
    }
}
