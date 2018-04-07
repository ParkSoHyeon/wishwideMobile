package com.media.mobile.elin.wishwidemobile.Model;

public class MarkerVO {
    private int markerNo;
    private String markerGameTypeCode;
    private String wideManagerId;
    private String arGameNo;
    private String markerId;


    public int getMarkerNo() {
        return markerNo;
    }

    public void setMarkerNo(int markerNo) {
        this.markerNo = markerNo;
    }

    public String getMarkerGameTypeCode() {
        return markerGameTypeCode;
    }

    public void setMarkerGameTypeCode(String markerGameTypeCode) {
        this.markerGameTypeCode = markerGameTypeCode;
    }

    public String getWideManagerId() {
        return wideManagerId;
    }

    public void setWideManagerId(String wideManagerId) {
        this.wideManagerId = wideManagerId;
    }

    public String getArGameNo() {
        return arGameNo;
    }

    public void setArGameNo(String arGameNo) {
        this.arGameNo = arGameNo;
    }

    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }


    @Override
    public String toString() {
        return "MarkerVO{" +
                "markerNo='" + markerNo + '\'' +
                ", wideManagerId='" + wideManagerId + '\'' +
                ", markerGameTypeCode='" + markerGameTypeCode + '\'' +
                ", arGameNo='" + arGameNo + '\'' +
                ", markerGameTypeCode='" + markerGameTypeCode + '\'' +
                ", markerId='" + markerId +
                '}';
    }
}
