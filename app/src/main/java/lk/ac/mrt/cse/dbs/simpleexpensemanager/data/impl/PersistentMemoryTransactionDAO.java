package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by Start on 11/19/2017.
 */

public class PersistentMemoryTransactionDAO extends SQLiteOpenHelper implements TransactionDAO {
    public static final String dbName = "150193M.db";
    public static final String tableAccountNo = "account_no";
    public static final String tableDate = "date";
    public static final String tableType = "type";
    public static final String tableAmount = "amount";

    private List<Transaction> transactions;

    public PersistentMemoryTransactionDAO (Context context){
        super(context, dbName, null, 1);
        transactions = new LinkedList<>();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS transac");
        onCreate(sqLiteDatabase);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        Date dates = transaction.getDate();
        byte[] byteDate = dates.toString().getBytes();
        ExpenseType types = transaction.getExpenseType();
        String strType = types.toString();
        Double amounts = transaction.getAmount();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = dateFormat.format(calendar.getTime());
        Log.d("Date",formattedDate);

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("account_no", accountNo);
        contentValues.put("amount", amounts);
        contentValues.put("type",strType);
        contentValues.put("date", byteDate);


        sqLiteDatabase.insert("transac", null, contentValues);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        transactions.clear();
        Log.d("creation","starting");
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor res =  sqLiteDatabase.rawQuery( " select * from transac", null );

        res.moveToFirst();

        while(res.isAfterLast() == false){

            String accountNo = res.getString(res.getColumnIndex(tableAccountNo));
            Double amount = res.getDouble(res.getColumnIndex(tableAmount));
            String transType = res.getString(res.getColumnIndex(tableType));

            ExpenseType type = ExpenseType.valueOf(transType);
            byte[] date = res.getBlob(res.getColumnIndex(tableDate));


            String str = new String(date, StandardCharsets.UTF_8);
            Log.d("loadedDate",str);

            Date finalDate;
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss 'GMT'z", Locale.ENGLISH);
                finalDate = inputFormat.parse(str);
                transactions.add(new Transaction(finalDate,accountNo,type,amount));
                Log.d("creation","success");
            }catch (java.text.ParseException e){
                Log.d("creation","failed");
                Calendar calendar = Calendar.getInstance();

                finalDate = calendar.getTime();
                transactions.add(new Transaction(finalDate,accountNo,type,amount));
            }
            res.moveToNext();
        }
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        return transactions.subList(size - limit, size);
    }
}
