package Objects;

/**
 * Created by SebastienA on 2017-03-10.
 */

public class UserEntity {
    //TODO: Possibly photo URL
    private String email;
    private String firstName;
    private String lastName;
    private String uid;

    private int signinType;

    public UserEntity(String email, String uid) {
        this.email = email;
        this.uid = uid;
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