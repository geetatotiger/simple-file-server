package org.simple.file.server.boxtest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import java.io.File;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileServerBoxTest {

    private final static String BASE_URI = "http://localhost";

    @LocalServerPort
    protected int serverPort = 0;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = serverPort;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void fileServerBoxTest() {
        //given file
        String filename = "boxtest.txt";
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filename).getFile());

        //upload to the file server
        given()
                .multiPart(file)
                .when()
                .post("/file")
                .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.TEXT)
                .header("Location", containsString(filename))
                .body(containsString("File uploaded successfully"));

        //Get the file uploaded
        byte[] result = given()
                .when()
                .get("/file/{filename}", filename)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().asByteArray();
        assertThat(result.length).isEqualTo(file.length());

        //getList of files uploaded
        given()
                .when()
                .get("/files")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasItem(filename));

        //delete file
        given()
                .when()
                .delete("/file/{filename}", filename)
                .then()
                .statusCode(HttpStatus.OK.value());

    }
}
