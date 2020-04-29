package com.FingerprintCapture.models;

public class Account {
    private String email;
    private boolean isLoggedIn;
    private boolean isCurrentAccount;
    private int customerId;

    public Account() {
    }

    public Account(String pUsername, boolean pIsLoggedIn, boolean pIsCurrentAccount, int pCustomerId) {
        email = pUsername;
        isLoggedIn = pIsLoggedIn;
        isCurrentAccount = pIsCurrentAccount;
        customerId = pCustomerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public boolean isCurrentAccount() {
        return isCurrentAccount;
    }

    public void setCurrentAccount(boolean currentAccount) {
        isCurrentAccount = currentAccount;
    }
}
