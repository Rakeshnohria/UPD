package com.FingerprintCapture.Utilities;

import com.FingerprintCapture.models.Account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortingUtility {

    public static ArrayList<Account> sortAccountsAlphabetically(ArrayList<Account> pAccountsList )
    {
        Collections.sort(pAccountsList, new Comparator<Account>() {
            @Override
            public int compare(Account accountOne, Account accountTwo) {
                String s1 = accountOne.getEmail();
                String s2 = accountTwo.getEmail();
                return s1.compareToIgnoreCase(s2);
            }
        });
        return pAccountsList;
    }
}
