package pyah.booksapi;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class BooksAPITest {


    @Test
    @Order(1)
    void createUser() {

        UserData.createInstance(Helper.randomFullName(), Helper.PASSWORD);

        UserData.setUserID(
                RestAssured
                        .given()
                        .log().all()
                        .when()
                        .contentType(ContentType.JSON)
                        .body(UserData.getInstance())
                        .post("https://demoqa.com/Account/v1/User")
                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().response().body().jsonPath().getString("userID"));
    }

    @Test
    @Order(2)
    void getToken() {
        UserData.setToken(
                RestAssured
                        .given()
                        .log().all()
                        .when()
                        .contentType(ContentType.JSON)
                        .body(UserData.getInstance())
                        .post("https://demoqa.com/Account/v1/GenerateToken")
                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract().response().body().jsonPath().getString("token"));
    }

    @Test
    @Order(3)
    void isUserAuthorized() {
        RestAssured
                .given()
                .log().all()
                .when()
                .contentType(ContentType.JSON)
                .header("authorization", "Bearer " + UserData.getToken())
                .body(UserData.getInstance())
                .post("https://demoqa.com/Account/v1/Authorized")
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    @Order(4)
    void getUserById() {
        RestAssured
                .given()
                .log().all()
                .when()
                .contentType(ContentType.JSON)
                .header("authorization", "Bearer " + UserData.getToken())
                .get("https://demoqa.com/Account/v1/User/" + UserData.getUserID())
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    @Order(5)
    void getAllBooks(){

         UserData.setUserBook(RestAssured
                .given()
                .log().all()
                .when()
                .contentType(ContentType.JSON)
                .header("authorization", "Bearer " + UserData.getToken())
                .get("https://demoqa.com/BookStore/v1/Books")
                .then()
                .log().body()
                .statusCode(200)
                .extract().response().body().jsonPath().getString("books.isbn[2]"));
    }

    @Test
    @Order(6)
    void setUserBook(){

        String body = "{\n" +
                "  \"userId\": " + UserData.getUserID() + ",\n" +
                "  \"collectionOfIsbns\": [\n" +
                "    {\n" +
                "      \"isbn\": " + UserData.getUserBook() +"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        RestAssured
                .given()
                .log().all()
                .when()
                .contentType(ContentType.JSON)
                .header("authorization", "Bearer " + UserData.getToken())
                .body(body)
                .post("https://demoqa.com/BookStore/v1/Books")
                .then()
                .log().body()
                .statusCode(201);
    }
}
