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

package com.chainstaysoftware.confidenceplot.charts.series;

import com.chainstaysoftware.confidenceplot.charts.ChartType;
import com.chainstaysoftware.confidenceplot.charts.Symbol;
import com.chainstaysoftware.confidenceplot.charts.XYConfidencePane;
import com.chainstaysoftware.confidenceplot.charts.data.XYConfidenceItem;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Comparator;
import java.util.List;


/**
 * Created by hansolo on 16.07.17.
 */
public class XYConfidenceSeries<T extends XYConfidenceItem> extends Series {

    private              Paint                          _confidenceIntervalFill;
    private              ObjectProperty<Paint>          confidenceIntervalFill;
    private              Paint                          _confidenceIntervalStroke;
    private              ObjectProperty<Paint>          confidenceIntervalStroke;
    private              boolean                        _confidenceIntervalVisible;
    private              BooleanProperty                confidenceIntervalVisible;

    // ******************** Constructors **************************************
    public XYConfidenceSeries(final List<T> ITEMS, final ChartType TYPE) {
        this(ITEMS, TYPE, "", Color.TRANSPARENT, Color.BLACK, Symbol.CIRCLE,true);
    }
    public XYConfidenceSeries(final List<T> ITEMS, final ChartType TYPE, final boolean SHOW_POINTS) {
        this(ITEMS, TYPE, "", Color.TRANSPARENT, Color.BLACK, Symbol.CIRCLE, SHOW_POINTS);
    }
    public XYConfidenceSeries(final List<T> ITEMS, final ChartType TYPE, final Paint STROKE) {
        this(ITEMS, TYPE, "", Color.TRANSPARENT, STROKE, Symbol.CIRCLE,true);
    }
    public XYConfidenceSeries(final List<T> ITEMS, final ChartType TYPE, final Paint FILL, final Paint STROKE) {
        this(ITEMS, TYPE, "", FILL, STROKE, Symbol.CIRCLE,true);
    }
    public XYConfidenceSeries(final List<T> ITEMS, final ChartType TYPE, final String NAME) {
        this(ITEMS, TYPE, NAME, Color.TRANSPARENT, Color.BLACK, Symbol.CIRCLE,true);
    }
    public XYConfidenceSeries(final List<T> ITEMS, final ChartType TYPE, final String NAME,
                              final Paint FILL, final Paint STROKE, final boolean SHOW_POINTS) {
        this(ITEMS, TYPE, NAME, FILL, STROKE, Symbol.CIRCLE, SHOW_POINTS);
    }
    public XYConfidenceSeries(final List<T> ITEMS, final ChartType TYPE, final String NAME,
                              final Paint FILL, final Paint STROKE, final Symbol SYMBOL,
                              final boolean SYMBOLS_VISIBLE) {
        super(ITEMS, TYPE, NAME, FILL, STROKE, SYMBOL);
        setSymbolsVisible(SYMBOLS_VISIBLE);
    }
    public XYConfidenceSeries(final List<T> ITEMS, final ChartType TYPE, final String NAME,
                              final Paint FILL, final Paint STROKE, final Symbol SYMBOL,
                              final boolean SYMBOLS_VISIBLE, final Paint CONFIDENCE_FILL,
                              final Paint CONFIDENCE_STROKE, final boolean CONFIDENCE_VISIBLE) {
        super(ITEMS, TYPE, NAME, FILL, STROKE, SYMBOL);
        setSymbolsVisible(SYMBOLS_VISIBLE);
        setConfidenceIntervalFill(CONFIDENCE_FILL);
        setConfidenceIntervalStroke(CONFIDENCE_STROKE);
        setConfidenceIntervalVisible(CONFIDENCE_VISIBLE);
    }

    // ******************** Methods *******************************************
    @Override public ObservableList<T> getItems() { return items; }

    public double getMinX() { return getItems().stream().min(Comparator.comparingDouble(T::getX)).get().getX(); }
    public double getMaxX() { return getItems().stream().max(Comparator.comparingDouble(T::getX)).get().getX(); }

    public double getMinY() { return getItems().stream().min(Comparator.comparingDouble(T::getY)).get().getY(); }
    public double getMaxY() { return getItems().stream().max(Comparator.comparingDouble(T::getY)).get().getY(); }

    public double getRangeX() { return getMaxX() - getMinX(); }
    public double getRangeY() { return getMaxY() - getMinY(); }

    public double getSumOfXValues() { return getItems().stream().mapToDouble(T::getX).sum(); }
    public double getSumOfYValues() { return getItems().stream().mapToDouble(T::getY).sum(); }

    public Paint getConfidenceIntervalFill() { return null == confidenceIntervalFill ? _confidenceIntervalFill : confidenceIntervalFill.get(); }
    public void setConfidenceIntervalFill(final Paint CONFIDENCE_INTERVAL_FILL) {
        if (null == confidenceIntervalFill) {
            _confidenceIntervalFill = CONFIDENCE_INTERVAL_FILL;
            refresh();
        } else {
            confidenceIntervalFill.set(CONFIDENCE_INTERVAL_FILL);
        }
    }
    public ObjectProperty<Paint> confidenceIntervalFillProperty() {
        if (null == confidenceIntervalFill) {
            confidenceIntervalFill = new ObjectPropertyBase<>(_confidenceIntervalFill) {
                @Override protected void invalidated() { refresh(); }
                @Override public Object getBean() { return XYConfidenceSeries.this; }
                @Override public String getName() { return "confidenceIntervalFill"; }
            };
            _confidenceIntervalFill = null;
        }
        return confidenceIntervalFill;
    }

    public Paint getConfidenceIntervalStroke() { return null == confidenceIntervalStroke ? _confidenceIntervalStroke : confidenceIntervalStroke.get(); }
    public void setConfidenceIntervalStroke(final Paint CONFIDENCE_INTERVAL_STROKE) {
        if (null == confidenceIntervalStroke) {
            _confidenceIntervalStroke = CONFIDENCE_INTERVAL_STROKE;
            refresh();
        } else {
            confidenceIntervalStroke.set(CONFIDENCE_INTERVAL_STROKE);
        }
    }
    public ObjectProperty<Paint> confidenceIntervalStrokeProperty() {
        if (null == confidenceIntervalStroke) {
            confidenceIntervalStroke = new ObjectPropertyBase<>(_confidenceIntervalStroke) {
                @Override protected void invalidated() { refresh(); }
                @Override public Object getBean() { return XYConfidenceSeries.this; }
                @Override public String getName() { return "confidenceIntervalStroke"; }
            };
            _confidenceIntervalStroke = null;
        }
        return confidenceIntervalStroke;
    }

    public boolean getConfidenceIntervalVisible() { return null == confidenceIntervalVisible ? _confidenceIntervalVisible : confidenceIntervalVisible.get(); }
    public void setConfidenceIntervalVisible(final boolean VISIBLE) {
        if (null == confidenceIntervalVisible) {
            _confidenceIntervalVisible = VISIBLE;
            refresh();
        } else {
            confidenceIntervalVisible.set(VISIBLE);
        }
    }
    public BooleanProperty confidenceIntervalVisibleProperty() {
        if (null == confidenceIntervalVisible) {
            confidenceIntervalVisible = new BooleanPropertyBase(_confidenceIntervalVisible) {
                @Override protected void invalidated() { refresh(); }
                @Override public Object getBean() { return XYConfidenceSeries.this; }
                @Override public String getName() { return "confidenceIntervalVisible"; }
            };
        }
        return confidenceIntervalVisible;
    }
}
