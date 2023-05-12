package user;

import api.stellarburgers.User;
import clientStellarBurgers.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.hamcrest.Matchers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LoginUserTests {

    private String token;

    @Before
    public void setUp() {
            RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }


    @Test
    @DisplayName("Авторизация пользователя.")
    @Description("Авторизация пользователя под существующем логином")
    public void authorizationTest() {
        Response response = UserClient.checkRequestAuthLogin(new User("izum9043@yandex.ru", "1214567"));
        response.then().log().all().assertThat().statusCode(200).and().body("success", Matchers.is(true))
                .and().body("accessToken", Matchers.notNullValue())
                .and().body("refreshToken", Matchers.notNullValue())
                .and().body("user.email", Matchers.notNullValue())
                .and().body("user.name", Matchers.notNullValue());
    }

    @Test
    @DisplayName("Авторизация с неверным логином.")
    @Description("Авторизация пользователя c некорректным логином.")
    public void authorizationIncorrectLoginTest() {
        User user = new User("izum77@yandex.ru", "1214567");
        UserClient userClient = new UserClient();
        Response response = UserClient.checkRequestAuthLogin(user);
        userClient.checkFailedResponseAuthLogin(response);
    }

    @Test
    @DisplayName("Авторизация с неверным паролем.")
    @Description("Авторизация пользователя c некорректным паролем.")
    public void authorizationIncorrectPasswordTest() {
        User user = new User("izum777@yandex.ru", "121456");
        UserClient userClient = new UserClient();
        Response response = UserClient.checkRequestAuthLogin(user);
        userClient.checkFailedResponseAuthLogin(response);
    }

    @Test
    @DisplayName("Авторизация без логина.")
    @Description("Авторизация пользователя без логина.")
    public void authorizationWithoutLoginTest() {
        User user = new User("", "121456");
        UserClient userClient = new UserClient();
        Response response = UserClient.checkRequestAuthLogin(user);
        userClient.checkFailedResponseAuthLogin(response);
    }

    @Test
    @DisplayName("Авторизация без пароля.")
    @Description("Авторизация пользователя без пароля")
    public void authorizationWithoutPasswordTest() {
        User user = new User("izum777@yandex.ru", "");
        UserClient userClient = new UserClient();
        Response response = UserClient.checkRequestAuthLogin(user);
        userClient.checkFailedResponseAuthLogin(response);
    }

    @Test
    @DisplayName("Авторизация без логина пароля.")
    @Description("Авторизация пользователя без пароля")
    public void authorizationWithoutLoginAndPasswordTest() {
        User user = new User("", "");
        UserClient userClient = new UserClient();
        Response response = UserClient.checkRequestAuthLogin(user);
        userClient.checkFailedResponseAuthLogin(response);
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
