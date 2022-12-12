package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * This is an In-Memory implementation of TransactionDAO interface. This is not a persistent storage. All the
 * transaction logs are stored in a LinkedList in memory.
 */
public class PersistantMemoryTransactionDAO implements TransactionDAO {
    private final DBHandler DB;

    public PersistantMemoryTransactionDAO(Context context) {
        DB = new DBHandler(context);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        SQLiteDatabase sqLiteDatabase = DB.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        DateFormat formatted_date = new SimpleDateFormat("dd-MM-yyyy");

        contentValues.put("date", formatted_date.format(date));
        contentValues.put("accountNo", accountNo);
        contentValues.put("expenseType", String.valueOf(expenseType));
        contentValues.put("amount", amount);

        sqLiteDatabase.insert("transaction_details", null, contentValues);

        DB.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        SQLiteDatabase sqLiteDatabase = DB.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + "transaction_details", null);

        ArrayList<Transaction> transactionList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                transactionList.add(new Transaction(new SimpleDateFormat("dd-MM-yyyy").parse(cursor.getString(1)),
                        cursor.getString(2),
                        ExpenseType.valueOf(cursor.getString(3)),
                        cursor.getDouble(4)));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return transactionList;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {
        List<Transaction> transactions = this.getAllTransactionLogs();
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }

}

