package com.pix.xcandroid.room;

import android.content.Context;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.pix.xcandroid.R;
import com.pix.xcandroid.bean.ChatUserInfo;
import com.pix.xcandroid.utils.WeakReferenceHandler;

import java.util.ArrayList;

import static com.pix.xcandroid.room.MessagePanelAdapter.ZHOU_BANG;

/**
 * @author  pix
 * @version 1.0.0
 * @description
 * @modify
 */
public class MessagePanel extends FrameLayout {
    private static final String TAG = "MessagePanel";

    public static final int SCOLL_TO_START = 0X1000;
    /**
     * 聊天消息的最大数目
     */
    private static final int MSG_MAX_NUM = 100;
    private static final int SCROLLSTOPTIME = 5000;//滑动停止时间
    private Context mContext;
    private long mAnchorId;
    private String mAnchorName;
    private int mWeight;
    public RecyclerView mMessageRV;
    private MessagePanelAdapter mMessagePanelAdapter;
    private ArrayList<MessagePanelAdapter.MessageBean> mMessageList;

    private WeakReferenceHandler<MessagePanel> mHandler;

    private int mPanelState = RecyclerView.SCROLL_STATE_IDLE;
    private StaggeredGridLayoutManager mLayoutManager;

    public MessagePanel(Context context) {
        super(context);
        this.mContext = context;
        View v = inflate(getContext(), R.layout.li_layout_messagepanel, this);
        initview(v);

    }

    public MessagePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        View v = inflate(getContext(), R.layout.li_layout_messagepanel, this);
        initview(v);
    }

    private void initview(View v) {
        if (mHandler == null) {
            mHandler = new WeakReferenceHandler<MessagePanel>(this) {
                @Override
                protected void handleMessage(MessagePanel reference, Message msg) {
                    switch (msg.what) {
                        case MessagePanel.SCOLL_TO_START:
                            mMessageRV.smoothScrollToPosition(0);
                            break;
                    }
                }
            };
        }
        mMessageRV = v.findViewById(R.id.rv_message_panel);
        mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mLayoutManager.setReverseLayout(true);
        mMessageRV.setLayoutManager(mLayoutManager);
        this.mMessageList = new ArrayList<>();
        mMessagePanelAdapter = new MessagePanelAdapter(mMessageList);
        mMessageRV.setAdapter(mMessagePanelAdapter);

        mMessageRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                mPanelState = newState;
                if (mLayoutManager == null || getHandler() == null) {
                    return;
                }
                if (mLayoutManager.findFirstVisibleItemPositions(null)[0] == 0) {
                    mHandler.removeCallbacksAndMessages(null);
                    return;
                } else {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        mHandler.sendEmptyMessageDelayed(SCOLL_TO_START, SCROLLSTOPTIME);
                    } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        mHandler.removeCallbacksAndMessages(null);
                    }
                }
                super.onScrollStateChanged(recyclerView, newState);

            }
        });
    }

    public void setAnchorId(long anchorId, String anchorName, int weight) {
        mAnchorId = anchorId;
        mAnchorName = anchorName;
        mWeight = weight;
    }

    public void showChatMsg(ChatUserInfo srcUser, String msg, int userBangDanTag) {
        if (msg != null) {
            msg = msg.trim();
        }
        MessagePanelAdapter.MessageBean bean = new MessagePanelAdapter.MessageBean();
        bean.fromId = srcUser.getUid();
        bean.level = srcUser.getLevel();
        bean.message = msg;
        bean.userBangDanType = userBangDanTag;
        bean.fromUrl = srcUser.getHeadImgSmall();
        bean.fromName = makeUserNameLegal(srcUser.getNickname());
        bean.guardLevel = srcUser.getGuardLevel();
        if (bean.userBangDanType == ZHOU_BANG) {
            bean.messageType = MessagePanelAdapter.MessageBean.MESSAGE_NORMAL;
        } else {
            bean.messageType = MessagePanelAdapter.MessageBean.MESSAGE_BANGDAN;
        }
        mMessageList.add(0, bean);
        if (mMessageList.size() >= MSG_MAX_NUM) {
            mMessageList.remove(mMessageList.size() - 1);
        }
        if (mPanelState == RecyclerView.SCROLL_STATE_IDLE) {
            updateRecyclerViewDataSet();
        }
    }

    /**
     * 显示用户第一次点赞，关注，分享
     */
    public void showUserFirstMsg(String fromName, long fromId, int level, String msg, int guardLevel, int bangDanType) {
        if (TextUtils.isEmpty(fromName) && TextUtils.isEmpty(msg + "")) {
            return;
        }
        MessagePanelAdapter.MessageBean bean = new MessagePanelAdapter.MessageBean();
        bean.fromName = makeUserNameLegal(fromName);
        bean.fromId = fromId;
        bean.level = level;
        bean.message = msg;
        bean.messageType = MessagePanelAdapter.MessageBean.MESSAGE_FIRST;//去掉冒号
        bean.userBangDanType = bangDanType;
        bean.guardLevel = guardLevel;

        if (bean.message.equals(getResources().getString(R.string.li_room_tip_come))) {
            int v = 0;
            int n = 0;
            if (mMessageList.size() < 6) {
                n = mMessageList.size();
            } else {
                n = 6;
            }
            for (int i = 0; i < n; i++) {
                if (mMessageList.get(i).message.equals(getResources().getString(R.string.li_room_tip_come))) {
                    mMessageList.get(i).fromName = bean.fromName;
                    mMessageList.get(i).fromId = bean.fromId;
                    mMessageList.get(i).level = bean.level;
                    mMessageList.get(i).guardLevel = bean.guardLevel;
                    mMessageList.get(i).userBangDanType = bean.userBangDanType;
                    if (mPanelState == RecyclerView.SCROLL_STATE_IDLE) {
                        updateRecyclerViewDataSet();
                    }
                    return;
                } else {
                    v++;
                }
            }
            if (v >= n) {
                mMessageList.add(0, bean);
            }
        } else {
            mMessageList.add(0, bean);
        }
        if (mMessageList.size() >= MSG_MAX_NUM) {
            mMessageList.remove(mMessageList.size() - 1);
        }
        if (mPanelState == RecyclerView.SCROLL_STATE_IDLE) {
            updateRecyclerViewDataSet();
        }
    }

    private void updateRecyclerViewDataSet() {
        mMessagePanelAdapter.notifyDataSetChanged();
        mMessageRV.scrollToPosition(0);
    }

    /**
     * 显示系统消息
     *
     * @param msg
     */
    public void showSystemMsg(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        MessagePanelAdapter.MessageBean bean = new MessagePanelAdapter.MessageBean();
        bean.fromName = getResources().getString(R.string.li_room_tip_systems);
        bean.messageType = MessagePanelAdapter.MessageBean.MESSAGE_SYSTEM;
        bean.message = msg;
        mMessageList.add(0, bean);
        if (mPanelState == RecyclerView.SCROLL_STATE_IDLE) {
            updateRecyclerViewDataSet();
        }
    }

    /**
     * 处理标准系统信息
     *
     * @param msg
     */
    public void showRealSystemMsg(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        MessagePanelAdapter.MessageBean bean = new MessagePanelAdapter.MessageBean();
        bean.fromName = getResources().getString(R.string.li_room_tip_systems);
        bean.messageType = MessagePanelAdapter.MessageBean.MESSAGE_SERVER_SYSTEM;
        bean.message = msg;
        mMessageList.add(0, bean);
        if (mPanelState == RecyclerView.SCROLL_STATE_IDLE) {
            updateRecyclerViewDataSet();
        }
    }

    public void showGiftDontExist(long fromId, String fromName, int level) {
        if (fromName == null) {
            return;
        }
        MessagePanelAdapter.MessageBean bean = new MessagePanelAdapter.MessageBean();
        bean.fromId = fromId;
        bean.fromName = makeUserNameLegal(fromName);
        bean.level = level;
        bean.messageType = MessagePanelAdapter.MessageBean.MESSAGE_FIRST;
        bean.message = getResources().getString(R.string.li_room_tip_gift);
        mMessageList.add(0, bean);
        if (mPanelState == RecyclerView.SCROLL_STATE_IDLE) {
            updateRecyclerViewDataSet();
        }
    }

    /**
     * 礼物的消息
     *
     * @param giftName
     * @param giftSrc
     * @param repeatCount
     */
    public void showGift(ChatUserInfo sendUser, String giftName, String giftSrc, int repeatCount) {
        //名字中包含\、'、"需要添加转义符
        String fromName = makeUserNameLegal(sendUser.getNickname());
        MessagePanelAdapter.MessageBean bean = new MessagePanelAdapter.MessageBean();
        bean.fromId = sendUser.getUid();
        bean.level = sendUser.getLevel();
        if (repeatCount <= 1) {
            bean.message = getResources().getString(R.string.li_room_tip_gift_one) + " " + giftName;
        } else {
            bean.message = getResources().getString(R.string.li_room_tip_gift_one) + " " + giftName + "!" + " " + repeatCount + " " + getResources().getString(R.string.li_room_tip_continuous_click);
        }

        bean.fromName = fromName;
        bean.giftUrl = giftSrc;
        bean.guardLevel = sendUser.getGuardLevel();
        bean.userBangDanType = ZHOU_BANG;
        bean.messageType = MessagePanelAdapter.MessageBean.MESSAGE_GIFT;
        mMessageList.add(0, bean);
        if (mMessageList.size() >= MSG_MAX_NUM) {
            mMessageList.remove(mMessageList.size() - 1);
        }
        if (mPanelState == RecyclerView.SCROLL_STATE_IDLE) {
            updateRecyclerViewDataSet();
        }
    }

    /**
     * 清楚所有消息
     */
    public void clearAllMsg() {
        if (null != mMessageList) {
            mMessageList.clear();
        }
        if (null != mMessagePanelAdapter) {
            mMessagePanelAdapter.notifyDataSetChanged();
        }
    }

    //名字中包含\、'、"需要添加转义符
    private String makeUserNameLegal(String userName) {
        if (TextUtils.isEmpty(userName)) {
            userName = getResources().getString(R.string.li_room_name_default);
        } else {
            userName = userName.replace("\\", "\\\\");
            userName = userName.replace("\'", "\\\'");
            userName = userName.replace("\"", "\\\"");
        }
        return userName;
    }

    public void destroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }
}
