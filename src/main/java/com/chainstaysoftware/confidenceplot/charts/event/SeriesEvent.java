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

package com.chainstaysoftware.confidenceplot.charts.event;

import com.chainstaysoftware.confidenceplot.charts.series.Series;
import com.chainstaysoftware.confidenceplot.charts.data.ChartItem;


/**
 * Created by hansolo on 16.07.17.
 */
public class SeriesEvent<T extends ChartItem> {
    private final Series<T> SERIES;


    // ******************** Constructors **************************************
    public SeriesEvent(final Series<T> SERIES) {
        this.SERIES = SERIES;
    }


    // ******************** Methods *******************************************
    public Series<T> getSeries() { return SERIES; }
}
