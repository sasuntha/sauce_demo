package testcase;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import base.basetest;

public class productsearchtest extends basetest {

    @Test(dataProvider = "validSearchData")
    public void searchProductReturnsResults(String searchTerm) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(pr.getProperty("testurl") + "search?q=" + searchTerm);

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(loc.getProperty("search_result_item"))));

        List<WebElement> results = driver.findElements(
                By.xpath(loc.getProperty("search_result_item")));

        Assert.assertTrue(results.size() > 0,
                "No search results returned for term: " + searchTerm);
    }

    @Test
    public void searchWithNoResultsShowsMessage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(pr.getProperty("testurl") + "search?q="
                + pr.getProperty("no_result_search_term"));

        WebElement noResultMsg = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath(loc.getProperty("search_no_result_message"))));

        Assert.assertTrue(noResultMsg.isDisplayed(),
                "Expected no-results message not shown for term: "
                        + pr.getProperty("no_result_search_term"));
    }

    @Test
    public void searchAndNavigateToProductPage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(pr.getProperty("testurl") + "search?q=" + pr.getProperty("search_term"));

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(loc.getProperty("search_result_item"))));

        driver.findElement(By.xpath(loc.getProperty("search_result_link"))).click();

        WebElement productTitle = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath(loc.getProperty("product_title"))));

        Assert.assertTrue(productTitle.isDisplayed(),
                "Product detail page did not load after clicking a search result");
    }

    @Test
    public void searchResultCountMatchesExpected() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(pr.getProperty("testurl") + "search?q=" + pr.getProperty("search_term"));

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(loc.getProperty("search_result_item"))));

        List<WebElement> results = driver.findElements(
                By.xpath(loc.getProperty("search_result_item")));

        Assert.assertTrue(results.size() >= 2,
                "Expected at least 2 results for 'jacket', but found: " + results.size());
    }

    @DataProvider(name = "validSearchData")
    public Object[][] validSearchData() {
        return new Object[][] {
            { "jacket" },
            { "top" }
        };
    }
}
