package com.example.bjit_asr.ui.Home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bjit_asr.ConversationsActivity;
import com.example.bjit_asr.Models.Conversation;
import com.example.bjit_asr.Models.RecognizeText;
import com.example.bjit_asr.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    private List<Conversation> conversations;

    public ConversationAdapter(List<Conversation> _conversations){
            this.conversations = _conversations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View contactView = inflater.inflate(R.layout.conversation_item, parent, false);

            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(contactView);
            return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Conversation conversation = conversations.get(position);

            holder.conversationTitle.setText(conversation.title);
            holder.conversationDetails.setText(conversation.details);
            holder.conversationDate.setText(conversation.saveAt);

            holder.seeConversationTexts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ConversationsActivity.class);
                    intent.putExtra("conversationId",conversation.conversationId);
                    intent.putExtra("conversationTitle",conversation.title);
                    intent.putExtra("isConversationRemote",conversation.isConversationRemote);
                    if (conversation.isConversationRemote)
                        intent.putExtra("remoteConversationId",conversation.remoteConversationId);
                    view.getContext().startActivity(intent);
                }
            });

    }

    @Override
    public int getItemCount() {
            return conversations.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView conversationTitle;
        public TextView conversationDate;
        public TextView conversationDetails;
        public MaterialButton seeConversationTexts;

        public ViewHolder(View itemView) {
            super(itemView);

            conversationTitle = itemView.findViewById(R.id.conversation_title);
            conversationDate = itemView.findViewById(R.id.conversation_date);
            conversationDetails = itemView.findViewById(R.id.conversation_details);
            seeConversationTexts = itemView.findViewById(R.id.see_conversation_texts);

        }
    }
}