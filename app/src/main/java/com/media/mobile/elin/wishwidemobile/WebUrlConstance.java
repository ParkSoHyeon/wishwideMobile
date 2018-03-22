package com.media.mobile.elin.wishwidemobile;

public interface WebUrlConstance {
    //Webserver Domin Name
//    public static final String DOMAIN_NAME = "http://210.89.180.127:8080/";    //실서버
//    public static final String DOMAIN_NAME = "http://192.168.0.23:8080/";  //테스트 서버1
    public static final String DOMAIN_NAME = "http://192.168.1.100:8080/";  //테스트 서버2r

    public static final String AUTO_LOGIN_PATH = "mobile/user/loginAuto";
    public static final String VISITED_STORE_LIST_PATH = "mobile/store/listVisitedStore";
    public static final String HOME_PATH = "mobile/store/listNearbyStore";
    public static final String GIFT_STORE_LIST_PATH = "mobile/gift/listGiftStore";
    public static final String RECEIVED_GIFT_LIST_PATH = "mobile/benefit/listReceiveGift";
    public static final String SEND_GIFT_LIST_PATH = "mobile/benefit/listSendGift";
    public static final String COUPON_LIST_PATH = "mobile/benefit/listCoupon";
    public static final String STAMP_AND_POINT_LIST_PATH = "mobile/benefit/listStampAndPoint";
    public static final String CONTACT_LIST_PATH = "mobile/gift/listContact";
    public static final String GIFT_DETAIL_PATH = "mobile/gift/detailGift";
    public static final String STORE_DETAIL_PATH = "mobile/store/detailStore";
    public static final String GAME_SETTING_PATH = "mobile/game/searchGameSetting";
    public static final String GAME_BENEFIT_REGISTER_PATH = "mobile/game/registerGameBenefit";
}
