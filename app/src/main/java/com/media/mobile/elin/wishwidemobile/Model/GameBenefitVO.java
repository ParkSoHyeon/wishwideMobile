package com.media.mobile.elin.wishwidemobile.Model;

import java.sql.Timestamp;

public class GameBenefitVO {
    private int couponNo;
    private int couponDeadLine;
    private String couponMemberSendTypeCode;
    private int couponPublishedCode;
    private String couponTitle;
    private int couponTypeNo;
    private int couponVisibleCode;
    private String franchiseId;
    private String partnerId;
    private String wideManagerId;
    private String couponDiscountTypeCode;
    private int couponDiscountValue;
    private String productTitle;
    private String couponimageUrl;
    private int couponArCode;
    private Timestamp couponRegDate;
    private Timestamp couponUpdateDate;


    public int getCouponNo() {
        return couponNo;
    }

    public void setCouponNo(int couponNo) {
        this.couponNo = couponNo;
    }

    public int getCouponDeadLine() {
        return couponDeadLine;
    }

    public void setCouponDeadLine(int couponDeadLine) {
        this.couponDeadLine = couponDeadLine;
    }

    public String getCouponMemberSendTypeCode() {
        return couponMemberSendTypeCode;
    }

    public void setCouponMemberSendTypeCode(String couponMemberSendTypeCode) {
        this.couponMemberSendTypeCode = couponMemberSendTypeCode;
    }

    public int getCouponPublishedCode() {
        return couponPublishedCode;
    }

    public void setCouponPublishedCode(int couponPublishedCode) {
        this.couponPublishedCode = couponPublishedCode;
    }

    public String getCouponTitle() {
        return couponTitle;
    }

    public void setCouponTitle(String couponTitle) {
        this.couponTitle = couponTitle;
    }

    public int getCouponTypeNo() {
        return couponTypeNo;
    }

    public void setCouponTypeNo(int couponTypeNo) {
        this.couponTypeNo = couponTypeNo;
    }

    public int getCouponVisibleCode() {
        return couponVisibleCode;
    }

    public void setCouponVisibleCode(int couponVisibleCode) {
        this.couponVisibleCode = couponVisibleCode;
    }

    public String getFranchiseId() {
        return franchiseId;
    }

    public void setFranchiseId(String franchiseId) {
        this.franchiseId = franchiseId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getWideManagerId() {
        return wideManagerId;
    }

    public void setWideManagerId(String wideManagerId) {
        this.wideManagerId = wideManagerId;
    }

    public String getCouponDiscountTypeCode() {
        return couponDiscountTypeCode;
    }

    public void setCouponDiscountTypeCode(String couponDiscountTypeCode) {
        this.couponDiscountTypeCode = couponDiscountTypeCode;
    }

    public int getCouponDiscountValue() {
        return couponDiscountValue;
    }

    public void setCouponDiscountValue(int couponDiscountValue) {
        this.couponDiscountValue = couponDiscountValue;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getCouponimageUrl() {
        return couponimageUrl;
    }

    public void setCouponimageUrl(String couponimageUrl) {
        this.couponimageUrl = couponimageUrl;
    }

    public int getCouponArCode() {
        return couponArCode;
    }

    public void setCouponArCode(int couponArCode) {
        this.couponArCode = couponArCode;
    }

    public Timestamp getCouponRegDate() {
        return couponRegDate;
    }

    public void setCouponRegDate(Timestamp couponRegDate) {
        this.couponRegDate = couponRegDate;
    }

    public Timestamp getCouponUpdateDate() {
        return couponUpdateDate;
    }

    public void setCouponUpdateDate(Timestamp couponUpdateDate) {
        this.couponUpdateDate = couponUpdateDate;
    }


    @Override
    public String toString() {
        return "GameBenefitVO{" +
                "couponNo=" + couponNo +
                ", couponDeadLine=" + couponDeadLine +
                ", couponMemberSendTypeCode='" + couponMemberSendTypeCode + '\'' +
                ", couponPublishedCode=" + couponPublishedCode +
                ", couponTitle='" + couponTitle + '\'' +
                ", couponTypeNo=" + couponTypeNo +
                ", couponVisibleCode=" + couponVisibleCode +
                ", franchiseId='" + franchiseId + '\'' +
                ", partnerId='" + partnerId + '\'' +
                ", wideManagerId='" + wideManagerId + '\'' +
                ", couponDiscountTypeCode='" + couponDiscountTypeCode + '\'' +
                ", couponDiscountValue=" + couponDiscountValue +
                ", productTitle='" + productTitle + '\'' +
                ", couponimageUrl='" + couponimageUrl + '\'' +
                ", couponArCode=" + couponArCode +
                ", couponRegDate=" + couponRegDate +
                ", couponUpdateDate=" + couponUpdateDate +
                '}';
    }
}
