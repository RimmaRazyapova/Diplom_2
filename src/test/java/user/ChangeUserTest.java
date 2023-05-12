package user;

import api.stellarburgers.User;
import clientStellarBurgers.UserClient;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;


public class ChangeUserTest {


    User user = new User("Izum", "izum90437yffj@yandex.ru", "121487567");
    UserClient userClient = new UserClient();
    private String accessToken;
    private final String modifiedName = "DGHsan";
    private final String modifiedEmail = "DGHann@yandex.ru";
    private final String modifiedPassword = "1645sv";
    private String token;


    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }


    @Test
    @DisplayName("Изменение имени пользователя с авторизацией.")
    @Description("Успешное изменение имени пользователя с авторизацией.")
    public void changeUserNameWithAuthorizationTest() {
        user.setName(modifiedName);
        Response response = userClient.sendPatchRequestWithAuthorizationApiAuthUser(user, accessToken);
        response.then().log().all().assertThat().statusCode(200).and().body("success", Matchers.is(true));
    }

    @Test
    @DisplayName("Изменение email пользователя с авторизацией.")
    @Description("Успешное изменение email пользователя с авторизацией.")
    public void changeUserEmailWithAuthorizationTest() {
        user.setEmail(modifiedEmail);
        Response response = userClient.sendPatchRequestWithAuthorizationApiAuthUser(user, accessToken);
        response.then().log().all().assertThat().statusCode(200).and().body("success", Matchers.is(true));
    }

    @Test
    @DisplayName("Изменение пароля пользователя с авторизацией.")
    @Description("Успешное изменение пароля пользователя с авторизацией.")
    public void changeUserPasswordWithAuthorizationTest() {
        user.setPassword(modifiedPassword);
        Response response = userClient.sendPatchRequestWithAuthorizationApiAuthUser(user, accessToken);
        userClient.checkSuccessResponseAuthUser(response, modifiedEmail, modifiedPassword);
    }

    @Test
    @DisplayName("Изменение имени пользователя без авторизации.")
    @Description("Проверка изменения имени без авторизации.")
    public void checkUpdateNameNotAuthUser() {
        accessToken = "";
        user.setName(modifiedName);
        Response response = userClient.sendPatchRequestWithoutAuthorizationApiAuthUser(user);
        userClient.checkFailedResponseAuthUser(response);
    }

    @After
    public void tearDown() {
        // Удаление созданного пользователя с помощью API
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete(baseURI + "/api/auth/user");
    }
}
