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

public class addtocarttest extends basetest {

    @Test
    public void addProductToCartAndVerifyCount() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(pr.getProperty("testurl") + pr.getProperty("product_url"));

        WebElement addToCartBtn = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath(loc.getProperty("add_to_cart_button"))));
        addToCartBtn.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(loc.getProperty("cart_count"))));

        WebElement cartCount = driver.findElement(
                By.xpath(loc.getProperty("cart_count")));

        int count = Integer.parseInt(cartCount.getText().trim());
        Assert.assertTrue(count >= 1,
                "Cart count did not update after adding product. Count: " + count);
    }

    @Test
    public void addProductToCartAndVerifyCartPage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(pr.getProperty("testurl") + pr.getProperty("product_url"));

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(loc.getProperty("add_to_cart_button")))).click();

        driver.get(pr.getProperty("testurl") + "cart");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(loc.getProperty("cart_item_title"))));

        WebElement cartItem = driver.findElement(
                By.xpath(loc.getProperty("cart_item_title")));

        Assert.assertTrue(cartItem.isDisplayed(),
                "Cart page does not show any added product");
    }

    @Test(dataProvider = "productData")
    public void addMultipleProductsToCart(String productSlug, String expectedNameFragment) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(pr.getProperty("testurl") + "products/" + productSlug);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(loc.getProperty("add_to_cart_button")))).click();

        driver.get(pr.getProperty("testurl") + "cart");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(loc.getProperty("cart_item_title"))));

        List<WebElement> cartItems = driver.findElements(
                By.xpath(loc.getProperty("cart_item_title")));

        boolean itemFound = cartItems.stream()
                .anyMatch(el -> el.getText().toLowerCase()
                        .contains(expectedNameFragment.toLowerCase()));

        Assert.assertTrue(itemFound,
                "Expected product '" + expectedNameFragment + "' not found in cart");
    }

    @Test
    public void cartPageDisplaysCheckoutButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(pr.getProperty("testurl") + pr.getProperty("product_url"));

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(loc.getProperty("add_to_cart_button")))).click();

        driver.get(pr.getProperty("testurl") + "cart");

        WebElement checkoutBtn = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath(loc.getProperty("cart_checkout_button"))));

        Assert.assertTrue(checkoutBtn.isDisplayed(),
                "Checkout button is not visible on the cart page");
    }

    @DataProvider(name = "productData")
    public Object[][] productData() {
        return new Object[][] {
            { "grey-jacket",  "grey jacket"  },
            { "noir-jacket",  "noir jacket"  },
            { "striped-top",  "striped top"  }
        };
    }
}
