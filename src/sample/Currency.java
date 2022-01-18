package sample;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;

public class Currency {
    private SimpleStringProperty name;
    private SimpleFloatProperty price;
    private SimpleFloatProperty converted;

    public Currency(String name, Float price) {
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleFloatProperty(null,"price",price);
        this.converted = new SimpleFloatProperty();
    }

    public String getName() {
        return name.getValue();
    }

    @Override
    public boolean equals(Object obj) {
        Currency currency = (Currency) obj;
        if (currency != null) {
            return (this != null ? (this.name.getValue().equals(currency.getName())) : false);
        } else {
            return (this == null);
        }
    }

    @Override
    public String toString() {
        return this.nameProperty().getValue();
    }


    public SimpleStringProperty nameProperty() {
        return name;
    }

    public float getPrice() {
        return price.get();
    }

    public SimpleFloatProperty priceProperty() {
        return price;
    }

    public void setPrice(float price) {
        this.price.set(price);
    }

    public SimpleFloatProperty convertedProperty() {
        return converted;
    }

    public void setConverted(float quantity) {
        converted.setValue(quantity * this.getPrice());
    }

    public void changeBase(Currency newBase, Currency oldBase) {
        this.price.setValue(getPrice()* newBase.getPrice()/ oldBase.getPrice());
    }
}
