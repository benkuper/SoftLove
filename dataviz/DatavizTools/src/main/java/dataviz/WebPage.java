package dataviz;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import java.io.File;
import java.io.IOException;

/**
 * Created by pierre on 30/11/15.
 */
public class WebPage {

    public WebDriver driver;

    public WebPage(){
        long startTime = System.currentTimeMillis();
        driver = new PhantomJSDriver();
    }

    public void screenshot(String url, String filename) throws IOException {
        long startTime = System.currentTimeMillis();
        driver.get(url);
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(scrFile, new File(filename));
        long finalTime = System.currentTimeMillis() - startTime;
        System.out.println("Screenshot "+filename+" generated from "+url+" in "+finalTime+" ms");
    }

    public void openInBrowser(String url) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        Runtime rt = Runtime.getRuntime();

        if (os.indexOf("win") >= 0) {

            // this doesn't support showing urls in the form of
            // "page.html#nameLink"
            rt.exec("rundll32 url.dll,FileProtocolHandler " + url);

        } else if (os.indexOf("mac") >= 0) {

            rt.exec("open " + url);

        } else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
            rt.exec("xdg-open " + url);
        }
    }
}
