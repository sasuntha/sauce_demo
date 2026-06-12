package testcase;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import base.basetest;

public class logintest extends basetest {
	
	@Test(dataProvider = "testdata")
	public void login(String username, String password) { // 'static' keyword removed
		
		// 1. Click your initial login button link
		driver.findElement(By.xpath(loc.getProperty("login_link"))).click();
		
		// 2. Define an explicit wait timer (up to a maximum of 10 seconds)
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		
		// 3. Pause until the email text input is completely loaded and ready
		WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='customer_email']")));
		
		// 4. Proceed with typing and form submission
		emailField.sendKeys(username);
		driver.findElement(By.xpath("//input[@id='customer_password']")).sendKeys(password);
		driver.findElement(By.xpath(loc.getProperty("signin_button"))).click();
		
		boolean isDashboardDisplayed = driver.findElements(By.xpath(loc.getProperty("logout_button"))).size() > 0;
		Assert.assertTrue(isDashboardDisplayed, "Login failed for account: " + username);
	}
	
	@DataProvider(name="testdata")
	public Object[][] tData(){
		return new Object[][] {
			{"sasunthan@gmail.com","Sasun@26557"},
			{"sasunthanujana@gmail.com","n@26557"},
			{"sasunthajana6@gmail.com","Sasun@27"},
			{"sasunthanujana6@gmail.com","Sasun@26557"}
		};
	}
}
