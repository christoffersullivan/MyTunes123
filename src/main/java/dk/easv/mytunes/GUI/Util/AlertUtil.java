package dk.easv.mytunes.GUI.Util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;


public class AlertUtil {

    public static void showError(Throwable t) {
        t.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(t.getMessage());
        if (t.getCause() != null) {
            alert.setContentText(t.getCause().getMessage());
        }
        alert.showAndWait();
    }

    public static void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Please check your input");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public static void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("MyTunes");
        alert.setHeaderText(message);
        alert.showAndWait();
    }


    public static boolean confirm(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Please confirm");
        alert.setHeaderText(header);
        alert.setContentText(content);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
