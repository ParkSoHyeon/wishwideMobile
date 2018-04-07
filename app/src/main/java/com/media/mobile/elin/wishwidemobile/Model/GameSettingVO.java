package com.media.mobile.elin.wishwidemobile.Model;

import java.util.List;

public class GameSettingVO {
    //Table: tb_ww_ar_game
    private int arGameNo;
    private int markerCnt;
    private String markerGameTypeCode;
    private String markerGameTypeName;
    private String markerGameValue;
    private String markerName;
    private String wideManagerId;


    //Table: tb_ww_marker_datafile
    private int markerDataFileNo;
    private String markerDatDbFile;
    private String markerDatFileDataType;
    private String markerDatFileName;
    private int markerDatFileSize;
    private String markerDatFileUrl;
    private String markerXmlDbFile;
    private String markerXmlFileDataType;
    private String markerXmlFileName;
    private int markerXmlFileSize;
    private String markerXmlFileUrl;


    private List<GameBenefitVO> gameBenefitList;
    private List<GameCharacterFileVO> gameCharacterFileList;
    private List<MarkerVO> markerList;


    private int totalCharacterCnt;
    private int benefitCnt;

    @Override
    public String toString() {
        return "GameSettingVO{" +
                "arGameNo=" + arGameNo +
                ", markerCnt=" + markerCnt +
                ", markerGameTypeCode='" + markerGameTypeCode + '\'' +
                ", markerGameTypeName='" + markerGameTypeName + '\'' +
                ", markerGameValue='" + markerGameValue + '\'' +
                ", markerName='" + markerName + '\'' +
                ", wideManagerId='" + wideManagerId + '\'' +
                ", markerDataFileNo=" + markerDataFileNo +
                ", markerDatDbFile='" + markerDatDbFile + '\'' +
                ", markerDatFileDataType='" + markerDatFileDataType + '\'' +
                ", markerDatFileName='" + markerDatFileName + '\'' +
                ", markerDatFileSize=" + markerDatFileSize +
                ", markerDatFileUrl='" + markerDatFileUrl + '\'' +
                ", markerXmlDbFile='" + markerXmlDbFile + '\'' +
                ", markerXmlFileDataType='" + markerXmlFileDataType + '\'' +
                ", markerXmlFileName='" + markerXmlFileName + '\'' +
                ", markerXmlFileSize=" + markerXmlFileSize +
                ", markerXmlFileUrl='" + markerXmlFileUrl + '\'' +
                ", gameBenefitList=" + gameBenefitList +
                ", gameCharacterFileList=" + gameCharacterFileList +
                ", markerList=" + markerList +
                '}';
    }

    public int getArGameNo() {
        return arGameNo;
    }

    public void setArGameNo(int arGameNo) {
        this.arGameNo = arGameNo;
    }

    public int getMarkerCnt() {
        return markerCnt;
    }

    public void setMarkerCnt(int markerCnt) {
        this.markerCnt = markerCnt;
    }

    public String getMarkerGameTypeCode() {
        return markerGameTypeCode;
    }

    public void setMarkerGameTypeCode(String markerGameTypeCode) {
        this.markerGameTypeCode = markerGameTypeCode;
    }

    public String getMarkerGameTypeName() {
        return markerGameTypeName;
    }

    public void setMarkerGameTypeName(String markerGameTypeName) {
        this.markerGameTypeName = markerGameTypeName;
    }

    public String getMarkerGameValue() {
        return markerGameValue;
    }

    public void setMarkerGameValue(String markerGameValue) {
        this.markerGameValue = markerGameValue;
    }

    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    public String getWideManagerId() {
        return wideManagerId;
    }

    public void setWideManagerId(String wideManagerId) {
        this.wideManagerId = wideManagerId;
    }

    public int getMarkerDataFileNo() {
        return markerDataFileNo;
    }

    public void setMarkerDataFileNo(int markerDataFileNo) {
        this.markerDataFileNo = markerDataFileNo;
    }

    public String getMarkerDatDbFile() {
        return markerDatDbFile;
    }

    public void setMarkerDatDbFile(String markerDatDbFile) {
        this.markerDatDbFile = markerDatDbFile;
    }

    public String getMarkerDatFileDataType() {
        return markerDatFileDataType;
    }

    public void setMarkerDatFileDataType(String markerDatFileDataType) {
        this.markerDatFileDataType = markerDatFileDataType;
    }

    public String getMarkerDatFileName() {
        return markerDatFileName;
    }

    public void setMarkerDatFileName(String markerDatFileName) {
        this.markerDatFileName = markerDatFileName;
    }

    public int getMarkerDatFileSize() {
        return markerDatFileSize;
    }

    public void setMarkerDatFileSize(int markerDatFileSize) {
        this.markerDatFileSize = markerDatFileSize;
    }

    public String getMarkerDatFileUrl() {
        return markerDatFileUrl;
    }

    public void setMarkerDatFileUrl(String markerDatFileUrl) {
        this.markerDatFileUrl = markerDatFileUrl;
    }

    public String getMarkerXmlDbFile() {
        return markerXmlDbFile;
    }

    public void setMarkerXmlDbFile(String markerXmlDbFile) {
        this.markerXmlDbFile = markerXmlDbFile;
    }

    public String getMarkerXmlFileDataType() {
        return markerXmlFileDataType;
    }

    public void setMarkerXmlFileDataType(String markerXmlFileDataType) {
        this.markerXmlFileDataType = markerXmlFileDataType;
    }

    public String getMarkerXmlFileName() {
        return markerXmlFileName;
    }

    public void setMarkerXmlFileName(String markerXmlFileName) {
        this.markerXmlFileName = markerXmlFileName;
    }

    public int getMarkerXmlFileSize() {
        return markerXmlFileSize;
    }

    public void setMarkerXmlFileSize(int markerXmlFileSize) {
        this.markerXmlFileSize = markerXmlFileSize;
    }

    public String getMarkerXmlFileUrl() {
        return markerXmlFileUrl;
    }

    public void setMarkerXmlFileUrl(String markerXmlFileUrl) {
        this.markerXmlFileUrl = markerXmlFileUrl;
    }

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

    public List<MarkerVO> getMarkerList() {
        return markerList;
    }

    public void setMarkerList(List<MarkerVO> markerList) {
        this.markerList = markerList;
    }

    public int getTotalCharacterCnt() {
        return totalCharacterCnt;
    }

    public void setTotalCharacterCnt(int totalCharacterCnt) {
        this.totalCharacterCnt = totalCharacterCnt;
    }

    public int getBenefitCnt() {
        return benefitCnt;
    }

    public void setBenefitCnt(int benefitCnt) {
        this.benefitCnt = benefitCnt;
    }
}
