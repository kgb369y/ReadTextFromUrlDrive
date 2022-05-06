package foo.selenium.ReadTextFromUrl;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import io.github.bonigarcia.wdm.WebDriverManager;



public class Selenium {
	

	WebDriver driver;

	public Map<String, String> getList() {
		Map<String, String> m = new HashMap<String, String>();
		List<WebElement> files = driver.findElements(By.className("l-Ab-T-r"));
		for (WebElement file : files) {
			new Actions(driver).contextClick(file).build().perform();
			List<WebElement> listmenu = driver.findElements(By.className("a-v-T"));
			for (WebElement option : listmenu) {
				if (option.getText().equals("Abrir con")) {
					option.click();
					break;
				}
			}
			driver.findElements(By.className("h-w")).get(4).findElements(By.xpath("./child::*")).get(0)
					.findElement(By.className("a-v-T")).click();
			String prefix = file.getText().replace("Test Cases - AP Matching S1 ", "").substring(0, 3);
			String[] par = moveNextWindowAndBack(prefix);
			m.put(par[0], par[1]);
		}
		return m;
	}

	private String[] moveNextWindowAndBack(String prefix) {
		String[] info = { "", "" };
		String oldTab = driver.getWindowHandle();
		ArrayList<String> newTab = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(newTab.get(1));
		info[0] = getInfo(prefix);
		info[1] = driver.getCurrentUrl().split("/edit")[0];
		driver.switchTo().window(newTab.get(1)).close();
		driver.switchTo().window(oldTab);
		return info;
	}

	private String getInfo(String prefix) {
		String value = "";
		for (WebElement meta : driver.findElements(By.tagName("meta"))) {
			if (meta.getAttribute("property").contains("og:description")) {
				String[] contents = meta.getAttribute("content").split(",");
				for (int i = contents.length - 1; i > 0; i--) {
					String c = contents[i].replaceFirst(" ", "");
					if (c.startsWith(prefix)) {
						value = c;
						break;
					}
				}
				if (!value.isEmpty())
					break;
			}
		}
		return value;

	}

	public void login(String user, String pass) throws Throwable {
		driver.findElement(By.cssSelector(".drive__hero-buttons a")).click();
		Set<String> handles = driver.getWindowHandles();
		handles.forEach(System.out::println);
		
		Iterator<String> it = handles.iterator();
		it.next();
		String nextP = it.next();
		driver.switchTo().window(nextP);
		
		driver.findElement(By.id("identifierId")).sendKeys(user);
		driver.findElement(By.id("identifierNext")).click();
		ready();
		driver.findElement(By.name("password")).sendKeys(pass);
		driver.findElement(By.id("passwordNext")).click();
	}

	public void goTo(String url) throws Throwable {
		driver.get(url);
		ready();
	}

	public void run() throws IOException {
		WebDriverManager.chromedriver().setup();
		
		//ChromeOptions options = chromeOptions();
		driver = new ChromeDriver();
		putRigth();
	}

	private void putRigth() {
		GraphicsDevice[] monitores = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		GraphicsDevice gd = monitores[monitores.length - 1];
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		driver.manage().window().setSize(new Dimension(width / 2, height - 40));
		driver.manage().window().setPosition(new Point(width / 2, 0));
	}

	private void ready() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		boolean isReady = js.executeScript("return document.readyState").toString().equals("complete");
		System.out.println(isReady);
	}

	public void end() throws Throwable {
		driver.close();
	}

	public void print(Map<String, String> map, String ruta) throws IOException {
		BufferedWriter bw;
		File archivo = new File(ruta);
		bw = new BufferedWriter(new FileWriter(archivo));
		bw.write("*********************************************************************************************\n");
		for (Map.Entry<String, String> entry : map.entrySet()) {
			bw.write(entry.getKey() + ", " + entry.getValue() + "\n");
		}
		bw.write("*********************************************************************************************\n");
		bw.close();
	}

	protected ChromeOptions chromeOptions() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--incognito");
		options.addArguments("--start-maximized");
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-dev-shm-usage");

		options.addArguments("enable-automation"); // https://stackoverflow.com/a/43840128/1689770
		options.addArguments("--disable-infobars"); // https://stackoverflow.com/a/43840128/1689770
		options.addArguments("--disable-browser-side-navigation"); // https://stackoverflow.com/a/49123152/1689770
		options.addArguments("--disable-gpu"); // https://stackoverflow.com/questions/51959986/how-to-solve-selenium-chromedriver-timed-out-receiving-message-from-renderer-exc

		options.setAcceptInsecureCerts(true); // apply if is needed access to wp without certs

		return options;
	}
}