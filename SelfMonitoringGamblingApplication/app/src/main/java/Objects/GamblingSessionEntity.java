package Objects;

import java.io.Serializable;

/**
 * Created by SebastienA on 2017-03-14.
 */

public class GamblingSessionEntity implements Serializable{
    private String uid;
    private String email;
    private String date;
    private String mode;
    private String game;
    private String startTime;
    private String endTime;
    private String startingAmount;
    private String finalAmount;
    private String outcome;
    private int duration;
    private String key;

    public GamblingSessionEntity(String uid, String email, String date, String mode, String game, String startingAmount, String finalAmount, String outcome, String startTime, String endTime, int duration) {
        this.uid = uid;
        this.email = email;
        this.date = date;
        this.mode = mode;
        this.game = game;
        this.startingAmount = startingAmount;
        this.finalAmount = finalAmount;
        this.outcome = outcome;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.key = null;
    }

    public GamblingSessionEntity(){

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getStartingAmount() {
        return startingAmount;
    }

    public void setStartingAmount(String startingAmount) {
        this.startingAmount = startingAmount;
    }

    public String getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(String finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
