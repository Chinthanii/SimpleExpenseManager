package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO extends DataBaseHelper implements AccountDAO {
    private List<String> accountNos;
    private List<Account> accounts;

    private Context context;

    private static final String TABLE_NAME = "accounts";
    private static final String COLUMN_ACCOUNT_NO = "accountNo";
    private static final String COLUMN_BANK_NAME = "bankName";
    private static final String COLUMN_ACCOUNT_HOLDER_NAME = "accountHolderName";
    private static final String COLUMN_BALANCE = "balance";




    public PersistentAccountDAO(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public List<String> getAccountNumbersList() {

        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        this.accountNos = new ArrayList<String>();

        Cursor cursor = db.rawQuery(query,null);


        while (cursor.moveToNext()){
            String AC_no = cursor.getString(0);

            accountNos.add(AC_no);
        }

        cursor.close();
        db.close();
        return accountNos;

    }

    @Override
    public List<Account> getAccountsList() {


        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor  = db.rawQuery(query,null);
        this.accounts = new ArrayList<Account>();

        while (cursor.moveToNext()){
            String AC_no = cursor.getString(0);
            String  AC_bankName_ = cursor.getString(1);
            String  AC_holderName = cursor.getString(2);
            double AC_balance =  cursor.getDouble(3);

            Account ac = new Account(AC_no,AC_bankName_,AC_holderName,AC_balance);
            accounts.add(ac);
        }
        cursor.close();
        db.close();
        return accounts;


    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " +TABLE_NAME+ " where " + COLUMN_ACCOUNT_NO + " = '"+ accountNo + "' ;";
        Cursor cursor = db.rawQuery(query,null);

        cursor.moveToFirst();
        String bankName = cursor.getString(1);
        String accountHolderName = cursor.getString(2);
        double balance = cursor.getDouble(3);

        Account ac = new Account(accountNo,bankName,accountHolderName,balance);

        cursor.close();
        db.close();
        return ac;

    }

    @Override
    public void addAccount(Account account) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ACCOUNT_NO,account.getAccountNo());
        cv.put(COLUMN_BANK_NAME,account.getBankName());
        cv.put(COLUMN_ACCOUNT_HOLDER_NAME,account.getAccountHolderName());
        cv.put(COLUMN_BALANCE,account.getBalance());

        long result = db.insert(TABLE_NAME,null,cv);
        if(result == -1) {
            Toast.makeText(context, "Cannot add the account!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {


        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME,COLUMN_ACCOUNT_NO + " LIKE ? ",new String[] {accountNo});

        if(result == -1){
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }


    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = this.getWritableDatabase();


        String Query = "select " + COLUMN_BALANCE + " from " + TABLE_NAME+ " where "
                + COLUMN_ACCOUNT_NO + " = '"+ accountNo +"' ;";
        Cursor cursor = db.rawQuery(Query,null);
        cursor.moveToFirst();
        double AC_balance = cursor.getDouble(0);

        switch(expenseType){
            case EXPENSE:
                AC_balance  -= amount;
                break;
            case INCOME:
                AC_balance  += amount;
                break;
        }
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BALANCE, AC_balance);

        long result = db.update(TABLE_NAME,cv,COLUMN_ACCOUNT_NO +  " LIKE ? ",new String[] {accountNo});

        if(result == -1){
            String msg = "Account " + accountNo + " can't Update.";
            throw new InvalidAccountException(msg);
        }


    }
}
