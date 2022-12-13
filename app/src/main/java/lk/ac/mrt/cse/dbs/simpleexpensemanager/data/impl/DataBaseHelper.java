package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context) {
        super(context, "200081B.db", null,  1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createAccSql = "CREATE TABLE accounts(accountNo text primary key, bankName text, accountHolderName text, balance real);";
        db.execSQL(createAccSql);

        String createTransactionSql = "create table transactions(transactionId integer primary key autoincrement, date text, accountNo text, type text, amount real, foreign key(accountNo) references account(accountNo));";
        db.execSQL(createTransactionSql);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
