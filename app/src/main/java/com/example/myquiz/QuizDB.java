package com.example.myquiz;

import android.provider.BaseColumns;

public final class QuizDB {

    private QuizDB(){}

    public static class PlayerTable implements BaseColumns {
        public static final String TABLE_NAME = "players";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SCORE = "score";
    }

}
