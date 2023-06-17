module com.example.gameclientfxidk {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.gameclientfxidk to javafx.fxml;
    exports com.example.gameclientfxidk;
}