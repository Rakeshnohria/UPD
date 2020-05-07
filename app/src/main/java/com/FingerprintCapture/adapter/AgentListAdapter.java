package com.FingerprintCapture.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.FingerprintCapture.AgentListActivity;
import com.FingerprintCapture.R;
import com.FingerprintCapture.Utilities.Constants;
import com.FingerprintCapture.models.response.CustomerData;
import com.FingerprintCapture.models.response.UserFingerData;

import java.util.ArrayList;
import java.util.List;

public class AgentListAdapter extends RecyclerView.Adapter<AgentListAdapter.AgentViewHolder> {
    private Context mContext;
    private ArrayList<CustomerData> mUserList;
    public static Boolean mIsSubscribed = false; //mIsSubscribed set from AgentListActivity
    private int visibility = View.GONE;

    public AgentListAdapter(Context pContext, ArrayList<CustomerData> pUserList) {
        mContext = pContext;
        mUserList = pUserList;
    }


    @NonNull
    @Override
    public AgentListAdapter.AgentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int pViewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.agent_item_detail, parent, false);
        return new AgentListAdapter.AgentViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AgentListAdapter.AgentViewHolder holder, final int position) {
        final CustomerData mCustomerData = mUserList.get(position);
        binddata(holder.mParentView, mCustomerData);
    }



    @SuppressLint("SetTextI18n")
    private void binddata(View pview, CustomerData pCustomerData) {
        List<UserFingerData> userFingerData = pCustomerData.getFingerData();
        if (mIsSubscribed) {
            visibility = View.VISIBLE;
        }else{
            visibility = View.GONE;

        }
        TextView nameofDecreased, createdDate, modifiedDate, updId,caseId;
        CheckBox noImageAlertImageButton;
//        ImageView verifiedIcon;

        nameofDecreased=pview.findViewById(R.id.nameDeceased);
//        verifiedIcon =pview.findViewById(R.id.verified_fingerprint_icon);
        noImageAlertImageButton=pview.findViewById(R.id.alert_no_image_image_button);
        createdDate=pview.findViewById(R.id.created_date);
        modifiedDate=pview.findViewById(R.id.modified_date);
        updId=pview.findViewById(R.id.upd_id);
        caseId=pview.findViewById(R.id.case_id);

//        pview.findViewById(R.id.verified_fingerprint_icon).setVisibility(visibility);
        nameofDecreased.setText(pCustomerData.getName());
//        if (pCustomerData.getFingerPrint() == 0) {
//            verifiedIcon.setColorFilter(mContext.getResources().getColor(R.color.progress_grey));
//        } else if (pCustomerData.getVerifiedUser() == Constants.sVerified) {
//            verifiedIcon.setColorFilter(mContext.getResources().getColor(R.color.buttonGreen));
//        } else {
//            verifiedIcon.setColorFilter(mContext.getResources().getColor(R.color.badge_red_color));
//        }
        if (!pCustomerData.getAgentId().isEmpty()) {
            caseId.setVisibility(View.VISIBLE);
            caseId.setText(mContext.getString(R.string.case_id)+Constants.sSPACE+pCustomerData.getAgentId());
        }
        else {
            caseId.setVisibility(View.GONE);
        }
        if (pCustomerData.getUpdId() != null) {
            updId.setText(mContext.getString(R.string.upd_id) + Constants.sSPACE + pCustomerData.getUpdId());
        } else {
            updId.setText("");
        }


        if (userFingerData.size() > 0) {

            String formattedCreatedAt = userFingerData.get(0).getCreatedAt().replace('-', '/');
            String formattedModifiedAt = userFingerData.get(userFingerData.size() - 1).getCreatedAt().replace('-', '/');
            createdDate.setText(mContext.getString(R.string.created_at) + Constants.sSPACE + formattedCreatedAt);
            modifiedDate.setText(mContext.getString(R.string.modified_at) + Constants.sSPACE + formattedModifiedAt);
            noImageAlertImageButton.setVisibility(View.VISIBLE);
        } else {
            noImageAlertImageButton.setVisibility(View.INVISIBLE);
            createdDate.setText(mContext.getString(R.string.created_at));
            modifiedDate.setText(mContext.getString(R.string.modified_at));
        }
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }


    class AgentViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout mParentView;

        AgentViewHolder(View pItemView) {
            super(pItemView);

            mParentView = itemView.findViewById(R.id.parentItemLayout);
            mParentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((AgentListActivity) mContext).getCustomerData(getAdapterPosition());
                }
            });
        }
    }
}