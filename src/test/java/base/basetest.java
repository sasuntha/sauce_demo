package base;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import io.github.bonigarcia.wdm.WebDriverManager;
import utilities.screenshotutil;

public class basetest {

	public static WebDriver driver;
	public static Properties pr = new Properties();
	public static Properties loc = new Properties();
	public static FileReader fr;
	public static FileReader fr1;

	@BeforeMethod
	public void setup() throws IOException {
		fr  = new FileReader("C:\\Users\\User\\eclipse-workspace\\seleniumtesting\\src\\test\\resources\\configfiles\\config.properties");
		fr1 = new FileReader("C:\\Users\\User\\eclipse-workspace\\seleniumtesting\\src\\test\\resources\\configfiles\\locators.properties");
		pr.load(fr);
		loc.load(fr1);

		if (pr.getProperty("browser").equalsIgnoreCase("chrome")) {
			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver();
			driver.get(pr.getProperty("testurl"));
		} else if (pr.getProperty("browser").equalsIgnoreCase("firefox")) {
			WebDriverManager.firefoxdriver().setup();
			driver = new FirefoxDriver();
			driver.get(pr.getProperty("testurl"));
		}
	}

	/**
	 * Runs after every test method regardless of pass/fail.
	 * When a test fails, a screenshot is captured before the browser is closed.
	 * The screenshot is saved to the directory defined by 'screenshot_path' in
	 * config.properties (defaults to test-output/screenshots if not set).
	 */
	@AfterMethod(alwaysRun = true)
	public void teardown(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			String testName = result.getName();
			String screenshotDir = pr.getProperty("screenshot_path", "test-output/screenshots");
			screenshotutil.captureScreenshot(driver, testName, screenshotDir);
		}
		if (driver != null) {
			driver.quit();
		}
	}
}
