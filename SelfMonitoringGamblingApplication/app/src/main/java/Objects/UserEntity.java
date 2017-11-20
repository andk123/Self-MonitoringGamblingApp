package Objects;

/**
 * Created by SebastienA on 2017-03-10.
 */

public class UserEntity {
    private String uid;
    private String yob;
    private String sex;
    private String dailyLimit;
    private String dailyTimeLimit;


    public UserEntity() {

    }



    public UserEntity(String uid, String yob, String sex, String dailyLimit, String dailyTimeLimit){
        this.uid = uid;
        this.yob = yob;
        this.sex = sex;
        this.dailyLimit= dailyLimit;
        this.dailyTimeLimit = dailyTimeLimit;
    }

    public String getDailyTimeLimit() {
        return dailyTimeLimit;
    }

    public void setDailyTimeLimit(String dailyTimeLimit) {
        this.dailyTimeLimit = dailyTimeLimit;
    }
    public String getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(String dailyLimit) {
        this.dailyLimit = dailyLimit;
    }


    public String getYob() {
        return yob;
    }

    public void setYob(String yob) {
        this.yob = yob;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }



}