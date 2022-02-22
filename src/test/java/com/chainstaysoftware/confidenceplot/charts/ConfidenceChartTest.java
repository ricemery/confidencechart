/*
 * Copyright (c) 2018 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chainstaysoftware.confidenceplot.charts;

import com.chainstaysoftware.confidenceplot.charts.data.XYConfidenceChartItem;
import com.chainstaysoftware.confidenceplot.charts.data.XYConfidenceItem;
import com.chainstaysoftware.confidenceplot.charts.series.XYConfidenceSeries;
import com.chainstaysoftware.confidenceplot.charts.series.XYConfidenceSeriesBuilder;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;

import java.time.Month;
import java.util.Locale;
import java.util.Random;


/**
 * User: hansolo
 * Date: 06.01.18
 * Time: 11:23
 */
public class ConfidenceChartTest extends Application {
    private static final Random               RND        = new Random();
    private static final Double               AXIS_WIDTH = 25d;
    private              double               mouseX     = -1;
    private              double               mouseY     = -1;
    private              Tooltip              tooltip    = new Tooltip("");
    private XYConfidenceSeries xyConfidenceSeries1;
    private XYConfidenceSeries xyConfidenceSeries2;
    private Axis xAxisBottom;
    private              Axis                 yAxisLeft;
    private XYConfidenceChart<XYConfidenceChartItem> confidenceChart;


    @Override public void init() {
        xyConfidenceSeries1 = createSeries1();
        xyConfidenceSeries2 = createSeries2();

        xAxisBottom = AxisBuilder.create(Orientation.HORIZONTAL, Position.BOTTOM)
                                 .type(AxisType.TEXT)
                                 .prefHeight(AXIS_WIDTH)
                                 .categories("", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                                 .minValue(1)
                                 .maxValue(13)
                                 .autoScale(true)
                                 .axisColor(Color.web("#85949B"))
                                 .tickLabelColor(Color.web("#85949B"))
                                 .tickMarkColor(Color.web("#85949B"))
                                 //.tickMarksVisible(false)
                                 .build();
        AnchorPane.setBottomAnchor(xAxisBottom, 0d);
        AnchorPane.setLeftAnchor(xAxisBottom, AXIS_WIDTH);
        AnchorPane.setRightAnchor(xAxisBottom, AXIS_WIDTH);

        yAxisLeft = AxisBuilder.create(Orientation.VERTICAL, Position.LEFT)
                               .type(AxisType.LINEAR)
                               .prefWidth(AXIS_WIDTH)
                               .minValue(0)
                               .maxValue(1000)
                               .autoScale(true)
                               .axisColor(Color.web("#85949B"))
                               .tickLabelColor(Color.web("#85949B"))
                               .tickMarkColor(Color.web("#85949B"))
                               //.tickMarksVisible(false)
                               .build();
        AnchorPane.setTopAnchor(yAxisLeft, 0d);
        AnchorPane.setBottomAnchor(yAxisLeft, AXIS_WIDTH);
        AnchorPane.setLeftAnchor(yAxisLeft, 0d);

        Grid grid = GridBuilder.create(xAxisBottom, yAxisLeft)
                               .gridLinePaint(Color.web("#384C57"))
                               .minorHGridLinesVisible(false)
                               .mediumHGridLinesVisible(false)
                               .minorVGridLinesVisible(false)
                               .mediumVGridLinesVisible(false)
                               .gridLineDashes(4, 4)
                               .build();

        XYConfidencePane confidenceChartPane = new XYConfidencePane(xyConfidenceSeries1, xyConfidenceSeries2);
        confidenceChartPane.setCrossHairVisible(true);
        confidenceChartPane.addCursorEventListener(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
        });

        confidenceChart = new XYConfidenceChart<>(confidenceChartPane, grid, yAxisLeft, xAxisBottom);
        Tooltip.install(confidenceChart, tooltip);
        tooltip.setAutoHide(true);
        confidenceChart.setOnMouseReleased(e -> {
            int month = (int) mouseX - 1;
            if (month == -1) { month = 11; }
            tooltip.setText("x: " + Month.values()[month] + "\ny: " + String.format(Locale.US, "%.2f", mouseY));
            if (tooltip.isShowing()) {
                tooltip.hide();
            }
            tooltip.show(confidenceChartPane.getScene().getWindow(), e.getScreenX(), e.getScreenY());
        });

    }

    private XYConfidenceSeries<XYConfidenceChartItem> createSeries1() {
        final var y1 = RND.nextDouble() * 300 + 200;
        final var y2 = RND.nextDouble() * 300 + 200;
        final var y3 = RND.nextDouble() * 300 + 200;
        final var y4 = RND.nextDouble() * 300 + 200;
        final var y5 = RND.nextDouble() * 300 + 200;
        final var y6 = RND.nextDouble() * 300 + 200;
        final var y7 = RND.nextDouble() * 300 + 200;
        final var y8 = RND.nextDouble() * 300 + 200;
        final var y9 = RND.nextDouble() * 300 + 200;
        final var y10 = RND.nextDouble() * 300 + 200;
        final var y11 = RND.nextDouble() * 300 + 200;
        final var y12 = RND.nextDouble() * 300 + 200;
        final var p1  = new XYConfidenceChartItem(1, y1, y1 - 50, y1 + 50, "Jan");
        final var p2  = new XYConfidenceChartItem(2, y2, y2 - 50, y2 + 75, "Feb");
        final var p3  = new XYConfidenceChartItem(3, y3, y3 - 25, y3 + 50, "Mar");
        final var p4  = new XYConfidenceChartItem(4, y4, y4 - 30, y4 + 50, "Apr");
        final var p5  = new XYConfidenceChartItem(5, y5, y5 - 40, y5 + 40, "May");
        final var p6  = new XYConfidenceChartItem(6, y6, y6 - 60, y6 + 40, "Jun");
        final var p7  = new XYConfidenceChartItem(7, y7, y7 - 60, y7 + 60, "Jul");
        final var p8  = new XYConfidenceChartItem(8, y8, y8 - 50, y8 + 60, "Aug");
        final var p9  = new XYConfidenceChartItem(9, y9, y9 - 45, y9 + 55, "Sep");
        final var p10 = new XYConfidenceChartItem(10, y10, y10 - 40, y10 + 40, "Oct");
        final var p11 = new XYConfidenceChartItem(11, y11, y11 - 40, y11 + 40, "Nov");
        final var p12 = new XYConfidenceChartItem(12, y12, y12 - 40, y12 + 40, "Dec");


        return XYConfidenceSeriesBuilder.create()
           .items(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12)
           .chartType(ChartType.CONFIDENCE_LINE)
           .fill(Color.web("#00AEF520"))
           .stroke(Color.web("#00AEF5"))
           .symbolFill(Color.web("#00AEF5"))
           .symbolStroke(Color.web("#293C47"))
           .symbolSize(10)
           .strokeWidth(3)
           .symbolsVisible(true)
           .confidenceIntervalFill(Color.CORNFLOWERBLUE)
           .confidenceIntervalStroke(Color.DARKBLUE)
           .confidenceIntervalVisible(true)
           .build();
    }

    private XYConfidenceSeries<XYConfidenceItem> createSeries2() {
        return XYConfidenceSeriesBuilder.create()
           .items(new XYConfidenceChartItem(1, 280, 250, 290, "Jan"),
              new XYConfidenceChartItem(2, 190, 150, 200, "Feb"),
              new XYConfidenceChartItem(3, 280, 270, 300, "Mar"),
              new XYConfidenceChartItem(4, 300, 300, 300, "Apr"),
              new XYConfidenceChartItem(5, 205, 190, 215, "May"),
              new XYConfidenceChartItem(6, 430, 400, 460, "Jun"),
              new XYConfidenceChartItem(7, 380, 370, 390, "Jul"),
              new XYConfidenceChartItem(8, 180, 160, 200, "Aug"),
              new XYConfidenceChartItem(9, 300, 280, 320, "Sep"),
              new XYConfidenceChartItem(10, 440, 400, 440, "Oct"),
              new XYConfidenceChartItem(11, 300, 300, 300, "Nov"),
              new XYConfidenceChartItem(12, 390, 375, 400, "Dec"))
           .chartType(ChartType.CONFIDENCE_SMOOTH_LINE)
           .fill(Color.web("#4EE29B20"))
           .stroke(Color.web("#4EE29B"))
           .symbolFill(Color.web("#4EE29B"))
           .symbolStroke(Color.web("#293C47"))
           .symbolSize(10)
           .strokeWidth(3)
           .symbolsVisible(true)
           .confidenceIntervalFill(Color.LIGHTGREEN)
           .confidenceIntervalStroke(Color.LIGHTGREEN)
           .confidenceIntervalVisible(true)
           .build();
    }


    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(confidenceChart);
        pane.setPadding(new Insets(10));
        pane.setBackground(new Background(new BackgroundFill(Color.web("#293C47"), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane);

        stage.setTitle("Confidence Chart");
        stage.setScene(scene);
        stage.show();

        confidenceChart.getXYPane().getListOfSeries().add(createSeries());
    }

    @Override public void stop() {
        System.exit(0);
    }

    private XYConfidenceSeries<XYConfidenceChartItem> createSeries() {
        return XYConfidenceSeriesBuilder.create()
                                           .items(new XYConfidenceChartItem(1, 600, "Jan"),
                                                  new XYConfidenceChartItem(2, 760, "Feb"),
                                                  new XYConfidenceChartItem(3, 585, "Mar"),
                                                  new XYConfidenceChartItem(4, 410, "Apr"),
                                                  new XYConfidenceChartItem(5, 605, "May"),
                                                  new XYConfidenceChartItem(6, 825, "Jun"),
                                                  new XYConfidenceChartItem(7, 595, "Jul"),
                                                  new XYConfidenceChartItem(8, 300, "Aug"),
                                                  new XYConfidenceChartItem(9, 515, "Sep"),
                                                  new XYConfidenceChartItem(10, 780, "Oct"),
                                                  new XYConfidenceChartItem(11, 570, "Nov"),
                                                  new XYConfidenceChartItem(12, 620, "Dec"))
                                           .chartType(ChartType.CONFIDENCE_SMOOTH_LINE)
                                           .fill(Color.web("#AE00F520"))
                                           .stroke(Color.web("#AE00F5"))
                                           .symbolFill(Color.web("#AE00F5"))
                                           .symbolStroke(Color.web("#293C47"))
                                           .symbolSize(10)
                                           .strokeWidth(3)
                                           .symbolsVisible(true)
                                           .build();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
