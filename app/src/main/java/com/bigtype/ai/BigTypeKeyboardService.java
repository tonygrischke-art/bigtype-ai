package com.bigtype.ai;

import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.util.Log;

public class BigTypeKeyboardService extends InputMethodService {
    private static final String TAG = "BigTypeAI";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
    }

    @Override
    public View onCreateInputView() {
        Log.d(TAG, "onCreateInputView called");
        try {
            BigTypeKeyboardView keyboardView = new BigTypeKeyboardView(this);
            keyboardView.setOnKeyPressListener((code, label) -> {
                InputConnection ic = getCurrentInputConnection();
                if (ic == null) {
                    Log.w(TAG, "InputConnection is null");
                    return;
                }
                if (code == -5) {
                    CharSequence selected = ic.getSelectedText(0);
                    if (selected != null && selected.length() > 0) {
                        ic.commitText("", 1);
                    } else {
                        ic.deleteSurroundingText(1, 0);
                    }
                } else if (code == -4) {
                    ic.sendKeyEvent(new android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_ENTER));
                    ic.sendKeyEvent(new android.view.KeyEvent(android.view.KeyEvent.ACTION_UP, android.view.KeyEvent.KEYCODE_ENTER));
                } else if (code == 32) {
                    ic.commitText(" ", 1);
                } else {
                    ic.commitText(label, 1);
                }
            });
            Log.d(TAG, "Keyboard view created OK");
            return keyboardView;
        } catch (Exception e) {
            Log.e(TAG, "CRASH in onCreateInputView: " + e.getMessage(), e);
            return new View(this);
        }
    }
}