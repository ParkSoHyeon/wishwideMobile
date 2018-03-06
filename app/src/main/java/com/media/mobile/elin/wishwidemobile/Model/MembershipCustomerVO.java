package com.media.mobile.elin.wishwidemobile.Model;

import java.sql.Timestamp;
import java.util.List;

public class MembershipCustomerVO {
    //멤버쉽고객
    private int membershipCustomerNo; //멤버쉽고객번호
    private int wideCustomerNo;       //통합고객번호
    private String membershipCustomerPhone;   //멤버쉽고객번호
    private String partnerId;  //파트너아이디
    private String franchiseId;    //가맹점아이디
    private String membershipCustomerBenefitType;    //멤버쉽혜택타입
    private int membershipCustomerBenefitValue;    //멤버쉽혜택값
    private int membershipCustomerReceiveGiftCnt;    //멤버쉽혜택값
    private String membershipCustomerGrade; //멤버쉽고객등급
    private int membershipCustomerVisibleCode;   //멤버쉽고객활성여부
    private Timestamp membershipCustomerRegDate; //멤버쉽고객등록일시
    private Timestamp membershipCustomerUpdateDate;  //멤버쉽고객수정일시

    private String wideManagerId;   //멤버쉽매장아이디
    private String partnerWideManagerName; //파트너멤버쉽매장명
    private String franchiseWideManagerName; //가맹점멤버쉽매장명


    public String getWideManagerId() {
        return wideManagerId;
    }

    public void setWideManagerId(String wideManagerId) {
        this.wideManagerId = wideManagerId;
    }

    public int getMembershipCustomerReceiveGiftCnt() {
        return membershipCustomerReceiveGiftCnt;
    }

    public void setMembershipCustomerReceiveGiftCnt(int membershipCustomerReceiveGiftCnt) {
        this.membershipCustomerReceiveGiftCnt = membershipCustomerReceiveGiftCnt;
    }

    public String getMembershipCustomerBenefitType() {
        return membershipCustomerBenefitType;
    }

    public void setMembershipCustomerBenefitType(String membershipCustomerBenefitType) {
        this.membershipCustomerBenefitType = membershipCustomerBenefitType;
    }

    public int getMembershipCustomerBenefitValue() {
        return membershipCustomerBenefitValue;
    }

    public void setMembershipCustomerBenefitValue(int membershipCustomerBenefitValue) {
        this.membershipCustomerBenefitValue = membershipCustomerBenefitValue;
    }

    public String getPartnerWideManagerName() {
        return partnerWideManagerName;
    }

    public void setPartnerWideManagerName(String partnerWideManagerName) {
        this.partnerWideManagerName = partnerWideManagerName;
    }

    public String getFranchiseWideManagerName() {
        return franchiseWideManagerName;
    }

    public void setFranchiseWideManagerName(String franchiseWideManagerName) {
        this.franchiseWideManagerName = franchiseWideManagerName;
    }

    public int getMembershipCustomerNo() {
        return membershipCustomerNo;
    }

    public void setMembershipCustomerNo(int membershipCustomerNo) {
        this.membershipCustomerNo = membershipCustomerNo;
    }

    public int getWideCustomerNo() {
        return wideCustomerNo;
    }

    public void setWideCustomerNo(int wideCustomerNo) {
        this.wideCustomerNo = wideCustomerNo;
    }

    public String getMembershipCustomerPhone() {
        return membershipCustomerPhone;
    }

    public void setMembershipCustomerPhone(String membershipCustomerPhone) {
        this.membershipCustomerPhone = membershipCustomerPhone;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getFranchiseId() {
        return franchiseId;
    }

    public void setFranchiseId(String franchiseId) {
        this.franchiseId = franchiseId;
    }

    public int getMembershipCustomerVisibleCode() {
        return membershipCustomerVisibleCode;
    }

    public void setMembershipCustomerVisibleCode(int membershipCustomerVisibleCode) {
        this.membershipCustomerVisibleCode = membershipCustomerVisibleCode;
    }

    public String getMembershipCustomerGrade() {
        return membershipCustomerGrade;
    }

    public void setMembershipCustomerGrade(String membershipCustomerGrade) {
        this.membershipCustomerGrade = membershipCustomerGrade;
    }

    public Timestamp getMembershipCustomerRegDate() {
        return membershipCustomerRegDate;
    }

    public void setMembershipCustomerRegDate(Timestamp membershipCustomerRegDate) {
        this.membershipCustomerRegDate = membershipCustomerRegDate;
    }

    public Timestamp getMembershipCustomerUpdateDate() {
        return membershipCustomerUpdateDate;
    }

    public void setMembershipCustomerUpdateDate(Timestamp membershipCustomerUpdateDate) {
        this.membershipCustomerUpdateDate = membershipCustomerUpdateDate;
    }

    @Override
    public String toString() {
        return "MembershipCustomerVO{" +
                "membershipCustomerNo='" + membershipCustomerNo + '\'' +
                ", wideCustomerNo='" + wideCustomerNo + '\'' +
                ", membershipCustomerPhone='" + membershipCustomerPhone + '\'' +
                ", partnerId='" + partnerId + '\'' +
                ", franchiseId='" + franchiseId + '\'' +
                ", membershipCustomerGrade='" + membershipCustomerGrade + '\'' +
                ", membershipCustomerVisibleCode='" + membershipCustomerVisibleCode + '\'' +
                ", membershipCustomerRegDate='" + membershipCustomerRegDate + '\'' +
                ", membershipCustomerUpdateDate=" + membershipCustomerUpdateDate +
                '}';
    }
}
