package dk.easv.mytunes.GUI.Util;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Helpers for song durations (stored as seconds, shown as m:ss).
 */
public class TimeUtil {

    /**
     * Formats seconds as m:ss.
     */
    public static String format(double totalSeconds) {
        int rounded = (int) Math.round(totalSeconds);
        return String.format("%d:%02d", rounded / 60, rounded % 60);
    }

    /**
     * Accepts "215", "215.5" or "3:35" and returns seconds.
     * @throws NumberFormatException if the text is not a positive time
     */
    public static double parse(String text) {
        String t = text.trim();
        double seconds;
        if (t.contains(":")) {
            String[] parts = t.split(":");
            if (parts.length != 2) throw new NumberFormatException("Bad time format");
            seconds = Integer.parseInt(parts[0].trim()) * 60 + Double.parseDouble(parts[1].trim());
        } else {
            seconds = Double.parseDouble(t);
        }
        if (seconds <= 0) throw new NumberFormatException("Time must be positive");
        return seconds;
    }

    /**
     * Cell factory that shows a Double column (seconds) as m:ss. Sorting stays numeric.
     */
    public static <S> Callback<TableColumn<S, Double>, TableCell<S, Double>> timeCellFactory() {
        return column -> new TableCell<S, Double>() {
            @Override
            protected void updateItem(Double seconds, boolean empty) {
                super.updateItem(seconds, empty);
                setText(empty || seconds == null ? null : format(seconds));
            }
        };
    }
}
