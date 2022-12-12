package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DBHandler extends SQLiteOpenHelper {
    public DBHandler(Context context) {
        super(context, "200516P.sqlite", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("Create Table account_details(accountNo TEXT primary key, bankName TEXT , accountHolderName TEXT , balance REAL )");
        sqLiteDatabase.execSQL("Create Table transaction_details(id INTEGER primary key autoincrement, date TEXT , accountNo TEXT , expenseType TEXT, amount REAL, foreign key(accountNo) references account_details(accountNo))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("Drop Table if exists account_details");
        sqLiteDatabase.execSQL("Drop Table if exists transaction_details");
        onCreate(sqLiteDatabase);
    }
}
