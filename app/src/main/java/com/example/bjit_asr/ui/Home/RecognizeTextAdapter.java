package com.example.bjit_asr.ui.Home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bjit_asr.Models.RecognizeText;
import com.example.bjit_asr.R;

import java.util.List;

public class RecognizeTextAdapter extends   RecyclerView.Adapter<RecognizeTextAdapter.ViewHolder> {

    private List<RecognizeText> recognizeTexts;

    public RecognizeTextAdapter(List<RecognizeText> _recognizeTexts){
        this.recognizeTexts = _recognizeTexts;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.reconize_speech_text_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecognizeText recognizeText = recognizeTexts.get(position);

        TextView recognizeTextView = holder.recognizeText;
        TextView recognizeTimeView = holder.recognizeTime;

        recognizeTextView.setText(recognizeText.getText());
        recognizeTimeView.setText(recognizeText.getTime());
    }

    @Override
    public int getItemCount() {
        return recognizeTexts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView recognizeText;
        public TextView recognizeTime;

        public ViewHolder(View itemView) {
            super(itemView);

            recognizeText = itemView.findViewById(R.id.speech_text);
            recognizeTime = itemView.findViewById(R.id.speech_rec_time);
        }
    }
}