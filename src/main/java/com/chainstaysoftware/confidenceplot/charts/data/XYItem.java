package com.chainstaysoftware.confidenceplot.charts.data;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;

public interface XYItem extends Item {
   double getX();

   void setX(double x);

   DoubleProperty xProperty();

   double getY();

   void setY(double y);

   String getTooltipText();

   void setTooltipText(String text);

   StringProperty tooltipTextProperty();
}
