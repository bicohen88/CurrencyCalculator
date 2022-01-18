package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controller {
    @FXML
    TableView rateList;
    @FXML
    ComboBox defaultCurrency;
    @FXML
    TextField quantityInput;
    @FXML
    Text baseText;
    @FXML
    Text bottomText;
    @FXML
    Text quantityText;
    int updateDelay = 0;
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


    //Upon opening creates downloads data and creates tables then binds them
    public void initialize() {
        Datasource.getInstance().open();
        rateList.setItems(Datasource.getInstance().getCurrencies());
        rateList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        defaultCurrency.setItems(Datasource.getInstance().getFullData());
        defaultCurrency.setValue(Datasource.getInstance().getBase());
        bottomText.setText("on " + Datasource.getInstance().getUpdateDate()
                + " at " + Datasource.getInstance().getUpdateTime());
        baseText.setText("Current base is " + Datasource.getInstance().getBase().toString());
        startAutoUpdate();
    }

    public void startAutoUpdate() {
        if (updateDelay != 0) {
            executorService.scheduleWithFixedDelay(() -> {
                while (!Thread.interrupted()) {
                    Datasource.getInstance().update();
                    updateCurrency();
                }
            }, updateDelay, updateDelay, TimeUnit.SECONDS);
        }
    }

    public void updateNow() {
        ExecutorService updateService = Executors.newSingleThreadExecutor();
        updateService.submit(() -> {
            Datasource.getInstance().update();
            updateCurrency();
        });
        updateService.shutdown();
    }

    //  When click update button will update base and recalculate conversion with new quantity field
    public void updateCurrency() {
        Currency newBase = (Currency) defaultCurrency.getSelectionModel().getSelectedItem();
        float quantity = 0;
        try {
            quantity = Float.parseFloat(quantityInput.getText());
        } catch (NumberFormatException e) {
            quantityText.setText("Please enter a number");
            return;
        }
        Datasource.getInstance().setBase(newBase);
        Datasource.getInstance().convert(quantity);
        baseText.setText("Current base is " + newBase.toString());
    }

    public void close() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Exit");
        alert.setContentText("Are you sure you would like to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
        }
    }

    public void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About This Currency Calculator");
        alert.setHeaderText("ABOUT");
        alert.setContentText("This is a simple tool use to convert various currencies. \n" +
                "All data is downloaded upon opening from \n" +
                " https://exchangeratesapi.io/");
        alert.showAndWait();
    }

    public void changeBase() {
        Dialog<Currency> dialog = new ChoiceDialog<>(Datasource.getInstance().getFullData().get(0),Datasource.getInstance().getFullData());
        dialog.setTitle("Select Base Currency");
        dialog.setContentText("Please select base currency from the dropdown list");
        Optional<Currency> result = dialog.showAndWait();
        if (result.isPresent()) {
            Datasource.getInstance().setBase(result.get());
            baseText.setText("Current base is " + result.get().toString());
        }
    }

    public void updateFrequency() {
        Dialog<String> dialog = new ChoiceDialog<>("Disabled", "Disabled","10 seconds", "30 seconds", "1 min", "5 min", "10 min");
        dialog.setTitle("Update Frequency");
        dialog.setHeaderText("Update Frequency");
        dialog.setContentText("Please select update frequency from the dropdown list");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            switch (result.get()) {
                case "Disabled": this.updateDelay = 0;
                    break;
                case "10 seconds": this.updateDelay= 10;
                    break;
                case "30 seconds": this.updateDelay = 30;
                    break;
                case "1 min": this.updateDelay = 60;
                    break;
                case "5 min": this.updateDelay = 5 * 60;
                    break;
                case "10 min": this.updateDelay = 10 *60;
                    break;
            }
        }
        if(!executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        startAutoUpdate();
    }

    public void selectCurrencies() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("selectCurrencies.fxml"));
        Parent parent = fxmlLoader.load();
        SelectCurrenciesController selectCurrenciesController = fxmlLoader.getController();
        Stage stage = new Stage();
        stage.setScene(new Scene(parent,300,300));
        stage.showAndWait();
    }

    public void shutdown() {
        if(!executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }
}
