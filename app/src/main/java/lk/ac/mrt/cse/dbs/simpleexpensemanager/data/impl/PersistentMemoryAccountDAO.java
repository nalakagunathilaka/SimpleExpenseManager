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

/**
 * Created by Start on 11/19/2017.
 */

public class PersistentMemoryAccountDAO extends SQLiteOpenHelper implements AccountDAO {
    public static final String dbName = "150193M.db";
    public static final String tableAccountNo = "account_no";
    public static final String tableBankName = "bank_name";
    public static final String tableHolderName = "holder_name";
    public static final String tableBalance = "balance";

    public PersistentMemoryAccountDAO(Context context) {
        super (context, dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table account " +
                "(account_no text primary key, bank_name text,holder_name text,balance double)"
        );
        sqLiteDatabase.execSQL( "create table transac " +
                "(account_no text, type text, date BLOB , amount double)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS account");
        onCreate(sqLiteDatabase);
    }

    @Override
    public List<String> getAccountNumbersList() {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor res =  sqLiteDatabase.rawQuery( "select * from account", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(tableAccountNo)));
            res.moveToNext();
        }
        return array_list;
    }

    @Override
    public List<Account> getAccountsList() {
        ArrayList<Account> array_list = new ArrayList<Account>();

        //hp = new HashMap();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor res =  sqLiteDatabase.rawQuery( "select * from account", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            String accountNo = res.getString(res.getColumnIndex(tableAccountNo));
            String bankName = res.getString(res.getColumnIndex(tableBankName));
            String accountHolderName = res.getString(res.getColumnIndex(tableHolderName));
            Double balance = res.getDouble(res.getColumnIndex(tableBalance));

            array_list.add(new Account(accountNo,bankName,accountHolderName,balance));
            res.moveToNext();
        }
        return array_list;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor res =  sqLiteDatabase.rawQuery( "select * from account where id="+accountNo+"", null );

        String accountno = res.getString(res.getColumnIndex(tableAccountNo));
        String bankName = res.getString(res.getColumnIndex(tableBankName));
        String accountHolderName = res.getString(res.getColumnIndex(tableHolderName));
        Double balance = res.getDouble(res.getColumnIndex(tableBalance));

        return  new Account(accountno,bankName,accountHolderName,balance);
    }

    @Override
    public void addAccount(Account account) {
        String accountNo = account.getAccountNo();
        String bankName = account.getBankName();
        String holderName = account.getAccountHolderName();
        Double balance = account.getBalance();


        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("account_no", accountNo);
        contentValues.put("bank_name", bankName);
        contentValues.put("holder_name", holderName);
        contentValues.put("balance", balance);

        sqLiteDatabase.insert("account", null, contentValues);


    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete("account", "account_no = ? ", new String[] { accountNo});
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

    }
}
