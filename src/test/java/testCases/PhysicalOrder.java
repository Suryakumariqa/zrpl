package testCases;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import pageObjects.CheckoutPageObjects;
import pageObjects.HomePageObjects;
import pageObjects.PDPObjects;
import resources.Base;

public class PhysicalOrder extends Base {
	WebDriver driver;
	HomePageObjects homePage;
	CheckoutPageObjects checkoutPage;
	PDPObjects PdpObjects;

	// Logger initialization
	private static final Logger logger = LogManager.getLogger(PhysicalOrder.class);

	@BeforeMethod
	public void setUp() throws IOException {
		driver = initializeBrowser();
		driver.get(prop.getProperty("url")); // URL from properties
		homePage = new HomePageObjects(driver);
		checkoutPage = new CheckoutPageObjects(driver);
		PdpObjects = new PDPObjects(driver);
		logger.info("Browser initialized and navigated to URL: " + prop.getProperty("url"));
	}

	@Test(priority = 0)
	public void OrderCreation() throws InterruptedException {

		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
		// driver.navigate().to("https://uat.zeppsandbox.com/electronics.html");

		String productNameToSearch = "test product not for sell";
		logger.info("Searching for product: " + productNameToSearch);

		// Search Product
		homePage.getSearchBar().sendKeys(productNameToSearch);
		homePage.getSearchBar().submit();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		// Select Product
		List<WebElement> productLinks = homePage.getProductLinks();
		for (WebElement product : productLinks) {
			if (product.getText().contains(productNameToSearch)) {
				// Re-locate the element before accessing its text again
				logger.info("Selected product: " + product.getText());
				wait.until(ExpectedConditions.elementToBeClickable(product)).click();
				break;
			}
		}

		// Click "Buy Now"
		homePage.getBuyNowButton().click();
		logger.info("Clicked on Buy Now");

		// driver.navigate().to("https://uat.zeppsandbox.com/checkout/#shipping"); //
		// Navigate to the checkout page and
		// refresh
		driver.navigate().refresh();

		// Shipment page next button
		checkoutPage.getshippingNextButton().click();
		logger.info("Navigating to shipping next page");

		// Locate the radio button for the payment method of cashfree
		WebElement radioButton = checkoutPage.getPaymentRadioButton();
		radioButton.getText();

		// Check if the radio button is enabled
		if (!radioButton.isSelected()) {
			radioButton.click(); // Select the radio button if not already selected
			logger.info("Selected payment method: Cashfree");
		}

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,1000);");

		WebDriverWait Wait = new WebDriverWait(driver, Duration.ofSeconds(80));
		Wait.until(ExpectedConditions.elementToBeClickable(checkoutPage.gettermsAndConditions())).click();
		logger.info("Terms and conditions clicked");
		// Proceed to payment step
		Wait.until(ExpectedConditions.elementToBeClickable(checkoutPage.getPayWithCashfreeButton())).click();
		logger.info("Proceeding to Cashfree payment");

		// Initialize the JavascriptExecutor
		js.executeScript("window.scrollBy(0,1000);");

		// Select UPI as payment method
		checkoutPage.getupiButton().click();
		logger.info("Selected UPI payment method");
		// .sendKeys("testsuccess@gocash"); // Enter UPI ID
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter UPI ID: ");
		String upiId = scanner.nextLine();

		// Enter the UPI ID into the field
		checkoutPage.getUpiField().sendKeys(upiId);
		logger.info("Entered UPI ID: " + upiId);

		Thread.sleep(1000);
		checkoutPage.getProceedToPayButton().click();
		logger.info("Clicked Proceed to Pay");

		// Wait for success message
		WebDriverWait LongWait = new WebDriverWait(driver, Duration.ofSeconds(30));
		logger.info("Order ID: "
				+ LongWait.until(ExpectedConditions.elementToBeClickable(checkoutPage.getorderID())).getText());
	}

	@Test(priority = 1)
	public void OrderCreationWithDiscountCoupon() throws InterruptedException {

		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
		logger.info("Order creation with discount coupon test started.");

		String productNameToSearch = "iFrogz Audio- Ear Pollution Plugz with Mic - Red";
		logger.info("Searching for product: " + productNameToSearch);

		// Search Product
		homePage.getSearchBar().sendKeys(productNameToSearch);
		homePage.getSearchBar().submit();
		logger.info("Product search submitted.");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		// Select Product
		List<WebElement> productLinks = homePage.getProductLinks();
		for (WebElement product : productLinks) {
			if (product.getText().contains(productNameToSearch)) {
				logger.info("Selected product: " + product.getText());
				wait.until(ExpectedConditions.elementToBeClickable(product)).click();
				break;
			}
		}

		logger.info("Extracting discount details from product page.");
		String discountText1 = PdpObjects.discountText().getText();

		// Split the text into individual lines
		String[] discountLines = discountText1.split("\n");

		int maxDiscount = 0;
		String bestCouponCode = "";
		Pattern discountPattern = Pattern.compile("(\\d+) % off.*Use code (\\w+)");

		for (String line : discountLines) {
			Matcher matcher = discountPattern.matcher(line);
			if (matcher.find()) {
				int discountValue = Integer.parseInt(matcher.group(1));
				String couponCode = matcher.group(2);

				logger.info("Discount Found: " + discountValue + "%, Coupon Code: " + couponCode);

				if (discountValue > maxDiscount) {
					maxDiscount = discountValue;
					bestCouponCode = couponCode;
				}
			}
		}
		logger.info("Best discount selected: " + maxDiscount + "% with Coupon Code: " + bestCouponCode);

		// Click "Buy Now"
		homePage.getBuyNowButton().click();
		logger.info("Clicked on Buy Now.");

		driver.navigate().refresh();
		logger.info("Refreshed the checkout page.");

		// Shipment page next button
		checkoutPage.getshippingNextButton().click();
		logger.info("Navigated to shipping step.");

		Thread.sleep(1000);
		wait.until(ExpectedConditions.elementToBeClickable(PdpObjects.discountDropDown())).click();
		logger.info("Opened discount dropdown.");

		List<WebElement> cancelDiscountElements = PdpObjects.cancelDiscountCoupon();
		if (!cancelDiscountElements.isEmpty()) {
			WebElement cancelElement = cancelDiscountElements.get(0);
			if (cancelElement.getText().contains("Cancel Discount")) {
				cancelElement.click();
				logger.info("Existing coupon canceled.");
			}
		} else {
			logger.info("No existing coupon to cancel.");
		}

		wait.until(ExpectedConditions.elementToBeClickable(PdpObjects.Code())).sendKeys(bestCouponCode);
		logger.info("Applied best coupon code: " + bestCouponCode);

		PdpObjects.applyButton().click();
		logger.info("Clicked on Apply button for coupon.");

		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("loading-mask")));
		logger.info("Loading mask disappeared after applying coupon.");

		WebElement radioButton = checkoutPage.getPaymentRadioButton();
		if (!radioButton.isSelected()) {
			radioButton.click();
			logger.info("Selected payment method: Cashfree.");
		}

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,1000);");
		logger.info("Scrolled down for terms and conditions.");

		WebDriverWait Wait = new WebDriverWait(driver, Duration.ofSeconds(80));
		Wait.until(ExpectedConditions.elementToBeClickable(checkoutPage.gettermsAndConditions())).click();
		logger.info("Agreed to terms and conditions.");

		Wait.until(ExpectedConditions.elementToBeClickable(checkoutPage.getPayWithCashfreeButton())).click();
		logger.info("Proceeded to Cashfree payment.");

		js.executeScript("window.scrollBy(0,1000);");
		logger.info("Scrolled down for UPI payment.");

		checkoutPage.getupiButton().click();
		logger.info("Selected UPI payment method.");

		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter UPI ID: ");
		String upiId = scanner.nextLine();
		checkoutPage.getUpiField().sendKeys(upiId);
		logger.info("Entered UPI ID: " + upiId);

		Thread.sleep(3000);
		checkoutPage.getProceedToPayButton().click();
		logger.info("Clicked Proceed to Pay.");

		WebDriverWait LongWait = new WebDriverWait(driver, Duration.ofSeconds(30));
		String orderId = LongWait.until(ExpectedConditions.elementToBeClickable(checkoutPage.getorderID())).getText();
		logger.info("Order successfully created. Order ID: " + orderId);
	}

	@Test(priority = 2)
	public void OrderCreationWithZeppCoins() throws InterruptedException {

		logger.info("Starting test: OrderCreationWithZeppCoins");
		driver.get(prop.getProperty("url"));
		logger.info("Navigated to URL: " + prop.getProperty("url"));

		WebElement Search = driver.findElement(By.xpath("//input[@id='search']"));
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		WebDriverWait Wait = new WebDriverWait(driver, Duration.ofSeconds(40));
		logger.info("Search bar located.");

		String productNameToSearch = "iFrogz Audio- Ear Pollution Plugz with Mic - Red";
		Search.sendKeys(productNameToSearch);
		logger.info("Entered product name in search bar: " + productNameToSearch);
		Search.sendKeys(Keys.ENTER);
		logger.info("Search submitted.");

		List<WebElement> productLinks = homePage.getProductLinks();
		for (WebElement product : productLinks) {
			if (product.getText().contains(productNameToSearch)) {
				logger.info("Selected product: " + product.getText());
				wait.until(ExpectedConditions.elementToBeClickable(product)).click();
				break;
			}
		}

		driver.findElement(By.xpath("//button[@title='Buy Now']")).click();
		logger.info("Clicked on 'Buy Now' button.");

		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("loading-mask")));
		driver.navigate().refresh();
		logger.info("Page refreshed after clicking 'Buy Now'.");

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,2000);");
		logger.info("Scrolled down for shipping step.");

		checkoutPage.getshippingNextButton().click();
		logger.info("Clicked 'Next' on the shipping page.");

		String Coins = driver.findElement(By.xpath("//span[@id='zeppvalue']")).getText();
		logger.info("Retrieved available Zepp Coins: " + Coins);

		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("loading-mask")));
		int coinsValue = Integer.parseInt(Coins);
		logger.info("Converted Zepp Coins to integer: " + coinsValue);

		if (coinsValue >= 500) {
			logger.info("Sufficient Zepp Coins available to apply.");

			List<WebElement> cancelZeppCoinsElements = driver
					.findElements(By.xpath("//span[text()='Cancel Reward Points']"));

			if (!cancelZeppCoinsElements.isEmpty()) {
				WebElement cancelZeppCoinsElement = cancelZeppCoinsElements.get(0);
				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("loading-mask")));
				cancelZeppCoinsElement.click();
				logger.info("Existing Zepp Coins canceled.");
			}

			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Apply ZEPP Coins']"))).click();
			logger.info("Opened 'Apply ZEPP Coins' section.");

			driver.findElement(By.id("rewardpoints-amount")).sendKeys("100");
			logger.info("Entered Zepp Coins amount: 100");
			driver.findElement(By.xpath("//span[text()='Apply']")).click();
			logger.info("Clicked on 'Apply' for Zepp Coins.");

			List<WebElement> errorElements = driver
					.findElements(By.xpath("//div[@class='message message-error error']"));
			if (!errorElements.isEmpty()) {
				String errorMessage = errorElements.get(0).getText();
				logger.error("Error while applying Zepp Coins: " + errorMessage);

				List<WebElement> cancelDiscountElements = PdpObjects.cancelDiscountCoupon();

				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("loading-mask")));
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Apply Discount Code']")))
						.click();
				logger.info("Opened 'Apply Discount Code' section.");
				//WebDriverWait Wait = new WebDriverWait(driver, Duration.ofSeconds(40));
				if (!cancelDiscountElements.isEmpty()) {
					WebElement cancelElement = cancelDiscountElements.get(0);
					wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("loading-mask")));
					cancelElement.click();
					logger.info("Existing discount coupon canceled.");

					wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("loading-mask")));
					wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Apply ZEPP Coins']")))
							.click();
					driver.findElement(By.id("rewardpoints-amount")).sendKeys("100");
					driver.findElement(By.xpath("//span[text()='Apply']")).click();
					logger.info("Reapplied Zepp Coins successfully.");
				} else {
					logger.info("No discount coupon found to cancel.");
				}
			} else {
				logger.info("Zepp Coins applied successfully without any errors.");
			}
		} else {
			logger.warn("Insufficient Zepp Coins to apply.");
		}

		
		WebElement radioButton = driver.findElement(By.xpath("//input[@id='cashfree']"));
		if (!radioButton.isSelected()) {
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("loading-mask")));
			Wait.until(ExpectedConditions.elementToBeClickable(radioButton)).click();
			logger.info("Selected 'Cashfree' as payment method.");
		}

		js.executeScript("window.scrollBy(0,1000);");
		logger.info("Scrolled down for payment step.");
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("loading-mask")));
		wait.until(ExpectedConditions.elementToBeClickable(checkoutPage.gettermsAndConditions())).click();
		logger.info("Agreed to terms and conditions.");

		wait.until(ExpectedConditions.elementToBeClickable(checkoutPage.getPayWithCashfreeButton())).click();
		logger.info("Proceeded to payment step.");

		js.executeScript("window.scrollBy(0,1000);");
		logger.info("Scrolled down for UPI payment method.");

		checkoutPage.getupiButton().click();
		logger.info("Selected UPI payment method.");

		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter UPI ID: ");
		String upiId = scanner.nextLine();
		checkoutPage.getUpiField().sendKeys(upiId);
		logger.info("Entered UPI ID: " + upiId);

		Thread.sleep(1000);
		checkoutPage.getProceedToPayButton().click();
		logger.info("Clicked on 'Proceed to Pay' button.");
		WebDriverWait Wait1 = new WebDriverWait(driver, Duration.ofSeconds(60));
		String orderId = Wait1.until(ExpectedConditions.elementToBeClickable(checkoutPage.getorderID())).getText();
		logger.info("Order successfully created. Order ID: " + orderId);
	}

	@AfterMethod()
	public void closure() {
		if (driver != null) {
			driver.close();
			logger.info("Driver closed successfully.");
		}
	}
}