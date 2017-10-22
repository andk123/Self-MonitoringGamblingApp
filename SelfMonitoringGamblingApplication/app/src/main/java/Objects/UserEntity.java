package Objects;

/**
 * Created by SebastienA on 2017-03-10.
 */

public class UserEntity {
    //TODO: Possibly photo URL
    private String firstName;
    private String lastName;
    private String uid;
    private String notifications;
    private String yob;
    private String sex;

    public String getSessionsAmount() {
        return sessionsAmount;
    }

    public void setSessionsAmount(String sessionsAmount) {
        this.sessionsAmount = sessionsAmount;
    }

    private String sessionsAmount;
    private String thresholdValue;

    private int signinType;

    public UserEntity() {

    }

    public UserEntity( String uid) {
        this.uid = uid;
    }

    public UserEntity(String uid, String notifications, String yob, String sex){
        this.uid = uid;
        this.notifications = notifications;
        this.yob = yob;
        this.sex = sex;
    }

    public String getNotifications() {
        return notifications;
    }

    public void setNotifications(String notifications) {
        this.notifications = notifications;
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


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getSigninType() {
        return signinType;
    }

    public void setSigninType(int signinType) {
        this.signinType = signinType;
    }


}