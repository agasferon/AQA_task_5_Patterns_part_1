package ru.netology;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class RegistrationTest {
    RandomiseDate randomiseDate = new RandomiseDate();
    public User randomUser;

    @BeforeEach
    void setUpAll() {
        randomUser = CreateUser.generateUser();
    }

    @Test
    void shouldSendIfAllFieldIsCorrect() {
        open("http://localhost:9999");
        $("[data-test-id='city'] input").setValue(randomUser.getCity());
        $("[data-test-id='date'] input").sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        String bookingDate = randomiseDate.getDateIncrement();
        $("[data-test-id='date'] input").setValue(bookingDate);
        $("[data-test-id='name'] input").setValue(randomUser.getName());
        $("[data-test-id='phone'] input").setValue(randomUser.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Запланировать")).click();
        $(withText("Успешно")).waitUntil(visible, 15000);
        $("[data-test-id=success-notification]").shouldHave(text("Встреча успешно запланирована на"));
        $("[data-test-id=success-notification]").shouldHave(text(bookingDate));
    }

    @Test
    void shouldReplanIfChangeDate() {
        open("http://localhost:9999");
        $("[data-test-id='city'] input").setValue(randomUser.getCity());
        $("[data-test-id='date'] input").sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        String bookingDate = randomiseDate.getDateIncrement();
        $("[data-test-id='date'] input").setValue(bookingDate);
        $("[data-test-id='name'] input").setValue(randomUser.getName());
        $("[data-test-id='phone'] input").setValue(randomUser.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Запланировать")).click();
        $(withText("Успешно")).waitUntil(visible, 15000);
        $("[data-test-id=success-notification]").shouldHave(text("Встреча успешно запланирована на"));
        $("[data-test-id=success-notification]").shouldHave(text(bookingDate));
        $("[data-test-id='date'] input").sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        String newBookingDate = randomiseDate.getDateIncrement();
        $("[data-test-id='date'] input").setValue(newBookingDate);
        $$("button").find(exactText("Запланировать")).click();
        $(withText("У вас уже запланирована встреча на другую дату. Перепланировать?")).waitUntil(visible, 15000);
        $$("button").find(exactText("Перепланировать")).click();
        $("[data-test-id=success-notification]").shouldHave(text("Встреча успешно запланирована на"));
        $("[data-test-id=success-notification]").shouldHave(text(newBookingDate));
    }

    @Test
    void shouldNotSendIfCityIsNotAvailable() {
        open("http://localhost:9999");
        $("[data-test-id='city'] input").setValue("Сызрань");
        $("[data-test-id='date'] input").sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        $("[data-test-id='date'] input").setValue(randomiseDate.getDateIncrement());
        $("[data-test-id='name'] input").setValue(randomUser.getName());
        $("[data-test-id='phone'] input").setValue(randomUser.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Запланировать")).click();
        $(".input_invalid[data-test-id='city']").shouldHave(text("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldNotSendIfNameIsNotCorrect() {
        open("http://localhost:9999");
        $("[data-test-id='city'] input").setValue(randomUser.getCity());
        $("[data-test-id='date'] input").sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        $("[data-test-id='date'] input").setValue(randomiseDate.getDateIncrement());
        $("[data-test-id='name'] input").setValue("Chuck Norris");
        $("[data-test-id='phone'] input").setValue(randomUser.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Запланировать")).click();
        $(".input_invalid[data-test-id='name']").shouldHave(text("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    void shouldNotSendIfPhoneNumberIsNotCorrect() {
        open("http://localhost:9999");
        $("[data-test-id='city'] input").setValue(randomUser.getCity());
        $("[data-test-id='date'] input").sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        $("[data-test-id='date'] input").setValue(randomiseDate.getDateIncrement());
        $("[data-test-id='name'] input").setValue(randomUser.getName());
        $("[data-test-id='phone'] input").setValue("+7123");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Запланировать")).click();
        $(".input_invalid[data-test-id='phone']").shouldHave(text("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void shouldNotSendIfWithoutAgreement() {
        open("http://localhost:9999");
        $("[data-test-id='city'] input").setValue(randomUser.getCity());
        $("[data-test-id='date'] input").sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        $("[data-test-id='date'] input").setValue(randomiseDate.getDateIncrement());
        $("[data-test-id='name'] input").setValue(randomUser.getName());
        $("[data-test-id='phone'] input").setValue(randomUser.getPhone());
        String previousColor = $("[data-test-id='agreement']").getCssValue("color");
        $$("button").find(exactText("Запланировать")).click();
        String invalidColor = $(".input_invalid[data-test-id='agreement']").getCssValue("color");
        assertNotEquals(previousColor, invalidColor);
    }

    @Test
    void shouldSendIfDataIsNotCorrect() {
        open("http://localhost:9999");
        $("[data-test-id='city'] input").setValue(randomUser.getCity());
        $("[data-test-id='date'] input").sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        $("[data-test-id='date'] input").setValue(randomiseDate.getTomorrowDate());
        $("[data-test-id='name'] input").setValue(randomUser.getName());
        $("[data-test-id='phone'] input").setValue(randomUser.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Запланировать")).click();
        $(".input_invalid .input__sub").shouldHave(text("Заказ на выбранную дату невозможен"));
    }
}