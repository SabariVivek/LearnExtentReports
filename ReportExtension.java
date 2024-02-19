package com.automation.qa.isafe.report;

import com.aventstack.extentreports.ExtentTest;

import java.util.HashMap;

import static com.automation.qa.isafe.cucumber.util.CustomAbstractTestNGCucumberTests.extent;
import static com.automation.qa.isafe.cucumber.util.CustomAbstractTestNGCucumberTests.parentTestThreadLocal;

public class ReportExtension {

    public static HashMap<String, ExtentTest> extentMap = new HashMap<>();

    public static synchronized void createParentTest(String featureName) {
        if (!extentMap.containsKey(featureName)) {
            parentTestThreadLocal.set(extent.createTest(featureName));
            extentMap.put(featureName, parentTestThreadLocal.get());
        }
    }
}