package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CheckoutPageObjects {
     WebDriver driver;

    public CheckoutPageObjects(WebDriver driver) {
        this.driver = driver;
    }

    // Locators
    By shippingNextButton =  By.xpath("//span[text()='Next']");
    By paymentRadioButton = By.xpath("//input[@id='cashfree']");
    By teramsAndConditions = By.id("agreement_cashfree_1");
    By payWithCashfreeButton = By.xpath("//span[text()='Pay Securely']");
    By upiButton = By.xpath("//a[@href='/checkout/payment-method/upi']");
    By upiField = By.id("upiId");
    By proceedToPayButton = By.xpath("//button[text()='Proceed to Pay']");
    By orderID = By.xpath("//a[@class='order-number']/strong[1]");

    // Methods
    public WebElement getshippingNextButton() {
    	return driver.findElement(shippingNextButton);
    }
    
    public WebElement gettermsAndConditions() {
    	return driver.findElement(teramsAndConditions);
    }
    public WebElement getPaymentRadioButton() {
        return driver.findElement(paymentRadioButton);
    }

    public WebElement getPayWithCashfreeButton() {
        return driver.findElement(payWithCashfreeButton);
    }

    public WebElement getupiButton() {
    	return driver.findElement(upiButton);
    }
    public WebElement getUpiField() {
        return driver.findElement(upiField);
    }

    public WebElement getProceedToPayButton() {
        return driver.findElement(proceedToPayButton);
    }

    public WebElement getorderID() {
        return driver.findElement(orderID);
    }
}
