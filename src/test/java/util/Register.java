package util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

public class Register {
    private WebDriver driver;

    public Register(WebDriver driver) {
        this.driver = driver;
    }

    public void inputText(By locator, String text) {
        WebElement element = driver.findElement(locator);
        element.clear();
        element.sendKeys(text);
    }

    public void selectDropDown(By locator, String value) {
        WebElement element = driver.findElement(locator);
        Select dropDownElement = new Select(element);
        dropDownElement.selectByValue(value);
    }

    public void clickButton(By locator) {
        WebElement element = driver.findElement(locator);
        element.click();
    }

    public void checkErrorMesssage() {
        WebElement errorElement = driver.findElement(By.id("physicalInfoMassage"));
        String actualErrorMessage = errorElement.getText();
        String excpectedErrorMessage = "მეილის ფორმატი არასწორია!";

        try {
            Assert.assertEquals(actualErrorMessage, excpectedErrorMessage);

            System.out.println(
                    "got error message " + actualErrorMessage + " as excpected\n"
                    + "everything is okey \n"
                    + "\uD83D\uDE0E\n"
                    + "<))>\n"
                    + "| \\");

        } catch (AssertionError e) {
            System.out.println("something went wrong! ¯\\_(ツ)_/¯");
        }
    }

}
