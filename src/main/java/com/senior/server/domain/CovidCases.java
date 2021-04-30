package com.senior.server.domain;

public class CovidCases {
    private int gotSickNumPCRplus;
    private int recoveredNumPCRplus;
    private int diedNumPCRplus;
    private int gotSickNumPCRminus;
    private int recoveredNumPCRminus;
    private int diedNumPCRminus;
    private int vaccinatedNum;
    private int doubleVaccinatedNum;
    private String message;
    private String vaccinatedMessage;

    public String getVaccinatedMessage() {
        return vaccinatedMessage;
    }

    public void setVaccinatedMessage(String vaccinatedMessage) {
        this.vaccinatedMessage = vaccinatedMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getGotSickNumPCRplus() {
        return gotSickNumPCRplus;
    }

    public void setGotSickNumPCRplus(int gotSickNumPCRplus) {
        this.gotSickNumPCRplus = gotSickNumPCRplus;
    }

    public int getRecoveredNumPCRplus() {
        return recoveredNumPCRplus;
    }

    public void setRecoveredNumPCRplus(int recoveredNumPCRplus) {
        this.recoveredNumPCRplus = recoveredNumPCRplus;
    }

    public int getDiedNumPCRplus() {
        return diedNumPCRplus;
    }

    public void setDiedNumPCRplus(int diedNumPCRplus) {
        this.diedNumPCRplus = diedNumPCRplus;
    }

    public int getGotSickNumPCRminus() {
        return gotSickNumPCRminus;
    }

    public void setGotSickNumPCRminus(int gotSickNumPCRminus) {
        this.gotSickNumPCRminus = gotSickNumPCRminus;
    }

    public int getRecoveredNumPCRminus() {
        return recoveredNumPCRminus;
    }

    public void setRecoveredNumPCRminus(int recoveredNumPCRminus) {
        this.recoveredNumPCRminus = recoveredNumPCRminus;
    }

    public int getDiedNumPCRminus() {
        return diedNumPCRminus;
    }

    public void setDiedNumPCRminus(int diedNumPCRminus) {
        this.diedNumPCRminus = diedNumPCRminus;
    }

    public int getVaccinatedNum() {
        return vaccinatedNum;
    }

    public void setVaccinatedNum(int vaccinatedNum) {
        this.vaccinatedNum = vaccinatedNum;
    }

    public int getDoubleVaccinatedNum() {
        return doubleVaccinatedNum;
    }

    public void setDoubleVaccinatedNum(int doubleVaccinatedNum) {
        this.doubleVaccinatedNum = doubleVaccinatedNum;
    }
}
