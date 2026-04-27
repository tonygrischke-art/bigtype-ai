package com.bigtype.ai;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class BigTypeKeyboardView extends View {
    private Paint keyPaint, textPaint, bgPaint;
    private List<Key> keys = new ArrayList<>();
    private OnKeyPressListener listener;

    public interface OnKeyPressListener {
        void onKeyPress(int code, String label);
    }

    public BigTypeKeyboardView(Context context) {
        super(context);
        init();
    }

    private void init() {
        keyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(48f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        bgPaint = new Paint();
        bgPaint.setColor(Color.parseColor("#0F172A"));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        buildKeys(w, h);
    }

    private void buildKeys(int width, int height) {
        keys.clear();
        String[] rows = {"qwertyuiop", "asdfghjkl", "zxcvbnm"};
        float kw = width / 10f;
        float kh = 130f * getResources().getDisplayMetrics().density;

        for (int r = 0; r < rows.length; r++) {
            String row = rows[r];
            float offset = r == 1 ? kw/2 : r == 2 ? kw : 0;
            for (int c = 0; c < row.length(); c++) {
                char ch = row.charAt(c);
                keys.add(new Key(offset + c*kw, r*(kh+8), kw-6, kh, String.valueOf(ch), (int)ch));
            }
        }
        keys.add(new Key(kw*2, 3*(kh+8), kw*6, kh, "SPACE", 32));
        keys.add(new Key((float)(kw*8.5), 2*(kh+8), (float)(kw*1.4), kh, "DEL", -5));
        keys.add(new Key((float)(kw*8.5), 3*(kh+8), (float)(kw*1.4), kh, "ENTER", -4));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), bgPaint);
        for (Key k : keys) {
            keyPaint.setColor(k.pressed ? Color.CYAN : Color.parseColor("#1E293B"));
            RectF r = new RectF(k.x+3, k.y+3, k.x+k.w-3, k.y+k.h-3);
            canvas.drawRoundRect(r, 12, 12, keyPaint);
            textPaint.setColor(k.pressed ? Color.BLACK : Color.WHITE);
            canvas.drawText(k.label, k.x+k.w/2, k.y+k.h/2+16, textPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            for (Key k : keys) {
                if (x > k.x && x < k.x+k.w && y > k.y && y < k.y+k.h) {
                    k.pressed = true;
                    if (listener != null) listener.onKeyPress(k.code, k.label);
                    invalidate();
                    postDelayed(() -> { k.pressed = false; invalidate(); }, 100);
                    break;
                }
            }
        }
        return true;
    }

    public void setOnKeyPressListener(OnKeyPressListener l) { listener = l; }

    private static class Key {
        float x, y, w, h;
        String label;
        int code;
        boolean pressed;
        Key(float x, float y, float w, float h, String l, int c) {
            this.x=x; this.y=y; this.w=w; this.h=h; label=l; code=c;
        }
    }
}