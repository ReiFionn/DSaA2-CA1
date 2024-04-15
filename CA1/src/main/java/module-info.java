module ca2.ca22 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;

    opens CA1 to javafx.fxml;
    exports CA1;
}