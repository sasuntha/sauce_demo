package base;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import io.github.bonigarcia.wdm.WebDriverManager;
import utilities.screenshotutil;

public class basetest {

	public static WebDriver driver;
	public static Properties pr  = new Properties();
	public static Properties loc = new Properties();

	@BeforeMethod
	public void setup() throws IOException {
		// Load config files from the test classpath (works locally and in CI).
		// Maven copies src/test/resources → target/test-classes automatically.
		try (InputStream configStream   = basetest.class.getClassLoader()
				                              .getResourceAsStream("configfiles/config.properties");
		     InputStream locatorsStream = basetest.class.getClassLoader()
				                              .getResourceAsStream("configfiles/locators.properties")) {

			if (configStream == null || locatorsStream == null) {
				throw new IOException(
					"Config files not found on classpath. " +
					"Ensure src/test/resources/configfiles/ contains " +
					"config.properties and locators.properties.");
			}
			pr.load(configStream);
			loc.load(locatorsStream);
		}

		// Headless mode is enabled when: the CI environment variable is set (GitHub
		// Actions sets CI=true automatically), OR headless=true in config.properties.
		boolean headless = "true".equalsIgnoreCase(System.getenv("CI"))
				|| "true".equalsIgnoreCase(pr.getProperty("headless", "false"));

		String browser = pr.getProperty("browser", "chrome").trim();

		if (browser.equalsIgnoreCase("chrome")) {
			ChromeOptions options = new ChromeOptions();
			if (headless) {
				options.addArguments("--headless=new");
				options.addArguments("--no-sandbox");
				options.addArguments("--disable-dev-shm-usage");
				options.addArguments("--disable-gpu");
				options.addArguments("--window-size=1920,1080");
			}
			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver(options);

		} else if (browser.equalsIgnoreCase("firefox")) {
			FirefoxOptions options = new FirefoxOptions();
			if (headless) {
				options.addArguments("--headless");
			}
			WebDriverManager.firefoxdriver().setup();
			driver = new FirefoxDriver(options);
		}

		driver.get(pr.getProperty("testurl"));
	}

	/**
	 * Runs after every test method regardless of pass/fail.
	 * On failure a screenshot is captured before the browser closes.
	 * Saved to the directory defined by screenshot_path in config.properties.
	 */
	@AfterMethod(alwaysRun = true)
	public void teardown(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			String screenshotDir = pr.getProperty("screenshot_path", "test-output/screenshots");
			screenshotutil.captureScreenshot(driver, result.getName(), screenshotDir);
		}
		if (driver != null) {
			driver.quit();
		}
	}
}
