package com.schorsche94.medi_track.telegram_bot;

public enum ConversationState {
    NONE,
    WAITING_NAME,
    WAITING_DESCRIPTION,
    WAITING_DOZE,
    WAITING_DOZE_TYPE,
    WAITING_MEDICATION_FORM,
    CONFIRMATION,
    FINISHED
}
