package testcase;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import base.basetest;

public class registertest extends basetest {
	
	@Test
    public static void register() {
    	
    	driver.findElement(By.xpath(loc.getProperty("signup_link"))).click();
        driver.findElement(By.xpath(loc.getProperty("firstname_field"))).sendKeys("sasun");
        driver.findElement(By.xpath(loc.getProperty("lastname_field"))).sendKeys("thanujana");
        driver.findElement(By.xpath(loc.getProperty("email_field"))).sendKeys("sasunthanujana6@gmail.com");
        driver.findElement(By.xpath(loc.getProperty("password_field"))).sendKeys("Sasun@26557");
        driver.findElement(By.xpath(loc.getProperty("create_button"))).click();
    	
    }
	

}
