package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;


public class PersistantMemoryAccountDAO implements AccountDAO {
    private final DBHandler DB;

    public PersistantMemoryAccountDAO(Context context) {
        DB = new DBHandler(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> accountNumbersList = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = DB.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT accountNo FROM " + "account_details", null);

        if (cursor.moveToFirst()) {
            do {
                accountNumbersList.add( cursor.getString(cursor.getColumnIndexOrThrow("accountNo")));
            } while (cursor.moveToNext());
        }

        return accountNumbersList;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase sqLiteDatabase = DB.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + "account_details", null);

        ArrayList<Account> AccountListList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                AccountListList.add(new Account(cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return AccountListList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Account account;

        SQLiteDatabase sqLiteDatabase = DB.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from account_details where accountNo = ?", new String[]{accountNo});

        if(cursor.getCount() != 0){
            String accountnum = cursor.getString(1);
            String bankName = cursor.getString(2);
            String accountHolderName = cursor.getString(3);
            double balance = cursor.getDouble(4);
            account = new Account(accountnum, bankName, accountHolderName, balance);
        } else {
            account = null;
        }

        if (account != null) {
            return account;
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase sqLiteDatabase = DB.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo", account.getAccountNo());
        contentValues.put("bankName", account.getBankName());
        contentValues.put("accountHolderName", account.getAccountHolderName());
        contentValues.put("balance", account.getBalance());
        sqLiteDatabase.insert("account_details", null, contentValues);

        DB.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase sqLiteDatabase = DB.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from account_details where accountNo = ?", new String[]{accountNo});

        Boolean delete;

        if (cursor.getCount() > 0){
            long deleted = sqLiteDatabase.delete("account_details", "accountNo = ?", new String[]{accountNo});
            if (deleted == -1) delete =false;
            else delete = true;
        }
        else delete = false;

        if (!delete) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

        DB.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        double current_balance;
        double new_balance = 0;

        SQLiteDatabase sqLiteDatabase = DB.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select balance from account_details where accountNo = ?", new String[]{accountNo});

        if(cursor.moveToFirst()){
            current_balance = cursor.getDouble(0);
        } else {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        cursor.close();

        // specific implementation based on the transaction type
        switch (expenseType) {
            case EXPENSE:
                new_balance = current_balance - amount;
                break; 
            case INCOME:
                new_balance = current_balance + amount;
                break;
        }
        
        ContentValues contentValues = new ContentValues();
        contentValues.put("balance", new_balance);
        sqLiteDatabase.update("account_details", contentValues, "accountNo = ?", new String[]{accountNo});
        sqLiteDatabase.close();
    }
}



