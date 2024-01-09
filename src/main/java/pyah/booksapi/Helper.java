package pyah.booksapi;


import com.github.javafaker.Faker;

public class Helper{

    public static final String PASSWORD = "Admin123!";

    public static String randomFullName(){
        Faker faker = new Faker();
        return faker.name().fullName();
    }
    public static String randomFirstName(){

        Faker faker = new Faker();
        return faker.name().firstName();
    }
    public static String randomLastName(){
        Faker faker = new Faker();
        return faker.name().lastName();
    }
    public static String randomStreetAddress(){
        Faker faker = new Faker();
        return faker.address().streetAddress();
    }


}