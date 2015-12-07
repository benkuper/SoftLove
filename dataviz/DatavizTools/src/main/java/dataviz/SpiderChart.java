package dataviz;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.IntervalBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class SpiderChart {

    /**
     * Create the image spiderChartEmotions.png
     */
    public void spider(String filename, Integer joyVal, Integer trustVal, Integer fearVal, Integer angerVal,
                       Integer sadnessVal, Integer distrustVal, Integer surpriseVal,
                       Integer anticipationVal, Integer intensity) throws IOException {

        String chartName = "Emotions";

        String joy = "Joy";
        String trust = "Trust";
        String fear = "Fear";
        String sadness = "Sadness";
        String anger = "Anger";
        String distrust = "Distrust";
        String surprise = "Surprise";
        String anticipation = "Anticipation";

        //Color codes
        ArrayList<Color> colorCode = new ArrayList<Color>(Arrays.asList(Color.lightGray,Color.blue,Color.green,Color.red));


        DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
        defaultcategorydataset.addValue(joyVal, chartName, joy);
        defaultcategorydataset.addValue(trustVal, chartName, trust);
        defaultcategorydataset.addValue(fearVal, chartName, fear);
        defaultcategorydataset.addValue(sadnessVal, chartName, sadness);
        defaultcategorydataset.addValue(angerVal, chartName, anger);
        defaultcategorydataset.addValue(distrustVal, chartName, distrust);
        defaultcategorydataset.addValue(surpriseVal, chartName, surprise);
        defaultcategorydataset.addValue(anticipationVal, chartName, anticipation);

        SpiderWebPlot plot=new SpiderWebPlot(defaultcategorydataset);
        plot.setSeriesPaint(0, colorCode.get(intensity));
        JFreeChart chart = new JFreeChart(plot);
        chart.setBackgroundPaint( new Color(255,255,255,0) );

        final Plot transparentPlot = (Plot) chart.getPlot();
        transparentPlot.setBackgroundPaint( new Color(255,255,255,0) );
        transparentPlot.setBackgroundImageAlpha(0.0f);

        /*final CategoryItemRenderer renderer = (CategoryItemRenderer) chart.getCategoryPlot().getRenderer();
        renderer.setSeriesVisible(0, true); // default
        renderer.setSeriesVisibleInLegend(0, true);  // default
        */

        int width = 640; /* Width of the image */
        int height = 480; /* Height of the image */
        File pieChart = new File( filename );
        ChartUtilities.saveChartAsPNG(pieChart,chart,width,height,null,true,0);
    }
}
