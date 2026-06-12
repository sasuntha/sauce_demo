# Sauce Demo — Selenium Automation Framework

End-to-end UI test suite for [sauce-demo.myshopify.com](https://sauce-demo.myshopify.com) built with **Selenium 4**, **TestNG**, and **Maven**. Tests cover the full customer journey — registration, login, product search, add to cart, checkout, payment, and order history. A GitHub Actions pipeline runs the suite on every push with headless Chrome and uploads test reports and failure screenshots automatically.

---

## CI Status

[![Selenium CI](https://github.com/sasuntha/sauce_demo/actions/workflows/ci.yml/badge.svg)](https://github.com/sasuntha/sauce_demo/actions/workflows/ci.yml)

---

## Tech Stack

| Tool | Version | Purpose |
|------|---------|---------|
| Java | 11 | Language |
| Selenium WebDriver | 4.44.0 | Browser automation |
| TestNG | 7.12.0 | Test framework & assertions |
| Maven | 3.x | Build & dependency management |
| WebDriverManager | 6.3.4 | Auto-downloads browser drivers |
| Apache POI | 5.5.1 | Excel data support (future use) |
| Log4j | 2.26.0 | Logging |
| ReportNG | 1.1.4 | Enhanced HTML reports |
| GitHub Actions | — | CI/CD pipeline |

---

## Project Structure

```
seleniumtesting/
├── src/test/
│   ├── java/
│   │   ├── base/
│   │   │   └── basetest.java          # Driver setup, teardown, screenshot on failure
│   │   ├── testcase/
│   │   │   ├── registertest.java      # Account registration
│   │   │   ├── logintest.java         # Login (valid & invalid credentials)
│   │   │   ├── productsearchtest.java # Product search & navigation
│   │   │   ├── addtocarttest.java     # Add to cart & cart verification
│   │   │   ├── checkouttest.java      # Checkout shipping flow
│   │   │   ├── paymenttest.java       # Payment with Shopify Bogus Gateway
│   │   │   └── orderhistorytest.java  # Account order history
│   │   └── utilities/
│   │       └── screenshotutil.java    # Screenshot capture utility
│   └── resources/
│       └── configfiles/
│           ├── config.properties      # Browser, URL, test data
│           └── locators.properties    # All XPath locators
├── testrunner/
│   └── testng.xml                     # TestNG suite definition
├── .github/
│   └── workflows/
│       └── ci.yml                     # GitHub Actions workflow
└── pom.xml
```

---

## Test Coverage

| Test Class | What It Tests |
|-----------|---------------|
| `registertest` | Creates a new customer account |
| `logintest` | Valid credentials redirect to `/account`; invalid credentials stay on the login page |
| `productsearchtest` | Search returns results, no-results message, click-through to product page |
| `addtocarttest` | Add product to cart, verify cart page, checkout button visible |
| `checkouttest` | Checkout page loads, shipping form fills and submits, shipping method selection |
| `paymenttest` | Full payment flow using Shopify Bogus Gateway (card `1` = success, `2` = fail) |
| `orderhistorytest` | Login, view order history, click order detail, logout |

---

## Prerequisites

- **Java 11+** (`java -version` should show 11 or higher)
- **Maven 3.6+** (`mvn -version`)
- **Google Chrome** (latest stable)
- A registered account on [sauce-demo.myshopify.com](https://sauce-demo.myshopify.com)

---

## Local Setup

### 1. Clone the repository

```bash
git clone https://github.com/sasuntha/sauce_demo.git
cd sauce_demo
```

### 2. Configure test credentials

Open `src/test/resources/configfiles/config.properties` and update:

```properties
# Your registered account on sauce-demo.myshopify.com
test_email = your-email@example.com
test_password = YourPassword

# Checkout details used by checkout/payment tests
checkout_email = your-email@example.com
checkout_firstname = Your
checkout_lastname  = Name
checkout_address1  = 123 Your Street
checkout_city      = London
checkout_zip       = SW1A 1AA
checkout_phone     = 01234567890
```

### 3. Run the full suite

```bash
mvn test
```

### 4. Run a single test class

```bash
mvn test -Dsurefire.suiteXmlFiles=testrunner/testng.xml -Dgroups=Login
```

Or right-click any test file in Eclipse and choose **Run As → TestNG Test**.

---

## Configuration Files

### `config.properties`

| Key | Default | Description |
|-----|---------|-------------|
| `browser` | `chrome` | Browser to use (`chrome` or `firefox`) |
| `testurl` | `https://sauce-demo.myshopify.com/` | Target site URL |
| `headless` | `false` | Run without a visible window (`true`/`false`) |
| `screenshot_path` | `test-output/screenshots` | Where to save failure screenshots |
| `test_email` | — | Login email for tests that require authentication |
| `test_password` | — | Login password |
| `search_term` | `jacket` | Search term used by product search tests |
| `product_url` | `products/grey-jacket` | Product path used by cart tests |
| `card_number` | `1` | Shopify Bogus Gateway card (`1`=success, `2`=fail) |

### `locators.properties`

All XPath selectors are centralised here — no locators are hardcoded inside test classes. To update a selector when the site changes, edit this file only.

---

## Screenshot on Failure

Whenever a test fails, a PNG screenshot is automatically captured **before** the browser closes.

```
test-output/screenshots/<testName>_FAILED_yyyy-MM-dd_HH-mm-ss.png
```

Screenshots are also uploaded as a GitHub Actions artifact (`failure-screenshots-<run>`) and kept for 7 days.

---

## GitHub Actions CI/CD

The pipeline lives in `.github/workflows/ci.yml`.

### Triggers

| Event | When |
|-------|------|
| `push` to `main` / `master` | Every code push |
| `pull_request` to `main` / `master` | Every PR |
| Nightly schedule | 9 PM UTC daily (2:30 AM IST) |
| `workflow_dispatch` | Manual run from the Actions tab |

### What the pipeline does

1. Checks out the code
2. Installs JDK 11 (Temurin) with Maven cache
3. Installs Google Chrome
4. Runs `mvn test --no-transfer-progress`  
   — GitHub Actions sets `CI=true` automatically, which activates headless mode
5. Uploads TestNG HTML reports as `testng-reports-<run>` (retained 30 days)
6. Uploads failure screenshots as `failure-screenshots-<run>` (retained 7 days, only on failure)

### Downloading artifacts

Go to **Actions → latest run → Artifacts** at the bottom of the run page to download the report ZIP or screenshot ZIP.

---

## Headless Mode

Chrome runs headless in CI automatically. To force headless locally (e.g. for background test runs):

```properties
# config.properties
headless = true
```

The following Chrome flags are applied in headless mode:

```
--headless=new
--no-sandbox
--disable-dev-shm-usage
--disable-gpu
--window-size=1920,1080
```

---

## Payment Testing

Checkout and payment tests use the **Shopify Bogus Gateway** — no real card is needed.

| Card number | Result |
|-------------|--------|
| `1` | Transaction succeeds |
| `2` | Transaction is declined |
| `3` | Gateway throws an error |

CVV `111`, any future expiry date (e.g. `12/30`) are accepted.

---

## Adding New Tests

1. Create `src/test/java/testcase/mynewtest.java` extending `base.basetest`
2. Add any new XPath selectors to `src/test/resources/configfiles/locators.properties`
3. Add any new test data to `src/test/resources/configfiles/config.properties`
4. Register the new class in `testrunner/testng.xml`

```java
package testcase;

import org.testng.annotations.Test;
import base.basetest;

public class mynewtest extends basetest {

    @Test
    public void myTest() {
        driver.get(pr.getProperty("testurl"));
        // test steps here
    }
}
```

---

## License

This project is for educational and demonstration purposes.
