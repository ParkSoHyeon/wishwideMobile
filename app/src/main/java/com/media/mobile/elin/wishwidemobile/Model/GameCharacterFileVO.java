package com.media.mobile.elin.wishwidemobile.Model;

public class GameCharacterFileVO {
    private int characterFileNo;    //캐릭터파일번호
    private String markerGameTypeCode;  //마커게임타입코드
    private String characterFileDataType;   //캐릭터파일타입
    private int characterFileSeq;   //캐릭터파일순서
    private int characterFileSize;  //캐릭터파일크기
    private String characterFileName;   //캐릭터파일명
    private String characterDbFile; //캐릭터DB파일
    private String characterFileUrl;    //캐릭터파일URL
    private String characterFileValue;  //캐릭터파일값(순서맞추기게임)


    public int getCharacterFileNo() {
        return characterFileNo;
    }

    public void setCharacterFileNo(int characterFileNo) {
        this.characterFileNo = characterFileNo;
    }

    public String getMarkerGameTypeCode() {
        return markerGameTypeCode;
    }

    public void setMarkerGameTypeCode(String markerGameTypeCode) {
        this.markerGameTypeCode = markerGameTypeCode;
    }

    public String getCharacterFileDataType() {
        return characterFileDataType;
    }

    public void setCharacterFileDataType(String characterFileDataType) {
        this.characterFileDataType = characterFileDataType;
    }

    public int getCharacterFileSeq() {
        return characterFileSeq;
    }

    public void setCharacterFileSeq(int characterFileSeq) {
        this.characterFileSeq = characterFileSeq;
    }

    public int getCharacterFileSize() {
        return characterFileSize;
    }

    public void setCharacterFileSize(int characterFileSize) {
        this.characterFileSize = characterFileSize;
    }

    public String getCharacterFileName() {
        return characterFileName;
    }

    public void setCharacterFileName(String characterFileName) {
        this.characterFileName = characterFileName;
    }

    public String getCharacterDbFile() {
        return characterDbFile;
    }

    public void setCharacterDbFile(String characterDbFile) {
        this.characterDbFile = characterDbFile;
    }

    public String getCharacterFileUrl() {
        return characterFileUrl;
    }

    public void setCharacterFileUrl(String characterFileUrl) {
        this.characterFileUrl = characterFileUrl;
    }

    public String getCharacterFileValue() {
        return characterFileValue;
    }

    public void setCharacterFileValue(String characterFileValue) {
        this.characterFileValue = characterFileValue;
    }

    @Override
    public String toString() {
        return "GameCharacterFileVO{" +
                "characterFileNo='" + characterFileNo + '\'' +
                ", markerGameTypeCode='" + markerGameTypeCode + '\'' +
                ", characterFileDataType='" + characterFileDataType + '\'' +
                ", characterFileSeq='" + characterFileSeq + '\'' +
                ", characterFileSize='" + characterFileSize + '\'' +
                ", characterFileName='" + characterFileName + '\'' +
                ", characterDbFile='" + characterDbFile + '\'' +
                ", characterFileUrl='" + characterFileUrl + '\'' +
                ", characterFileValue='" + characterFileValue +
                '}';
    }
}
