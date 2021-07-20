package com.blz.selenium;

import com.google.common.collect.ImmutableList;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.network.Network;
import org.openqa.selenium.devtools.network.model.BlockedReason;
import org.openqa.selenium.devtools.network.model.ConnectionType;
import org.openqa.selenium.devtools.network.model.ResourceType;
import org.testng.Assert;
import java.util.Optional;

import static org.openqa.selenium.devtools.network.Network.loadingFailed;

public class BaseClass {

    static WebDriver driver;
    static DevTools devTools;

    public void emulateNetworkConditionTest() throws InterruptedException {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        devTools =  ((ChromeDriver)driver).getDevTools();

        driver.get("https://www.facebook.com");

        devTools.createSession();

        devTools.send(Network.enable(Optional.of(1000000), Optional.empty(), Optional.empty()));
        //change the download, upload speed and connection type to verify how your web app reacts
        devTools.send(
                Network.emulateNetworkConditions(true, 100, 200000, 100000, Optional.of(ConnectionType.ETHERNET)));

        //when offline
        devTools.addListener(loadingFailed(), loadingFailed ->
        {
            System.out.println(loadingFailed.getErrorText());
            Assert.assertEquals(loadingFailed.getErrorText(), "net::ERR_INTERNET_DISCONNECTED");
        });

        long startTime = System.currentTimeMillis();
        driver.get("https://www.facebook.com");

        long endTime = System.currentTimeMillis();

        System.out.println("page loaded in " + (endTime - startTime));

        Thread.sleep(3000);
        driver.quit();
    }

    public void networkIntercepting() throws InterruptedException {
        //https://chromedevtools.github.io/devtools-protocol/tot/Network/
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        devTools =  ((ChromeDriver)driver).getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        driver.get("https://www.facebook.com");
        Thread.sleep(3000);


        //devTools.send(Network.setBlockedURLs(ImmutableList.of("any specific resource path")));

        devTools.send(Network.setBlockedURLs(ImmutableList.of("*.png","*.css")));

        devTools.addListener(loadingFailed(), loadingFailed -> {

            if (loadingFailed.getType().equals(ResourceType.STYLESHEET)) {
                Assert.assertEquals(loadingFailed.getBlockedReason(), BlockedReason.INSPECTOR);
            }

            else if (loadingFailed.getType().equals(ResourceType.IMAGE)) {
                Assert.assertEquals(loadingFailed.getBlockedReason(), BlockedReason.INSPECTOR);
            }

        });

        driver.get("https://www.facebook.com");
        Thread.sleep(3000);
        driver.close();
    }
}
