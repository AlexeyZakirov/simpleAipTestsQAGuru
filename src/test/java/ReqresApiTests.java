import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class ReqresApiTests extends TestBase{
    private final Faker faker = new Faker();

    @DisplayName("Проверка количества пользователей на одной странице")
    @Test
    public void totalUsersOnPageTest(){
        int page = faker.number().numberBetween(1,2);
        given().queryParam("page",page)
                .get("/users")
                .then()
                .log().all()
                .statusCode(200)
                .body("data", hasSize(6));
    }

    @DisplayName("Проверка, что список пользователей пустой на не существующей странице")
    @Test
    public void emptyListUsersPageTest(){
        int page = faker.number().numberBetween(4,50);
        given().queryParam("page", page)
                .get("/users")
                .then()
                .log().all()
                .statusCode(200)
                .body("data", hasSize(0));
    }

    static Stream<Arguments> userNamesAndIdShouldBeEqualsTest(){
        return Stream.of(
                Arguments.of(4, "Eve", "Holt"),
                Arguments.of(9, "Tobias", "Funke")
        );
    }
    @MethodSource()
    @ParameterizedTest(name = "Проверка, что у пользователя с id = {0}, имя - {1}, фамилия {2}")
    public void userNamesAndIdShouldBeEqualsTest(int id, String firstName, String lastName){
        given().pathParam("id", id)
                .get("/users/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("data.id", equalTo(id))
                .body("data.first_name", equalTo(firstName))
                .body("data.last_name", equalTo(lastName));
    }

    @DisplayName("Проверка создания пользователя с именем и работой")
    @Test
    public void createUserWithNameAndJobTest(){
        String name = faker.name().firstName();
        String body = String.format("{\n" +
                "    \"name\": \"%s\",\n" +
                "    \"job\": \"leader\"\n" +
                "}", name);
        given().body(body)
                .contentType(ContentType.JSON)
                .post("/users")
                .then()
                .log().all()
                .statusCode(201)
                .body("name", equalTo(name));
    }

    @DisplayName("Проверка создания пользователя без указания работы")
    @Test
    public void createUserWithoutJobTest(){
        String name = faker.name().firstName();
        String body = String.format("{\n" +
                "    \"name\": \"%s\"\n" +
                "}", name);
        given().body(body)
                .contentType(ContentType.JSON)
                .post("/users")
                .then()
                .log().all()
                .statusCode(201)
                .body("name", equalTo(name));
    }

    @DisplayName("Проверка создания пользователя без указания имени")
    @Test
    public void createUserWithoutNameTest(){
        String body = "{\n" +
                "    \"job\": \"leader\"\n" +
                "}";
        given().body(body)
                .contentType(ContentType.JSON)
                .post("/users")
                .then()
                .log().all()
                .statusCode(201)
                .body("job", equalTo("leader"));
    }
}
