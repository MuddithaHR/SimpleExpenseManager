package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.InMemoryAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.InMemoryTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistantMemoryAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistantMemoryTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

/**
 *
 */
public class PersistantMemoryDemoExpenseManager extends ExpenseManager {

    public PersistantMemoryDemoExpenseManager(Context context) {
        super(context);
        setup(context);
    }

    @Override
    public void setup(Context context) {
        /*** Begin generating dummy data for In-Memory implementation ***/

        TransactionDAO persistantMemoryTransactionDAO = new PersistantMemoryTransactionDAO(context);
        setTransactionsDAO(persistantMemoryTransactionDAO);

        AccountDAO persistantMemoryAccountDAO = new PersistantMemoryAccountDAO(context);
        setAccountsDAO(persistantMemoryAccountDAO);

        // dummy data
        Account dummyAcct1 = new Account("12345A", "Yoda Bank", "Anakin Skywalker", 10000.0);
        Account dummyAcct2 = new Account("78945Z", "Clone BC", "Obi-Wan Kenobi", 80000.0);
        getAccountsDAO().addAccount(dummyAcct1);
        getAccountsDAO().addAccount(dummyAcct2);

        /*** End ***/
    }
}