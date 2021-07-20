package com.blz.selenium.test;

import com.blz.selenium.BaseClass;
import org.testng.annotations.Test;

public class NetworkConnectivity_Test extends BaseClass {

    @Test
    public void network_Test() throws InterruptedException {
        BaseClass baseClass = new BaseClass();
        baseClass.emulateNetworkConditionTest();
    }

    @Test
    public void html_network_Test() throws InterruptedException {
        BaseClass baseClass=new BaseClass();
        baseClass.networkIntercepting();
    }
}
