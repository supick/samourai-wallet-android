package com.samourai.wallet.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.samourai.wallet.R;

/**
 * Created by Sarath kumar on 1/23/2018.
 */
public class MnemonicSeedEditText extends EditText {
    TextWatcher textWatcher;

    String lastString;

    String separator = " ";

    public MnemonicSeedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MnemonicSeedEditText(Context context) {
        super(context);
        init();
    }

    public MnemonicSeedEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setMovementMethod(LinkMovementMethod.getInstance());
        setSelection(0);
        setCursorVisible(true);
        requestFocus();
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String thisString = s.toString();
                if (thisString.length() > 0 && !thisString.equals(lastString)) {
                    format();
                }
            }
        };
        addTextChangedListener(textWatcher);
    }


    private void format() {

        SpannableStringBuilder sb = new SpannableStringBuilder();
        String fullString = getText().toString();

        String[] strings = fullString.split(separator);

        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            sb.append(string.replace("\n", ""));
            if (fullString.charAt(fullString.length() - 1) != separator.charAt(0) && i == strings.length - 1) {
                break;
            }
            BitmapDrawable bd = convertViewToDrawable(createTokenView(string));
            bd.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());
            int startIdx = sb.length() - (string.length());
            int endIdx = sb.length();
            sb.setSpan(new ImageSpan(bd), startIdx, endIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpanClick myClickableSpan = new SpanClick(startIdx, endIdx);
            sb.setSpan(myClickableSpan, Math.max(endIdx - 2, startIdx), endIdx, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (i < strings.length - 1) {
                sb.append(separator);
            } else if (fullString.charAt(fullString.length() - 1) == separator.charAt(0)) {
                sb.append(separator);
            }
        }
        lastString = sb.toString();
        setText(sb);
        setSelection(sb.length());

    }

    public View createTokenView(String text) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setBackgroundResource(R.drawable.round_rectangle_token_word_background);
        TextView tv = new TextView(getContext());
        tv.setPadding(22, 5, 22, 5);
        layout.addView(tv);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        tv.setTextColor(Color.WHITE);
        return layout;
    }

    public BitmapDrawable convertViewToDrawable(View view) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap b = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.translate(-view.getScrollX(), -view.getScrollY());
        view.draw(c);
        view.setDrawingCacheEnabled(true);
        Bitmap cacheBmp = view.getDrawingCache();
        Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
        view.destroyDrawingCache();
        return new BitmapDrawable(getContext().getResources(), viewBmp);
    }

    private class SpanClick extends ClickableSpan {

        int startIdx;
        int endIdx;

        public SpanClick(int startIdx, int endIdx) {
            super();
            this.startIdx = startIdx;
            this.endIdx = endIdx;
        }

        @Override
        public void onClick(View widget) {
            String s = getText().toString();
            String s1 = s.substring(0, startIdx);
            String s2 = s.substring(Math.min(endIdx + 1, s.length() - 1), s.length());
            MnemonicSeedEditText.this.setText(s1 + s2);
        }

    }
}
