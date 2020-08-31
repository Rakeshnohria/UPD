package com.FingerprintCapture;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.FingerprintCapture.Utilities.SharedPrefUtility;
import com.FingerprintCapture.adapter.AccountsAdapter;
import com.FingerprintCapture.models.Account;

import java.util.ArrayList;

public class SelectAccountActivity extends AppCompatActivity {
    private RecyclerView mAccountsRecyclerView;
    private AccountsAdapter mAdapter;
    private ArrayList<Account> mAccountsList = new ArrayList<>();
    private ImageView mBackIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_account);
        initViews();
        mAccountsList = SharedPrefUtility.getAccounts();
        setUpRecyclerView();
    }

    private void initViews() {
        mBackIcon = findViewById(R.id.toolbar).findViewById(R.id.back_icon);
        mAccountsRecyclerView = findViewById(R.id.accounts_recycler_view);
        mBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAccountsRecyclerView.setNestedScrollingEnabled(false);
    }

    private void setUpRecyclerView() {
        if (mAccountsList != null) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL, false);
            mAccountsRecyclerView.setLayoutManager(linearLayoutManager);
            mAdapter = new AccountsAdapter(this, mAccountsList);
            mAccountsRecyclerView.setAdapter(mAdapter);
        }
    }

    public void accountSelected(boolean pIsNewAccountSelected) {
        /*
        * Donot refresh the list on Agent list activity if same account is selected again
        * */
        if (pIsNewAccountSelected && (AgentListActivity.mCurrentInstance != null) &&
                (AgentListActivity.mCurrentInstance.get() != null)) {
            AgentListActivity.mCurrentInstance.get().mIsAccountSwitched = true;
            SharedPrefUtility.saveAccounts(mAccountsList);
        }
        finish();
    }
}
