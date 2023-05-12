package user;

import api.stellarburgers.User;
import clientStellarBurgers.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;


public class UserCreatingTests {

    private String token;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Проверка создание уникального пользователя.")
    @Description("Регистрация уникального пользователя c корректными данными.")
    public void checkCreateUserTest() {
        Response response = UserClient.postCreateNewUser(new User("Izum", "izum9037yffj@yandex.ru", "121487567"));
        response.then().log().all().assertThat().statusCode(200).and().body("success", Matchers.is(true))
                .and().body("accessToken", Matchers.notNullValue())
                .and().body("refreshToken", Matchers.notNullValue())
                .and().body("user.email", Matchers.notNullValue())
                .and().body("user.name", Matchers.notNullValue());
    }

    @Test
    @DisplayName("Проверка создания пользователя, который уже зарегистрирован.")
    @Description("Регистрация уже зарегистрированного пользователя.")
    public void checkRegisteredUserTest() {
        User user = new User("Izum", "izum9037yffj@yandex.ru", "121487567");
        UserClient userClient = new UserClient();
        Response response = UserClient.postCreateNewUser(user);
        userClient.checkExistingAuthRegister(response);
    }

    @Test
    @DisplayName("Проверка создания пользователя без имени.")
    @Description("Регистрация поверка пользователя без имени, но с заполненными email и password")
    public void createUserWithoutNameTest() {
        User user = new User("", "izumizum@yandex.ru", "1214567");
        UserClient userClient = new UserClient();
        Response response = UserClient.postCreateNewUser(user);
        userClient.checkFailedResponseAuthRegister(response);
    }

    @Test
    @DisplayName("Проверка создания пользователя без email.")
    @Description("Регистрация поверка пользователя без email, но с заполненными именем и паролем")
    public void createUserWithoutEmailTest() {
        User user = new User("Izum", "", "1214567");
        UserClient userClient = new UserClient();
        Response response = UserClient.postCreateNewUser(user);
        userClient.checkFailedResponseAuthRegister(response);
    }

    @Test
    @DisplayName("Проверка создания пользователя без пароля.")
    @Description("Регистрация поверка пользователя без пароля, но с заполненными именем и email.")
    public void createUserWithoutPasswordTest() {
        User user = new User("Izum", "izum5454@yandex.ru", "");
        UserClient userClient = new UserClient();
        Response response = UserClient.postCreateNewUser(user);
        userClient.checkFailedResponseAuthRegister(response);
    }

    @Test
    @DisplayName("Проверка создания пользователя без имени и email.")
    @Description("Регистрация поверка пользователя без имени и email, но с заполненными password.")
    public void createUserWithoutNameAndEmailTest() {
        User user = new User("", "", "1214567");
        UserClient userClient = new UserClient();
        Response response = UserClient.postCreateNewUser(user);
        userClient.checkFailedResponseAuthRegister(response);
    }

    @Test
    @DisplayName("Проверка создания пользователя без имени и пароля.")
    @Description("Регистрация поверка пользователя без имени и пароля, но с заполненными email.")
    public void createUserWithoutNameAndPasswordTest() {
        User user = new User("", "izum5454@yandex.ru", "");
        UserClient userClient = new UserClient();
        Response response = UserClient.postCreateNewUser(user);
        userClient.checkFailedResponseAuthRegister(response);
    }

    @Test
    @DisplayName("Проверка создания пользователя без email и пароля.")
    @Description("Регистрация поверка пользователя без email и пароля, но с заполненными именем.")
    public void createUserWithoutEmailAndPasswordTest() {
        User user = new User("", "izum5454@yandex.ru", "");
        UserClient userClient = new UserClient();
        Response response = UserClient.postCreateNewUser(user);
        userClient.checkFailedResponseAuthRegister(response);
    }

    @Test
    @DisplayName("Проверка создания пользователя без всех полей.")
    @Description("Регистрация поверка пользователя без имени, email, пароля.")
    public void createUserWithoutNameAndEmailAndPasswordTest() {
        User user = new User("", "", "");
        UserClient userClient = new UserClient();
        Response response = UserClient.postCreateNewUser(user);
        userClient.checkFailedResponseAuthRegister(response);
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