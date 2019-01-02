package com.pix.xcandroid.room;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pix.xcandroid.R;
import com.pix.xcandroid.utils.Utils;
import com.pix.xcserverlibrary.utils.LogUtils;

import java.util.Random;

public class RoomInputView extends RelativeLayout implements View.OnClickListener  {
    private static final String TAG = RoomInputView.class.getSimpleName();
    private static final int[] HINT = {R.string.li_room_message_hint_1, R.string.li_room_message_hint_2, R.string.li_room_message_hint_3};
    private static final int TEXT_CHAT_LENGTH = 60;
    private static final int MSG_MAX_TIME = 1000;

    public EditText mInputET;
    private TextView mSendBtn;

    private Random mRandom;
    private long lastSendTime = 0L;

    private OnSendMsgListener sendMsgListener;


    public RoomInputView(Context context) {
        this(context, null);
    }

    public RoomInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRandom = new Random();
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.li_layout_room_input_view, this);
        mInputET = findViewById(R.id.et_input_message_panel);
        refreshHint(1);
        mSendBtn = findViewById(R.id.btn_send_message_panel);
        mSendBtn.setOnClickListener(this);

        // 设置过滤器，用来限制字数，现在加了弹屏的字数限制
        mInputET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30) {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.length() == 0) {
                    // 删除操作，返回null接收原操作
                    return null;
                }
                int keep = TEXT_CHAT_LENGTH - (getCustomStringLength(dest.toString()) - (dend - dstart));
                if (keep <= 0) {
                    //这里，用来给用户提示
                    Toast.makeText(getContext(),getContext().getString(R.string.li_room_text_input_reached_limits),Toast.LENGTH_SHORT).show();
                    return "";
                } else if (keep >= end - start) {
                    return null; // keep original
                } else {
                    keep += start;
                    if (Character.isHighSurrogate(source.charAt(keep - 1))) {
                        --keep;
                        if (keep == start) {
                            return "";
                        }
                    }
                    return source.subSequence(start, keep);
                }
            }

            private int getCustomStringLength(String dest) {
                int len = 0;
                for (int i = 0; i < dest.length(); i++) {
                    if (dest.substring(i, i + 1).getBytes().length == 1) {
                        len += 1;
                    } else {
                        len += 2;
                    }
                }
                return len;
            }
        }});
    }

    public void setInputText(String text) {
        mInputET.setText(text);
        mInputET.setSelection(mInputET.getText().length());
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_message_panel:
                sendMsg();
                break;
        }
    }


    private void sendMsg() {
        if (mInputET != null && !TextUtils.isEmpty(mInputET.getText())) {
            String message = mInputET.getText().toString();
            message = message.replace(System.getProperty("line.separator"), "");
            if (TextUtils.isEmpty(message) || message.trim().length() == 0) {
                showToast(getContext(), R.string.li_room_message_send_kong);
                return;
            }
            long curTime = System.currentTimeMillis();
            if (curTime < lastSendTime + MSG_MAX_TIME) {
                showToast(getContext(), R.string.li_room_message_send_time);
                return;
            }
            lastSendTime = curTime;
            LogUtils.d(TAG, "send msg : " + message);
            mInputET.setText("");
            // 发送消息
            if(null != sendMsgListener) {
                sendMsgListener.sendMsg(message);
            }
        }
    }

    public void refreshHint(int type) {
        if (mInputET == null) return;

        switch (type) {
            case 1:
                mInputET.setHint(HINT[mRandom.nextInt(HINT.length)]);
                break;
        }
    }

    public void setSoftKeyboardVisible(boolean visible) {
        if (visible) {
            setVisibility(VISIBLE);
            if (!mInputET.isFocused()) {
                mInputET.requestFocus();
                Utils.Companion.showSoftInput(getContext());
            }
        } else {
            Utils.Companion.hideSoftInput(mInputET, getContext());
            setVisibility(GONE);
        }
    }

    private void showToast(Context ctx,@StringRes  int msg) {
        Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show();
    }

    /**
     * 发送消息监听器
     */
    public interface OnSendMsgListener {
        public void sendMsg(String msg);
    }

    public void setOnSendMsgListener(OnSendMsgListener listener) {
        sendMsgListener = listener;
    }
}
