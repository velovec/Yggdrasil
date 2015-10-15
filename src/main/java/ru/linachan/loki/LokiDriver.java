package ru.linachan.loki;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import ru.linachan.yggdrasil.YggdrasilCore;

import java.util.List;

public class LokiDriver {

    private YggdrasilCore core;
    private LokiCore browser;

    private WebDriver webDriver;

    public LokiDriver(YggdrasilCore core, LokiCore browser) {
        this.core = core;
        this.browser = browser;

        webDriver = new FirefoxDriver();
        webDriver.manage().window().maximize();
    }

    public void goTo(String url) {
        webDriver.get(url);
    }

    public WebElement findElement(By by) {
        return webDriver.findElement(by);
    }

    public List<WebElement> findElements(By by) {
        return webDriver.findElements(by);
    }

    public String getTitle() {
        return webDriver.getTitle();
    }

    public void quit() {
        webDriver.quit();
    }
}
