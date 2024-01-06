package pyah.booksapi;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;



public class BooksTest {
    private String userBook;

    @Test
    @Order(1)
    void getAllBooks(){
        userBook = RestAssured
                .given()
                .log().all()
                .when()
                .contentType(ContentType.JSON)
                .header("authorization", "Bearer " + UserData.getToken())
                .get("https://demoqa.com/BookStore/v1/Books")
                .then()
                .log().body()
                .statusCode(200)
                .extract().response().body().jsonPath().getString("books.isbn[2]");
        System.out.println(userBook);
    }

    @Test
    @Order(2)
    void setUserBook(){

        String body = "{\n" +
                "  \"userId\": " + UserData.getUserID() + ",\n" +
                "  \"collectionOfIsbns\": [\n" +
                "    {\n" +
                "      \"isbn\": " + userBook +"\n" +
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
