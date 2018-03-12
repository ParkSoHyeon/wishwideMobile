package com.media.mobile.elin.wishwidemobile.Model;

import java.sql.Timestamp;
import java.util.List;

public class WideCustomerVO {
    private int wideCustomerNo;   //통학고객번호
    private String wideCustomerBirth; //통학고객생일
    private String wideCustomerEmail; //통학고객이메일
    private String wideCustomerName;  //통학고객이름
    private String wideCustomerPhone; //통학고객전화번호
    private String wideCustomerMemo;  //통학고객메모
    private int wideCustomerSex;  //통학고객성별
    private int wideCustomerVisibleCode; //통학고객활성여부
    private Timestamp wideCustomerRegDate;   //통학고객등록일시
    private Timestamp wideCustomerUpdateDate;    //통학고객수정일시

    private List<MembershipCustomerVO> membershipCustomers;

    public int getWideCustomerNo() {
        return wideCustomerNo;
    }

    public void setWideCustomerNo(int wideCustomerNo) {
        this.wideCustomerNo = wideCustomerNo;
    }

    public String getWideCustomerBirth() {
        return wideCustomerBirth;
    }

    public void setWideCustomerBirth(String wideCustomerBirth) {
        this.wideCustomerBirth = wideCustomerBirth;
    }

    public String getWideCustomerEmail() {
        return wideCustomerEmail;
    }

    public void setWideCustomerEmail(String wideCustomerEmail) {
        this.wideCustomerEmail = wideCustomerEmail;
    }

    public String getWideCustomerName() {
        return wideCustomerName;
    }

    public void setWideCustomerName(String wideCustomerName) {
        this.wideCustomerName = wideCustomerName;
    }

    public String getWideCustomerPhone() {
        return wideCustomerPhone;
    }

    public void setWideCustomerPhone(String wideCustomerPhone) {
        this.wideCustomerPhone = wideCustomerPhone;
    }

    public String getWideCustomerMemo() {
        return wideCustomerMemo;
    }

    public void setWideCustomerMemo(String wideCustomerMemo) {
        this.wideCustomerMemo = wideCustomerMemo;
    }

    public int getWideCustomerSex() {
        return wideCustomerSex;
    }

    public void setWideCustomerSex(int wideCustomerSex) {
        this.wideCustomerSex = wideCustomerSex;
    }

    public int getWideCustomerVisibleCode() {
        return wideCustomerVisibleCode;
    }

    public void setWideCustomerVisibleCode(int wideCustomerVisibleCode) {
        this.wideCustomerVisibleCode = wideCustomerVisibleCode;
    }

    public Timestamp getWideCustomerRegDate() {
        return wideCustomerRegDate;
    }

    public void setWideCustomerRegDate(Timestamp wideCustomerRegDate) {
        this.wideCustomerRegDate = wideCustomerRegDate;
    }

    public Timestamp getWideCustomerUpdateDate() {
        return wideCustomerUpdateDate;
    }

    public void setWideCustomerUpdateDate(Timestamp wideCustomerUpdateDate) {
        this.wideCustomerUpdateDate = wideCustomerUpdateDate;
    }

    public List<MembershipCustomerVO> getMembershipCustomers() {
        return membershipCustomers;
    }

    public void setMembershipCustomers(List<MembershipCustomerVO> membershipCustomers) {
        this.membershipCustomers = membershipCustomers;
    }

    @Override
    public String toString() {
        return "WideCustomerVO{" +
                "wideCustomerNo='" + wideCustomerNo + '\'' +
                ", wideCustomerBirth='" + wideCustomerBirth + '\'' +
                ", wideCustomerEmail='" + wideCustomerEmail + '\'' +
                ", wideCustomerName='" + wideCustomerName + '\'' +
                ", wideCustomerPhone='" + wideCustomerPhone + '\'' +
//                ", wideCustomerMemo='" + wideCustomerMemo + '\'' +
                ", wideCustomerSex='" + (wideCustomerSex == 0 ? "남자" : "여자") + '\'' +
                ", wideCustomerVisibleCode='" + wideCustomerVisibleCode + '\'' +
//                ", wideCustomerRegDate='" + wideCustomerRegDate + '\'' +
//                ", wideCustomerUpdateDate=" + wideCustomerUpdateDate +
//                ", membershipCustomers=" + membershipCustomers.toArray() +
                '}';
    }
}
