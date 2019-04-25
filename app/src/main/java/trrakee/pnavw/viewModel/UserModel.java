package trrakee.pnavw.viewModel;

public class UserModel {
    private long mId;
    private int mImage;
    private String mName;
    private String mEmail;
    private String mMobileNumber;

    UserModel() {

    }

    public UserModel(int image, String name, String email, String mobileNumber) {
        mImage = image;
        mName = name;
        mEmail = email;
        mMobileNumber = mobileNumber;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    int getImage() {
        return mImage;
    }

    public void setImage(int image) {
        mImage = image;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getMobileNumber() {
        return mMobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        mMobileNumber = mobileNumber;
    }
}
