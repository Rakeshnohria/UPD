package com.FingerprintCapture;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.FingerprintCapture.Utilities.Constants;
import com.FingerprintCapture.Utilities.PaginationAbstractClass;
import com.FingerprintCapture.Utilities.SharedPrefUtility;
import com.FingerprintCapture.adapter.AgentListAdapter;
import com.FingerprintCapture.application.FingerprintCaptureApplication;
import com.FingerprintCapture.interfaces.AgentListingListener;
import com.FingerprintCapture.models.Account;
import com.FingerprintCapture.models.request.UserRequest;
import com.FingerprintCapture.models.response.CustomerData;
import com.FingerprintCapture.models.response.UploadImageResponse;
import com.FingerprintCapture.models.response.UserResponse;
import com.FingerprintCapture.retorfitApiCalls.AgentListing.AgentListingCall;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.FingerprintCapture.Utilities.Constants.sHANLDER_DELAY_SEARCH;

public class AgentListActivity extends AppCompatActivity implements AgentListingListener, View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener {
    public RecyclerView mRecyclerView;
    public ArrayList<CustomerData> mUserProfiles = new ArrayList<>(); // for recyclerview
    public ArrayList<CustomerData> mUserListData = new ArrayList<>(); //for saving user database local
    public int mTotalCountsForList = 0;
    public int mTotalCountsForSearch = 0;
    public int mStartingIndex = 0;
    Toolbar mToolbar;
    FloatingActionButton mAddAgent;
    ProgressBar mProgressBar;
    LinearLayoutManager mLinearLayoutManager;
    TextView mNoResultTextView;
    ImageView mLogout;
    AgentListAdapter mAdapter;
    private Boolean mSubscribed;
    //pagination
    boolean isLoading = false, isSearchingPaginationLoaded = false;
    private View mRootView;
    private AgentListingCall mAgentListCall;
    private String mSearchString = Constants.sEMPTY_STRING;
    //for handler
    private boolean doubleBackToExitPressedOnce = false;
    private Handler mHandler;
    private Runnable mRunnable;
    private boolean isSearchingAgentList = false, mRecordUpdated = false;
    private boolean isLastPage = false;
    private Menu mNavigationMenu;
    public boolean mIsAccountSwitched = false;
    private SearchView mSearchView;
    private Menu mMenu;
    private NavigationView mNavigationView;
    public static WeakReference<AgentListActivity> mCurrentInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_list);
        FingerprintCaptureApplication.getApplicationInstance().setActivity(this);
        mCurrentInstance = new WeakReference<>(AgentListActivity.this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


//        toggle.setDrawerIndicatorEnabled(false);
//        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.add_icon_white,
//                AgentListActivity.this.getTheme());
//        toggle.setHomeAsUpIndicator(drawable);
//        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (drawer.isDrawerVisible(GravityCompat.START)) {
//                    drawer.closeDrawer(GravityCompat.START);
//                } else {
//                    drawer.openDrawer(GravityCompat.START);
//                }
//            }
//        });


        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationMenu = mNavigationView.getMenu();
        // NavigationMenuView navMenuView = (NavigationMenuView) navigationView.getChildAt(0);
        //navMenuView.addItemDecoration(new DividerItemDecoration(AgentListActivity.this,DividerItemDecoration.VERTICAL));
        initViews();
        initFloatingActionMenu();
        initInstance();
        initRecyclerView();
        initListeners();
        initHandler();
        // setSupportActionBar(mToolbar);
        fetchUserData(mStartingIndex, true, Constants.sEMPTY_STRING, false);
    }

    private void initHandler() {
        mHandler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FingerprintCaptureApplication.getApplicationInstance().setActivity(this);
        String email;
        if (SharedPrefUtility.getCurrentAccount() != null) {
            email = SharedPrefUtility.getCurrentAccount().getEmail();
        } else {
            email = SharedPrefUtility.getString(AgentListActivity.this,
                    Constants.SharedPreferences.sEMAIL_ADDRESS);
        }
        ((TextView) mNavigationView.getHeaderView(0).findViewById(R.id.email_id)).
                setText(email);
        //If more than 1 accounts are there show switch account in the Nav menu otherwise not
        if ((SharedPrefUtility.getAccounts() != null) && (SharedPrefUtility.getAccounts().size() > 1)) {
            mNavigationMenu.findItem(R.id.nav_switch_account).setVisible(true);
            setAccountCount();
        } else {
            mNavigationMenu.findItem(R.id.nav_switch_account).setVisible(false);
        }

        if (mIsAccountSwitched) {

            (mMenu.findItem(R.id.search)).collapseActionView();
            mStartingIndex = 0;
            mTotalCountsForList = 0;
            mTotalCountsForSearch = 0;
            if ((mAdapter != null) && (mUserProfiles != null)) {
                mUserProfiles.clear();
                mUserListData.clear();
                mAdapter.notifyDataSetChanged();
            } else {
                mUserProfiles = new ArrayList<>();
                mUserListData = new ArrayList<>();
                initRecyclerView();
            }
            fetchUserData(mStartingIndex, true, Constants.sEMPTY_STRING, false);
        }
//        if ((SharedPrefUtility.getAccounts() != null) && (SharedPrefUtility.getAccounts().size() > 1)
//                && (checkIfBothAccountsAreLoggedIn(SharedPrefUtility.getAccounts()))) {
//            String emailAddress = Constants.sEMPTY_STRING;
//            for (int i = 0; i < SharedPrefUtility.getAccounts().size(); i++) {
//                if (!SharedPrefUtility.getAccounts().get(i).isCurrentAccount()) {
//                    emailAddress = SharedPrefUtility.getAccounts().get(i).getEmail();
//                    break;
//                }
//            }
//            String switchAccountText = String.format(getString(R.string.switch_account), emailAddress);
//            mNavigationMenu.findItem(R.id.nav_switch_account).setIcon(ContextCompat.getDrawable
//                    (getApplicationContext(), R.drawable.switch_user_menu));
//            mNavigationMenu.findItem(R.id.nav_switch_account).setTitle(switchAccountText);
//        } else {
//            mNavigationMenu.findItem(R.id.nav_switch_account).setIcon(ContextCompat.getDrawable
//                    (getApplicationContext(), R.drawable.add_account_menu));
//            mNavigationMenu.findItem(R.id.nav_switch_account).setTitle(getString(R.string.add_account));
//        }
        mIsAccountSwitched = false;
    }

    private boolean checkIfBothAccountsAreLoggedIn(ArrayList<Account> pAccountsList) {
        int loggedInAccountsSize = 0;
        for (int i = 0; i < pAccountsList.size(); i++) {
            if (pAccountsList.get(i).isLoggedIn()) {
                loggedInAccountsSize++;
            }
        }
        if (loggedInAccountsSize == pAccountsList.size()) {
            return true;
        }
        return false;
    }

    private void initInstance() {
        mAgentListCall = new AgentListingCall(this);
    }

    public void initViews() {
        mRootView = findViewById(R.id.root_view_agent_listing);
        mRecyclerView = findViewById(R.id.agent_list_recycler_view);
        // mToolbar = findViewById(R.id.agent_detail_toolbar);
        mAddAgent = findViewById(R.id.add_agent);
        mProgressBar = findViewById(R.id.circularProgressbar);
        mNoResultTextView = findViewById(R.id.no_results_found_text_view);
        //   mLogout = findViewById(R.id.logout);
    }

    public void initRecyclerView() {
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new AgentListAdapter(this, mUserProfiles);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initListeners() {
        mAddAgent.setOnClickListener(this);
        //   mLogout.setOnClickListener(this);
        mRecyclerView.addOnScrollListener(new PaginationAbstractClass(mLinearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                int totalItemCount = mLinearLayoutManager.getItemCount();
                int totalCount;
                if (mSearchString.isEmpty()) {
                    totalCount = mTotalCountsForList;
                } else {
                    totalCount = mTotalCountsForSearch;
                }
                if (totalCount > totalItemCount && !isLoading()) {
                    if (FingerprintCaptureApplication.getApplicationInstance().isConnected()) {
                        isLoading = true;
                        if (!mSearchString.isEmpty()) {
                            isSearchingPaginationLoaded = true;
                        }
                        mProgressBar.setVisibility(View.VISIBLE);
                        if (mSearchString.isEmpty()) {
                            fetchUserData(totalItemCount, false, mSearchString, false);
                        } else {
                            fetchUserData(totalItemCount, false, mSearchString, true);
                        }

                    }
                }
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_screen, menu);
        MenuItem search = menu.findItem(R.id.search);
        mSearchView = (SearchView) search.getActionView();
        mSearchView.setMaxWidth(getDisplayMetrices().widthPixels);
        search(mSearchView);
        return true;
    }

    private DisplayMetrics getDisplayMetrices() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    private UserRequest setUserRequestModel(int pPageIndex, String pSearchString) {
        UserRequest userRequest = new UserRequest();
        userRequest.setCustId(String.valueOf(SharedPrefUtility.getCustomerId(this)));
        userRequest.setPageIndex(String.valueOf(pPageIndex));
        userRequest.setPageSize(String.valueOf(Constants.sLIMIT));
        if (!pSearchString.isEmpty()) {
            userRequest.setSeachName(pSearchString);
        }
        return userRequest;
    }

    private void fetchUserData(int index, boolean showProgress, String searchString, boolean isSearchApiCall) {
        if (FingerprintCaptureApplication.getApplicationInstance().isConnected()) {
            mAgentListCall.getAgentListing(setUserRequestModel(index, searchString), showProgress, isSearchApiCall);
        }
    }

    @Override
    public void onSuccess(Object object, boolean isSearchApiCall) {
        if (object instanceof UserResponse) {
            mProgressBar.setVisibility(View.GONE);
            isLoading = false;
            UserResponse userResponse = (UserResponse) object;
            mNoResultTextView.setVisibility(View.GONE);
            if (isSearchApiCall && mSearchString.isEmpty()) {
                return;
            }

            if (mSearchString.isEmpty()) {
                mTotalCountsForList = userResponse.getData().getRowCount();
                mUserProfiles.clear();
                mUserListData.addAll(userResponse.getData().getCustomerData());
                mUserProfiles.addAll(mUserListData);

            } else {
                mTotalCountsForSearch = userResponse.getData().getRowCount();
                //for search string
                if (mTotalCountsForSearch == 0) {
                    mNoResultTextView.setVisibility(View.VISIBLE);
                    mNoResultTextView.setText(getString(R.string.no_results_found));
                }
                if (!isSearchingPaginationLoaded) {
                    mUserProfiles.clear();
                }
                mUserProfiles.addAll(userResponse.getData().getCustomerData());
                isSearchingAgentList = false;
            }
            mSubscribed=userResponse.getData().getIsSubscribedUser()==Constants.sSubscribed;
            AgentListAdapter.mIsSubscribed=mSubscribed;
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(String message) {
        FingerprintCaptureApplication.getApplicationInstance().showSnackBar(mRootView, message);
    }

    @Override
    public void showProgress() {
        FingerprintCaptureApplication.getApplicationInstance().showProgress(this, Constants.sEMPTY_STRING);
    }

    @Override
    public void hideProgress() {
        FingerprintCaptureApplication.getApplicationInstance().hideProgress();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.add_agent:
//                openUploadActivity();
//                break;
//            case R.id.logout:
//                logoutFromApp();
//                break;
        }
    }

    private void logoutFromApp() {
        ArrayList<Account> accountsList = SharedPrefUtility.getAccounts();
        if (accountsList == null) {
            accountsList = new ArrayList<>();
        }
        for (int i = 0; i < accountsList.size(); i++) {
            if (accountsList.get(i).isCurrentAccount()) {
                accountsList.remove(i);
            }
        }
        if (!accountsList.isEmpty()) {
            accountsList.get(0).setCurrentAccount(true);
        }
        SharedPrefUtility.saveAccounts(accountsList);
        if (SharedPrefUtility.getAccounts().isEmpty()) {
            SharedPrefUtility.logoutFromApp(this, false);
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        } else {
            //   startActivity(new Intent(getApplicationContext(), AgentListActivity.class));
            mIsAccountSwitched = true;
            (mMenu.findItem(R.id.search)).collapseActionView();
            onResume();
        }
    }

    private void openUploadActivity() {
        startActivityForResult(new Intent(this, UploadData.class), Constants.RequestCode.sCUSTOMER_DATA_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Constants.RequestCode.sCUSTOMER_DATA_NEW && data != null ) {
            UploadImageResponse uploadImageResponse = data.getParcelableExtra(Constants.sCUSTOMER_DATA_ARRAY);
            List<CustomerData> customerDataList = uploadImageResponse.getCustomerData();
           mUserListData.add(0,customerDataList.get(0));
            mAdapter.notifyDataSetChanged();
        } else if (FingerprintCaptureApplication.getApplicationInstance().isUpdated() &&resultCode==Constants.RequestCode.sCUSTOMER_DATA_CODE
                && data!=null && mSearchString.isEmpty()) {
            UploadImageResponse uploadImageResponse = data.getParcelableExtra(Constants.sCUSTOMER_DATA_ARRAY);
            List<CustomerData> customerDataList = uploadImageResponse.getCustomerData();
            mUserListData.set(mPosition, customerDataList.get(0));
            mAdapter.notifyItemChanged(mPosition);
        } else if(resultCode == Constants.RequestCode.sGALLERY_DATA_CODE&& data.getParcelableExtra(Constants.sCUSTOMER_DATA)!=null && mSearchString.isEmpty()) {
            mUserListData.set(mPosition,data.getParcelableExtra(Constants.sCUSTOMER_DATA));
            mAdapter.notifyItemChanged(mPosition);
        }
//            if (resultCode == Constants.RequestCode.sCUSTOMER_DATA_CODE && data != null) {
//                if (mTotalCountsForList == mLinearLayoutManager.getItemCount()) {
//                    UploadImageResponse uploadImageResponse = data.getParcelableExtra(Constants.sCUSTOMER_DATA_ARRAY);
//                    List<CustomerData> customerDataList = uploadImageResponse.getCustomerData();
//                    checkListForId(uploadImageResponse.getCustomerData());
//                    if (!mRecordUpdated) {
//                        mUserListData.addAll(customerDataList);
//                        mTotalCountsForList += customerDataList.size();
//                    }
//                }
//            } else if (resultCode == Constants.RequestCode.sGALLERY_DATA_CODE && data != null && !mSearchString.isEmpty()) {
//                setCustomerData(null);
//                mAdapter.notifyDataSetChanged();
//            } else if (resultCode == Constants.RequestCode.sGALLERY_DATA_CODE && data != null) {
//                setCustomerData(null);
//            }

        if (mSearchString.isEmpty()) {
            mUserProfiles.clear();
            mUserProfiles.addAll(mUserListData);
            mAdapter.notifyDataSetChanged();
        }
        else if(resultCode==Constants.RequestCode.sCUSTOMER_DATA_CODE &&data!=null){
           UploadImageResponse uploadImageResponse = data.getParcelableExtra(Constants.sCUSTOMER_DATA_ARRAY);
            setSearchData(uploadImageResponse.getCustomerData().get(0));
        }
        else if(resultCode==Constants.RequestCode.sGALLERY_DATA_CODE && FingerprintCaptureApplication.getApplicationInstance().isUpdated() && data!=null){
            setSearchData(data.getParcelableExtra(Constants.sCUSTOMER_DATA));
        }

    }
    private void setSearchData(CustomerData pCustomerDataList)
    {
        mUserProfiles.set(mPosition, pCustomerDataList);
        mAdapter.notifyDataSetChanged();
        setCustomerData(pCustomerDataList);
    }

    private void setCustomerData(CustomerData pCustomerData) {
        if (pCustomerData == null) {
            pCustomerData = FingerprintCaptureApplication.getApplicationInstance().getmCustomerData();
        }
        if (pCustomerData != null) {
            for (int i = 0; i < mUserProfiles.size(); i++) {
                if (mUserProfiles.get(i).getId().equals(pCustomerData.getId())) {
                    mUserProfiles.set(i, pCustomerData);
                    break;
                }
            }
            for (int i = 0; i < mUserListData.size(); i++) {
                if (mUserListData.get(i).getId().equals(pCustomerData.getId())) {
                    mUserListData.set(i, pCustomerData);
                    break;
                }
            }
        }
    }

    private void checkListForId(List<CustomerData> pCustomerDataUpdatedList) {
        mRecordUpdated = false;
        for (int i = 0; i < mUserListData.size(); i++) {
            if (mUserListData.get(i).getId().equals(pCustomerDataUpdatedList.get(0).getId())) {
                mRecordUpdated = true;
                mUserListData.get(i).setAgentId(pCustomerDataUpdatedList.get(0).getAgentId());
                mUserListData.get(i).setName(pCustomerDataUpdatedList.get(0).getName());
                mUserListData.get(i).setFirstName(pCustomerDataUpdatedList.get(0).getFirstName());
                mUserListData.get(i).setEmail(pCustomerDataUpdatedList.get(0).getEmail());
                mUserListData.get(i).setFingerData(pCustomerDataUpdatedList.get(0).getFingerData());
                mUserListData.get(i).setDob(pCustomerDataUpdatedList.get(0).getDob());
            }
        }
    }
    private int mPosition;
    public void getCustomerData(int position) {
            mPosition=position;
        openUploadActivity(mUserProfiles.get(position));
    }

    private void openGalleryActivity(CustomerData customerData) {
        Intent intent = new Intent(this, AgentGalleryActivity.class);
        intent.putExtra(Constants.sCUSTOMER_DATA, customerData);
        startActivityForResult(intent, Constants.RequestCode.sGALLERY_DATA_CODE);
    }

    private void openUploadActivity(CustomerData customerData) {
        Intent intent = new Intent(this, UploadData.class);
        intent.putExtra(Constants.sCUSTOMER_DATA, customerData);
        intent.putExtra(Constants.sIsSubscribed,mSubscribed);
        startActivityForResult(intent, Constants.RequestCode.sCUSTOMER_DATA_CODE);
    }


    private void search(final SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                if (FingerprintCaptureApplication.getApplicationInstance().isConnected()) {
                    if (!query.isEmpty() && !isSearchingAgentList) {
                        mSearchString = query;
                        initSearchingList();
                        fetchUserData(mStartingIndex, false, query, true);
                    }
                }
                FingerprintCaptureApplication.getApplicationInstance().hideKeyboard(mRootView);
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                mHandler.removeCallbacksAndMessages(null);
                if (!newText.isEmpty() && FingerprintCaptureApplication.getApplicationInstance().isConnected()) {
                    initSearchingList();
                    isSearchingAgentList = true;
                    mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            mSearchString = newText;
                            fetchUserData(mStartingIndex, false, newText, true);
                        }
                    };
                    mHandler.postDelayed(mRunnable, sHANLDER_DELAY_SEARCH);
                } else {
                    mSearchString = Constants.sEMPTY_STRING;
                    mUserProfiles.clear();
                    mUserProfiles.addAll(mUserListData);
                    mAdapter.notifyDataSetChanged();
                    mNoResultTextView.setVisibility(View.GONE);
                }
                return true;
            }
        });
    }

    private void initSearchingList() {
        mTotalCountsForSearch = 0;
        mUserProfiles.clear();
        mAdapter.notifyDataSetChanged();
        mNoResultTextView.setText(getString(R.string.searching));
        mNoResultTextView.setVisibility(View.VISIBLE);
        isSearchingPaginationLoaded = false;
    }

//    @Override
//    public void onBackPressed() {
//        if (doubleBackToExitPressedOnce) {
//            super.onBackPressed();
//            return;
//        }
//        this.doubleBackToExitPressedOnce = true;
//        FingerprintCaptureApplication.getApplicationInstance().showSnackBar(mRootView, getString(R.string.backpressed_message));
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce = false;
//            }
//        }, Constants.sHANDLER_DELAY);
//    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            FingerprintCaptureApplication.getApplicationInstance().showSnackBar(mRootView, getString(R.string.backpressed_message));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, Constants.sHANDLER_DELAY);
        }
    }

    private void initFloatingActionMenu() {
//        ImageView fabContent = new ImageView(AgentListActivity.this);
//        fabContent.setImageDrawable(getResources().getDrawable(R.drawable.add_icon_white));
//        FloatingActionButton darkButton = new FloatingActionButton.Builder(AgentListActivity.this)
//                //.setTheme(FloatingActionButton.THEME_DARK)
//                .setBackgroundDrawable(R.drawable.fab_selector)
//                .setContentView(fabContent)
//                .setPosition(FloatingActionButton.POSITION_BOTTOM_RIGHT)
//                .build();
//        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(AgentListActivity.this)
//        .setTheme(SubActionButton.THEME_DARKER);
//
//        ImageView subActionAddNewDeceased = new ImageView(AgentListActivity.this);
//        ImageView subActionSupport = new ImageView(AgentListActivity.this);
//        ImageView subActionHelp = new ImageView(AgentListActivity.this);
//
////        subActionAddNewDeceased.setImageDrawable(getResources().getDrawable(R.drawable.test_icon));
////        subActionSupport.setImageDrawable(getResources().getDrawable(R.drawable.add_icon_white));
////        subActionHelp.setImageDrawable(getResources().getDrawable(R.drawable.add_icon_white));
//
//        // Set 4 SubActionButtons
//        FloatingActionMenu centerBottomMenu = new FloatingActionMenu.Builder(AgentListActivity.this)
//                .addSubActionView(rLSubBuilder.setContentView(subActionAddNewDeceased).
//                        setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),
//                                R.drawable.fab_selector)).build())
//                .addSubActionView(rLSubBuilder.setContentView(subActionSupport).build())
//                .addSubActionView(rLSubBuilder.setContentView(subActionHelp).build())
//                .attachTo(darkButton)
//                .build();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_new_listing: {
                openUploadActivity();

                break;
            }
            case R.id.nav_faq: {
                startActivity(new Intent(getApplicationContext(), FAQActivity.class));
                break;
            }
            case R.id.nav_support: {
                startActivity(new Intent(getApplicationContext(), HelpActivity.class));
                break;
            }
            case R.id.nav_add_account: {
                addAccount();
                break;
            }
            case R.id.nav_switch_account: {
//                if (item.getTitle().equals(getString(R.string.add_account))) {
//                    addAccount();
//                } else {
                switchAccount();
                //
                break;
            }
            case R.id.nav_log_out: {
                logoutFromApp();
                break;
            }

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void switchAccount() {
        startActivity(new Intent(this, SelectAccountActivity.class));
  /*      ArrayList<Account> accountsList = SharedPrefUtility.getAccounts();
        if (accountsList.isEmpty()) {
            accountsList = new ArrayList<>();
        }
        for (int i = 0; i < accountsList.size(); i++) {
            if (accountsList.get(i).isCurrentAccount()) {
                accountsList.get(i).setCurrentAccount(false);
            } else {
                accountsList.get(i).setCurrentAccount(true);
            }
        }
        SharedPrefUtility.saveAccounts(accountsList);
        mIsAccountSwitched = true;
        (mMenu.findItem(R.id.search)).collapseActionView();
        mStartingIndex = 0;
        mTotalCountsForList = 0;
        mTotalCountsForSearch = 0;
        onResume();*/
        // startActivity(new Intent(getApplicationContext(), AgentListActivity.class));
        //finish();
    }

    private void addAccount() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class).
                putExtra(Constants.sIS_ADD_ACCOUNT, true));
    }

    private void setAccountCount()
    {
        TextView view = (TextView) mNavigationMenu.findItem(R.id.nav_switch_account).
                getActionView().findViewById(R.id.account_count);
        String text=SharedPrefUtility.getAccounts().size()+Constants.sEMPTY_STRING;
        if(SharedPrefUtility.getAccounts().size()>99)
        {
            text=getString(R.string.badge_count_big);
        }
        view.setText(text);
    }

}
