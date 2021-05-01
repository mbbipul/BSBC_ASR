package com.example.bjit_asr.ui.RemoteConversation;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bjit_asr.Models.RemoteMessage;
import com.example.bjit_asr.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import static com.example.bjit_asr.utils.Utils.getUserId;

public class RemoteMessageAdapter extends FirebaseRecyclerAdapter<RemoteMessage, RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    FirebaseRecyclerOptions firebaseRecyclerOptions ;

    public RemoteMessageAdapter(Context context, @NonNull FirebaseRecyclerOptions options) {
        super(options);
        this.mContext = context;
        this.firebaseRecyclerOptions = options;
    }


    @Override
    public int getItemViewType(int position) {
        RemoteMessage remoteMessage = getItem(position);
        if (remoteMessage.getSender().getUserId().equals(getUserId(mContext)))
            return VIEW_TYPE_MESSAGE_SENT;
        else
            return VIEW_TYPE_MESSAGE_RECEIVED;

    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.remote_conversation_me_perspect_item, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.remote_conversation_other_pers_list_item, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position,RemoteMessage remoteMessage) {

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(remoteMessage);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(remoteMessage);
        }
    }



    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText,dateText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_gchat_message_me);
            timeText = (TextView) itemView.findViewById(R.id.text_gchat_timestamp_me);
            dateText = (TextView) itemView.findViewById(R.id.text_gchat_date_me);
        }

        void bind(RemoteMessage message) {
            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
            try {
                Date date = formatter.parse(message.getCreatedAt());
                String formatDate = new SimpleDateFormat("MM-dd-yyyy").format(date);
                String formatTime = new SimpleDateFormat("HH:mm").format(date);

                dateText.setText(formatDate);
                timeText.setText(formatTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            messageText.setText(message.getMessage());

        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText,dateText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_gchat_message_other);
            timeText = (TextView) itemView.findViewById(R.id.text_gchat_timestamp_other);
            dateText = (TextView) itemView.findViewById(R.id.text_gchat_date_other);
            nameText = (TextView) itemView.findViewById(R.id.text_gchat_user_other);
            profileImage = (ImageView) itemView.findViewById(R.id.image_gchat_profile_other);
        }

        void bind(RemoteMessage message) {
            messageText.setText(message.getMessage());
            nameText.setText(message.getSender().getUserName());
            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
            try {
                Date date = formatter.parse(message.getCreatedAt());
                String formatDate = new SimpleDateFormat("MM-dd-yyyy").format(date);
                String formatTime = new SimpleDateFormat("HH:mm").format(date);

                dateText.setText(formatDate);
                timeText.setText(formatTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
