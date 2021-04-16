package com.example.bjit_asr.Models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class ConversationWithTexts {
    @Embedded
    public Conversation conversation;
    @Relation(
            parentColumn = "conversationId",
            entityColumn = "recognizeTextId"
    )
    public List<RecognizeText> recognizeTextList;
}
