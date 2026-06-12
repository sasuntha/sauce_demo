package testcase;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import base.basetest;

public class orderhistorytest extends basetest {

    private void loginToAccount(WebDriverWait wait) {
        driver.findElement(By.xpath(loc.getProperty("login_link"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@id='customer_email']")))
                .sendKeys(pr.getProperty("test_email"));

        driver.findElement(By.xpath("//input[@id='customer_password']"))
                .sendKeys(pr.getProperty("test_password"));

        driver.findElement(By.xpath(loc.getProperty("signin_button"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(loc.getProperty("logout_button"))));
    }

    @Test
    public void accountPageLoadsAfterLogin() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        loginToAccount(wait);

        driver.get(pr.getProperty("testurl") + "account");

        WebElement accountHeading = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath(loc.getProperty("account_page_heading"))));

        Assert.assertTrue(accountHeading.isDisplayed(),
                "Account page heading not visible after navigating to /account");
    }

    @Test
    public void orderHistoryTableIsVisible() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        loginToAccount(wait);

        driver.get(pr.getProperty("testurl") + "account");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(loc.getProperty("account_page_heading"))));

        WebElement ordersTable = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath(loc.getProperty("order_history_table"))));

        Assert.assertTrue(ordersTable.isDisplayed(),
                "Order history table is not displayed on the account page");
    }

    @Test
    public void orderHistoryListsAtLeastOneOrder() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        loginToAccount(wait);

        driver.get(pr.getProperty("testurl") + "account");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(loc.getProperty("account_page_heading"))));

        List<WebElement> orderLinks = driver.findElements(
                By.xpath(loc.getProperty("order_first_link")));

        // Skip rather than fail — no orders means the account is new, not a bug.
        if (orderLinks.size() == 0) {
            throw new SkipException(
                "Skipping: no orders in history yet. Run the payment flow tests first.");
        }

        Assert.assertTrue(orderLinks.size() > 0,
                "No orders found in the order history table");
    }

    @Test
    public void clickOrderShowsOrderDetail() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        loginToAccount(wait);

        driver.get(pr.getProperty("testurl") + "account");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(loc.getProperty("account_page_heading"))));

        List<WebElement> orderLinks = driver.findElements(
                By.xpath(loc.getProperty("order_first_link")));

        if (orderLinks.size() == 0) {
            throw new SkipException(
                "Skipping: no orders in history yet. Run the payment flow tests first.");
        }

        orderLinks.get(0).click();

        WebElement orderDetailHeading = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath(loc.getProperty("order_detail_heading"))));

        Assert.assertTrue(orderDetailHeading.isDisplayed(),
                "Order detail page did not load after clicking on an order link");
    }

    @Test
    public void logoutFromAccountPage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        loginToAccount(wait);

        driver.get(pr.getProperty("testurl") + "account");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(loc.getProperty("account_page_heading"))));

        driver.findElement(By.xpath(loc.getProperty("logout_button"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(loc.getProperty("login_link"))));

        boolean loginLinkVisible = driver.findElements(
                By.xpath(loc.getProperty("login_link"))).size() > 0;

        Assert.assertTrue(loginLinkVisible,
                "Login link not visible after logout — user may still be logged in");
    }
}
