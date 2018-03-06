package com.media.mobile.elin.wishwidemobile.Model;

import java.sql.Timestamp;

public class GameCharacterFileVO {
    private int characterFileNo;    //캐릭터파일번호
    private int markerNo;   //마커번호
    private String wideManagerId;   //운영자아이디
    private String markerGameTypeCode;  //마커게임타입코드
    private String characterFileDataType;   //캐릭터파일타입
    private int characterFileSeq;   //캐릭터파일순서
    private int characterFileSize;  //캐릭터파일크기
    private String characterFileName;   //캐릭터파일명
    private String characterDbFile; //캐릭터DB파일
    private String characterFileUrl;    //캐릭터파일URL
    private String characterFileThumbnailName;  //캐릭터파일썸네일명
    private String characterFileThumbnailUrl;   //캐릭터파일썸네일URL
    private String characterFileSession;    //캐릭터파일세션
    private Timestamp characterFileRegDate; //캐릭터파일등록일시
    private Timestamp characterFileUpdateDate;  //캐릭터파일수정일시

    public int getCharacterFileNo() {
        return characterFileNo;
    }

    public void setCharacterFileNo(int characterFileNo) {
        this.characterFileNo = characterFileNo;
    }

    public int getMarkerNo() {
        return markerNo;
    }

    public void setMarkerNo(int markerNo) {
        this.markerNo = markerNo;
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

    public String getCharacterFileThumbnailName() {
        return characterFileThumbnailName;
    }

    public void setCharacterFileThumbnailName(String characterFileThumbnailName) {
        this.characterFileThumbnailName = characterFileThumbnailName;
    }

    public String getCharacterFileThumbnailUrl() {
        return characterFileThumbnailUrl;
    }

    public void setCharacterFileThumbnailUrl(String characterFileThumbnailUrl) {
        this.characterFileThumbnailUrl = characterFileThumbnailUrl;
    }

    public String getCharacterFileSession() {
        return characterFileSession;
    }

    public void setCharacterFileSession(String characterFileSession) {
        this.characterFileSession = characterFileSession;
    }

    public Timestamp getCharacterFileRegDate() {
        return characterFileRegDate;
    }

    public void setCharacterFileRegDate(Timestamp characterFileRegDate) {
        this.characterFileRegDate = characterFileRegDate;
    }

    public Timestamp getCharacterFileUpdateDate() {
        return characterFileUpdateDate;
    }

    public void setCharacterFileUpdateDate(Timestamp characterFileUpdateDate) {
        this.characterFileUpdateDate = characterFileUpdateDate;
    }

    @Override
    public String toString() {
        return "GameCharacterFileVO{" +
                "characterFileNo='" + characterFileNo + '\'' +
                ", markerNo='" + markerNo + '\'' +
                ", wideManagerId='" + wideManagerId + '\'' +
                ", markerGameTypeCode='" + markerGameTypeCode + '\'' +
                ", characterFileDataType='" + characterFileDataType + '\'' +
                ", characterFileSeq='" + characterFileSeq + '\'' +
                ", characterFileSize='" + characterFileSize + '\'' +
                ", characterFileName='" + characterFileName + '\'' +
                ", characterDbFile='" + characterDbFile + '\'' +
                ", characterFileUrl='" + characterFileUrl + '\'' +
                ", characterFileThumbnailName='" + characterFileThumbnailName + '\'' +
                ", characterFileThumbnailUrl='" + characterFileThumbnailUrl + '\'' +
                ", characterFileSession='" + characterFileSession + '\'' +
                ", characterFileRegDate='" + characterFileRegDate + '\'' +
                ", characterFileUpdateDate='" + characterFileUpdateDate +
                '}';
    }
}
