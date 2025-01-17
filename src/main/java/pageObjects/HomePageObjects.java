package pageObjects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HomePageObjects {
    WebDriver driver;

    public HomePageObjects(WebDriver driver) {
        this.driver = driver;
    }

    // Locators
    By searchBar = By.id("search");
    By productLinks = By.cssSelector("a.product-item-link");
    By buyNowButton = By.xpath("//button[@title='Buy Now']");
    
    // Methods
    public WebElement getSearchBar() {
        return driver.findElement(searchBar);
    }

    public WebElement getBuyNowButton() {
        return driver.findElement(buyNowButton);
    }

    public List<WebElement> getProductLinks() {
        return driver.findElements(productLinks);
    }
}
