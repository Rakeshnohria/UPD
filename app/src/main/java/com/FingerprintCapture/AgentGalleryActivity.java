package com.FingerprintCapture;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.FingerprintCapture.Utilities.Constants;
import com.FingerprintCapture.adapter.GalleryAdapter;
import com.FingerprintCapture.application.FingerprintCaptureApplication;
import com.FingerprintCapture.models.response.CustomerData;
import com.FingerprintCapture.models.response.UploadImageResponse;
import com.FingerprintCapture.models.response.UserFingerData;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AgentGalleryActivity extends AppCompatActivity implements View.OnClickListener {
    FloatingActionButton mAddAgent;
    ImageButton mBackButton;
    CustomerData mCustomerData;
    TextView mNoFingerPrints, mActionBarNameView;
    List<UserFingerData> fingerData = new ArrayList<>();
    GridView mImageGridView;
    GalleryAdapter mCustomAdapter;
    boolean mFromUpload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_gallery);
        FingerprintCaptureApplication.getApplicationInstance().setActivity(this);
        if (getIntent() != null) {
            mCustomerData = getIntent().getParcelableExtra(Constants.sCUSTOMER_DATA_ARRAY);
        }
        initViews();
        initListeners();
        setAgentNameInActionBar();
        initGridView();
        setGridView();
    }

    private void initGridView() {
        mCustomAdapter = new GalleryAdapter(this, fingerData);
        mImageGridView.setAdapter(mCustomAdapter);
    }

    public void setAgentNameInActionBar() {
        mActionBarNameView.setText(mCustomerData.getName());
    }

    // Create an object of CustomAdapter and set Adapter to GirdView
    private void setGridView() {
        if (mCustomerData != null) {
            if (mCustomerData.getFingerData().isEmpty()) {
                mNoFingerPrints.setVisibility(View.VISIBLE);
            } else {
                fingerData.clear();
                fingerData.addAll(mCustomerData.getFingerData());
                mCustomAdapter.notifyDataSetChanged();
                mNoFingerPrints.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_agent_images:
                super.onBackPressed();
                /*openAddImageActivity();*/
                break;
            case R.id.action_bar_back_button:
                onBackPressed();
                break;
            case R.id.closeView:
                FingerprintCaptureApplication.getApplicationInstance().dismissCustomDialog();
                break;
        }
    }

    private void openAddImageActivity() {
        Intent toAgentListingDetail = new Intent(this, AddImageActivity.class);
        toAgentListingDetail.putExtra(Constants.sCUSTOMER_DATA, mCustomerData);
        startActivityForResult(toAgentListingDetail, Constants.RequestCode.sCUSTOMER_DATA_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RequestCode.sCUSTOMER_DATA_CODE && data != null) {
            UploadImageResponse uploadImageResponse = data.getParcelableExtra(Constants.sCUSTOMER_DATA_ARRAY);
            List<CustomerData> customerDataList = uploadImageResponse.getCustomerData();
            mCustomerData = customerDataList.get(0);
            setGridView();
        }
    }

    public void initViews() {
        mActionBarNameView = findViewById(R.id.agent_gallery_action_bar_title);
        mAddAgent = findViewById(R.id.add_agent_images);
        mBackButton = findViewById(R.id.action_bar_back_button);
        mNoFingerPrints = findViewById(R.id.textview_no_fingerprints);
        mImageGridView = findViewById(R.id.gallery_grid_view);
    }

    public void initListeners() {
        mAddAgent.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
//        mImageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // set an Intent to Another Activity
//                 openGalleryActivity(mCustomerData.getFingerData().get(position).getMainImage());
//            }
//        });
    }

    public void openGalleryActivity(final String pMainImage) {
        Dialog imagePopupDialog = FingerprintCaptureApplication.getApplicationInstance().showCustomDialog(this, R.layout.dialog_layout_popup);
        ImageView imageView = imagePopupDialog.findViewById(R.id.full_screeen_image);
        ImageButton closeView = imagePopupDialog.findViewById(R.id.closeView);
        Glide.with(this).load(pMainImage).placeholder(R.drawable.place_holder).into(imageView);
        closeView.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        setData();
        super.onBackPressed();
    }

    private void setData() {
        Intent intent = new Intent();
        FingerprintCaptureApplication.getApplicationInstance().setmCustomerData(mCustomerData);
        intent.putExtra(Constants.sCUSTOMER_DATA, mCustomerData);
        setResult(Constants.RequestCode.sGALLERY_DATA_CODE, intent);
        finish();
    }
}
