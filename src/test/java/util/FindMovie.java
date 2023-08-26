package util;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Objects;

public class FindMovie {
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;
    private JavascriptExecutor js;

    public FindMovie(WebDriver driver, WebDriverWait wait, Actions actions) {
        this.driver = driver;
        this.wait = wait;
        this.actions = actions;
        this.js = (JavascriptExecutor) driver;
    }

    public WebElement byCinemaName(String cinemaName) {
        List<WebElement> moviesList;
        WebElement movie;
        WebElement buyButton;
        WebElement cinemasContainer = null;
        List<WebElement> cinemas;


        boolean foundCaveaEastPoint = false;
        int movieIndex = 0;
        while (!foundCaveaEastPoint) {
            moviesList = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@class='movies-deal']")));
            movie = moviesList.get(movieIndex);

            wait.until(ExpectedConditions.visibilityOf(movie));
            actions.moveToElement(movie).perform();

            buyButton = movie.findElement(By.xpath(".//div[@class='cinema-hover']/a[div[@class='info-cinema-ticket']]"));
            wait.until(ExpectedConditions.elementToBeClickable(buyButton));
            buyButton.click();

            cinemasContainer = driver.findElement(By.xpath("//div[contains(@class, 'all-cinemas')]"));
            cinemas = cinemasContainer.findElements(By.xpath("./ul[contains(@class, 'cinema-tabs')]/li"));

            for (WebElement cinema : cinemas) {
                if (Objects.equals(cinema.getText(), "კავეა ისთ ფოინთი")) {
                    js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'end', inline: 'end'});", cinema);
                    cinema.click();
                    foundCaveaEastPoint = true;
                    break;
                }
            }

            if (!foundCaveaEastPoint) {
                driver.navigate().back();
                movieIndex++;
            }

        }

        return cinemasContainer;
    }

}

