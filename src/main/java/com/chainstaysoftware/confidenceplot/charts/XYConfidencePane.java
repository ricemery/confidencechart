/*
 * Copyright (c) 2017 by Gerrit Grunwald
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

import com.chainstaysoftware.confidenceplot.charts.data.XYConfidenceItem;
import com.chainstaysoftware.confidenceplot.charts.data.XYItem;
import com.chainstaysoftware.confidenceplot.charts.series.Series;
import com.chainstaysoftware.confidenceplot.charts.series.XYConfidenceSeries;
import com.chainstaysoftware.confidenceplot.charts.event.CursorEvent;
import com.chainstaysoftware.confidenceplot.charts.event.CursorEventListener;
import com.chainstaysoftware.confidenceplot.charts.event.SeriesEventListener;
import com.chainstaysoftware.confidenceplot.charts.tools.Helper;
import com.chainstaysoftware.confidenceplot.charts.tools.TooltipPopup;
import eu.hansolo.toolboxfx.geom.Point;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.chainstaysoftware.confidenceplot.charts.tools.Helper.clamp;


/**
 * Created by hansolo on 16.07.17.
 */
public class XYConfidencePane<T extends XYConfidenceItem> extends Region implements ChartArea {
    private static final double                         PREFERRED_WIDTH  = 250;
    private static final double                         PREFERRED_HEIGHT = 250;
    private static final double                         MINIMUM_WIDTH    = 0;
    private static final double                         MINIMUM_HEIGHT   = 0;
    private static final double                         MAXIMUM_WIDTH    = 4096;
    private static final double                         MAXIMUM_HEIGHT   = 4096;
    private static final double                         MIN_SYMBOL_SIZE  = 2;
    private static final double                         MAX_SYMBOL_SIZE  = 6;
    private static final int                            SUB_DIVISIONS    = 24;
    private static       double                         aspectRatio;
    private              boolean                        keepAspect;
    private              double                         size;
    private              double                         width;
    private              double                         height;
    private              Paint                          _chartBackground;
    private              ObjectProperty<Paint>          chartBackground;
    private              ObservableList<XYConfidenceSeries<T>>    listOfSeries;
    private              Canvas                         canvas;
    private              GraphicsContext                ctx;
    private              Canvas                         cursorCanvas;
    private              GraphicsContext                cursorCtx;
    private              double                         cursorX;
    private              double                         cursorY;
    private              double                         scaleX;
    private              double                         scaleY;
    private              double                         symbolSize;
    private              int                            noOfBands;
    private              double                         _lowerBoundX;
    private              DoubleProperty                 lowerBoundX;
    private              double                         _upperBoundX;
    private              DoubleProperty                 upperBoundX;
    private              double                         _lowerBoundY;
    private              DoubleProperty                 lowerBoundY;
    private              double                         _upperBoundY;
    private              DoubleProperty                 upperBoundY;
    private              boolean                        referenceZero;
    private              double                         _thresholdY;
    private              DoubleProperty                 thresholdY;
    private              boolean                        _thresholdYVisible;
    private              BooleanProperty                thresholdYVisible;
    private              Color                          _thresholdYColor;
    private              ObjectProperty<Color>          thresholdYColor;
    private              boolean                        _crossHairVisible;
    private              BooleanProperty                crossHairVisible;
    private              Color                          _crossHairColor;
    private              ObjectProperty<Color>          crossHairColor;
    private              TooltipPopup                   popup;
    private              SeriesEventListener            seriesListener;
    private              EventHandler<MouseEvent>       mouseHandler;
    private              List<CursorEventListener>      cursorEventListeners;



    // ******************** Constructors **************************************
    public XYConfidencePane(final List<XYConfidenceSeries<T>> SERIES) {
        this(Color.TRANSPARENT, 1, SERIES.toArray(new XYConfidenceSeries[0]));
    }
    public XYConfidencePane(final XYConfidenceSeries<T>... SERIES) {
        this(Color.TRANSPARENT, 1,  SERIES);
    }
    public XYConfidencePane(final int BANDS, final XYConfidenceSeries<T>... SERIES) {
        this(Color.TRANSPARENT, BANDS, SERIES);
    }
    public XYConfidencePane(final Paint BACKGROUND, final int BANDS, final XYConfidenceSeries<T>... SERIES) {
        getStylesheets().add(XYConfidencePane.class.getResource("chart.css").toExternalForm());
        aspectRatio          = PREFERRED_HEIGHT / PREFERRED_WIDTH;
        cursorEventListeners = new CopyOnWriteArrayList<>();
        keepAspect           = false;
        _chartBackground     = BACKGROUND;
        listOfSeries         = FXCollections.observableArrayList(SERIES);
        scaleX               = 1;
        scaleY               = 1;
        symbolSize           = 2;
        noOfBands            = clamp(1, 5, BANDS);
        _lowerBoundX         = 0;
        _upperBoundX         = 100;
        _lowerBoundY         = 0;
        _upperBoundY         = 100;
        referenceZero        = true;
        _thresholdY          = 100;
        _thresholdYVisible   = false;
        _thresholdYColor     = Color.RED;
        _crossHairVisible    = false;
        _crossHairColor      = Color.GRAY;
        cursorX              = -1;
        cursorY              = -1;
        popup                = new TooltipPopup(2000);
        seriesListener       = e -> redraw();
        mouseHandler         = e -> {
            cursorX = e.getX();
            cursorY = e.getY();
            drawCursor();
            for (XYConfidenceSeries<T> series : listOfSeries) {
                double  radius = series.getSymbolSize() * 0.5;
                for (T item : series.getItems()) {
                    Point2D pointInScene = localToScene(new Point2D((item.getX() - getLowerBoundX()) * scaleX , height - (item.getY() - getLowerBoundY()) * scaleY));
                    if (Helper.isInCircle(e.getSceneX(), e.getSceneY(), pointInScene.getX(), pointInScene.getY(), radius) && !item.getTooltipText().isEmpty() && !popup.getText().equals(item.getTooltipText())) {
                        popup.setX(e.getScreenX());
                        popup.setY(e.getScreenY() - popup.getHeight());
                        popup.setText(item.getTooltipText());
                        popup.animatedShow(getScene().getWindow());
                        break;
                    }
                }
            }
        };
        popup.setOnHiding(e -> popup.setText(""));

        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void initGraphics() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 || Double.compare(getWidth(), 0.0) <= 0 ||
            Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        getStyleClass().setAll("chart", "xy-chart");

        canvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ctx    = canvas.getGraphicsContext2D();

        cursorCanvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        cursorCanvas.setMouseTransparent(true);
        Helper.enableNode(cursorCanvas, true);
        cursorCtx    = cursorCanvas.getGraphicsContext2D();

        getChildren().setAll(canvas, cursorCanvas);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        listOfSeries.addListener((ListChangeListener<XYConfidenceSeries<T>>) c -> {
            while(c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(series -> series.setOnSeriesEvent(seriesListener));
                } else if (c.wasRemoved()) {
                    c.getRemoved().forEach(series -> series.removeSeriesEventListener(seriesListener));
                }
            }
            redraw();
        });
        listOfSeries.forEach(series -> {
            if (null != series) {
                series.setOnSeriesEvent(seriesEvent -> redraw());
            }
        });
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, mouseHandler);
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double HEIGHT)  { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH)  { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT)  { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH)  { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public void dispose() {
        canvas.removeEventHandler(MouseEvent.MOUSE_MOVED, mouseHandler);
        removeAllCursorEventListeners();
    }

    public Paint getChartBackground() { return null == chartBackground ? _chartBackground : chartBackground.get(); }
    public void setChartBackground(final Paint PAINT) {
        if (null == chartBackground) {
            _chartBackground = PAINT;
            redraw();
        } else {
            chartBackground.set(PAINT);
        }
    }
    public ObjectProperty<Paint> chartBackgroundProperty() {
        if (null == chartBackground) {
            chartBackground = new ObjectPropertyBase<Paint>(_chartBackground) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return XYConfidencePane.this; }
                @Override public String getName() { return "chartBackground"; }
            };
            _chartBackground = null;
        }
        return chartBackground;
    }

    public int getNoOfBands() { return noOfBands; }
    public void setNoOfBands(final int BANDS) {
        noOfBands = clamp(1, 5, BANDS);
        redraw();
    }

    public double getLowerBoundX() { return null == lowerBoundX ? _lowerBoundX : lowerBoundX.get(); }
    public void setLowerBoundX(final double VALUE) {
        if (null == lowerBoundX) {
            _lowerBoundX = VALUE;
            resize();
        } else {
            lowerBoundX.set(VALUE);
        }
    }
    public DoubleProperty lowerBoundXProperty() {
        if (null == lowerBoundX) {
            lowerBoundX = new DoublePropertyBase(_lowerBoundX) {
                @Override protected void invalidated() { resize(); }
                @Override public Object getBean() { return XYConfidencePane.this; }
                @Override public String getName() { return "lowerBoundX"; }
            };
        }
        return lowerBoundX;
    }

    public double getUpperBoundX() { return null == upperBoundX ? _upperBoundX : upperBoundX.get(); }
    public void setUpperBoundX(final double VALUE) {
        if (null == upperBoundX) {
            _upperBoundX = VALUE;
            resize();
        } else {
            upperBoundX.set(VALUE);
        }
    }
    public DoubleProperty upperBoundXProperty() {
        if (null == upperBoundX) {
            upperBoundX = new DoublePropertyBase(_upperBoundX) {
                @Override protected void invalidated() { resize(); }
                @Override public Object getBean() { return XYConfidencePane.this; }
                @Override public String getName() { return "upperBoundX"; }
            };
        }
        return upperBoundX;
    }

    public double getLowerBoundY() { return null == lowerBoundY ? _lowerBoundY : lowerBoundY.get(); }
    public void setLowerBoundY(final double VALUE) {
        if (null == lowerBoundY) {
            _lowerBoundY = VALUE;
            resize();
        } else {
            lowerBoundY.set(VALUE);
        }
    }
    public DoubleProperty lowerBoundYProperty() {
        if (null == lowerBoundY) {
            lowerBoundY = new DoublePropertyBase(_lowerBoundY) {
                @Override protected void invalidated() { resize(); }
                @Override public Object getBean() { return XYConfidencePane.this; }
                @Override public String getName() { return "lowerBoundY"; }
            };
        }
        return lowerBoundY;
    }

    public double getUpperBoundY() { return null == upperBoundY ? _upperBoundY : upperBoundY.get(); }
    public void setUpperBoundY(final double VALUE) {
        if (null == upperBoundY) {
            _upperBoundY = VALUE;
            resize();
        } else {
            upperBoundY.set(VALUE);
        }
    }
    public DoubleProperty upperBoundYProperty() {
        if (null == upperBoundY) {
            upperBoundY = new DoublePropertyBase(_upperBoundY) {
                @Override protected void invalidated() { resize(); }
                @Override public Object getBean() { return XYConfidencePane.this; }
                @Override public String getName() { return "upperBoundY"; }
            };
        }
        return upperBoundY;
    }

    public boolean isReferenceZero() { return referenceZero; }
    public void setReferenceZero(final boolean IS_ZERO) {
        referenceZero = IS_ZERO;
        redraw();
    }

    public double getRangeX() {  return getUpperBoundX() - getLowerBoundX();  }
    public double getRangeY() { return getUpperBoundY() - getLowerBoundY(); }

    public double getDataMinX() { return listOfSeries.stream().mapToDouble(XYConfidenceSeries::getMinX).min().getAsDouble(); }
    public double getDataMaxX() { return listOfSeries.stream().mapToDouble(XYConfidenceSeries::getMaxX).max().getAsDouble(); }

    public double getDataMinY() { return listOfSeries.stream().mapToDouble(XYConfidenceSeries::getMinY).min().getAsDouble(); }
    public double getDataMaxY() { return listOfSeries.stream().mapToDouble(XYConfidenceSeries::getMaxY).max().getAsDouble(); }

    public double getDataRangeX() { return getDataMaxX() - getDataMinX(); }
    public double getDataRangeY() { return getDataMaxY() - getDataMinY(); }
    
    public List<XYConfidenceSeries<T>> getListOfSeries() { return listOfSeries; }

    public double getThresholdY() { return null == thresholdY ? _thresholdY : thresholdY.get(); }
    public void setThresholdY(final double THRESHOLD) {
        if (null == thresholdY) {
            _thresholdY = THRESHOLD;
            redraw();
        } else {
            thresholdY.set(THRESHOLD);
        }
    }
    public DoubleProperty thresholdYProperty() {
        if (null == thresholdY) {
            thresholdY = new DoublePropertyBase(_thresholdY) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return XYConfidencePane.this; }
                @Override public String getName() { return "thresholdY"; }
            };
        }
        return thresholdY;
    }

    public boolean isThresholdYVisible() { return null == thresholdYVisible ? _thresholdYVisible : thresholdYVisible.get(); }
    public void setThresholdYVisible(final boolean VISIBLE) {
        if (null == thresholdYVisible) {
            _thresholdYVisible = VISIBLE;
            redraw();
        } else {
            thresholdYVisible.set(VISIBLE);
        }
    }
    public BooleanProperty thresholdYVisibleProperty() {
        if (null == thresholdYVisible) {
            thresholdYVisible = new BooleanPropertyBase(_thresholdYVisible) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return XYConfidencePane.this; }
                @Override public String getName() { return "thresholdYVisible"; }
            };
        }
        return thresholdYVisible;
    }

    public Color getThresholdYColor() { return null == thresholdYColor ? _thresholdYColor : thresholdYColor.get(); }
    public void setThresholdYColor(final Color COLOR) {
        if (null == thresholdYColor) {
            _thresholdYColor = COLOR;
            redraw();
        } else {
            thresholdYColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> thresholdYColorProperty() {
        if (null == thresholdYColor) {
            thresholdYColor = new ObjectPropertyBase<Color>(_thresholdYColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return XYConfidencePane.this; }
                @Override public String getName() { return "thresholdYColor"; }
            };
            _thresholdYColor = null;
        }
        return thresholdYColor;
    }

    public boolean isCrossHairVisible() { return null == crossHairVisible ? _crossHairVisible : crossHairVisible.get(); }
    public void setCrossHairVisible(final boolean VISIBLE) {
        if (null == crossHairVisible) {
            _crossHairVisible = VISIBLE;
            Helper.enableNode(cursorCanvas, VISIBLE);
            drawCursor();
        } else {
            crossHairVisible.set(VISIBLE);
        }
    }
    public BooleanProperty crossHairVisibleProperty() {
        if (null == crossHairVisible) {
            crossHairVisible = new BooleanPropertyBase(_crossHairVisible) {
                @Override protected void invalidated() {
                    Helper.enableNode(cursorCanvas, get());
                    drawCursor();
                }
                @Override public Object getBean() { return XYConfidencePane.this; }
                @Override public String getName() { return "crossHairVisible"; }
            };
        }
        return crossHairVisible;
    }

    public Color getCrossHairColor() { return null == crossHairColor ? _crossHairColor : crossHairColor.get(); }
    public void setCrossHairColor(final Color COLOR) {
        if (null == crossHairColor) {
            _crossHairColor = COLOR;
            drawCursor();
        } else {
            crossHairColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> crossHairColorProperty() {
        if (null == crossHairColor) {
            crossHairColor = new ObjectPropertyBase<>(_crossHairColor) {
                @Override public Object getBean() { return XYConfidencePane.this; }
                @Override public String getName() { return "crossHairColor"; }
            };
            _crossHairColor = null;
        }
        return crossHairColor;
    }


    // ******************** Draw Chart ****************************************
    protected void redraw() {
        drawChart();
        drawCursor();
    }

    private void drawChart() {
        if (null == listOfSeries || listOfSeries.isEmpty()) { return; }

        ctx.clearRect(0, 0, width, height);
        ctx.setFill(getChartBackground());
        ctx.fillRect(0, 0, width, height);

        if (listOfSeries.size() == 2) {
            boolean     deltaChart = false;
            ChartType[] chartTypes = new ChartType[2];
            int         count      = 0;
            for(Series series : listOfSeries) {
                chartTypes[count] = series.getChartType();
                count++;
            }
        }

        for (XYConfidenceSeries<T> series : listOfSeries) {
            final ChartType TYPE        = series.getChartType();
            final boolean   SHOW_POINTS = series.getSymbolsVisible();
            switch (TYPE) {
                case CONFIDENCE_LINE -> drawConfidenceLine(series, SHOW_POINTS);
                case CONFIDENCE_SMOOTH_LINE -> drawConfidenceSmoothLine(series, SHOW_POINTS);
            }
        }
    }

    private void drawCursor() {
        cursorCtx.clearRect(0, 0, width, height);
        if (isCrossHairVisible()) {
            cursorCtx.setStroke(getCrossHairColor());
            cursorCtx.strokeLine(0, cursorY, width, cursorY);
            cursorCtx.strokeLine(cursorX, 0, cursorX, height);

            double x = cursorX / scaleX + getLowerBoundX();
            double y = ((cursorY - height) / scaleY - getLowerBoundY()) * -1;
            fireCursorEvent(new CursorEvent(x, y));
        }
    }

    private void drawConfidenceLine(final XYConfidenceSeries<T> SERIES, final boolean SHOW_POINTS) {
        final double LOWER_BOUND_X = getLowerBoundX();
        final double LOWER_BOUND_Y = getLowerBoundY();
        List<T> items    = SERIES.getItems();
        double  oldX     = (items.get(0).getX() - LOWER_BOUND_X) * scaleX;
        double  oldY     = height - (items.get(0).getY() - LOWER_BOUND_Y) * scaleY;
        boolean wasEmpty = items.get(0).isEmptyItem();

        // draw confidence interval
        if (SERIES.getConfidenceIntervalVisible()) {
            ctx.setFill(SERIES.getConfidenceIntervalFill());
            ctx.setStroke(SERIES.getConfidenceIntervalStroke());
            ctx.setLineWidth(0.5);
            ctx.beginPath();
            final double startX = (items.get(0).getX() - LOWER_BOUND_X) * scaleX;
            final double startY = height - (items.get(0).getYMax() - LOWER_BOUND_Y) * scaleY;
            ctx.moveTo(startX, startY);
            for (T t : items) {
                double x = (t.getX() - LOWER_BOUND_X) * scaleX;
                double y = height - (t.getYMax() - LOWER_BOUND_Y) * scaleY;
                ctx.lineTo(x, y);
            }
            for (int i = items.size() - 1; i >= 0; i--) {
                double x       = (items.get(i).getX() - LOWER_BOUND_X) * scaleX;
                double y       = height - (items.get(i).getYMin() - LOWER_BOUND_Y) * scaleY;
                ctx.lineTo(x, y);
            }
            ctx.lineTo(startX, startY);
            ctx.fill();
            ctx.stroke();
        }

        // Draw x/y line.
        ctx.setLineWidth(SERIES.getStrokeWidth() > -1 ? SERIES.getStrokeWidth() : size * 0.0025);
        ctx.setStroke(SERIES.getStroke());
        ctx.setFill(Color.TRANSPARENT);
        for (T item : SERIES.getItems()) {
            double x        = (item.getX() - LOWER_BOUND_X) * scaleX;
            double y        = height - (item.getY() - LOWER_BOUND_Y) * scaleY;
            boolean isEmpty = item.isEmptyItem();
            if (!isEmpty && !wasEmpty) { ctx.strokeLine(oldX, oldY, x, y); }
            oldX     = x;
            oldY     = y;
            wasEmpty = isEmpty;
        }

        if (SHOW_POINTS) { drawSymbols(SERIES); }
    }

    private void drawConfidenceSmoothLine(final XYConfidenceSeries<T> SERIES, final boolean SHOW_POINTS) {
        final double LOWER_BOUND_X = getLowerBoundX();
        final double LOWER_BOUND_Y = getLowerBoundY();

        ctx.setLineWidth(SERIES.getStrokeWidth() > -1 ? SERIES.getStrokeWidth() : size * 0.0025);
        ctx.setStroke(SERIES.getStroke());
        ctx.setFill(Color.TRANSPARENT);

        List<Point> points = new ArrayList<>(SERIES.getItems().size());
        SERIES.getItems().forEach(item -> points.add(new Point(item.getX(), item.getY(), item.isEmptyItem())));
        List<Point> yMinPoints = SERIES.getItems().stream()
           .map(item -> new Point(item.getX(), item.getYMin()))
           .toList();
        List<Point> yMaxPoints = SERIES.getItems().stream()
           .map(item -> new Point(item.getX(), item.getYMax())).toList();

        Point[] interpolatedPoints = Helper.subdividePoints(points.toArray(new Point[0]), SUB_DIVISIONS);
        Point[] interpolatedMinPoints = Helper.subdividePoints(yMinPoints.toArray(new Point[0]), SUB_DIVISIONS);
        Point[] interpolatedMaxPoints = Helper.subdividePoints(yMaxPoints.toArray(new Point[0]), SUB_DIVISIONS);

        // Draw confidence interval
        if (SERIES.getConfidenceIntervalVisible()) {
            ctx.setFill(SERIES.getConfidenceIntervalFill());
            ctx.setStroke(SERIES.getConfidenceIntervalStroke());
            ctx.setLineWidth(0.5);
            ctx.beginPath();
            final double startX = (interpolatedMaxPoints[0].getX() - LOWER_BOUND_X) * scaleX;
            final double startY = height - (interpolatedMaxPoints[0].getY() - LOWER_BOUND_Y) * scaleY;
            ctx.moveTo(startX, startY);
            for (int i = 0; i < interpolatedMaxPoints.length; i++) {
                double x = (interpolatedMaxPoints[i].getX() - LOWER_BOUND_X) * scaleX;
                double y = height - (interpolatedMaxPoints[i].getY() - LOWER_BOUND_Y) * scaleY;
                ctx.lineTo(x, y);
            }
            for (int i = interpolatedMinPoints.length - 1; i >= 0; i--) {
                double x = (interpolatedMinPoints[i].getX() - LOWER_BOUND_X) * scaleX;
                double y = height - (interpolatedMinPoints[i].getY() - LOWER_BOUND_Y) * scaleY;
                ctx.lineTo(x, y);
            }
            ctx.lineTo(startX, startY);
            ctx.fill();
            ctx.stroke();
        }

        // Draw x/y line
        ctx.beginPath();
        for(Point p : interpolatedPoints) {
            if (p.isEmpty()) {
                ctx.moveTo((p.getX() - LOWER_BOUND_X) * scaleX, height - (p.getY() - LOWER_BOUND_Y) * scaleY);
            } else {
                ctx.lineTo((p.getX() - LOWER_BOUND_X) * scaleX, height - (p.getY() - LOWER_BOUND_Y) * scaleY);
            }
        }
        ctx.stroke();

        if (SHOW_POINTS) { drawSymbols(SERIES); }
    }

    private void drawSymbols(final XYConfidenceSeries<T> SERIES) {
        final double LOWER_BOUND_X = getLowerBoundX();
        final double LOWER_BOUND_Y = getLowerBoundY();
        Symbol       seriesSymbol  = SERIES.getSymbol();
        Color        symbolFill    = SERIES.getSymbolFill();
        Color        symbolStroke  = SERIES.getSymbolStroke();
        double       size          = SERIES.getSymbolSize() > -1 ? SERIES.getSymbolSize() : symbolSize;
        for (T item : SERIES.getItems()) {
            double x          = (item.getX() - LOWER_BOUND_X) * scaleX;
            double y          = height - (item.getY() - LOWER_BOUND_Y) * scaleY;
            Symbol itemSymbol = item.getSymbol();
            if (item.isEmptyItem()) { continue; }
            if (Symbol.NONE == itemSymbol) {
                drawSymbol(x, y, symbolFill, symbolStroke, seriesSymbol, size);
            } else {
                drawSymbol(x, y, item.getFill(), item.getStroke(), itemSymbol, size);
            }
        }
    }

    private void drawSymbol(final double X, final double Y, final Paint FILL, final Paint STROKE, final Symbol SYMBOL, final double SYMBOL_SIZE) {
        double halfSymbolSize = SYMBOL_SIZE * 0.5;
        ctx.save();
        switch(SYMBOL) {
            case NONE:
                break;
            case SQUARE:
                ctx.setStroke(STROKE);
                ctx.setFill(FILL);
                ctx.fillRect(X - halfSymbolSize, Y - halfSymbolSize, SYMBOL_SIZE, SYMBOL_SIZE);
                ctx.strokeRect(X - halfSymbolSize, Y - halfSymbolSize, SYMBOL_SIZE, SYMBOL_SIZE);
                break;
            case TRIANGLE:
                ctx.setStroke(STROKE);
                ctx.setFill(FILL);
                ctx.beginPath();
                ctx.moveTo(X, Y - halfSymbolSize);
                ctx.lineTo(X + halfSymbolSize, Y + halfSymbolSize);
                ctx.lineTo(X - halfSymbolSize, Y + halfSymbolSize);
                ctx.lineTo(X, Y - halfSymbolSize);
                ctx.closePath();
                ctx.fill();
                ctx.stroke();
                break;
            case STAR:
                ctx.setStroke(STROKE);
                ctx.setFill(null);
                ctx.strokeLine(X - halfSymbolSize, Y, X + halfSymbolSize, Y);
                ctx.strokeLine(X, Y - halfSymbolSize, X, Y + halfSymbolSize);
                ctx.strokeLine(X - halfSymbolSize, Y - halfSymbolSize, X + halfSymbolSize, Y + halfSymbolSize);
                ctx.strokeLine(X + halfSymbolSize, Y - halfSymbolSize, X - halfSymbolSize, Y + halfSymbolSize);
                break;
            case CROSS:
                ctx.setStroke(STROKE);
                ctx.setFill(null);
                ctx.strokeLine(X - halfSymbolSize, Y, X + halfSymbolSize, Y);
                ctx.strokeLine(X, Y - halfSymbolSize, X, Y + halfSymbolSize);
                break;
            case CIRCLE:
            default    :
                ctx.setStroke(STROKE);
                ctx.setFill(FILL);
                ctx.fillOval(X - halfSymbolSize, Y - halfSymbolSize, SYMBOL_SIZE, SYMBOL_SIZE);
                ctx.strokeOval(X - halfSymbolSize, Y - halfSymbolSize, SYMBOL_SIZE, SYMBOL_SIZE);
                break;
        }
        ctx.restore();
    }


    // ******************** Event Handling ************************************
    public void addCursorEventListener(final CursorEventListener LISTENER) {
        if (cursorEventListeners.contains(LISTENER)) { return; }
        cursorEventListeners.add(LISTENER);
    }
    public void removeCursorEventListener(final CursorEventListener LISTENER) {
        if (cursorEventListeners.contains(LISTENER)) {
            cursorEventListeners.remove(LISTENER);
        }
    }
    public void removeAllCursorEventListeners() { cursorEventListeners.clear(); }

    public void fireCursorEvent(final CursorEvent EVT) {
        cursorEventListeners.forEach(listener -> listener.handleCursorEvent(EVT));
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth(); // - getInsets().getLeft() - getInsets().getRight();
        height = getHeight(); // - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (keepAspect) {
            if (aspectRatio * width > height) {
                width = 1 / (aspectRatio / height);
            } else if (1 / (aspectRatio / height) > width) {
                height = aspectRatio * width;
            }
        }

        if (width > 0 && height > 0) {
            canvas.setWidth(width);
            canvas.setHeight(height);
            canvas.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            cursorCanvas.setWidth(width);
            cursorCanvas.setHeight(height);
            cursorCanvas.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            symbolSize = clamp(MIN_SYMBOL_SIZE, MAX_SYMBOL_SIZE, size * 0.016);

            scaleX = width / getRangeX();
            scaleY = height / getRangeY();

            redraw();
        }
    }
}
