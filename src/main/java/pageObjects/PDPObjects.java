package pageObjects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PDPObjects {
	WebDriver driver;
	
	public PDPObjects(WebDriver driver){
		this.driver = driver;
	}
	
	By discountText = By.xpath("//div[@class='info-container']");
	By discounCodeDropdown = By.xpath("//span[@id='block-discount-heading' and @class='action action-toggle']");
	By cancelCoupon = By.xpath("//span[text()='Cancel Discount']");
	By couponCode = By.xpath("//input[@id='discount-code']");
	By button = By.xpath("(//button[@class='action action-apply'])[1]");
	
	public WebElement discountText() {
		return driver.findElement(discountText);
		
	}
	
	public WebElement discountDropDown() {
		return driver.findElement(discounCodeDropdown);
	}
	
	public List<WebElement> cancelDiscountCoupon() {
		return driver.findElements(cancelCoupon);
	}
	
	public WebElement Code() {
		return driver.findElement(couponCode);
	}

	public WebElement applyButton() {
		return driver.findElement(button);
	}
}

