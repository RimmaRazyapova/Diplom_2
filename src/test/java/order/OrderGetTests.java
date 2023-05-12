package order;

import api.stellarburgers.User;
import clientStellarBurgers.OrderClient;
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

public class OrderGetTests {
    private OrderClient orderClient;
    private String token;
    User user = new User("Izum", "izum9043@yandex.ru", "1214567");
    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        userClient = new UserClient();
        orderClient = new OrderClient();
        Response response = UserClient.postCreateNewUser(user);
        accessToken = userClient.checkRequestAuthLogin(user).then().extract().path("accessToken");
    }

    @Test
    @DisplayName("Получение списка заказов авторизованный пользователь.")
    @Description("Успешная проверка получение списка заказов авторизованного пользователя.")
    public void getUserOrderWithAuthorizationTest() {
        Response response = given().log().all()
                .header("Content-Type", "application/json")
                .header("authorization", accessToken)
                .when()
                .get("/api/orders");
        response.then().log().all()
                .assertThat().statusCode(200).and().body("success", Matchers.is(true))
                .and().body("orders", Matchers.notNullValue())
                .and().body("total", Matchers.any(Integer.class))
                .and().body("totalToday", Matchers.any(Integer.class));
    }

    @Test
    @DisplayName("Получение списка заказов без авторизации.")
    @Description("Неуспешная проверка получение списка заказов без авторизации.")
    public void getUserOrderWithoutAuthorizationTest() {
        Response response = given().log().all()
                .header("Content-Type", "application/json")
                .when()
                .get("/api/orders");
        response.then().log().all()
                .assertThat().statusCode(401).and().body("success", Matchers.is(false))
                .and().body("message", Matchers.is("You should be authorised"));
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
