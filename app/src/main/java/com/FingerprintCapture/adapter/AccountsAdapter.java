package com.FingerprintCapture.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.FingerprintCapture.R;
import com.FingerprintCapture.SelectAccountActivity;
import com.FingerprintCapture.models.Account;

import java.util.ArrayList;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.AccountViewHolder> {
    private Activity mContext;
    private ArrayList<Account> mAccountsList = new ArrayList<>();
    private int mCurrentAccountPosition = -1;

    public AccountsAdapter(Activity pActivity, ArrayList<Account> pAccountsList) {
        if (pAccountsList != null) {
            mAccountsList = pAccountsList;
        }
        mContext = pActivity;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate
                (R.layout.account_view_holder_item_layout, parent, false);
        return new AccountsAdapter.AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        Account account = mAccountsList.get(position);
        holder.mAccountRadioButton.setChecked(account.isCurrentAccount());
        holder.mAccountRadioButton.setText(account.getEmail());
        if (account.isCurrentAccount()) {
            mCurrentAccountPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return mAccountsList.size();
    }

    class AccountViewHolder extends RecyclerView.ViewHolder {
        RadioButton mAccountRadioButton;

        AccountViewHolder(View pItemView) {
            super(pItemView);
            mAccountRadioButton = itemView.findViewById(R.id.account_radio_button);
            mAccountRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setCurrentAccount(getAdapterPosition());
                }
            });
        }
    }

    private void setCurrentAccount(int pPosition) {
        boolean isAccountSwitched = false;
        if (mCurrentAccountPosition != pPosition) {
        for (int i = 0; i < mAccountsList.size(); i++) {
            if (i == pPosition) {
                mAccountsList.get(i).setCurrentAccount(true);
            } else {
                mAccountsList.get(i).setCurrentAccount(false);
            }
        }
        notifyDataSetChanged();
            isAccountSwitched = true;
        }
        ((SelectAccountActivity) mContext).accountSelected(isAccountSwitched);
    }
}
