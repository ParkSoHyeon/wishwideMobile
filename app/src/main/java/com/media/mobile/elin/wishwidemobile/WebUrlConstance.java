package com.media.mobile.elin.wishwidemobile;

public interface WebUrlConstance {
    //Webserver Domin Name
    String DOMAIN_NAME = "http://210.89.180.127:8080/";    //실서버
//    String DOMAIN_NAME = "http://192.168.0.23:8080/";  //테스트 서버1
//    String DOMAIN_NAME = "http://192.168.1.101:8080/";  //테스트 서버2

    String AUTO_LOGIN_PATH = "mobile/user/loginAuto";
    String JOIN_PATH = "mobile/user/join";
    String VISITED_STORE_LIST_PATH = "mobile/store/listVisitedStore";
    String HOME_PATH = "mobile/store/listNearbyStore";
    String GIFT_STORE_LIST_PATH = "mobile/gift/listGiftStore";
    String RECEIVED_GIFT_LIST_PATH = "mobile/benefit/listReceiveGift";
    String SEND_GIFT_LIST_PATH = "mobile/benefit/listSendGift";
    String COUPON_LIST_PATH = "mobile/benefit/listCoupon";
    String STAMP_AND_POINT_LIST_PATH = "mobile/benefit/listStampAndPoint";
    String CONTACT_LIST_PATH = "mobile/gift/listContact";
    String GIFT_DETAIL_PATH = "mobile/gift/detailGift";
    String GIFT_ORDER_PATH = "mobile/gift/orderGift";
    String STORE_DETAIL_PATH = "mobile/store/detailStore";
    String GAME_SETTING_PATH = "mobile/game/searchGameSetting";
    String GAME_BENEFIT_REGISTER_PATH = "mobile/game/registerGameBenefit";
    String NEARBY_BEACON_LIST_PATH = "mobile/store/listNearbyBeacon";
}
