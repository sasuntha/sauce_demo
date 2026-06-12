package testcase;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import base.basetest;

/**
 * End-to-end payment flow tests using Shopify's Bogus Gateway.
 *
 * Bogus Gateway test card values:
 *   Card number 1  → successful transaction
 *   Card number 2  → failed transaction
 *   Card number 3  → exception / gateway error
 */
public class paymenttest extends basetest {

    private void navigateToPaymentStep(WebDriverWait wait) {
        driver.get(pr.getProperty("testurl") + pr.getProperty("product_url"));
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(loc.getProperty("add_to_cart_button")))).click();

        driver.get(pr.getProperty("testurl") + "cart");
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(loc.getProperty("cart_checkout_button")))).click();

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

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(loc.getProperty("payment_card_number"))));
    }

    @Test
    public void paymentPageDisplaysCardFields() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        navigateToPaymentStep(wait);

        Assert.assertTrue(
                driver.findElement(By.xpath(loc.getProperty("payment_card_number"))).isDisplayed(),
                "Card number field not displayed on payment page");
        Assert.assertTrue(
                driver.findElement(By.xpath(loc.getProperty("payment_card_name"))).isDisplayed(),
                "Card name field not displayed on payment page");
        Assert.assertTrue(
                driver.findElement(By.xpath(loc.getProperty("payment_card_expiry"))).isDisplayed(),
                "Card expiry field not displayed on payment page");
        Assert.assertTrue(
                driver.findElement(By.xpath(loc.getProperty("payment_card_cvv"))).isDisplayed(),
                "Card CVV field not displayed on payment page");
    }

    @Test
    public void successfulPaymentLeadsToOrderConfirmation() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        navigateToPaymentStep(wait);

        driver.findElement(By.xpath(loc.getProperty("payment_card_number")))
                .sendKeys(pr.getProperty("card_number"));
        driver.findElement(By.xpath(loc.getProperty("payment_card_name")))
                .sendKeys(pr.getProperty("card_name"));
        driver.findElement(By.xpath(loc.getProperty("payment_card_expiry")))
                .sendKeys(pr.getProperty("card_expiry"));
        driver.findElement(By.xpath(loc.getProperty("payment_card_cvv")))
                .sendKeys(pr.getProperty("card_cvv"));

        driver.findElement(By.xpath(loc.getProperty("payment_pay_button"))).click();

        WebElement confirmationHeading = wait.until(ExpectedConditions
                .visibilityOfElementLocated(
                        By.xpath(loc.getProperty("order_confirmation_heading"))));

        Assert.assertTrue(confirmationHeading.isDisplayed(),
                "Order confirmation page did not appear after successful payment");
    }

    @Test(dataProvider = "bogusCardData")
    public void paymentWithDifferentCardScenarios(String cardNumber, boolean expectSuccess) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        navigateToPaymentStep(wait);

        driver.findElement(By.xpath(loc.getProperty("payment_card_number")))
                .sendKeys(cardNumber);
        driver.findElement(By.xpath(loc.getProperty("payment_card_name")))
                .sendKeys(pr.getProperty("card_name"));
        driver.findElement(By.xpath(loc.getProperty("payment_card_expiry")))
                .sendKeys(pr.getProperty("card_expiry"));
        driver.findElement(By.xpath(loc.getProperty("payment_card_cvv")))
                .sendKeys(pr.getProperty("card_cvv"));

        driver.findElement(By.xpath(loc.getProperty("payment_pay_button"))).click();

        if (expectSuccess) {
            WebElement confirmationHeading = wait.until(ExpectedConditions
                    .visibilityOfElementLocated(
                            By.xpath(loc.getProperty("order_confirmation_heading"))));
            Assert.assertTrue(confirmationHeading.isDisplayed(),
                    "Order confirmation not shown for successful card: " + cardNumber);
        } else {
            wait.until(ExpectedConditions.urlContains("checkout"));
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("checkout"),
                    "Expected to remain on checkout after a declined card scenario");
        }
    }

    @DataProvider(name = "bogusCardData")
    public Object[][] bogusCardData() {
        return new Object[][] {
            { "1", true  },
            { "2", false }
        };
    }
}
