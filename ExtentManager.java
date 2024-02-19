package com.automation.qa.isafe.report;

import com.automation.qa.isafe.driver.DriverManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static com.automation.qa.isafe.cucumber.util.CustomAbstractTestNGCucumberTests.child;
import static com.automation.qa.isafe.driver.DriverManager.driver;

public class ExtentManager {
    private static ExtentReports extent;
    public static String Path = System.getProperty("user.dir") + "/TestResults/" + date() + "/" + "TestClass-Report (" + time() + ").html";

    public static ExtentReports getInstance() {
        if (null == extent) createInstance(Path);
        return extent;
    }

    private static void createInstance(String fileName) {
        try {
            ExtentSparkReporter htmlReporter = new ExtentSparkReporter(fileName);

            //------ Extent Spark Report Configuration ------//
            htmlReporter.viewConfigurer().viewOrder().as(new ViewName[]{
                    ViewName.TEST,
                    ViewName.CATEGORY,
                    ViewName.DEVICE,
                    ViewName.EXCEPTION,
                    ViewName.DASHBOARD,
                    ViewName.LOG,
            }).apply();

            htmlReporter.config().setTimelineEnabled(true);
            htmlReporter.config().setTheme(Theme.DARK);
            htmlReporter.config().setDocumentTitle(fileName);
            htmlReporter.config().setEncoding("utf-8");
            htmlReporter.config().setReportName("MW Automation Report");
            htmlReporter.config().setDocumentTitle("MW Automation Report");

            extent = new ExtentReports();
            extent.attachReporter(htmlReporter);
            System.out.println("Successfully created the extent report instance...");
        } catch (Exception e) {
            System.out.println("Failing in Create Instance of Extent Manager...");
        }
    }

    public static String date() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy EEE");
        return dateFormat.format(date);
    }

    public static String time() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH-mm-ss");
        return dateFormat.format(date);
    }

    public static void fail(String description) {
        if (driver != null) {
            try {
                addScreenCaptureOnFailure(DriverManager.getDriver());
            } catch (Exception ignored) {
            }
        } else {
            child.get().fail(description);
        }
        Assert.fail(description);
    }

    public static void warning(String description) {
        child.get().warning(description);
    }

    public static void pass(String description) {
        child.get().pass(description);
    }

    public static void logTitleInExtentReport(String message) {
        child.get().pass(MarkupHelper.createLabel(message, ExtentColor.BROWN));
    }

    public static void addScreenCaptureOnFailure(WebDriver driver) {
        child.get().fail(MediaEntityBuilder.createScreenCaptureFromBase64String(getBase64(driver)).build());
    }

    public static String getBase64(WebDriver driver) {
        String base = "";
        try {
            base = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return base;
    }

    public static void logHeadersInExtentReport(Response response) {

        String tableAppender = "";
        for (Header header : response.getHeaders()) {
            String headerName = header.getName();
            String headerValue = header.getValue();

            tableAppender = tableAppender + "<tr>\n" +
                    "    <td>" + headerName + "</td>\n" +
                    "    <td>" + headerValue + "</td>\n" +
                    "</tr>";
        }

        String headerCode = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<details>\n" +
                "<summary>Click here to view the  \"Response Headers\"</summary>\n" +
                "<table>\n" +
                "<br>" +
                "  <tr>\n" +
                "    <th>Header</th>\n" +
                "    <th>Value</th>\n" +
                "  </tr>\n" +
                "  " + tableAppender +
                "</table>\n" +
                "</details>\n" +
                "</body>\n" +
                "</html>\n";

        pass(headerCode);
    }

    public static void createResponseBodyTabInExtentReport(String responseBody, String status) {
        Random random = new Random();
        int idValue = random.nextInt(9999) + random.nextInt(9999);

        String data = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Open Text in New Window</title>\n" +
                "    <p id=\"" + idValue + "\" style=\"display: none;\">" + responseBody + "</p>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <a href=\"#\" onclick=\"openTextInNewWindow('" + idValue + "')\">Response Body</a>" +
                "</body>\n" +
                "</html>";

        String javaScriptFunction = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <script>\n" +
                "        function openTextInNewWindow(value) { \n" +
                "            var newWindow = window.open(\"\", \"_blank\", \"width=500,height=500\");\n" +
                "            var jsonObject = JSON.parse(document.getElementById(value).textContent);\n" +
                "            var jsonString = JSON.stringify(jsonObject, null, 4);\n" +
                "            newWindow.document.write(\"<pre>\" + jsonString + \"</pre>\");\n" +
                "        }\n" +
                "    </script>\n" +
                "</head>\n" +
                "</html>\n";

        if (status.equals("Pass")) {
            pass(data + javaScriptFunction);
        } else {
            fail(data + javaScriptFunction);
        }
    }
}