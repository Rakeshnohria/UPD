package com.FingerprintCapture.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.FingerprintCapture.application.FingerprintCaptureApplication;
import com.FingerprintCapture.models.Account;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPrefUtility {
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;

    private static void initSharedPreferences(Context pContext) {
        mSharedPreferences = pContext.getSharedPreferences
                (Constants.SharedPreferences.sKEY_FOR_SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    public static void setCustomerId(Context pContext) {
        if (mSharedPreferences == null) {
            initSharedPreferences(pContext);
        }
    }

    public static int getCustomerId(Context pContext) {
        if (mSharedPreferences == null) {
            initSharedPreferences(pContext);
        }
        if (getCurrentAccount() != null) {
            return getCurrentAccount().getCustomerId();
        }
        return mSharedPreferences.getInt(Constants.SharedPreferences.sCUSTOMER_ID, -1);
    }

    public static void logoutFromApp(Context pContext, boolean val) {
        if (mSharedPreferences == null) {
            initSharedPreferences(pContext);
        }
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(Constants.SharedPreferences.sLOGIN_STATUS, val);
        mEditor.apply();
    }

    public static String getString(Context pContext, String pKey) {
        if (mSharedPreferences == null) {
            initSharedPreferences(pContext);
        }
        return mSharedPreferences.getString(pKey, Constants.sEMPTY_STRING);
    }

    public static void saveAccounts(ArrayList<Account> accountsList) {
        if (mSharedPreferences == null) {
            initSharedPreferences(FingerprintCaptureApplication.getApplicationInstance());
        }
        accountsList=SortingUtility.sortAccountsAlphabetically(accountsList);
        Gson gson = new Gson();
        String json = gson.toJson(accountsList);
        mEditor = mSharedPreferences.edit();
        mEditor.putString(Constants.SharedPreferences.sACCOUNTS_LIST, json);
        mEditor.apply();
    }

    public static ArrayList<Account> getAccounts() {
        if (mSharedPreferences == null) {
            initSharedPreferences(FingerprintCaptureApplication.getApplicationInstance());
        }
        Type type = new TypeToken<List<Account>>() {
        }.getType();
        Gson gson = new Gson();
        String json = mSharedPreferences.getString(Constants.SharedPreferences.sACCOUNTS_LIST, "");
        ArrayList<Account> accountsList=gson.fromJson(json, type);
        if(accountsList!=null) {
            accountsList = SortingUtility.sortAccountsAlphabetically(accountsList);
        }
        return accountsList;
    }

    public static Account getCurrentAccount() {
        if (mSharedPreferences == null) {
            initSharedPreferences(FingerprintCaptureApplication.getApplicationInstance());
        }
        Type type = new TypeToken<List<Account>>() {
        }.getType();
        Gson gson = new Gson();
        String json = mSharedPreferences.getString(Constants.SharedPreferences.sACCOUNTS_LIST, "");
        ArrayList<Account> accountsList = gson.fromJson(json, type);
        if (accountsList != null) {
            for (int i = 0; i < accountsList.size(); i++) {
                if (accountsList.get(i).isCurrentAccount()) {
                    return accountsList.get(i);
                }
            }
        }
        return null;
    }

    public static boolean checkIfAccountAlreadyExistsInPreferences(String pEmail) {
        if (mSharedPreferences == null) {
            initSharedPreferences(FingerprintCaptureApplication.getApplicationInstance());
        }
        Type type = new TypeToken<List<Account>>() {
        }.getType();
        Gson gson = new Gson();
        String json = mSharedPreferences.getString(Constants.SharedPreferences.sACCOUNTS_LIST, "");
        ArrayList<Account> accountsList = gson.fromJson(json, type);
        if (accountsList != null) {
            for (int i = 0; i < accountsList.size(); i++) {
                if (accountsList.get(i).getEmail().equals(pEmail)) {
                    return true;
                }
            }
        }
        return false;
    }
}
