package com.media.mobile.elin.wishwidemobile.Model;

import java.sql.Timestamp;

public class GameBenefitVO {
    private int gameBenefitNo;    //게임혜택번호
    private int markerNo;  //마커번호
    private int wwNo;  //위시와이드번호
    private String wideManagerId; //운영자아이디
    private String markerGameTypeCode; //게임혜택게임타입코드
    private String  gameBenefitGradeTypeCode;   //게임혜택등급타입코드
    private String gameBenefitTypeCode;  //게임혜택타입코드
    private int gameBenefitTypeValue;    //게임혜택타입값
    private Timestamp gameBenefitRegDate;    //게임혜택등록일시
    private Timestamp gameBenefitUpdateDate; //게임혜택수정일시

    public int getGameBenefitNo() {
        return gameBenefitNo;
    }

    public void setGameBenefitNo(int gameBenefitNo) {
        this.gameBenefitNo = gameBenefitNo;
    }

    public int getMarkerNo() {
        return markerNo;
    }

    public void setMarkerNo(int markerNo) {
        this.markerNo = markerNo;
    }

    public int getWwNo() {
        return wwNo;
    }

    public void setWwNo(int wwNo) {
        this.wwNo = wwNo;
    }

    public String getWideManagerId() {
        return wideManagerId;
    }

    public void setWideManagerId(String wideManagerId) {
        this.wideManagerId = wideManagerId;
    }

    public String getMarkerGameTypeCode() {
        return markerGameTypeCode;
    }

    public void setMarkerGameTypeCode(String markerGameTypeCode) {
        this.markerGameTypeCode = markerGameTypeCode;
    }

    public String getGameBenefitGradeTypeCode() {
        return gameBenefitGradeTypeCode;
    }

    public void setGameBenefitGradeTypeCode(String gameBenefitGradeTypeCode) {
        this.gameBenefitGradeTypeCode = gameBenefitGradeTypeCode;
    }

    public String getGameBenefitTypeCode() {
        return gameBenefitTypeCode;
    }

    public void setGameBenefitTypeCode(String gameBenefitTypeCode) {
        this.gameBenefitTypeCode = gameBenefitTypeCode;
    }

    public int getGameBenefitTypeValue() {
        return gameBenefitTypeValue;
    }

    public void setGameBenefitTypeValue(int gameBenefitTypeValue) {
        this.gameBenefitTypeValue = gameBenefitTypeValue;
    }

    public Timestamp getGameBenefitRegDate() {
        return gameBenefitRegDate;
    }

    public void setGameBenefitRegDate(Timestamp gameBenefitRegDate) {
        this.gameBenefitRegDate = gameBenefitRegDate;
    }

    public Timestamp getGameBenefitUpdateDate() {
        return gameBenefitUpdateDate;
    }

    public void setGameBenefitUpdateDate(Timestamp gameBenefitUpdateDate) {
        this.gameBenefitUpdateDate = gameBenefitUpdateDate;
    }

    @Override
    public String toString() {
        return "GameBenefitVO{" +
                "gameBenefitNo='" + gameBenefitNo + '\'' +
                ", wwNo='" + wwNo + '\'' +
                ", wideManagerId='" + wideManagerId + '\'' +
                ", markerGameTypeCode='" + markerGameTypeCode + '\'' +
                ", gameBenefitGradeTypeCode='" + gameBenefitGradeTypeCode + '\'' +
                ", gameBenefitTypeCode='" + gameBenefitTypeCode + '\'' +
                ", gameBenefitTypeValue='" + gameBenefitTypeValue + '\'' +
                ", gameBenefitRegDate='" + gameBenefitRegDate + '\'' +
                ", gameBenefitUpdateDate='" + gameBenefitUpdateDate + '\'' +
                ", markerNo='" + markerNo + '\'' +
                '}';
    }
}
