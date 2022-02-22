# Confidence Chart
JavaFx Line Chart with Confidence Interval

Code forked from https://github.com/HanSolo/charts .

This chart is similar to the HanSolo Chart - ChartType.MULTI_TIME_SERIES. 
The Confidence Chart is different from MULTI_TIME_SERIES. The plotted line, 
max and min of the confidence interval must be passed by the caller of the 
Confidence Chart. In MULTI_TIME_SERIES the plotted line (average) and confidence 
interval (std deviation) is calculated by the chart from a series of points.

Major changes from HanSolo Charts:
* Packages moved.
* Classes renamed.
* Only Confidence Chart and Legend included.

<img src="https://i.imgur.com/wKTombT.png">
