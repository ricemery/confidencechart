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

package com.chainstaysoftware.confidenceplot.charts.series;

import com.chainstaysoftware.confidenceplot.charts.ChartType;
import com.chainstaysoftware.confidenceplot.charts.Symbol;
import com.chainstaysoftware.confidenceplot.charts.data.XYConfidenceItem;
import com.chainstaysoftware.confidenceplot.charts.data.XYItem;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.HashMap;
import java.util.List;


public class XYConfidenceSeriesBuilder {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected XYConfidenceSeriesBuilder() {}


    // ******************** Methods *******************************************
    public static final XYConfidenceSeriesBuilder create() {
        return new XYConfidenceSeriesBuilder();
    }

    public final XYConfidenceSeriesBuilder items(final XYItem... ITEMS) {
        properties.put("itemsArray", new SimpleObjectProperty<>(ITEMS));
        return this;
    }

    public final XYConfidenceSeriesBuilder items(final List<XYItem> ITEMS) {
        properties.put("itemsList", new SimpleObjectProperty<>(ITEMS));
        return this;
    }

    public final XYConfidenceSeriesBuilder name(final String NAME) {
        properties.put("name", new SimpleStringProperty(NAME));
        return this;
    }

    public final XYConfidenceSeriesBuilder fill(final Paint FILL) {
        properties.put("fill", new SimpleObjectProperty<>(FILL));
        return this;
    }

    public final XYConfidenceSeriesBuilder stroke(final Paint STROKE) {
        properties.put("stroke", new SimpleObjectProperty<>(STROKE));
        return this;
    }

    public final XYConfidenceSeriesBuilder textFill(final Color FILL) {
        properties.put("textFill", new SimpleObjectProperty<>(FILL));
        return this;
    }

    public final XYConfidenceSeriesBuilder symbolFill(final Color FILL) {
        properties.put("symbolFill", new SimpleObjectProperty<>(FILL));
        return this;
    }

    public final XYConfidenceSeriesBuilder symbolStroke(final Color STROKE) {
        properties.put("symbolStroke", new SimpleObjectProperty<>(STROKE));
        return this;
    }

    public final XYConfidenceSeriesBuilder symbol(final Symbol SYMBOL) {
        properties.put("symbol", new SimpleObjectProperty<>(SYMBOL));
        return this;
    }

    public final XYConfidenceSeriesBuilder chartType(final ChartType TYPE) {
        properties.put("chartType", new SimpleObjectProperty<>(TYPE));
        return this;
    }

    public final XYConfidenceSeriesBuilder symbolsVisible(final boolean VISIBLE) {
        properties.put("symbolsVisible", new SimpleBooleanProperty(VISIBLE));
        return this;
    }

    public final XYConfidenceSeriesBuilder symbolSize(final double SIZE) {
        properties.put("symbolSize", new SimpleDoubleProperty(SIZE));
        return this;
    }

    public final XYConfidenceSeriesBuilder strokeWidth(final double WIDTH) {
        properties.put("strokeWidth", new SimpleDoubleProperty(WIDTH));
        return this;
    }

    public final XYConfidenceSeriesBuilder confidenceIntervalStroke(final Color STROKE) {
        properties.put("confidenceIntervalStroke", new SimpleObjectProperty<>(STROKE));
        return this;
    }

    public final XYConfidenceSeriesBuilder confidenceIntervalFill(final Color FILL) {
        properties.put("confidenceIntervalFill", new SimpleObjectProperty<>(FILL));
        return this;
    }

    public final XYConfidenceSeriesBuilder confidenceIntervalVisible(final boolean VISIBLE) {
        properties.put("confidenceIntervalVisible", new SimpleBooleanProperty(VISIBLE));
        return this;
    }

    public final XYConfidenceSeriesBuilder animated(final boolean AUTO) {
        properties.put("animated", new SimpleBooleanProperty(AUTO));
        return this;
    }

    public final XYConfidenceSeriesBuilder animationDuration(final long DURATION) {
        properties.put("animationDuration", new SimpleLongProperty(DURATION));
        return this;
    }


    public final <T extends XYConfidenceItem> XYConfidenceSeries<T> build() {
        final XYConfidenceSeries<T> SERIES = new XYConfidenceSeries<>(List.of(), ChartType.CONFIDENCE_LINE);

        if (properties.keySet().contains("itemsArray")) {
            SERIES.setItems(((ObjectProperty<XYItem[]>) properties.get("itemsArray")).get());
        }
        if(properties.keySet().contains("itemsList")) {
            SERIES.setItems(((ObjectProperty<List<XYItem>>) properties.get("itemsList")).get());
        }

        for (String key : properties.keySet()) {
            if ("name".equals(key)) {
                SERIES.setName(((StringProperty) properties.get(key)).get());
            } else if ("fill".equals(key)) {
                SERIES.setFill(((ObjectProperty<Paint>) properties.get(key)).get());
            } else if ("stroke".equals(key)) {
                SERIES.setStroke(((ObjectProperty<Paint>) properties.get(key)).get());
            } else if ("textFill".equals(key)) {
                SERIES.setTextFill(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("symbolFill".equals(key)) {
                SERIES.setSymbolFill(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("symbolStroke".equals(key)) {
                SERIES.setSymbolStroke(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("symbol".equals(key)) {
                SERIES.setSymbol(((ObjectProperty<Symbol>) properties.get(key)).get());
            } else if ("chartType".equals(key)) {
                SERIES.setChartType(((ObjectProperty<ChartType>) properties.get(key)).get());
            } else if ("symbolsVisible".equals(key)) {
                SERIES.setSymbolsVisible(((BooleanProperty) properties.get(key)).get());
            } else if ("symbolSize".equals(key)) {
                SERIES.setSymbolSize(((DoubleProperty) properties.get(key)).get());
            } else if ("strokeWidth".equals(key)) {
                SERIES.setStrokeWidth(((DoubleProperty) properties.get(key)).get());
            } else if ("confidenceIntervalStroke".equals(key)) {
                SERIES.setConfidenceIntervalStroke(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("confidenceIntervalFill".equals(key)) {
                SERIES.setConfidenceIntervalFill(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("confidenceIntervalVisible".equals(key)) {
                SERIES.setConfidenceIntervalVisible(((BooleanProperty) properties.get(key)).get());
            } else if("animated".equals(key)) {
                SERIES.setAnimated(((BooleanProperty) properties.get(key)).get());
            } else if("animationDuration".equals(key)) {
                SERIES.setAnimationDuration(((LongProperty) properties.get(key)).get());
            }
        }
        return SERIES;
    }
}
