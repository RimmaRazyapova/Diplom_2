package order;

import api.stellarburgers.Ingredient;
import api.stellarburgers.Ingredients;
import api.stellarburgers.User;
import api.stellarburgers.Order;
import clientStellarBurgers.OrderClient;
import clientStellarBurgers.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.List;

public class OrderCreatingTests {
    private OrderClient orderClient;
    User user = new User("Izum", "izum90437yffj@yandex.ru", "121487567");
    private UserClient userClient;
    private String accessToken;

    private List<String> ingredient;
    private Order order;
    private String token;


    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        userClient = new UserClient();
        orderClient = new OrderClient();
        Response response = UserClient.postCreateNewUser(user);
        accessToken = userClient.checkRequestAuthLogin(user).then().extract().path("accessToken");
        ingredient = new ArrayList<>();
        order = new Order(ingredient);
    }


    @Test
    @DisplayName("Создание заказа без авторизации.")
    @Description("Создание заказа без авторизации. Проверка успешного ответа от сервера.")
    public void createOrderWithoutAuthorizationTest() {
        Ingredients ingredients = orderClient.getIngredient();
        ingredient.add(ingredients.getData().get(1).get_id());
        ingredient.add(ingredients.getData().get(2).get_id());
        ingredient.add(ingredients.getData().get(3).get_id());
        Response response = OrderClient.createOrderWithoutAuthorization(order);
        response.then().log().all()
                .assertThat().statusCode(200).and().body("success", Matchers.is(true))
                .and().body("name", Matchers.notNullValue())
                .and().body("order.number", Matchers.any(Integer.class));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов и без авторизации")
    @Description("Создание заказа без ингредиентов и без авторизации. Проверка неуспешного ответа от сервера.")
    public void createEmptyOrderWithoutAuthorization() {
        Response response = orderClient.createOrderWithoutAuthorization(order);
        orderClient.checkFailedResponseApiOrders(response);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов с авторизацией.")
    @Description("Создание заказа без ингредиентов с авторизацией. Проверка неуспешного ответа от сервера.")
    public void createEmptyOrderWithAuthorization() {
        Response response = orderClient.createOrderWithAuthorization(order, accessToken);
        orderClient.checkFailedResponseApiOrders(response);
    }

    @Test
    @DisplayName("Создание заказа без авторизации с неверным хэшем ингредиентов.")
    @Description("Создание заказа без авторизации с неверным хэшем ингредиентов. Проверка ошибки сервера.")
    public void createOrderWithoutAuthorizationWithWrongHashTest() {
        Ingredients ingredients = orderClient.getIngredient();
        ingredient.add(ingredients.getData().get(0).get_id() + "1234nhf8");
        ingredient.add(ingredients.getData().get(8).get_id() + "9876bgfj2");
        Response response = orderClient.createOrderWithoutAuthorization(order);
        response.then().log().all()
                .statusCode(500);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией с неверным хешем ингредиентов.")
    @Description("Создание заказа с авторизацией с неверным хешем ингредиентов. Проверка ошибки сервера.")
    public void createOrderWithAuthorizationWithWrongHashTest() {
        Ingredients ingredients = orderClient.getIngredient();
        ingredient.add(ingredients.getData().get(1).get_id() + "1234nhf8");
        ingredient.add(ingredients.getData().get(2).get_id() + "9876bgfj2");
        Response response = orderClient.createOrderWithAuthorization(order, accessToken);
        response.then().log().all()
                .statusCode(500);
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
