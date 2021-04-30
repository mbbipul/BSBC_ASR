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
import com.example.bjit_asr.Models.RemoteUser;
import com.example.bjit_asr.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.List;

import static com.example.bjit_asr.utils.Utils.getDeviceUniqueId;

public class RemoteMessageAdapter extends FirebaseRecyclerAdapter<RemoteMessage, RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    FirebaseRecyclerOptions firebaseRecyclerOptions ;

    public RemoteMessageAdapter(Context context, @NonNull FirebaseRecyclerOptions options) {
        super(options);
        mContext = context;
        firebaseRecyclerOptions = options;
    }




    @Override
    public int getItemViewType(int position) {
        RemoteMessage remoteMessage = getItem(position);
        if (remoteMessage.getIsSender())
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
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_gchat_message_me);
            timeText = (TextView) itemView.findViewById(R.id.text_gchat_timestamp_me);
        }

        void bind(RemoteMessage message) {
            messageText.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            timeText.setText("");
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_gchat_message_other);
            timeText = (TextView) itemView.findViewById(R.id.text_gchat_timestamp_other);
            nameText = (TextView) itemView.findViewById(R.id.text_gchat_user_other);
            profileImage = (ImageView) itemView.findViewById(R.id.image_gchat_profile_other);
        }

        void bind(RemoteMessage message) {
            messageText.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            timeText.setText("");

            // Insert the profile image from the URL into the ImageView.
        }
    }
}
