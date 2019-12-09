package appium_example;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;

import static io.appium.java_client.touch.offset.PointOption.point;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.id;
import static org.openqa.selenium.By.xpath;


public class Tests {

    public static AndroidDriver<MobileElement> dr;

    public static final int IMPLICITY_WAIT = 5;

    @BeforeAll
    static void startDriver() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "9.0.0");
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Pixel 2 API 29");
        capabilities.setCapability(MobileCapabilityType.UDID, "emulator-5554");
        capabilities.setCapability(MobileCapabilityType.APP,"https://raw.githubusercontent.com/afollestad/material-dialogs/master/sample/sample.apk");
        try {
            dr = new AndroidDriver<>(new URL("http://0.0.0.0:4723/wd/hub"), capabilities);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        dr.manage().timeouts().implicitlyWait(IMPLICITY_WAIT, TimeUnit.SECONDS);
    }

    @AfterAll
    static void tearDown() {
        dr.quit();
    }

    @BeforeEach
    void resetApp() {
        dr.resetApp();
    }


    @Test
    void switchTheme() {
        dr.findElementByClassName("android.widget.ImageView").click();
        dr.findElement(xpath("//*[@text='Dark Theme']")).click();
        dr.findElementByClassName("android.widget.ImageView").click();
        dr.findElement(xpath("//*[@text='Custom Theme']")).click();
        dr.findElementByClassName("android.widget.ImageView").click();
        dr.findElement(xpath("//*[@text='Light Theme']")).click();
    }

    @Test
    void scrollDown() {
        TouchAction action = new TouchAction(dr);
        int swipeCount = 10;
        int xOffset = (int) (getScreenWidth() * 0.5);
        int startYOffset = (int) (getScreenHeight() * 0.8);
        int endYOffset = (int) (getScreenHeight() * 0.2);
        while (!isElementPresent(xpath("//*[@text='DATETIME PICKER']"))) {
            action.press(point(xOffset, startYOffset))
                    .waitAction()
                    .moveTo(point(xOffset, endYOffset))
                    .release()
                    .perform();
            Assertions.assertNotEquals(0, swipeCount, "Последний элемент не найден, количество свайпов больше 10");
            swipeCount--;
        }
    }


    @Test
    void basicButtonsAgreeDisagreeLocation() {
        dr.findElement(xpath("//*[@text='BASIC + BUTTONS']")).click();
        dr.findElementById("com.afollestad.materialdialogssample:id/md_button_negative").click();
        assertTrue(isElementNotPresent(id("com.afollestad.materialdialogssample:id/md_root"), 2));
        dr.findElement(xpath("//*[@text='BASIC + BUTTONS']")).click();
        dr.findElementById("com.afollestad.materialdialogssample:id/md_button_positive").click();
        assertTrue(isElementNotPresent(id("com.afollestad.materialdialogssample:id/md_root"), 2));
    }


    @Test
    void basicIconButtonsСheckDialog() {
        dr.findElement(xpath("//*[@text='BASIC + ICON + BUTTONS']")).click();
        By icon = id("com.afollestad.materialdialogssample:id/md_icon_title");
        By title = id("com.afollestad.materialdialogssample:id/md_text_title");
        By agreeButton = id("com.afollestad.materialdialogssample:id/md_button_positive");
        By disagreeButton = id("com.afollestad.materialdialogssample:id/md_button_negative");
        String titleText = dr.findElement(title).getText();
        String expectedTitleText = "Use Google's Location Services?";
        assertAll(
                () -> assertTrue(isElementPresent(icon), "Иконка отсутствует"),
                () -> assertEquals(expectedTitleText, titleText, "Текст заголовка отличается от ожидаемого"),
                () -> assertTrue(isElementPresent(agreeButton)),
                () -> assertTrue(isElementPresent(disagreeButton))
        );
        dr.findElement(agreeButton).click();
    }

    @Test
    void basicTitleCheckboxButtonsAcceptDialog() {
        dr.findElement(xpath("//*[@text='BASIC + TITLE + CHECKBOX + BUTTONS']")).click();
        By title = id("com.afollestad.materialdialogssample:id/md_text_title");
        String titleText = dr.findElement(title).getText();
        String expectedTitleText = "Use Google's Location Services?";
        assertEquals(expectedTitleText, titleText, "Текст заголовка отличается от ожидаемого");
        dr.findElementByClassName("android.widget.CheckBox").click();
        dr.findElement(id("com.afollestad.materialdialogssample:id/md_button_positive")).click();
        assertTrue(isElementNotPresent(id("com.afollestad.materialdialogssample:id/md_root"), 2));
    }

    @Test
    void listLongSelectWyoming() {
        By listLong = xpath("//*[@text='LIST LONG']");
        By wyoming = xpath("//*[@text='Wyoming']");
        swipeToElement(listLong, 4);
        dr.findElement(listLong).click();
        swipeToElement(wyoming, 10);
        dr.findElement(wyoming).click();
        assertTrue(isElementNotPresent(id("com.afollestad.materialdialogssample:id/md_recyclerview_content"), 2));
    }

    @Test
    void multipleChoiceButtonsSelectAllElements() {
        By multipleChoice = xpath("//*[@text='MULTIPLE CHOICE + BUTTONS']");
        swipeToElement(multipleChoice, 4);
        dr.findElement(multipleChoice).click();
        List<MobileElement> socialNetworks = dr.findElements(className("android.widget.CheckBox"));
        assertEquals(4 , socialNetworks.size(), "В списке должно быть 4 социальные сети");
        for (MobileElement socialNetwork : socialNetworks) {
            if (!Boolean.parseBoolean(socialNetwork.getAttribute("checked"))) socialNetwork.click();
            assertTrue(Boolean.parseBoolean(socialNetwork.getAttribute("checked")));
        }
        for (MobileElement socialNetwork : socialNetworks) {
            if (Boolean.parseBoolean(socialNetwork.getAttribute("checked"))) socialNetwork.click();
            assertFalse(Boolean.parseBoolean(socialNetwork.getAttribute("checked")));
        }
        socialNetworks.get(1).click();
        dr.findElement(id("com.afollestad.materialdialogssample:id/md_button_positive")).click();
        assertTrue(isElementNotPresent(id("com.afollestad.materialdialogssample:id/md_recyclerview_content"), 2));
    }

    @Test
    void inputSetValue() {
        By input = xpath("//*[@text='INPUT']");
        swipeToElement(input, 6);
        dr.findElement(input).click();
        dr.findElement(id("com.afollestad.materialdialogssample:id/md_input_message"))
                .setValue("TEST MESSAGE");
        dr.findElement(id("com.afollestad.materialdialogssample:id/md_button_positive")).click();
        assertTrue(isElementNotPresent(id("com.afollestad.materialdialogssample:id/md_root"), 2));
    }

    @Test
    void inputSetAndRemoveValue() {
        By input = xpath("//*[@text='INPUT']");
        swipeToElement(input, 6);
        dr.findElement(input).click();
        MobileElement inputMessage = dr.findElement(id("com.afollestad.materialdialogssample:id/md_input_message"));
        inputMessage.setValue("TEST MESSAGE");
        inputMessage.clear();
        dr.findElementById("com.afollestad.materialdialogssample:id/md_button_negative").click();
        assertTrue(isElementNotPresent(id("com.afollestad.materialdialogssample:id/md_root"), 2));
    }

    @Test
    void timePickerSetTime() {
        int setHour = 13;
        int setMinutes = 45;
        By timePicker = xpath("//*[@text='TIME PICKER']");
        swipeToElement(timePicker, 8);
        dr.findElement(timePicker).click();
        dr.findElements(className("android.widget.RadialTimePickerView$RadialPickerTouchHelper"))
                .get(setHour)
                .click();
        dr.findElement(xpath("//*[@content-desc='" + setMinutes + "']")).click();
        dr.findElement(id("com.afollestad.materialdialogssample:id/md_button_positive")).click();
        assertTrue(isElementPresent(xpath("//*[@text='Selected time: " + setHour + ":" + setMinutes +" PM']")));
    }

    public void swipeToElement(By element, int swipeCountLimit) {
        TouchAction action = new TouchAction(dr);
        int swipeCount = swipeCountLimit;
        int xOffset = (int) (getScreenWidth() * 0.5);
        int startYOffset = (int) (getScreenHeight() * 0.8);
        int endYOffset = (int) (getScreenHeight() * 0.2);
        while (!isElementPresent(element)) {
            action.press(point(xOffset, startYOffset))
                    .waitAction()
                    .moveTo(point(xOffset, endYOffset))
                    .release()
                    .perform();
            Assertions.assertNotEquals(0, swipeCount, "Последний элемент не найден, количество свайпов больше " + swipeCountLimit);
            swipeCount--;
        }

    }

    public int getScreenWidth() {
        return dr.manage().window().getSize().getWidth();
    }

    public int getScreenHeight() {
        return dr.manage().window().getSize().getHeight();
    }

    public static boolean isElementPresent(By locator) {
        implicitlyWait(1);
        boolean value = dr.findElements(locator).size() > 0;
        implicitlyWait(IMPLICITY_WAIT);
        return value;
    }

    public static boolean isElementNotPresent(By locator, int waitTimeInSeconds) {
        int waitTime = waitTimeInSeconds * 1000;
        long timer = System.currentTimeMillis() + waitTime;
        while (System.currentTimeMillis() < timer) {
            if (!isElementPresent(locator)) return !isElementPresent(locator);
            sleepMs(250);
        }
        return !isElementPresent(locator);
    }

    public static void implicitlyWait(int seconds) {
        dr.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
    }
    
    public static void sleep(int seconds) {
        sleepMs(seconds * 1000);
    }

    public static void sleepMs(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
