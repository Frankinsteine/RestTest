package org.example;

import io.restassured.http.Cookies;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class RestTest {
    @Test
    void test () {
        String baseUri = "http://localhost:8080/";

        //set cookies
        Response response = given()
                .baseUri(baseUri)
                .when()
                .get("api/food")
        ;

        Cookies cookies = response.getDetailedCookies();

        //init test data
        String product = "{\n" +
                "  \"name\": \"Огурец\",\n" +
                "  \"type\": \"VEGETABLE\",\n" +
                "  \"exotic\": \"false\" \n}";

        //check product table
        given()
                .baseUri(baseUri)
                .cookies(cookies)
                .when()
                .get("api/food")
                .then()
                .assertThat()
                .statusCode(200)
                .log().all()
        ;

        //add test product in table
        given()
                .baseUri(baseUri)
                .cookies(cookies)
                .header("Content-type", "application/json")
                .and()
                .body(product)
                .when()
                .post("api/food")
                .then()
                .assertThat()
                .statusCode(200)
        ;

        //check table after adding test product
        given()
                .baseUri(baseUri)
                .cookies(cookies)
                .when()
                .get("api/food")
                .then()
                .assertThat()
                .statusCode(200)
                .assertThat()
                .body(
                        "name[4]", equalTo("Огурец"),
                        "type[4]", equalTo("VEGETABLE"),
                        "exotic[4]", equalTo(false)
                )
                .log().all()
        ;

        //clear test data
        given()
                .baseUri(baseUri)
                .cookies(cookies)
                .when()
                .post("api/data/reset")
                .then()
                .assertThat()
                .statusCode(200)
        ;
    }

}
