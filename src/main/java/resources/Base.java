package resources;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Base {
	WebDriver driver;
	public Properties prop;

	public WebDriver initializeBrowser() throws IOException {

		prop = new Properties(); // create obj for the properties
		String propPath = System.getProperty("user.dir") + "\\src\\main\\java\\resources\\config.properties";
		FileInputStream fis = new FileInputStream(propPath); //
		prop.load(fis); // using prop object, properties file will get load, for that file i/o class we
						// need to use

		String browser = prop.getProperty("browser");

		if (browser.equalsIgnoreCase("chrome")) {

			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver();

		} else if (browser.equalsIgnoreCase("firefox")) {

			WebDriverManager.chromedriver().setup();
			driver = new FirefoxDriver();

		} else if (browser.equalsIgnoreCase("safari")) {

			WebDriverManager.chromedriver().setup();
			driver = new SafariDriver();
		}

		driver.manage().window().maximize();

		return driver;
	}

}
