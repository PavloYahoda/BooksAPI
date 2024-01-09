package pyah.booksapi;

import java.util.List;

public class UserData {
    private static String userName;
    private static String password;
    private static String token;
    private static String userID;
    private static String userBook;
    private static List<String> allBooks;


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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        UserData.token = token;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        UserData.userID = userID;
    }
    public void setAllBooks(List<String> allBooks){
        UserData.allBooks = allBooks;
    }

    public List<String> getAllBooks() {
        return allBooks;
    }

    public void setUserBook(String userBook) {
        UserData.userBook = userBook;
    }

    public String getUserBook() {
        return userBook;
    }
}
