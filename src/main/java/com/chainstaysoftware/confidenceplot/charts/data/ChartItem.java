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

package com.chainstaysoftware.confidenceplot.charts.data;

import com.chainstaysoftware.confidenceplot.charts.Category;
import com.chainstaysoftware.confidenceplot.charts.Symbol;
import com.chainstaysoftware.confidenceplot.charts.event.ChartEvt;
import com.chainstaysoftware.confidenceplot.charts.tools.Helper;
import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.EvtType;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class ChartItem implements Item, Comparable<ChartItem> {
    private final ChartEvt UPDATE_EVENT   = new ChartEvt(ChartItem.this, ChartEvt.ITEM_UPDATE);
    private final ChartEvt                                  FINISHED_EVENT = new ChartEvt(ChartItem.this, ChartEvt.FINISHED);
    private       Map<EvtType, List<EvtObserver<ChartEvt>>> observers      = new ConcurrentHashMap<>();
    private       int                                       _index;
    private       IntegerProperty                           index;
    private       String                                    _name;
    private       StringProperty                            name;
    private       String                                    _unit;
    private       StringProperty                            unit;
    private       String                                    _description;
    private       StringProperty                            description;
    private Category _category;
    private       ObjectProperty<Category>                  category;
    private       double                                    _value;
    private       DoubleProperty                            value;
    private       double                                    oldValue;
    private       Color                                     _fill;
    private       ObjectProperty<Color>                     fill;
    private       Color                                     _stroke;
    private       ObjectProperty<Color>                     stroke;
    private       Color                                     _textFill;
    private       ObjectProperty<Color>                     textFill;
    private       Instant                                   _timestamp;
    private       ObjectProperty<Instant>                   timestamp;
    private Symbol _symbol;
    private       ObjectProperty<Symbol>                    symbol;
    private       boolean                                   _animated;
    private       BooleanProperty                           animated;
    private       double                                    _x;
    private       DoubleProperty                            x;
    private       double                                    _y;
    private       DoubleProperty                            y;
    private       boolean                                   _isEmpty;
    private       BooleanProperty                           isEmpty;
    private       long                                      animationDuration;
    private       DoubleProperty                            currentValue;
    private       Timeline                                  timeline;


    // ******************** Constructors **************************************
    public ChartItem() {
        this("", 0, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, Instant.now(), false, 800);
    }
    public ChartItem(final boolean IS_EMPTY) {
        this("", 0, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, Instant.now(), false, 800, IS_EMPTY);
    }
    public ChartItem(final String NAME) {
        this(NAME, 0, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, Instant.now(), false, 800);
    }
    public ChartItem(final String NAME, final boolean IS_EMPTY) {
        this(NAME, 0, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, Instant.now(), false, 800, IS_EMPTY);
    }
    public ChartItem(double VALUE) {
        this("", VALUE, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, Instant.now(), false, 800);
    }
    public ChartItem(double VALUE, final boolean IS_EMPTY) {
        this("", VALUE, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, Instant.now(), false, 800, IS_EMPTY);
    }
    public ChartItem(final double VALUE, final Instant TIMESTAMP) {
        this("", VALUE, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, TIMESTAMP, false, 800);
    }
    public ChartItem(final double VALUE, final Instant TIMESTAMP, final boolean IS_EMPTY) {
        this("", VALUE, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, TIMESTAMP, false, 800, IS_EMPTY);
    }
    public ChartItem(final double VALUE, final Color FILL_COLOR) {
        this("", VALUE, FILL_COLOR, Color.TRANSPARENT, Color.BLACK, Instant.now(), false, 800);
    }
    public ChartItem(final double VALUE, final Color FILL_COLOR, final boolean IS_EMPTY) {
        this("", VALUE, FILL_COLOR, Color.TRANSPARENT, Color.BLACK, Instant.now(), false, 800, IS_EMPTY);
    }
    public ChartItem(final String NAME, final Color FILL_COLOR) {
        this(NAME, 0, FILL_COLOR, Color.TRANSPARENT, Color.BLACK, Instant.now(), false, 800);
    }
    public ChartItem(final String NAME, final Color FILL_COLOR, final boolean IS_EMPTY) {
        this(NAME, 0, FILL_COLOR, Color.TRANSPARENT, Color.BLACK, Instant.now(), false, 800, IS_EMPTY);
    }
    public ChartItem(final String NAME, final double VALUE) {
        this(NAME, VALUE, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, Instant.now(), false, 800);
    }
    public ChartItem(final String NAME, final double VALUE, final boolean IS_EMPTY) {
        this(NAME, VALUE, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, Instant.now(), false, 800, IS_EMPTY);
    }
    public ChartItem(final String NAME, final double VALUE, final Instant TIMESTAMP) {
        this(NAME, VALUE, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, TIMESTAMP, true, 800);
    }
    public ChartItem(final String NAME, final double VALUE, final Instant TIMESTAMP, final boolean IS_EMPTY) {
        this(NAME, VALUE, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, TIMESTAMP, true, 800, IS_EMPTY);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL) {
        this(NAME, VALUE, FILL, Color.TRANSPARENT, Color.BLACK, Instant.now(), false, 800);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL, final boolean IS_EMPTY) {
        this(NAME, VALUE, FILL, Color.TRANSPARENT, Color.BLACK, Instant.now(), false, 800, IS_EMPTY);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL, final Color TEXT_FILL) {
        this(NAME, VALUE, FILL, Color.TRANSPARENT, TEXT_FILL, Instant.now(), false, 800);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL, final Color TEXT_FILL, final boolean IS_EMPTY) {
        this(NAME, VALUE, FILL, Color.TRANSPARENT, TEXT_FILL, Instant.now(), false, 800, IS_EMPTY);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL, final Instant TIMESTAMP) {
        this(NAME, VALUE, FILL, Color.TRANSPARENT, Color.BLACK, TIMESTAMP, false, 800);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL, final Instant TIMESTAMP, final boolean IS_EMPTY) {
        this(NAME, VALUE, FILL, Color.TRANSPARENT, Color.BLACK, TIMESTAMP, false, 800, IS_EMPTY);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL, final Color TEXT_FILL, final Instant TIMESTAMP) {
        this(NAME, VALUE, FILL, Color.TRANSPARENT, TEXT_FILL, TIMESTAMP, false, 800);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL, final Color TEXT_FILL, final Instant TIMESTAMP, final boolean IS_EMPTY) {
        this(NAME, VALUE, FILL, Color.TRANSPARENT, TEXT_FILL, TIMESTAMP, false, 800, IS_EMPTY);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL, final Instant TIMESTAMP, final boolean ANIMATED, final long ANIMATION_DURATION) {
        this(NAME, VALUE, FILL, Color.TRANSPARENT, Color.BLACK, TIMESTAMP, ANIMATED, ANIMATION_DURATION);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL, final Instant TIMESTAMP, final boolean ANIMATED, final long ANIMATION_DURATION, final boolean IS_EMPTY) {
        this(NAME, VALUE, FILL, Color.TRANSPARENT, Color.BLACK, TIMESTAMP, ANIMATED, ANIMATION_DURATION, IS_EMPTY);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL, final Color TEXT_FILL, final Instant TIMESTAMP, final boolean ANIMATED, final long ANIMATION_DURATION) {
        this(NAME, VALUE, FILL, Color.TRANSPARENT, TEXT_FILL, TIMESTAMP, ANIMATED, ANIMATION_DURATION);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL, final Color TEXT_FILL, final Instant TIMESTAMP, final boolean ANIMATED, final long ANIMATION_DURATION, final boolean IS_EMPTY) {
        this(NAME, VALUE, FILL, Color.TRANSPARENT, TEXT_FILL, TIMESTAMP, ANIMATED, ANIMATION_DURATION, IS_EMPTY);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL, final Color STROKE, final Color TEXT_FILL, final Instant TIMESTAMP, final boolean ANIMATED, final long ANIMATION_DURATION) {
        this(NAME, VALUE, FILL, STROKE, TEXT_FILL, TIMESTAMP, ANIMATED, ANIMATION_DURATION, false);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL, final Color STROKE, final Color TEXT_FILL, final Instant TIMESTAMP, final boolean ANIMATED, final long ANIMATION_DURATION, final boolean IS_EMPTY) {
        _index            = -1;
        _name             = NAME;
        _unit             = "";
        _description      = "";
        _category         = null;
        _value            = VALUE;
        oldValue          = 0;
        _fill             = FILL;
        _stroke           = STROKE;
        _textFill         = TEXT_FILL;
        _timestamp        = TIMESTAMP;
        _symbol           = Symbol.NONE;
        _animated         = ANIMATED;
        _x                = 0;
        _y                = 0;
        _isEmpty          = IS_EMPTY;
        currentValue      = new DoublePropertyBase(_value) {
            @Override protected void invalidated() {
                oldValue = ChartItem.this.getValue();
                ChartItem.this.setValue(get());
                fireChartEvt(UPDATE_EVENT);
            }
            @Override public Object getBean() { return ChartItem.this; }
            @Override public String getName() { return "currentValue"; }
        };
        timeline          = new Timeline();
        animationDuration = ANIMATION_DURATION;

        timeline.setOnFinished(e -> fireChartEvt(FINISHED_EVENT));
    }


    // ******************** Methods *******************************************
    public int getIndex() { return null == index ? _index : index.get(); }
    public void setIndex(final int index) {
        if (null == this.index) {
            _index = index;
        } else {
            this.index.set(index);
        }
    }
    public IntegerProperty indexProperty() {
        if (null == index) {
            index = new IntegerPropertyBase(_index) {
                @Override protected void invalidated() {  }
                @Override public Object getBean() { return ChartItem.this; }
                @Override public String getName() { return "index"; }
            };
        }
        return index;
    }

    @Override public String getName() { return null == name ? _name : name.get(); }
    public void setName(final String NAME) {
        if (null == name) {
            _name = NAME;
            fireChartEvt(UPDATE_EVENT);
        } else {
            name.set(NAME);
        }
    }
    public StringProperty nameProperty() {
        if (null == name) {
            name = new StringPropertyBase(_name) {
                @Override protected void invalidated() { fireChartEvt(UPDATE_EVENT); }
                @Override public Object getBean() { return ChartItem.this; }
                @Override public String getName() { return "name"; }
            };
            _name = null;
        }
        return name;
    }

    public String getUnit() { return null == unit ? _unit : unit.get(); }
    public void setUnit(final String UNIT) {
        if (null == unit) {
            _unit = UNIT;
            fireChartEvt(UPDATE_EVENT);
        } else {
            unit.set(UNIT);
        }
    }
    public StringProperty unitProperty() {
        if (null == unit) {
            unit = new StringPropertyBase(_unit) {
                @Override protected void invalidated() { fireChartEvt(UPDATE_EVENT); }
                @Override public Object getBean() { return ChartItem.this; }
                @Override public String getName() { return "unit"; }
            };
            _unit = null;
        }
        return unit;
    }

    public String getDescription() { return null == description ? _description : description.get(); }
    public void setDescription(final String DESCRIPTION) {
        if (null == description) {
            _description = DESCRIPTION;
            fireChartEvt(UPDATE_EVENT);
        } else {
            description.set(DESCRIPTION);
        }
    }
    public StringProperty descriptionProperty() {
        if (null == description) {
            description = new StringPropertyBase(_description) {
                @Override protected void invalidated() { fireChartEvt(UPDATE_EVENT); }
                @Override public Object getBean() { return ChartItem.this; }
                @Override public String getName() { return "description"; }
            };
            _description = null;
        }
        return description;
    }

    public Category getCategory() { return null == category ? _category : category.get(); }
    public void setCategory(final Category CATEGORY) {
        if (null == category) {
            _category = CATEGORY;
            fireChartEvt(UPDATE_EVENT);
        } else {
            category.set(CATEGORY);
        }
    }
    public ObjectProperty<Category> categoryProperty() {
        if (null == category) {
            category = new ObjectPropertyBase<>(_category) {
                @Override protected void invalidated() { fireChartEvt(UPDATE_EVENT); }
                @Override public Object getBean() { return ChartItem.this; }
                @Override public String getName() { return "category"; }
            };
            _category = null;
        }
        return category;
    }

    public double getValue() { return null == value ? _value : value.get(); }
    public void setValue(final double VALUE) {
        if (null == value) {
            if (isAnimated()) {
                if (timeline.getCurrentRate() > 0) {
                    // Only update values if timeline is already running
                    oldValue = _value;
                    _value   = VALUE;
                } else {
                    // Start timeline only if it is NOT already running
                    oldValue = _value;
                    _value = VALUE;
                    timeline.stop();
                    KeyValue kv1 = new KeyValue(currentValue, oldValue, Interpolator.EASE_BOTH);
                    KeyValue kv2 = new KeyValue(currentValue, VALUE, Interpolator.EASE_BOTH);
                    KeyFrame kf1 = new KeyFrame(Duration.ZERO, kv1);
                    KeyFrame kf2 = new KeyFrame(Duration.millis(animationDuration), kv2);
                    timeline.getKeyFrames().setAll(kf1, kf2);
                    timeline.play();
                }
            } else {
                oldValue = _value;
                _value = VALUE;
                fireChartEvt(FINISHED_EVENT);
            }
        } else {
            value.set(VALUE);
        }
    }
    public DoubleProperty valueProperty() {
        if (null == value) {
            value = new DoublePropertyBase(_value) {
                @Override public void set(final double VALUE) {
                    oldValue = get();
                    super.set(VALUE);
                }
                @Override protected void invalidated() {
                    if (isAnimated()) {
                        if (Double.compare(timeline.getCurrentRate(), 0.0) == 0) {
                            // Only start timeline if it is NOT already running
                            timeline.stop();
                            KeyValue kv1 = new KeyValue(currentValue, oldValue, Interpolator.EASE_BOTH);
                            KeyValue kv2 = new KeyValue(currentValue, get(), Interpolator.EASE_BOTH);
                            KeyFrame kf1 = new KeyFrame(Duration.ZERO, kv1);
                            KeyFrame kf2 = new KeyFrame(Duration.millis(animationDuration), kv2);
                            timeline.getKeyFrames().setAll(kf1, kf2);
                            timeline.play();
                        }
                    } else {
                        fireChartEvt(FINISHED_EVENT);
                    }
                }
                @Override public Object getBean() { return ChartItem.this; }
                @Override public String getName() { return "value"; }
            };
        }
        return value;
    }

    public double getOldValue() { return oldValue; }

    @Override public Color getFill() { return null == fill ? _fill : fill.get(); }
    public void setFill(final Color FILL) {
        if (null == fill) {
            _fill = FILL;
            fireChartEvt(UPDATE_EVENT);
        } else {
            fill.set(FILL);
        }
    }
    public ObjectProperty<Color> fillProperty() {
        if (null == fill) {
            fill = new ObjectPropertyBase<Color>(_fill) {
                @Override protected void invalidated() { fireChartEvt(UPDATE_EVENT); }
                @Override public Object getBean() { return ChartItem.this; }
                @Override public String getName() { return "fill"; }
            };
            _fill = null;
        }
        return fill;
    }

    public Color getStroke() { return null == stroke ? _stroke : stroke.get(); }
    public void setStroke(final Color STROKE) {
        if (null == stroke) {
            _stroke = STROKE;
            fireChartEvt(UPDATE_EVENT);
        } else {
            stroke.set(STROKE);
        }
    }
    public ObjectProperty<Color> strokeProperty() {
        if (null == stroke) {
            stroke = new ObjectPropertyBase<Color>(_stroke) {
                @Override protected void invalidated() { fireChartEvt(UPDATE_EVENT); }
                @Override public Object getBean() { return ChartItem.this; }
                @Override public String getName() { return "stroke"; }
            };
            _stroke = null;
        }
        return stroke;
    }

    public Color getTextFill() { return null == textFill ? _textFill : textFill.get(); }
    public void setTextFill(final Color COLOR) {
        if (null == textFill) {
            _textFill = COLOR;
            fireChartEvt(UPDATE_EVENT);
        } else {
            textFill.set(COLOR);
        }
    }
    public ObjectProperty<Color> textFillProperty() {
        if (null == textFill) {
            textFill = new ObjectPropertyBase<Color>(_textFill) {
                @Override protected void invalidated() { fireChartEvt(UPDATE_EVENT); }
                @Override public Object getBean() { return ChartItem.this; }
                @Override public String getName() { return "textFill"; }
            };
            _textFill = null;
        }
        return textFill;
    }

    @Override public Symbol getSymbol() { return null == symbol ? _symbol : symbol.get(); }
    @Override public void setSymbol(final Symbol SYMBOL) {
        if (null == symbol) {
            _symbol = SYMBOL;
            fireChartEvt(UPDATE_EVENT);
        } else {
            symbol.set(SYMBOL);
        }
    }
    public ObjectProperty<Symbol> symbolProperty() {
        if (null == symbol) {
            symbol = new ObjectPropertyBase<Symbol>(_symbol) {
                @Override protected void invalidated() { fireChartEvt(UPDATE_EVENT); }
                @Override public Object getBean() {  return ChartItem.this;  }
                @Override public String getName() {  return "symbol";  }
            };
            _symbol = null;
        }
        return symbol;
    }

    public Instant getTimestamp() { return null == timestamp ? _timestamp : timestamp.get(); }
    public void setTimestamp(final ZonedDateTime TIMESTAMP) { setTimestamp(TIMESTAMP.toInstant()); }
    public void setTimestamp(final Instant TIMESTAMP) {
        if (null == timestamp) {
            _timestamp = TIMESTAMP;
            fireChartEvt(UPDATE_EVENT);
        } else {
            timestamp.set(TIMESTAMP);
        }
    }
    public ObjectProperty<Instant> timestampProperty() {
        if (null == timestamp) {
            timestamp = new ObjectPropertyBase<Instant>(_timestamp) {
                @Override protected void invalidated() { fireChartEvt(UPDATE_EVENT); }
                @Override public Object getBean() { return ChartItem.this; }
                @Override public String getName() { return "timestamp"; }
            };
            _timestamp = null;
        }
        return timestamp;
    }

    public ZonedDateTime getTimestampAdDateTime() { return getTimestampAsDateTime(ZoneId.systemDefault()); }
    public ZonedDateTime getTimestampAsDateTime(final ZoneId ZONE_ID) { return ZonedDateTime.ofInstant(getTimestamp(), ZONE_ID); }

    public LocalDate getTimestampAsLocalDate() { return getTimestampAsLocalDate(ZoneId.systemDefault()); }
    public LocalDate getTimestampAsLocalDate(final ZoneId ZONE_ID) { return getTimestampAsDateTime(ZONE_ID).toLocalDate(); }

    public boolean isAnimated() { return null == animated ? _animated : animated.get(); }
    public void setAnimated(final boolean ANIMATED) {
        if (null == animated) {
            _animated = ANIMATED;
        }  else {
            animated.set(ANIMATED);
        }
    }
    public BooleanProperty animatedProperty() {
        if (null == animated) {
            animated = new BooleanPropertyBase(_animated) {
                @Override public Object getBean() { return ChartItem.this; }
                @Override public String getName() { return "animated"; }
            };
        }
        return animated;
    }

    public double getX() { return null == x ? _x : x.get(); }
    public void setX(final double X) {
        if (null == x) {
            _x = X;
        } else {
            x.set(X);
        }
    }
    public DoubleProperty xProperty() {
        if (null == x) {
            x = new DoublePropertyBase(_x) {
                @Override protected void invalidated() { }
                @Override public Object getBean() { return ChartItem.this; }
                @Override public String getName() { return "x"; }
            };
        }
        return x;
    }

    public double getY() { return null == y ? _y : y.get(); }
    public void setY(final double Y) {
        if (null == y) {
            _y = Y;
        } else {
            y.set(Y);
        }
    }
    public DoubleProperty yProperty() {
        if (null == y) {
            y = new DoublePropertyBase(_y) {
                @Override protected void invalidated() { }
                @Override public Object getBean() { return ChartItem.this; }
                @Override public String getName() { return "y"; }
            };
        }
        return y;
    }

    @Override public boolean isEmptyItem() { return null == isEmpty ? _isEmpty : isEmpty.get(); }
    public void setIsEmpty(final boolean isEmpty) {
        if (null == this.isEmpty) {
            _isEmpty = isEmpty;
            fireChartEvt(UPDATE_EVENT);
        } else {
            this.isEmpty.set(isEmpty);
        }
    }
    public BooleanProperty isEmptyProperty() {
        if (null == isEmpty) {
            isEmpty = new BooleanPropertyBase(_isEmpty) {
                @Override protected void invalidated() { fireChartEvt(UPDATE_EVENT); }
                @Override public Object getBean() { return ChartItem.this; }
                @Override public String getName() { return "isEmpty"; }
            };
        }
        return isEmpty;
    }

    
    public long getAnimationDuration() { return animationDuration; }
    public void setAnimationDuration(final long DURATION) { animationDuration = Helper.clamp(10, 10000, DURATION); }

    @Override public String toString() {
        return new StringBuilder().append("{\n")
                                  .append("  \"name\":").append(getName()).append(",\n")
                                  .append("  \"unit\":").append(getUnit()).append(",\n")
                                  .append("  \"description\":").append(getDescription()).append(",\n")
                                  .append("  \"category\":").append(getCategory()).append(",\n")
                                  .append("  \"value\":").append(getValue()).append(",\n")
                                  .append("  \"timestamp\":").append(getTimestamp().toEpochMilli()).append(",\n")
                                  .append("}")
                                  .toString();
    }

    @Override public int compareTo(final ChartItem ITEM) { return Double.compare(getValue(), ITEM.getValue()); }

    @Override public boolean equals(Object o) {
        if (o == this) { return true; }
        if (!(o instanceof ChartItem)) { return false; }

        ChartItem item = (ChartItem) o;

        return item.getName().equals(getName()) &&
               item.getUnit().equals(getUnit()) &&
               item.getDescription().equals(getDescription()) &&
               item.getTimestamp().equals(getTimestamp()) &&
               item.isEmptyItem() == isEmptyItem() &&
               Double.compare(item.getValue(), getValue()) == 0;
    }


    // ******************** Event Handling ************************************
    public void addChartEvtObserver(final EvtType type, final EvtObserver<ChartEvt> observer) {
        if (!observers.containsKey(type)) { observers.put(type, new CopyOnWriteArrayList<>()); }
        if (observers.get(type).contains(observer)) { return; }
        observers.get(type).add(observer);
    }
    public void removeChartEvtObserver(final EvtType type, final EvtObserver<ChartEvt> observer) {
        if (observers.containsKey(type)) {
            if (observers.get(type).contains(observer)) {
                observers.get(type).remove(observer);
            }
        }
    }
    public void removeAllChartEvtObservers() { observers.clear(); }

    public void fireChartEvt(final ChartEvt evt) {
        final EvtType type = evt.getEvtType();
        observers.entrySet().stream().filter(entry -> entry.getKey().equals(ChartEvt.ANY)).forEach(entry -> entry.getValue().forEach(observer -> observer.handle(evt)));
        if (observers.containsKey(type) && !type.equals(ChartEvt.ANY)) {
            observers.get(type).forEach(observer -> observer.handle(evt));
        }
    }
}
