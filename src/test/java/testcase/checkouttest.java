package testcase;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import base.basetest;

public class checkouttest extends basetest {

    private void addProductAndGoToCart(WebDriverWait wait) {
        driver.get(pr.getProperty("testurl") + pr.getProperty("product_url"));
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(loc.getProperty("add_to_cart_button")))).click();
        driver.get(pr.getProperty("testurl") + "cart");
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(loc.getProperty("cart_checkout_button"))));
    }

    @Test
    public void checkoutPageLoadsFromCart() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        addProductAndGoToCart(wait);

        driver.findElement(By.xpath(loc.getProperty("cart_checkout_button"))).click();

        WebElement emailField = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath(loc.getProperty("checkout_email_field"))));

        Assert.assertTrue(emailField.isDisplayed(),
                "Checkout page did not load — email field not visible");
    }

    @Test
    public void fillShippingInfoAndContinue() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        addProductAndGoToCart(wait);
        driver.findElement(By.xpath(loc.getProperty("cart_checkout_button"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(loc.getProperty("checkout_email_field"))))
                .sendKeys(pr.getProperty("checkout_email"));

        driver.findElement(By.xpath(loc.getProperty("checkout_firstname_field")))
                .sendKeys(pr.getProperty("checkout_firstname"));
        driver.findElement(By.xpath(loc.getProperty("checkout_lastname_field")))
                .sendKeys(pr.getProperty("checkout_lastname"));
        driver.findElement(By.xpath(loc.getProperty("checkout_address_field")))
                .sendKeys(pr.getProperty("checkout_address1"));
        driver.findElement(By.xpath(loc.getProperty("checkout_city_field")))
                .sendKeys(pr.getProperty("checkout_city"));

        WebElement countryDropdown = driver.findElement(
                By.xpath(loc.getProperty("checkout_country_field")));
        new Select(countryDropdown).selectByVisibleText("United Kingdom");

        driver.findElement(By.xpath(loc.getProperty("checkout_zip_field")))
                .sendKeys(pr.getProperty("checkout_zip"));
        driver.findElement(By.xpath(loc.getProperty("checkout_phone_field")))
                .sendKeys(pr.getProperty("checkout_phone"));

        driver.findElement(By.xpath(loc.getProperty("checkout_continue_button"))).click();

        WebElement shippingStep = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath(loc.getProperty("checkout_step_shipping"))));

        Assert.assertTrue(shippingStep.isDisplayed(),
                "Did not reach the shipping method step after submitting contact info");
    }

    @Test
    public void shippingMethodSelectedAndContinue() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        addProductAndGoToCart(wait);
        driver.findElement(By.xpath(loc.getProperty("cart_checkout_button"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(loc.getProperty("checkout_email_field"))))
                .sendKeys(pr.getProperty("checkout_email"));

        driver.findElement(By.xpath(loc.getProperty("checkout_firstname_field")))
                .sendKeys(pr.getProperty("checkout_firstname"));
        driver.findElement(By.xpath(loc.getProperty("checkout_lastname_field")))
                .sendKeys(pr.getProperty("checkout_lastname"));
        driver.findElement(By.xpath(loc.getProperty("checkout_address_field")))
                .sendKeys(pr.getProperty("checkout_address1"));
        driver.findElement(By.xpath(loc.getProperty("checkout_city_field")))
                .sendKeys(pr.getProperty("checkout_city"));

        new Select(driver.findElement(By.xpath(loc.getProperty("checkout_country_field"))))
                .selectByVisibleText("United Kingdom");

        driver.findElement(By.xpath(loc.getProperty("checkout_zip_field")))
                .sendKeys(pr.getProperty("checkout_zip"));
        driver.findElement(By.xpath(loc.getProperty("checkout_phone_field")))
                .sendKeys(pr.getProperty("checkout_phone"));

        driver.findElement(By.xpath(loc.getProperty("checkout_continue_button"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(loc.getProperty("shipping_rate_radio"))));

        WebElement shippingOption = driver.findElement(
                By.xpath(loc.getProperty("shipping_rate_radio")));

        if (!shippingOption.isSelected()) {
            shippingOption.click();
        }

        driver.findElement(By.xpath(loc.getProperty("shipping_continue_button"))).click();

        WebElement paymentField = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath(loc.getProperty("payment_card_number"))));

        Assert.assertTrue(paymentField.isDisplayed(),
                "Did not reach the payment step after selecting a shipping method");
    }
}
