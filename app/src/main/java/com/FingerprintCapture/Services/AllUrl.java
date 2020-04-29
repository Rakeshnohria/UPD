package com.FingerprintCapture.Services;

/**
 * Created by chicmic on 8/14/17.
 */

public class AllUrl {

    //public static final String baseUrl="http://server.chicmic.in/trufru/magento/api/";
//    public static final String baseUrl = " https://updurns.com/api/";
     public static final String baseUrl = " https://updurns.com/api/"; //LIVE
//      public static final String baseUrl = " https://amazingdepartures.com/api/"; //LOCAL

 //   public static final String baseUrl="http://69.167.185.85/api/"; //LOCAL DATE:24-april-2019

    static final String loginUrl = baseUrl + "login_api.php";
    public static final String uploadUrl = baseUrl + "update_customer_detail.php";
    public static final String customerApi = baseUrl + "customer-api.php";
    public static final String deceasedVerify=baseUrl+"deceased_set_to_verified.php";

    public static final String updRegisterUrl = "https://updurns.com/request-account-upd/";
    public static final String updMoreInfoUrl = "https://updurns.com/prints2";
}
