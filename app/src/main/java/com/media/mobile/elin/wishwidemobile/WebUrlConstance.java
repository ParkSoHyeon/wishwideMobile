package com.media.mobile.elin.wishwidemobile;

public interface WebUrlConstance {
    //Webserver Domin Name
//    public static final String DOMAIN_NAME = "http://220.230.113.159:8080/";    //실서버
//    public static final String DOMAIN_NAME = "http://220.149.254.76/";  //회사 서버
    public static final String DOMAIN_NAME = "http://192.168.0.23:8080/";  //테스트 서버

    public static final String VISITED_STORE_LIST_PATH = "mobile/home/listVisitedStore";
    public static final String NEARBY_STORE_LIST_PATH = "mobile/home/listNearbyStore";
    public static final String GIFT_STORE_LIST_PATH = "mobile/gift/listGiftStore";
    public static final String RECEIVED_GIFT_LIST_PATH = "mobile/gift/listReceiveGift";
    public static final String SEND_GIFT_LIST_PATH = "mobile/gift/listSendGift";
    public static final String COUPON_LIST_PATH = "mobile/benefit/listCoupon";
    public static final String STAMP_AND_POINT_LIST_PATH = "mobile/benefit/listStampAndPoint";
    public static final String CONTACT_LIST_PATH = "mobile/gift/listContact";
    public static final String GIFT_DETAIL_PATH = "mobile/gift/detailGift";
    public static final String SETTING_PATH = "";
}
