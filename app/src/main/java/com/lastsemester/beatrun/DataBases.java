package com.lastsemester.beatrun;

import android.provider.BaseColumns;

public final class DataBases {

    public static final class CreateDB implements BaseColumns{
        public static final String DATE = "date";
        public static final String DIST = "dist";
        public static final String TIME = "time";
        public static final String AVGSPEED = "avgspeed";
        public static final String AVGBPM = "avgbpm";

        public static final String _TABLENAME0 = "jogtable";
        public static final String _CREATE0 = "create table if not exists " + _TABLENAME0 + " ( "
                +_ID + " integer primary key autoincrement, "
                +DATE + " text not null , "
                +DIST + " text not null , "
                +TIME + " text not null , "
                +AVGSPEED + " text not null , "
                +AVGBPM + " text not null  );";
    }
}
