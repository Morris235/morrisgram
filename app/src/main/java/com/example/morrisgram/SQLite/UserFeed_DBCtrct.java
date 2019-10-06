package com.example.morrisgram.SQLite;

//계약 클래스 -유저피드-
public class UserFeed_DBCtrct {
    public UserFeed_DBCtrct() {
    }

    //속성
    private static final String TBL_USER_FEED = "USER_FEED_T";
    private static final String COL_UID = "UID";
    private static final String COL_ID = "ID";
    private static final String COL_NICNAME = "NICKNAME";
    private static final String COL_POSTER_COUNT = "POSTER_COUNT";

    // CREATE TABLE IF NOT EXISTS CONTACT_T (NO INTEGER NOT NULL, NAME TEXT, PHONE TEXT, OVER20 INTEGER)
    public static final String SQL_CREATE_TBL = "CREATE TABLE IF NOT EXISTS" + TBL_USER_FEED + " " +
            "(" +
            COL_UID +                      "TEXT" + "," +
            COL_ID +                       "TEXT" + "," +
            COL_NICNAME +                  "TEXT" + "," +
            COL_POSTER_COUNT + "INTEGER NOT NULL" +
            ")";

    //DROP TABLE IF EXISTS CONTACT_T
    public static final String SQL_DROP_TBL = "DROP TABLE IF EXISTS " + TBL_USER_FEED;

    // SELECT * FROM CONTACT_T
    public static final String SQL_SELECT= "SELECT * FROM" + TBL_USER_FEED;

    // INSERT OR REPLACE INTO CONTACT_T (NO, NAME, PHONE, OVER20) VALUES (x, x, x, x)
    public static final String SQL_INSERT = "INSERT OR REPLACE INTO" + TBL_USER_FEED + " "+
            "(" + COL_UID + "," + COL_ID + "." + COL_NICNAME + "," + COL_POSTER_COUNT + ") VALUES";

    // DELETE FROM CONTACT_T
    public static final String SQL_DELETE = "DELETE FROM" + TBL_USER_FEED;
}