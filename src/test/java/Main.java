import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Objects;

public class Main {
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        this.driver = new ChromeDriver();
        driver.manage().window().maximize();

        this.wait = new WebDriverWait(driver, 15);
        this.actions = new Actions(driver);
    }

    @Test
    public void testSwoop() {
        driver.get("https://www.swoop.ge/");

        WebElement movieLink = driver.findElement(By.linkText("კინო"));
        movieLink.click();

        List<WebElement> moviesList;
        WebElement firstMovie;
        WebElement buyButton;
        WebElement cinemasContainer = null;
        List<WebElement> cinemas;


        boolean foundCaveaEastPoint = false;
        int movieIndex = 0;
        while (!foundCaveaEastPoint) {
            moviesList = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@class='movies-deal']")));
            firstMovie = moviesList.get(movieIndex);

            wait.until(ExpectedConditions.visibilityOf(firstMovie));
            actions.moveToElement(firstMovie).perform();

            buyButton = firstMovie.findElement(By.xpath(".//div[@class='cinema-hover']/a[div[@class='info-cinema-ticket']]"));
            wait.until(ExpectedConditions.elementToBeClickable(buyButton));
            buyButton.click();

            cinemasContainer = driver.findElement(By.xpath("//div[contains(@class, 'all-cinemas')]"));
            cinemas = cinemasContainer.findElements(By.xpath("./ul[contains(@class, 'cinema-tabs')]/li"));

            for (WebElement cinema: cinemas) {
                if (Objects.equals(cinema.getText(), "კავეა ისთ ფოინთი")) {
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

        WebElement cookieButton = driver.findElement(By.className("acceptCookie"));
        cookieButton.click();

        List<WebElement> cinemasOptions = cinemasContainer.findElements(By.xpath("./div[@aria-hidden = 'false']/div/div[@aria-hidden = 'false']"));

        //Check that only ‘კავეა ისთ ფოინთი’ options are returned
        for (WebElement cinemaOption : cinemasOptions) {
            String cinemaTitle = cinemaOption.findElement(By.xpath("./a/p[contains(@class, 'cinema-title')]")).getText();
            Assert.assertEquals(cinemaTitle,"კავეა ისთ ფოინთი");
        }

        List<WebElement> seanseDates = cinemasContainer.findElements(By.xpath("./div[@aria-hidden = 'false']/div/ul[@role = 'tablist']/li"));

        WebElement lastSeanseDate = seanseDates.get(seanseDates.size() - 1);
        lastSeanseDate.click();

        // div - ები იცვლება თარიღის ცვლილებასთან ერთად და  lastSeanseDate.click() - შემდეგ სიას განახლება სჭირდება რადგან ვალიდური ელემენტები წამოიღოს
        cinemasOptions = cinemasContainer.findElements(By.xpath("./div[@aria-hidden = 'false']/div/div[@aria-hidden = 'false']"));

        WebElement lastCinemaOption = cinemasOptions.get(cinemasOptions.size() - 1);
        lastCinemaOption.click();




    }

    @AfterMethod
    public void tearDown() {
        //driver.quit();
    }
}
