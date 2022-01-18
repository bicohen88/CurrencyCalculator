package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class SelectCurrenciesController {
    @FXML
    ListView activeCurrencies;
    @FXML
    ComboBox<Currency> selectCurrency;

    public void initialize() {
        selectCurrency.setItems(Datasource.getInstance().getFullData());
        activeCurrencies.setItems(Datasource.getInstance().getCurrencies());
    }

    public void addCurrency() {
        Currency current = selectCurrency.getSelectionModel().getSelectedItem();
        if (!Datasource.getInstance().getCurrencies().contains(current)) {
            Datasource.getInstance().getCurrencies().add(current);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot Add Currency");
            alert.setContentText("This currency cannot be added because it already on the list!");
            alert.showAndWait();
        }
    }

    public void removeCurrency() {
        Currency current = (Currency) activeCurrencies.getSelectionModel().getSelectedItem();
        Datasource.getInstance().getCurrencies().remove(current);
    }

    public boolean savePreset() {
        File file = new File("currencies.txt");
        try {
            if (!file.createNewFile()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Overwrite Presets");
                alert.setHeaderText("");
                alert.setContentText("There is already a preset saved are you sure you would like overwrite?");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.isPresent() && result.get() == (ButtonType.OK)) {
                    System.out.println(file.delete());
                    if(!file.createNewFile()) {
                        System.out.println("Error creating file");
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } catch (IOException e) {
            System.out.println("Exception while creating file");
            return false;
        }
        try (FileWriter fileWriter = new FileWriter("currencies.txt")) {
            String s = Datasource.getInstance().getCurrencies().toString();
            fileWriter.write(Datasource.getInstance().getBase().toString() + s);
        } catch (IOException e) {
            System.out.println("Exception while writing file");
            return false;
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Save Completed");
        alert.setContentText("Base currency and list of currencies will automatically open in your next session.");
        alert.showAndWait();
        return true;
    }

    public void closeWindow() {
        Stage stage = (Stage) activeCurrencies.getScene().getWindow();
        stage.close();
    }
}
