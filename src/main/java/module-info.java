module com.chainstaysoftware.confidenceplot {

    // Java
    requires java.base;
    requires java.logging;

    // Java-FX
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.swing;

    // 3rd party
    requires transitive eu.hansolo.toolbox;
    requires transitive eu.hansolo.toolboxfx;

    exports com.chainstaysoftware.confidenceplot.charts;
    exports com.chainstaysoftware.confidenceplot.charts.data;
    exports com.chainstaysoftware.confidenceplot.charts.event;
    exports com.chainstaysoftware.confidenceplot.charts.series;
    exports com.chainstaysoftware.confidenceplot.charts.tools;
}