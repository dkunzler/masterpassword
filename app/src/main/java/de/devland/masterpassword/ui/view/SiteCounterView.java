package de.devland.masterpassword.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.devland.masterpassword.R;
import lombok.Setter;

/**
 * Created by deekay on 27/02/15.
 */
public class SiteCounterView extends LinearLayout implements TextWatcher, View.OnFocusChangeListener {

    @Bind(R.id.editText_siteCounter)
    protected EditText counter;
    @Bind(R.id.plusButton)
    protected FloatingActionButton plus;
    @Bind(R.id.minusButton)
    protected FloatingActionButton minus;

    @Setter
    protected int minValue = 1;
    @Setter
    protected TextWatcher onChangeListener;

    public SiteCounterView(Context context) {
        super(context);
        init();
    }

    public SiteCounterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SiteCounterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SiteCounterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    public void init() {
        inflate(getContext(), R.layout.view_sitecounter, this);
        ButterKnife.bind(this);
        counter.addTextChangedListener(this);
        counter.setOnFocusChangeListener(this);
//        this.setOrientation(LinearLayout.HORIZONTAL);
//        int _4dp = Math.round(Utils.convertDpToPixel(4f, getContext()));
//        this.setPadding(_4dp, _4dp, _4dp, _4dp);
//
//        minus = new Button(getContext());
//        minus.setText("-");
//        minus.setBackgroundResource(R.color.accent);
//        minus.setOnClickListener(this);
////        minus.set(10, 10, 10, 10);
//        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        params.setMargins(50,100,150,200);
//        this.addView(minus, params);
//
//        counter = new EditText(getContext());
//        counter.setEllipsize(TextUtils.TruncateAt.START);
//        counter.setGravity(Gravity.CENTER);
//        counter.setEms(10);
//        counter.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
//        counter.addTextChangedListener(this);
//        counter.setOnFocusChangeListener(this);
//        LayoutParams counterLayoutParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
//        counterLayoutParams.setMargins(_4dp, 0, _4dp, 0);
//        this.addView(counter, counterLayoutParams);
//
//        plus = new Button(getContext());
//        plus.setText("+");
//        plus.setBackgroundResource(R.color.accent);
//        plus.setOnClickListener(this);
//        this.addView(plus, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }


    public int getValue() {
        try {
            return Integer.parseInt(counter.getText().toString());
        } catch (NumberFormatException e) {
            return minValue;
        }
    }

    public void setValue(int value) {
        if (value < minValue) {
            value = minValue;
        }
        counter.setText(String.valueOf(value));
    }

    @OnClick({R.id.plusButton, R.id.minusButton})
    public void onClick(View view) {
        if (view == minus) {
            setValue(getValue() - 1);
        }
        if (view == plus) {
            setValue(getValue() + 1);
        }
    }


    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.length() != 0) {
            try {
                int count = Integer.parseInt(editable.toString());
                if (count < minValue) {
                    counter.setText(String.valueOf(minValue));
                }
            } catch (NumberFormatException e) {
                counter.setText(String.valueOf(minValue));
            }
        }
        if (onChangeListener != null) {
            onChangeListener.afterTextChanged(editable);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        if (onChangeListener != null) {
            onChangeListener.beforeTextChanged(charSequence, i, i2, i3);
        }
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        if (onChangeListener != null) {
            onChangeListener.onTextChanged(charSequence, i, i2, i3);
        }
    }

    @Override
    public void onFocusChange(View view, boolean focus) {
        if (!focus) {
            try {
                Integer.parseInt(counter.getText().toString());
            } catch (NumberFormatException e) {
                counter.setText(String.valueOf(minValue));
            }
        }
    }
}
