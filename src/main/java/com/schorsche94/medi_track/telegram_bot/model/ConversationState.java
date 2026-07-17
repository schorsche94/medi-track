package com.schorsche94.medi_track.telegram_bot.model;

public enum ConversationState {
    NONE,
    WAITING_NAME,
    WAITING_ACTIVE_SUBSTANCE,
    WAITING_DOSAGE,
    WAITING_FORM,
    WAITING_INSTRUCTIONS,
    CONFIRMATION,
    FINISHED
}
