package pyah.booksapi;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class BooksAPITest {


    @Test
    @Order(1)
    void createUser() {

        UserData.createInstance(Helper.randomFullName(), Helper.PASSWORD);

        String body = "{\n" +
                "  \"userName\": \"" + UserData.getInstance().getUserName() + "\",\n" +
                "  \"password\": \"" + Helper.PASSWORD + "\"\n" +
                "}";

        UserData.getInstance().setUserID(
                RestAssured
                        .given()
                        .log().all()
                        .when()
                        .baseUri("https://demoqa.com/Account/v1")
                        .contentType(ContentType.JSON)
                        .body(body)
                        .post("/User")
                        .then()
                        .log().body()
                        .statusCode(201)
                        .assertThat().statusCode(HttpStatus.SC_CREATED)
                        .assertThat().body(Matchers.containsString("userID"))
                        .extract().response().body().jsonPath().getString("userID"));
    }

    @Test
    @Order(2)
    void getToken() {

        String body = "{\n" +
                "  \"userName\": \"" + UserData.getInstance().getUserName() + "\",\n" +
                "  \"password\": \"" + Helper.PASSWORD + "\"\n" +
                "}";


        UserData.getInstance().setToken(

                RestAssured
                        .given()
                        .log().all()
                        .when()
                        .baseUri("https://demoqa.com/Account/v1")
                        .contentType(ContentType.JSON)
                        .body(body)
                        .post("/GenerateToken")
                        .then()
                        .log().body()
                        .statusCode(200)
                        .assertThat()
                            .body("status", equalTo("Success"))
                            .body("token", notNullValue())
                        .extract().response().body().jsonPath().getString("token"));
    }

    @Test
    @Order(3)
    void userLogin() {

        String body = "{\n" +
                "  \"userName\": \"" + UserData.getInstance().getUserName() + "\",\n" +
                "  \"password\": \"" + Helper.PASSWORD + "\"\n" +
                "}";


        RestAssured
                .given()
                .log().all()
                .when()
                .baseUri("https://demoqa.com/Account/v1")
                .contentType(ContentType.JSON)
                .header("authorization", "Bearer " + UserData.getInstance().getToken())
                .body(body)
                .post("/Login")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat().body("userId", equalTo(UserData.getInstance().getUserID()));
    }

    @Test
    @Order(4)
    void getUserById() {
        RestAssured
                .given()
                .log().all()
                .when()
                .baseUri("https://demoqa.com/Account/v1")
                .contentType(ContentType.JSON)
                .header("authorization", "Bearer " + UserData.getInstance().getToken())
                .get("/User/" + UserData.getInstance().getUserID())
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("userId", equalTo(UserData.getInstance().getUserID()))
                .body("username", equalTo(UserData.getInstance().getUserName()));
    }

    @Test
    @Order(5)
    void getAllBooks(){

         UserData.getInstance().setUserBook(RestAssured
                .given()
                .log().all()
                .when()
                .baseUri("https://demoqa.com/BookStore/v1")
                .contentType(ContentType.JSON)
                .header("authorization", "Bearer " + UserData.getInstance().getToken())
                .get("/Books")
                .then()
                .log().body()
                .statusCode(200)
                .assertThat().body("books.isbn[2]", notNullValue())
                .extract().response().body().jsonPath().getString("books.isbn[2]"));
    }

    @Test
    @Order(6)
    void setUserBook() {

        String body = "{\n" +
                "  \"userId\": \"" + UserData.getInstance().getUserID() + "\",\n" +
                "  \"collectionOfIsbns\": [\n" +
                "    {\n" +
                "      \"isbn\": \"" + UserData.getInstance().getUserBook() + "\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        RestAssured
                .given()
                .log().all()
                .when()
                .baseUri("https://demoqa.com/BookStore/v1")
                .contentType(ContentType.JSON)
                .header("authorization", "Bearer " + UserData.getInstance().getToken())
                .body(body)
                .post("/Books")
                .then()
                .log().body()
                .statusCode(201)
                .assertThat().body("books.isbn[0]", equalTo(UserData.getInstance().getUserBook()));
    }

    @Test
    @Order(7)
    void getBookByISBN(){
        RestAssured
                .given()
                .log().all()
                .when()
                .baseUri("https://demoqa.com/BookStore/v1")
                .contentType(ContentType.JSON)
                .header("authorization", "Bearer " + UserData.getInstance().getToken())
                .get("/Book?ISBN=" + UserData.getInstance().getUserBook())
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("isbn", equalTo("9781449337711"))
                .body("title", equalTo("Designing Evolvable Web APIs with ASP.NET"))
                .body("subTitle", equalTo("Harnessing the Power of the Web"))
                .body("author", equalTo("Glenn Block et al."))
                .body("publish_date", equalTo("2020-06-04T09:12:43.000Z"))
                .body("publisher", equalTo("O'Reilly Media"))
                .body("pages", equalTo(238))
                .body("description", containsString("Design and build Web APIs"))
                .body("website", equalTo("http://chimera.labs.oreilly.com/books/1234000001708/index.html"));
    }

    @Test
    @Order(8)
    void getUserBooks(){
        RestAssured
                .given()
                .log().all()
                .when()
                .baseUri("https://demoqa.com/Account/v1")
                .contentType(ContentType.JSON)
                .header("authorization", "Bearer " + UserData.getInstance().getToken())
                .get("/User/" + UserData.getInstance().getUserID())
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("books.isbn[0]", equalTo(UserData.getInstance().getUserBook()));
    }

    @Test
    @Order(9)
    void deleteUserBook(){

        String body = "{\n" +
                "  \"isbn\": \"" + UserData.getInstance().getUserBook() + "\",\n" +
                "  \"userId\": \"" + UserData.getInstance().getUserID() + "\"\n" +
                "}";

        RestAssured
                .given()
                .log().all()
                .when()
                .baseUri("https://demoqa.com/BookStore/v1")
                .contentType(ContentType.JSON)
                .header("authorization", "Bearer " + UserData.getInstance().getToken())
                .body(body)
                .delete("/Book")
                .then()
                .log().all()
                .statusCode(204);
    }

    @Test
    @Order(10)
    void isBooksDeleted(){
        RestAssured
                .given()
                .log().all()
                .when()
                .baseUri("https://demoqa.com/Account/v1")
                .contentType(ContentType.JSON)
                .header("authorization", "Bearer " + UserData.getInstance().getToken())
                .get("/User/" + UserData.getInstance().getUserID())
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("books.isbn[0]", nullValue());
    }

    @Test
    @Order(11)
    void deleteUser(){
        RestAssured
                .given()
                .log().all()
                .when()
                .baseUri("https://demoqa.com/Account/v1")
                .contentType(ContentType.JSON)
                .header("authorization", "Bearer " + UserData.getInstance().getToken())
                .delete("/User/" + UserData.getInstance().getUserID())
                .then()
                .log().all()
                .statusCode(204);
    }

    @Test
    @Order(12)
    void isUserDeleted(){

        System.out.println(UserData.getInstance().getUserID());
        RestAssured
                .given()
                .log().all()
                .when()
                .baseUri("https://demoqa.com/Account/v1")
                .contentType(ContentType.JSON)
                .header("authorization", "Bearer " + UserData.getInstance().getToken())
                .get("/User/" + UserData.getInstance().getUserID())
                .then()
                .log().all()
                .statusCode(401)
                .assertThat().body("message", equalTo("User not found!"));
    }
}
