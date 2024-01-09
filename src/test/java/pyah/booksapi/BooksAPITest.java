package pyah.booksapi;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

        String key="books";//array key (as it mentioned in your Json)
        Response response = RestAssured
                .given()
                .when()
                .get("https://demoqa.com/BookStore/v1/Books")
                .thenReturn();
        //your API call which will return Json Object
        List<HashMap<String,Object>> booksList = response.jsonPath().getList(key);

        //Now parse value from List
        List<String> allBooks = new ArrayList<>();

        for(int i = 0; i < booksList.size(); i++){
            HashMap<String,Object> firstBookDetails = booksList.get(i);
            allBooks.add((String)firstBookDetails.get("isbn"));
        }
        UserData.getInstance().setAllBooks(allBooks);
        System.out.println(UserData.getInstance().getAllBooks());
    }

    @Test
    @Order(6)
    void setUserBooks() {

        String body = "{\n" +
                "  \"userId\": \"" + UserData.getInstance().getUserID() + "\",\n" +
                "  \"collectionOfIsbns\": [\n" +
                "    {\n" +
                "      \"isbn\": \"" + UserData.getInstance().getAllBooks().get(0) + "\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"isbn\": \"" + UserData.getInstance().getAllBooks().get(1) + "\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"isbn\": \"" + UserData.getInstance().getAllBooks().get(2) + "\"\n" +
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
                .assertThat().body("books.isbn[0]", equalTo(UserData.getInstance().getAllBooks().get(0)))
                .assertThat().body("books.isbn[1]", equalTo(UserData.getInstance().getAllBooks().get(1)))
                .assertThat().body("books.isbn[2]", equalTo(UserData.getInstance().getAllBooks().get(2)));
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
                .get("/Book?ISBN=" + UserData.getInstance().getAllBooks().get(2))
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
                .body("books.isbn[0]", equalTo(UserData.getInstance().getAllBooks().get(0)))
                .body("books.isbn[1]", equalTo(UserData.getInstance().getAllBooks().get(1)))
                .body("books.isbn[2]", equalTo(UserData.getInstance().getAllBooks().get(2)));
    }

    @Test
    @Order(9)
    void deleteUserBook(){

        String body = "{\n" +
                "  \"isbn\": \"" + UserData.getInstance().getAllBooks().get(0) + "\",\n" +
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
    void isBookDeleted(){
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
                .body(UserData.getInstance().getAllBooks().get(0), nullValue());
    }

    @Test
    @Order(11)
    void deleteAllUserBooks(){

        RestAssured
                .given()
                .log().all()
                .when()
                .baseUri("https://demoqa.com/BookStore/v1")
                .queryParam("UserId", UserData.getInstance().getUserID())
                .contentType(ContentType.JSON)
                .header("authorization", "Bearer " + UserData.getInstance().getToken())
                .delete("/Books")
                .then()
                .log().all()
                .statusCode(204);
    }

    @Test
    @Order(12)
    void areBooksDeleted(){
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
                .body("books.isbn[1]", nullValue())
                .body("books.isbn[2]", nullValue());
    }

    @Test
    @Order(13)
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
    @Order(14)
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
