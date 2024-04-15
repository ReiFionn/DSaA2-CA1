module ca2.ca22 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;
    requires jmh.core;
    requires jmh.generator.annprocess;

    opens CA1 to javafx.fxml,jmh.core,jmh.generator.annprocess;
    exports CA1;
}