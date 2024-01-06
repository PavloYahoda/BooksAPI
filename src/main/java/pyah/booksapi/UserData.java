package pyah.booksapi;

public class UserData {
    private static String userName;
    private static String password;
    private static String token;
    private static String userID;
    private static String userBook;


    private UserData(String userName, String password) {
        UserData.userName = userName;
        UserData.password = password;
    }

    private static class SingletonHelper {
        private static final UserData INSTANCE = new UserData(userName, password);
    }

    public static UserData createInstance(String name, String pass) {
        UserData.userName = name;
        UserData.password = pass;
        return SingletonHelper.INSTANCE;
    }

    public static UserData getInstance(){
        return SingletonHelper.INSTANCE;
    }



    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        UserData.token = token;
    }

    public static String getUserID() {
        return userID;
    }

    public static void setUserID(String userID) {
        UserData.userID = userID;
    }

    public static String getUserBook() {
        return userBook;
    }

    public static void setUserBook(String userBook) {
        UserData.userBook = userBook;
    }
}
