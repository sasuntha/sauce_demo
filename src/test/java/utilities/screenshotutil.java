package utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class screenshotutil {

    /**
     * Captures a screenshot of the current browser state and saves it as a PNG file.
     *
     * @param driver        active WebDriver instance
     * @param testName      name of the failed test method (used in the filename)
     * @param screenshotDir directory path where the screenshot will be saved
     * @return absolute path of the saved screenshot file, or null if capture failed
     */
    public static String captureScreenshot(WebDriver driver, String testName, String screenshotDir) {
        if (driver == null) {
            System.err.println("[SCREENSHOT] Driver is null — cannot capture screenshot for: " + testName);
            return null;
        }

        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String fileName = testName + "_FAILED_" + timestamp + ".png";
        File destFile = new File(screenshotDir + File.separator + fileName);

        try {
            destFile.getParentFile().mkdirs();

            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            String absolutePath = destFile.getAbsolutePath();
            System.out.println("[SCREENSHOT] Captured: " + absolutePath);
            return absolutePath;

        } catch (IOException e) {
            System.err.println("[SCREENSHOT] Failed to save screenshot for '" + testName + "': " + e.getMessage());
            return null;
        }
    }
}
