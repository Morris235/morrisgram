package com.example.morrisgram.SQLite;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DBFILE_="InnerDatabase(SQLite).db";
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //최초 DB를 만들 때 한번만 호출
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserFeed_DBCtrct.SQL_CREATE_TBL);
    }

    //버전이 업데이트 되었을 경우 DB를 다시 만들어주는 메소드
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //업데이트를 했는데 DB가 존재할 경우 onCreate를 다시 불러온다
        db.execSQL(UserFeed_DBCtrct.SQL_DROP_TBL) ;
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
//        onUpgrade(db,oldVersion,newVersion);
    }

    //Db를 여는 메소드
    public DatabaseHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DBFILE_, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }
    //Db를 다 사용한 후 닫는 메소드
    public void close() {
        mDB.close();
    }
}
