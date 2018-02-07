package android.ebs.zunderapp;

/**
 * Created by Carlos on 06/02/2018.
 */

public class User {

    private String fullName, title, id;
    private int dob;

    public User() {
    }

    public User(String name, String title, String id) {
        this.id = id;
        this.title = title;
        fullName = name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getTitle() {
        return title;
    }

    public int getDob() {
        return dob;
    }

    public String getId() {
        return id;
    }
}
