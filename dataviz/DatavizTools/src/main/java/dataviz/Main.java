package dataviz;

import java.io.IOException;

/**
 * Created by pierre on 30/11/15.
 */
public class Main {
    
    public static void main(String[] args) throws IOException {
        SpiderChart chart = new SpiderChart();
        chart.spider("spiderChartEmotions.png",60,50,40,60,50,30,70,50,0);

        WebPage webpage = new WebPage();
        webpage.openInBrowser("http://www.amazon.fr/Patterns-Enterprise-Application-Architecture-Martin/dp/0321127420/ref=sr_1_3?ie=UTF8&qid=1448893942&sr=8-3&keywords=enterprise+software+architecture");
        webpage.screenshot("http://www.amazon.fr/Patterns-Enterprise-Application-Architecture-Martin/dp/0321127420/ref=sr_1_3?ie=UTF8&qid=1448893942&sr=8-3&keywords=enterprise+software+architecture","screenshot.png");
    }
}
