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
import util.Helper;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        String movieTitle = driver.findElement(By.xpath("//div[@class = 'movie_first_section']/div[@class = 'info']/p[@class = 'name']")).getText();
        String cinemaTitle = lastCinemaOption.findElement(By.xpath("./a/p[contains(@class, 'cinema-title')]")).getText();
        String dateTime = lastSeanseDate.getText();

        lastCinemaOption.click();

        WebElement popUpMovieInfo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'right-content']/div[@class = 'content-header']")));
        String popUpMovieTitle = popUpMovieInfo.findElement(By.xpath("./p[@class = 'movie-title']")).getText();
        String popUpCinemaTitle = popUpMovieInfo.findElement(By.xpath("./p[@class = 'movie-cinema']")).getText();
        String popUpDateTime = popUpMovieInfo.findElement(By.xpath("./p[@class = 'movie-cinema'][2]")).getText();

        // Check in opened popup that movie name, cinema and datetime is valid
        Assert.assertEquals(movieTitle,popUpMovieTitle, "movieTitle and popUpMovieTitle are not equal");
        Assert.assertEquals(cinemaTitle,popUpCinemaTitle, "cinemaTitle and popUpCinemaTitle are not equal");
        Assert.assertEquals(Helper.splitDate(dateTime),Helper.splitDate(popUpDateTime), "dateTime and popUoDateTime are not equal");

    }


    @AfterMethod
    public void tearDown() {
        //driver.quit();
    }
}
