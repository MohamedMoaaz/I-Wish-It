package controller;

import client.Client;
import client.DataStore;
import client.SceneSwitcher;
import connection.NetworkManager;
import connection.NotificationPayload;
import connection.Request;
import connection.Response;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller class for the notifications view.
 * Handles the initialization and display of notifications.
 */
public class NotificationsController extends MainController implements Initializable {

    @FXML
    private VBox container; // Container for displaying notification cards
    @FXML
    private Label text; // Label for displaying the count of notifications

    private int count = 0; // Count of notifications

    /**
     * Initializes the controller class.
     * This method is automatically called after the FXML file has been loaded.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param rb The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);

        // Send request to get the notifications for the current member
        NetworkManager.send(new Request("GET", "/api/notification", DataStore.getMember().getId()));
        Response response = NetworkManager.receive();

        ArrayList<NotificationPayload> notifications = (ArrayList<NotificationPayload>) response.getPayload();
        setCount(notifications.size());

        try {
            // Load and display each notification card
            for (NotificationPayload notification : notifications) {
                FXMLLoader loader = new FXMLLoader(
                        SceneSwitcher.class.getResource("/scene/NotificationCard.fxml")
                );
                Parent card = loader.load();
                NotificationCardController controller = loader.getController();
                controller.setDecrementCount(this::decrementCount);
                controller.setData(notification);
                container.getChildren().add(card);
            }
        } catch (IOException e) {
            Client.logFail(e.getMessage());
        }
    }

    /**
     * Sets the count of notifications and updates the label.
     *
     * @param new_count The new count of notifications.
     */
    private void setCount(int new_count) {
        count = new_count;
        text.setText("You have (" + count + ") notifications:");
    }

    /**
     * Decrements the count of notifications by one.
     */
    public void decrementCount() {
        setCount(count - 1);
    }
}
