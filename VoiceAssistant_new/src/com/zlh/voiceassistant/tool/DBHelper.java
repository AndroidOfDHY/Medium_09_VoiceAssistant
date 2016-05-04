package com.zlh.voiceassistant.tool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

public DBHelper(Context context, String name, CursorFactory factory,
int version) {
super(context, name, factory, version);
System.out.println("------------>");
System.out.println("The DataBase is create");
}

//如果是第一次创建数据库，就调用此方法，否则该方法不会调用
public void onCreate(SQLiteDatabase db) {
System.out.println("---------------->");
System.out.println("The table is create");
db.execSQL("create table user (userid int,username varchar(20))");
}

//如果是数据库版本号，参数version变了就会调用
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
}

}
