package com.pix.xcandroid.room;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.pix.xcandroid.R;
import com.pix.xcandroid.utils.Utils;
import com.pix.xcandroid.widget.LevelLayout;
import com.pix.xcandroid.widget.PicAndTextView;
import com.pix.xcserverlibrary.utils.LogUtils;

import java.util.List;

/**
 * @author  pix
 * @version 1.0.0
 * @description This is MessageViewAdapter
 * @modify
 */
public class MessagePanelAdapter extends RecyclerView.Adapter<MessagePanelAdapter.ViewHolder> {
    private static final String TAG = "MessagePanelAdapter";

    private static final int VIEW_TYPE_CHAT = 1;
    private static final int VIEW_TYPE_NOTICE = 2;
    private static final int VIEW_TYPE_GIFT = 3;
    private static final int VIEW_TYPE_BANG_CHAT = 4;
    private static final int VIEW_TYPE_SERVER_SYSTEM = 5;

    public static final int ZONG_BANG=0;
    public static final int ZHOU_BANG=1;
    public static final int GONG_XIAN_BANG=2;
    public static final int NO_BANG=-1;

    protected List<MessageBean> mMessageList;

    public MessagePanelAdapter(List<MessageBean> list) {
        this.mMessageList = list;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*View view = LayoutInflater.from(parent.getContext())
                .inflate(R.seekbar_view_play_layout.li_layout_message_item, parent, false);
        return null;*/
        if (viewType == VIEW_TYPE_CHAT) {
            return new ChatViewHolder(View.inflate(parent.getContext(), R.layout.layout_room_message_chat, null));
        }
        if (viewType == VIEW_TYPE_NOTICE) {
            return new NoticeViewHolder(View.inflate(parent.getContext(), R.layout.layout_room_message_chat, null));
        }
        if (viewType == VIEW_TYPE_GIFT) {
            return new GiftViewHolder(View.inflate(parent.getContext(), R.layout.layout_room_message_chat, null));
        }
        if (viewType == VIEW_TYPE_BANG_CHAT) {
            return new BangShouViewHolder(View.inflate(parent.getContext(), R.layout.layout_room_message_chat, null));
        }
        if(viewType==VIEW_TYPE_SERVER_SYSTEM){
            return new SystemViewHolder(View.inflate(parent.getContext(),R.layout.layout_room_message_chat,null));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        MessageBean data = mMessageList.get(pos);
        if(holder!=null && data != null) {
            holder.bindData(data);
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        MessageBean msg = mMessageList.get(position);
        if (msg.messageType == MessageBean.MESSAGE_NORMAL) {
            return VIEW_TYPE_CHAT;
        } else if (msg.messageType == MessageBean.MESSAGE_GIFT) {
            return VIEW_TYPE_GIFT;
        } else if (msg.messageType == MessageBean.MESSAGE_BANGDAN) {
            return VIEW_TYPE_BANG_CHAT;
        } else if (msg.messageType == MessageBean.MESSAGE_SERVER_SYSTEM) {
            return VIEW_TYPE_SERVER_SYSTEM;
        } else {
            return VIEW_TYPE_NOTICE;
        }
    }


    abstract class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        protected PicAndTextView ptvContent;
        protected ImageView ivGuardLevel;
        protected ImageView ivBang;
        protected final RelativeLayout rlMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            rlMessage = itemView.findViewById(R.id.rl_message);
            ptvContent = (PicAndTextView) itemView.findViewById(R.id.pic_text);
            PicAndTextView.LayoutParam layoutParam =new PicAndTextView.LayoutParam(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL);
            ivGuardLevel = new ImageView(itemView.getContext());
            ivGuardLevel.setLayoutParams(layoutParam);
            ivGuardLevel.setImageResource(R.drawable.li_guardian_gold);
            ivGuardLevel.setVisibility(View.GONE);

            ivBang = new ImageView(itemView.getContext());
            ivBang.setImageResource(R.drawable.li_user_zhou_bang_flag);
            ivBang.setVisibility(View.GONE);
            if(layoutParam!=null) {
                layoutParam.setMargins(Utils.Companion.dip2px(itemView.getContext(), 3),0,0,0);
                ivBang.setLayoutParams(layoutParam);
            }
            ptvContent.setOnClickListener(this);
            ptvContent.setOnLongClickListener(this);
        }

        public void bindData(MessageBean msg) {
            /*if (ivGuardLevel == null || ivBang == null) {
                PicAndTextView.LayoutParam layoutParam =new PicAndTextView.LayoutParam(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL);
                ivGuardLevel = new ImageView(itemView.getContext());
                ivGuardLevel.setLayoutParams(layoutParam);
                ivGuardLevel.setImageResource(R.mic_connect_frame.li_guardian_gold);
                ivGuardLevel.setVisibility(View.GONE);

                ivBang = new ImageView(itemView.getContext());
                ivBang.setImageResource(R.mic_connect_frame.li_user_zhou_bang_flag);
                ivBang.setVisibility(View.GONE);
                if(layoutParam!=null) {
                    layoutParam.setMargins(Utils.dip2px(itemView.getContext(), 3),0,0,0);
                    ivBang.setLayoutParams(layoutParam);
                }
            }*/
            ptvContent.setTag(msg);
            switch (msg.guardLevel) {
                case 1:
                    ivGuardLevel.setImageResource(R.drawable.li_guardian_silver);
                    ivGuardLevel.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    ivGuardLevel.setImageResource(R.drawable.li_guardian_gold);
                    ivGuardLevel.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    ivGuardLevel.setImageResource(R.drawable.li_guardian_diamond);
                    ivGuardLevel.setVisibility(View.VISIBLE);
                    break;
                default:
                    ivGuardLevel.setVisibility(View.GONE);
                    break;
            }

            switch (msg.userBangDanType) {
                case ZHOU_BANG:
                    ivBang.setImageResource(R.drawable.li_user_zhou_bang_flag);
                    ivBang.setVisibility(View.VISIBLE);
                    break;
                case ZONG_BANG:
                    ivBang.setImageResource(R.drawable.li_user_zong_bang_flag);
                    ivBang.setVisibility(View.VISIBLE);
                    break;
                default:
                    ivBang.setVisibility(View.GONE);
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            MessageBean msg = (MessageBean) ptvContent.getTag();
            if (msg != null) {
                long fromId = msg.fromId;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            MessageBean msg = (MessageBean) ptvContent.getTag();
            if (msg != null) {
                String long_name = msg.fromName;
                long_name = "@" + long_name + " ";
                LogUtils.d(TAG,"onLongClick(),long_name:" + long_name);
            }
            return true;
        }

    }

    class ChatViewHolder extends ViewHolder  {

        protected TextPaint tvNickname;
        protected TextPaint tvMessage;
        protected final LevelLayout viewLevel;
        public ChatViewHolder(View itemView) {
            super(itemView);
//            PicAndTextView.LayoutParam layoutParam =new PicAndTextView.LayoutParam(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL);
            PicAndTextView.LayoutParam levelParam=new PicAndTextView.LayoutParam(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL);
//            ptvContent = (PicAndTextView) itemView.findViewById(R.id.pic_text);
//            itemView.setOnLongClickListener(this);
//            itemView.setOnClickListener(this);
            viewLevel = new LevelLayout(itemView.getContext());
            viewLevel.setLayoutParams(levelParam);

            tvNickname = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG |Paint.FAKE_BOLD_TEXT_FLAG);
//            tvNickname.setShadowLayer(1,3,3,itemView.getContext().getResources().getColor(R.color.P50_BLACK));
            tvNickname.setTextSize(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP, 14, Resources.getSystem().getDisplayMetrics()));
            tvNickname.setColor(Color.WHITE);

            tvMessage = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG );
            tvMessage.setShadowLayer(0,1,1,Color.GRAY);
            tvMessage.setTextSize(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP, 14, Resources.getSystem().getDisplayMetrics()));
            tvMessage.setColor(Color.WHITE);
        }

        @Override
        public void bindData(MessageBean msg) {
            super.bindData(msg);
            rlMessage.setBackgroundResource(R.drawable.li_bg_message_chat);
            viewLevel.setLevel(msg.level);
//            viewLevel.setTag(msg);
            LogUtils.d(TAG,"bindData(),msg.guardLevel:" + msg.guardLevel);
            if(ptvContent!=null) {
                ptvContent.clean();
                ptvContent.addNewChild(viewLevel);
                if(ivGuardLevel != null && ivGuardLevel.getVisibility() == View.VISIBLE) {
                    ptvContent.addNewChild(ivGuardLevel);
                }
                if(ivBang != null && ivBang.getVisibility() == View.VISIBLE) {
                    ptvContent.addNewChild(ivBang);
                }
                ptvContent.addTextChild(" " +msg.fromName,tvNickname, true);
                ptvContent.addTextChild("  " +msg.message,tvMessage);
            }
        }

        /*@Override
        public void onClick(View v) {
            MessageBean msg = (MessageBean) viewLevel.getTag();
            if (msg != null) {
                long fromId = msg.fromId;
                EventBusManager.getInstance().post(new RoomBaseEvent.HandleRoomUserInfoDialog(null, fromId));

            }
        }

        @Override
        public boolean onLongClick(View v) {
            MessageBean msg = (MessageBean) viewLevel.getTag();
            if (msg != null) {
                String long_name = msg.fromName;
                long_name = "@" + long_name + " ";
                LogUtils.d(TAG,"onLongClick(),long_name:" + long_name);
                EventBusManager.getInstance().post(new Etyou(long_name));
            }
            return true;
        }*/
    }

    /**
     * 礼物信息使用的布局
     */
    class GiftViewHolder extends ViewHolder  {
        private LevelLayout levelLayout;
        private SimpleDraweeView simpleDraweeView;
//        private final View layoutMessage;
        private TextPaint tvNickname;
        TextPaint messagePaint;

        public GiftViewHolder(View itemView) {
            super(itemView);
//            layoutMessage = itemView.findViewById(R.id.layout_message);
//            ptvContent = (PicAndTextView) itemView.findViewById(R.id.pic_text);
            levelLayout = new LevelLayout(itemView.getContext());
            PicAndTextView.LayoutParam layoutParam=new PicAndTextView.LayoutParam(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL);
            layoutParam.setMargins(0,0,0,0);
            levelLayout.setLayoutParams(layoutParam);
            simpleDraweeView = new SimpleDraweeView(itemView.getContext());
            simpleDraweeView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            PicAndTextView.LayoutParam giftLP=new PicAndTextView.LayoutParam(Utils.Companion.dip2px(itemView.getContext()
                    ,18), Utils.Companion.dip2px(itemView.getContext(), 18), Gravity.CENTER_VERTICAL);
            giftLP.setMargins(10,0,0,0);
            simpleDraweeView.setLayoutParams(giftLP);

            tvNickname = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG |Paint.FAKE_BOLD_TEXT_FLAG);;
//            tvNickname.setShadowLayer(1,3,3,itemView.getContext().getResources().getColor(R.color.P50_BLACK));
            tvNickname.setTextSize(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP, 14, Resources.getSystem().getDisplayMetrics()));
            tvNickname.setColor(Color.WHITE);

            messagePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG );
            messagePaint.setTextSize(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP, 14, Resources.getSystem().getDisplayMetrics()));
            messagePaint.setColor(Color.WHITE);
//            messagePaint.setShadowLayer(1,2,2,itemView.getContext().getResources().getColor(R.color.P50_BLACK));
//            itemView.setOnClickListener(this);
//            itemView.setOnLongClickListener(this);
        }

        /*@Override
        public void onClick(View v) {
            MessageBean msg = (MessageBean) ptvContent.getTag();
            if (msg != null) {
                long fromId = msg.fromId;
                EventBusManager.getInstance().post(new RoomBaseEvent.HandleRoomUserInfoDialog(null, fromId));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            MessageBean msg = (MessageBean) ptvContent.getTag();
            if (msg != null && msg.messageType != MessageBean.MESSAGE_SYSTEM) {
                String long_name = msg.fromName;
                long_name = "@" + long_name + " ";
                LogUtils.d(TAG, "onLongClick(),long_name:" + long_name);
                EventBusManager.getInstance().post(new Etyou(long_name));
            }
            return true;
        }*/

        @Override
        public void bindData(MessageBean msg) {
            super.bindData(msg);
//            layoutMessage.setTag(msg);
//            ptvContent.setTag(msg);
            rlMessage.setBackgroundResource(R.drawable.li_bg_message_other);
            ptvContent.clean();
            ptvContent.addNewChild(levelLayout);
            if(ivGuardLevel != null && ivGuardLevel.getVisibility() == View.VISIBLE) {
                ptvContent.addNewChild(ivGuardLevel);
            }
            if(ivBang != null && ivBang.getVisibility() == View.VISIBLE) {
                ptvContent.addNewChild(ivBang);
            }
            ptvContent.addTextChild(" " + msg.fromName, tvNickname, true);
            ptvContent.addTextChild(" " + msg.message,messagePaint);
            ptvContent.addNewChild(simpleDraweeView);
            simpleDraweeView.setImageURI(Uri.parse(msg.giftUrl));
            levelLayout.setLevel(msg.level);
//            layoutMessage.setOnLongClickListener(this);
//            layoutMessage.setOnClickListener(this);
        }
    }


    class BangShouViewHolder extends ChatViewHolder {
        public BangShouViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindData(MessageBean msg) {
            super.bindData(msg);
            if (msg.userBangDanType == ZHOU_BANG) {
                tvNickname.setColor(Color.BLACK);
            } else if (msg.userBangDanType == ZONG_BANG) {
                tvNickname.setColor(Color.BLACK);
            }

        }
    }

    class SystemViewHolder extends ViewHolder {
        private TextPaint namePaint;
        private TextPaint contentPaint;

        public SystemViewHolder(View itemView) {
            super(itemView);
//            ptvContent= (PicAndTextView) itemView.findViewById(R.id.pic_text);
            namePaint=new TextPaint(Paint.DITHER_FLAG|Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
            namePaint.setColor(0xffb87cfb);
            namePaint.setTextSize(Utils.Companion.dip2px(itemView.getContext(),16));
            namePaint.setShadowLayer(0,1,1,Color.BLACK);

            contentPaint=new TextPaint(Paint.DITHER_FLAG|Paint.ANTI_ALIAS_FLAG);
            contentPaint.setColor(0xffb87cfb);
            contentPaint.setTextSize(Utils.Companion.dip2px(itemView.getContext(),16));
            contentPaint.setShadowLayer(0,1,1,Color.BLACK);
        }

        @Override
        public void bindData(MessageBean msg) {
            super.bindData(msg);
            rlMessage.setBackgroundResource(0);
            ptvContent.clean();
            ptvContent.addTextChild(msg.fromName.trim(),namePaint);
            ptvContent.addTextChild(" " + msg.message,contentPaint);
        }
    }


     class NoticeViewHolder extends ViewHolder  {

//        private final View layoutMessage;
        LevelLayout levelLayout;
        TextPaint namePaint;
        ImageView imageView;
        TextPaint systemPaint;
        TextPaint heartPaint;
        ImageView heartImageView;
        TextPaint messagePaint;
        TextPaint messageContent;
        public NoticeViewHolder(View itemView) {
            super(itemView);
//            layoutMessage = itemView.findViewById(R.id.layout_message);
//            ptvContent = (PicAndTextView) itemView.findViewById(R.id.pic_text);
            levelLayout = new LevelLayout(itemView.getContext());
            PicAndTextView.LayoutParam layoutParam=new PicAndTextView.LayoutParam(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL);
            layoutParam.setMargins(0,0,0,0);
            levelLayout.setLayoutParams(layoutParam);

            namePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
            namePaint.setTextSize(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP, 14, Resources.getSystem().getDisplayMetrics()));
            namePaint.setColor(Color.WHITE);
//            namePaint.setShadowLayer(1,3,3,itemView.getContext().getResources().getColor(R.color.P50_BLACK));

            messagePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG );
            messagePaint.setTextSize(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP, 14, Resources.getSystem().getDisplayMetrics()));
            messagePaint.setColor(0xffffffff);
//            messagePaint.setShadowLayer(1,2,2,itemView.getContext().getResources().getColor(R.color.P50_BLACK));

            systemPaint = new TextPaint(Paint.DITHER_FLAG|Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
            systemPaint.setColor(0xffb87cfb);
            systemPaint.setTextSize(Utils.Companion.dip2px(itemView.getContext(),16));
            systemPaint.setShadowLayer(0,1,1,Color.BLACK);

            messageContent=new TextPaint(Paint.DITHER_FLAG|Paint.ANTI_ALIAS_FLAG);
            messageContent.setColor(0xffb87cfb);
            messageContent.setTextSize(Utils.Companion.dip2px(itemView.getContext(),16));
            messageContent.setShadowLayer(0,1,1,Color.BLACK);

            imageView=new ImageView(itemView.getContext());
            heartPaint=new TextPaint(Paint.DITHER_FLAG|Paint.ANTI_ALIAS_FLAG);
            heartPaint.setColor(0xFFFF638B);
            heartPaint.setTextSize(Utils.Companion.dip2px(itemView.getContext(),16));
            imageView.setBackgroundDrawable(null);
            imageView.setImageResource(R.drawable.li_room_system_message_laba);
            layoutParam=new PicAndTextView.LayoutParam(Utils.Companion.dip2px(itemView.getContext(),15),Utils.Companion.dip2px(itemView.getContext(),15),Gravity.CENTER_VERTICAL);
            imageView.setLayoutParams(layoutParam);
//            itemView.setOnLongClickListener(this);
//            itemView.setOnClickListener(this);

            heartImageView = new ImageView(itemView.getContext());
            heartImageView.setImageResource(R.drawable.li_message_heart);

            PicAndTextView.LayoutParam heartLP=new PicAndTextView.LayoutParam(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL);
            heartLP.setMargins(10,0,0,0);
            heartImageView.setLayoutParams(heartLP);
        }

        @Override
        public void bindData(MessageBean msg) {
            super.bindData(msg);
//            ptvContent.setTag(msg);
            levelLayout.setLevel(msg.level);
            ptvContent.clean();
            switch (msg.messageType) {
                case MessageBean.MESSAGE_FIRST:
                    rlMessage.setBackgroundResource(R.drawable.li_bg_message_other);
                    if(msg.message.contains("❤")){
                        LogUtils.d(TAG,"bindData(),(点赞)msg.guardLevel:" + msg.guardLevel + ",msg.userBangDanType:" + msg.userBangDanType);
                        ptvContent.addNewChild(levelLayout);
                        if(ivGuardLevel != null && ivGuardLevel.getVisibility() == View.VISIBLE) {
                            ptvContent.addNewChild(ivGuardLevel);
                        }
                        if(ivBang != null && ivBang.getVisibility() == View.VISIBLE) {
                            ptvContent.addNewChild(ivBang);
                        }
                        if (msg.fromName.length() > 9) {
                            ptvContent.addTextChild(" " +msg.fromName.substring(0, 7) + "...", namePaint, true);
                        }else {
                            ptvContent.addTextChild(" " + msg.fromName, namePaint, true);
                        }

//                        ptvContent.addTextChild("  " +msg.message.substring(0,msg.message.lastIndexOf("❤")),messagePaint);
                        //将点赞语言本地化
                        String m = itemView.getContext().getString(R.string.li_room_message_like);
                        ptvContent.addTextChild("  " +m.substring(0,m.lastIndexOf("❤")),messagePaint);
//                        picAndTextView.addTextChild(" ❤",heartPaint);
                        ptvContent.addNewChild(heartImageView);

                    }else{
                        ptvContent.addNewChild(levelLayout);
                        if(ivGuardLevel!=null && ivGuardLevel.getVisibility() == View.VISIBLE) {
                            ptvContent.addNewChild(ivGuardLevel);
                        }
                        if(ivBang != null && ivBang.getVisibility() == View.VISIBLE) {
                            ptvContent.addNewChild(ivBang);
                        }
                        ptvContent.addTextChild(" " + msg.fromName, namePaint, true);
                        if(msg.message.contains(itemView.getContext().getString(R.string.li_room_click_follow_tip))){
                            ptvContent.addTextChild("  " + itemView.getContext().getString(R.string.li_room_click_follow),messagePaint);
                        }else if(msg.message.contains(itemView.getContext().getString(R.string.li_room_share_success_tip))){
                            ptvContent.addTextChild("  " + itemView.getContext().getString(R.string.li_room_share_success),messagePaint);
                        }else if(msg.message.contains("成为了主播的房间管理员")){
                            ptvContent.addTextChild(itemView.getContext().getString(R.string.li_room_become_room_manage));
                        }else{
                            ptvContent.addTextChild("  " + msg.message,messagePaint);
                        }

                    }

                    break;
                case MessageBean.MESSAGE_SYSTEM:
                    rlMessage.setBackgroundResource(0);
                    ptvContent.addTextChild(msg.fromName.trim(),systemPaint);
                    ptvContent.addTextChild(" " + msg.message,messageContent);
                    break;
            }
        }

        /*@Override
        public void onClick(View v) {
            MessageBean msg = (MessageBean) ptvContent.getTag();
            if (msg != null) {
                long fromId = msg.fromId;
                EventBusManager.getInstance().post(new RoomBaseEvent.HandleRoomUserInfoDialog(null, fromId));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            MessageBean msg = (MessageBean) ptvContent.getTag();
            if (msg != null && msg.messageType != MessageBean.MESSAGE_SYSTEM) {
                String long_name = msg.fromName;
                long_name = "@" + long_name + " ";
                LogUtils.d(TAG, "onLongClick(),long_name:" + long_name);
                EventBusManager.getInstance().post(new Etyou(long_name));
            }
            return true;
        }*/
    }


    public static class MessageBean {
        public static final int MESSAGE_NORMAL = 0;
        public static final int MESSAGE_SYSTEM = 1;//2呢？
        public static final int MESSAGE_GIFT = 3;
        public static final int MESSAGE_FIRST = 4;
        public static final int MESSAGE_BANGDAN = 5;
        public static final int MESSAGE_SERVER_SYSTEM = 6;


        public long fromId;
        public int messageType;
        public SpannableString mSpannableString;
        public int level;
        public int userBangDanType = -1;
        public String fromName;
        public String message;
        public String targetName;
        public String giftUrl;
        public String fromUrl;
        /**
         * 礼物的连击数目
         */
        public String giftRepCount;

        public int defaultHeaderResId;
        public int guardLevel;
    }

}
