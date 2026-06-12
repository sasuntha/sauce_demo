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

    // ── Positive test: valid credentials should land on the account page ────────

    @Test(dataProvider = "validCredentials")
    public void loginWithValidCredentials(String username, String password) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.findElement(By.xpath(loc.getProperty("login_link"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@id='customer_email']")))
                .sendKeys(username);

        driver.findElement(By.xpath("//input[@id='customer_password']"))
                .sendKeys(password);

        driver.findElement(By.xpath(loc.getProperty("signin_button"))).click();

        // Wait for Shopify to POST the form and redirect to /account.
        // Without this wait the logout link is checked before the page even loads,
        // which is why "expected [true] but found [false]" was always the result.
        WebElement logoutLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(loc.getProperty("logout_button"))));

        Assert.assertTrue(logoutLink.isDisplayed(),
                "Login did not succeed for account: " + username);
    }

    // ── Negative test: wrong credentials must stay on the login page ────────────

    @Test(dataProvider = "invalidCredentials")
    public void loginWithInvalidCredentialsFails(String username, String password) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.findElement(By.xpath(loc.getProperty("login_link"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@id='customer_email']")))
                .sendKeys(username);

        driver.findElement(By.xpath("//input[@id='customer_password']"))
                .sendKeys(password);

        driver.findElement(By.xpath(loc.getProperty("signin_button"))).click();

        // Shopify keeps the user on the login page and shows an error message
        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(loc.getProperty("login_error_message"))));

        Assert.assertTrue(errorMsg.isDisplayed(),
                "Expected an error message for invalid credentials: " + username);
    }

    @DataProvider(name = "validCredentials")
    public Object[][] validCredentials() {
        return new Object[][] {
            { "sasunthan@gmail.com",      "Sasun@26557" },
            { "sasunthanujana6@gmail.com", "Sasun@26557" }
        };
    }

    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentials() {
        return new Object[][] {
            { "sasunthanujana@gmail.com", "n@26557"   },
            { "sasunthajana6@gmail.com",  "Sasun@27"  }
        };
    }
}
