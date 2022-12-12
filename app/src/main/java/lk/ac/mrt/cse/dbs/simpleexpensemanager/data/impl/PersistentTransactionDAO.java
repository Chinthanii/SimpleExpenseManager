package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO extends DataBaseHelper implements TransactionDAO {

    private List<Transaction> transactions;

    private Context context;

    private static final String TABLE_NAME = "transactions";
    private static final String COLUMN_TRANSACTION_ID = "transactionId";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_ACCOUNT_NO = "accountNo";
    private static final String COLUMN_EXPENSE_TYPE = "type";
    private static final String COLUMN_AMOUNT = "amount";


    public PersistentTransactionDAO(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        String strDate =  new SimpleDateFormat("dd-MM-yyyy").format(date);

        cv.put(COLUMN_ACCOUNT_NO,accountNo);
        cv.put(COLUMN_AMOUNT,amount);
        cv.put(COLUMN_EXPENSE_TYPE , String.valueOf(expenseType));
        cv.put(COLUMN_DATE,strDate);

        long result = db.insert(TABLE_NAME,null,cv);
        if(result == -1) {
            Toast.makeText(context, "Cannot add the transaction!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor  = db.rawQuery(query,null);
        this.transactions = new ArrayList<Transaction>();

        while (cursor.moveToNext()){
            String TR_date = cursor.getString(1);
            String  TR_accountNo = cursor.getString(2);
            String  TR_expenseType = cursor.getString(3);
            double TR_amount =  cursor.getDouble(4);
            try {
                Date dateObj = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(TR_date);
                ExpenseType expenseType = ExpenseType.valueOf(TR_expenseType);
                Transaction transaction = new Transaction(dateObj, TR_accountNo, expenseType, TR_amount);
                transactions.add(transaction);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        cursor.close();
        db.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        transactions = getAllTransactionLogs();

        int size = transactions.size();
        if (size<limit){
            return transactions;
        }
        else {
            return transactions.subList(size - limit, size);
        }
    }
}
