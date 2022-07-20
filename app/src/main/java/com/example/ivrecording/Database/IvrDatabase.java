package com.example.ivrecording.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class IvrDatabase extends SQLiteOpenHelper {
    //--------- Database_Name--------//
    public static final String DB_NAME = "Vivek_Database";

    //-------local table-----//
    public static final String Table_Call_Reports = "call_reports";

    //------------Table_Call_Reports---------//
    public final String Table_Call_Reports_Id = "id";
    public final String Table_Call_Reports_Number = "number";
    public final String Table_Call_Reports_Call_Status = "call_status";
    public final String Table_Call_Reports_Sync_Status = "sync_status";
    public final String Table_Call_Reports_Call_Type = "call_type";
    public final String Table_Call_Reports_Duration = "duration";
    public final String Table_Call_Reports_Recording = "recording";
    public final String Table_Call_Reports_Filepath = "filepath";
    public final String Table_Call_Reports_Start_Time = "start_time";
    public final String Table_Call_Reports_End_Time = "end_time";
    public final String Table_Call_Reports_File_Extension = "file_extension";
    public final String Table_Call_Reports_Created_At = "created_at";

    Context db_context;

    public IvrDatabase(Context context) {
        super(context, DB_NAME, null, 1);
        db_context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String Create_Table_Call_Reports = " CREATE TABLE " + Table_Call_Reports + "(" + Table_Call_Reports_Id + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ," + Table_Call_Reports_Number + " VARCHAR(256)," +
                Table_Call_Reports_Call_Status + " VARCHAR(256)," + Table_Call_Reports_Filepath + " VARCHAR(256)," + Table_Call_Reports_Sync_Status + " VARCHAR(256) DEFAULT 'No'," + Table_Call_Reports_Call_Type + " VARCHAR(256)," + Table_Call_Reports_Recording + " VARCHAR(256)," + Table_Call_Reports_File_Extension + " VARCHAR(256)," + Table_Call_Reports_Duration + " INTEGER(11)," + Table_Call_Reports_Start_Time + " VARCHAR(256)," + Table_Call_Reports_End_Time + " VARCHAR(256)," + Table_Call_Reports_Created_At + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);";
        db.execSQL(Create_Table_Call_Reports);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL(String.format("DROP TABLE IF EXISTS %s", Table_Call_Reports));
            onCreate(db);
        }
    }
}
