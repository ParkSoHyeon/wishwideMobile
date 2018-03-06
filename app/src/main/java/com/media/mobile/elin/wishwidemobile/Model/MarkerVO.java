package com.media.mobile.elin.wishwidemobile.Model;

import java.sql.Timestamp;
import java.util.List;

public class MarkerVO {
    private int markerNo;  //마커번호
    private int wwNo;  //위시와이드번호
    private String wideManagerId; //운영자아이디
    private int markerGameCharacterCnt;  //마커게임캐릭터수
    private String markerGameTypeCode;   //마커게임타입코드
    private String markerTouchEventCode; //마커터치이벤트코드
    private String markerVuforiaCode;   //마터Vuforia코드
    private Timestamp markerRegDate;  //마커등급일시
    private Timestamp markerUpdateDate;   //마커수정일시

    private List<GameBenefitVO> gameBenefitList;
    private List<GameCharacterFileVO> gameCharacterFileList;

    public List<GameBenefitVO> getGameBenefitList() {
        return gameBenefitList;
    }

    public void setGameBenefitList(List<GameBenefitVO> gameBenefitList) {
        this.gameBenefitList = gameBenefitList;
    }

    public List<GameCharacterFileVO> getGameCharacterFileList() {
        return gameCharacterFileList;
    }

    public void setGameCharacterFileList(List<GameCharacterFileVO> gameCharacterFileList) {
        this.gameCharacterFileList = gameCharacterFileList;
    }

    public String getMarkerVuforiaCode() {
        return markerVuforiaCode;
    }

    public void setMarkerVuforiaCode(String markerVuforiaCode) {
        this.markerVuforiaCode = markerVuforiaCode;
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

    public int getMarkerGameCharacterCnt() {
        return markerGameCharacterCnt;
    }

    public void setMarkerGameCharacterCnt(int markerGameCharacterCnt) {
        this.markerGameCharacterCnt = markerGameCharacterCnt;
    }

    public String getMarkerGameTypeCode() {
        return markerGameTypeCode;
    }

    public void setMarkerGameTypeCode(String markerGameTypeCode) {
        this.markerGameTypeCode = markerGameTypeCode;
    }

    public String getMarkerTouchEventCode() {
        return markerTouchEventCode;
    }

    public void setMarkerTouchEventCode(String markerTouchEventCode) {
        this.markerTouchEventCode = markerTouchEventCode;
    }

    public Timestamp getMarkerRegDate() {
        return markerRegDate;
    }

    public void setMarkerRegDate(Timestamp markerRegDate) {
        this.markerRegDate = markerRegDate;
    }

    public Timestamp getMarkerUpdateDate() {
        return markerUpdateDate;
    }

    public void setMarkerUpdateDate(Timestamp markerUpdateDate) {
        this.markerUpdateDate = markerUpdateDate;
    }

    @Override
    public String toString() {
        return "MarkerVO{" +
                "wwNo='" + wwNo + '\'' +
                ", wideManagerId='" + wideManagerId + '\'' +
                ", markerNo='" + markerNo + '\'' +
                ", markerGameCharacterCnt='" + markerGameCharacterCnt + '\'' +
                ", markerGameTypeCode='" + markerGameTypeCode + '\'' +
                ", markerTouchEventCode='" + markerTouchEventCode + '\'' +
                ", markerVuforiaCode='" + markerVuforiaCode + '\'' +
                ", markerRegDate='" + markerRegDate + '\'' +
                ", markerUpdateDate='" + markerUpdateDate +
                '}';
    }
}
