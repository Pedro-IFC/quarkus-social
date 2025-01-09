package io.github.pedro.ifc.quarkussocial.rest;

import io.github.pedro.ifc.quarkussocial.rest.dto.CreateUserRequest;
import io.github.pedro.ifc.quarkussocial.rest.dto.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {
    @TestHTTPResource("/users")
    URL apiURL;

    @Test
    @DisplayName("Should create an user successfully")
    @Order(1)
    public void createUserTest(){
         Response response = given()
                 .contentType(ContentType.JSON)
                 .body("{\"name\":\"Egor\", \"age\": 19}")
             .when()
                     .post(apiURL)
             .then()
                     .extract().response();

         assertEquals(201, response.statusCode());
         assertNotNull(response.jsonPath().getString("id"));
    }

    @Test
    @DisplayName("Should create an user with error when json is not valid")
    @Order(2)
    public void createUserValidationErrorTest(){
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post(apiURL)
                .then()
                .extract().response();

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
    }


    @Test
    @DisplayName("Should list all users")
    @Order(3)
    public void listAllUsersTest() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get(apiURL)
        .then()
            .statusCode(200)
            .body("size()", Matchers.is(1));
    }
}