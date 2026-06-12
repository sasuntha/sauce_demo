package testcase;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import base.basetest;

public class logintest extends basetest {

    private void fillLoginForm(WebDriverWait wait, String username, String password) {
        driver.findElement(By.xpath(loc.getProperty("login_link"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@id='customer_email']")))
                .sendKeys(username);

        driver.findElement(By.xpath("//input[@id='customer_password']"))
                .sendKeys(password);

        driver.findElement(By.xpath(loc.getProperty("signin_button"))).click();
    }

    // ── Positive test: valid credentials redirect away from the login page ──────

    @Test(dataProvider = "validCredentials")
    public void loginWithValidCredentials(String username, String password) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        fillLoginForm(wait, username, password);

        // Shopify redirects to /account on success — wait for that redirect.
        // This is far more reliable than looking for a specific logout element
        // whose id/class can vary between themes.
        wait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("/account/login")));

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/account"),
                "Login did not redirect to /account for: " + username
                + " | landed at: " + currentUrl);
    }

    // ── Negative test: wrong credentials must keep the browser on the login page ─

    @Test(dataProvider = "invalidCredentials")
    public void loginWithInvalidCredentialsFails(String username, String password) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        fillLoginForm(wait, username, password);

        // A successful login redirects away from /account/login within seconds.
        // If the URL does NOT change within 8 s the credentials were rejected.
        try {
            new WebDriverWait(driver, Duration.ofSeconds(8))
                    .until(ExpectedConditions.not(
                            ExpectedConditions.urlContains("/account/login")));
            // We should NOT get here for invalid credentials
            Assert.fail("Invalid credentials unexpectedly logged in for: " + username
                    + " | URL: " + driver.getCurrentUrl());
        } catch (TimeoutException e) {
            // Expected — stayed on login page, credentials were rejected
            Assert.assertTrue(driver.getCurrentUrl().contains("/account/login"),
                    "Unexpected URL after failed login: " + driver.getCurrentUrl());
        }
    }

    @DataProvider(name = "validCredentials")
    public Object[][] validCredentials() {
        return new Object[][] {
            { "sasunthan@gmail.com",       "Sasun@26557" },
            { "sasunthanujana6@gmail.com", "Sasun@26557" }
        };
    }

    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentials() {
        return new Object[][] {
            { "sasunthanujana@gmail.com", "n@26557"  },
            { "sasunthajana6@gmail.com",  "Sasun@27" }
        };
    }
}
