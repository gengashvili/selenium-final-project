import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import util.FindMovie;
import util.Helper;
import util.Register;

import java.util.List;

public class Main {
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;
    private Helper helper;
    private FindMovie findMovie;


    @BeforeMethod
    @Parameters("browser")
    public void setUp(@Optional("chrome") String browser) throws Exception {

        if (browser.equalsIgnoreCase("chrome")) {

            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();

        } else if (browser.equalsIgnoreCase("edge")) {

            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver();

        } else {
            throw new Exception("Invalid browser name: " + browser);
        }

        driver.manage().window().maximize();

        this.wait = new WebDriverWait(driver, 25); // 25 წამი დიდი დრო ჩანს, მაგრამ საიტი ნელა მუშაობს:)
        this.actions = new Actions(driver);
        this.helper = new Helper(driver,wait);
        this.findMovie = new FindMovie(driver, wait, actions);
    }


    @Test
    public void testSwoop() {
            driver.get("https://www.swoop.ge/");

            helper.clickElement(By.linkText("კინო"));

            // ეს მოძების ისეთ ფილმს რომლის კინოეთეატრების ჩამონათვალში კავეა ისთ ფოინთია და დააბრუნებს ზოგადად კინოთეატრების wrapper ელემენტს სამომავლო ინტერაქციისთვის
            WebElement cinemasContainer = findMovie.byCinemaName("კავეა ისთ ფოინთი");

            helper.clickElement(By.className("acceptCookie"));

            List<WebElement> cinemasOptions = cinemasContainer.findElements(By.xpath("./div[@aria-hidden = 'false']/div/div[@aria-hidden = 'false']"));

            //Check that only ‘კავეა ისთ ფოინთი’ options are returned
            for (WebElement cinemaOption : cinemasOptions) {
                String cinemaTitle = cinemaOption.findElement(By.xpath("./a/p[contains(@class, 'cinema-title')]")).getText();
                Assert.assertEquals(cinemaTitle, "კავეა ისთ ფოინთი");
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
            Assert.assertEquals(movieTitle, popUpMovieTitle, "movieTitle and popUpMovieTitle are not equal");
            Assert.assertEquals(cinemaTitle, popUpCinemaTitle, "cinemaTitle and popUpCinemaTitle are not equal");
            Assert.assertEquals(helper.splitDate(dateTime), helper.splitDate(popUpDateTime), "dateTime and popUoDateTime are not equal");


            List<WebElement> vacantPlaces = driver.findElements(By.xpath("//div[@class = 'seat free']"));
            vacantPlaces.get(0).click();

            helper.clickElement(By.className("register"));

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("registertabs")));

            Register register = new Register(driver);

            register.inputText(By.id("pFirstName"), "giorgi"); //enter first name
            register.inputText(By.id("pLastName"), "gengashvili"); //enter last name
            register.inputText(By.id("pEmail"), "wrong email"); //enter email
            register.inputText(By.id("pPhone"), "25 60 60"); //enter phone number
            register.inputText(By.id("pDateBirth"), "23-08-2002"); //enter birthday date
            register.selectDropDown(By.id("pGender"),"1"); //choose gender
            register.inputText(By.id("pPassword"), "password"); //enter password
            register.inputText(By.id("pConfirmPassword"), "password"); //enter confirm password

            register.clickButton(By.id("pIsAgreeTerns"));
            register.clickButton(By.xpath("//input[@type = 'button' and @value = 'რეგისტრაცია']"));

            //check that error message ‘მეილის ფორმატი არასწორია!' is appear
            register.checkErrorMesssage();


    }


    @AfterMethod
    public void tearDown() {
        driver.quit();
    }
}
