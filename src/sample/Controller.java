package sample;
/**
 * @author Rick
 * @author Thore
 * @author Lennerd
 * @version otter
 */

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;


import java.util.LinkedList;
import java.util.List;

/**
 * Main Controller generated by InteliJ, no idea what it does but seems to be working doh
 */
public class Controller {
    @FXML
    private TextField width, height, depth, weight, result, result1,sizeSUPER, weightSUPER;

    @FXML
    private Label wUnit, hUnit, dUnit, weightUnit;

    @FXML
    private ComboBox lengthUnitBox, weightUnitBox;

    @FXML
    private MenuItem closeItem;

    @FXML
    private Button addbutton, clearbutton;

    // enum of length and weight properties
    private ObjectProperty<ULength> lengthProp = new SimpleObjectProperty<>();
    private ObjectProperty<UWeight> weightProp = new SimpleObjectProperty<>();

    // Lists to store things
    private List<Packet> tempPacket = new LinkedList<>();
    private List<Packet> packageList = new LinkedList<>();
    private List<Packet> superPackage = new LinkedList<>();
    private numberConverter convert = new numberConverter();
    private calcShippingCosts shippingCalculator = new calcShippingCosts();

    /**
     * Initializes fxml with correct data and adds events filters/listeners
     */
    public void initialize() {

        packageList.add(new Packet(60.0, 30.0, 15.0, 2.0, 4.5));
        packageList.add(new Packet(60.0, 30.0, 15.0, 2.0, 4.5));
        packageList.add(new Packet(120.0, 60.0, 60.0, 5.0, 6.99));
        packageList.add(new Packet(120.0, 60.0, 60.0, 10.0, 9.49));
        packageList.add(new Packet(120.0, 60.0, 60.0, 31.5, 16.49));

        width.addEventFilter(KeyEvent.KEY_TYPED, e -> inputFilter(e));
        height.addEventFilter(KeyEvent.KEY_TYPED, e -> inputFilter(e));
        depth.addEventFilter(KeyEvent.KEY_TYPED, e -> inputFilter(e));
        weight.addEventFilter(KeyEvent.KEY_TYPED, e -> inputFilter(e));


        addbutton.setOnAction((event) -> addSuperPackage());
        clearbutton.setOnAction((event) -> clearSuperPackage());


        closeItem.setOnAction(e -> Platform.exit());

        width.textProperty().addListener((observable, oldValue, newValue) -> {
            result.setText(Double.toString(updateInputWorker(depth.getText(), height.getText(), width.getText(), weight.getText())));
        });
        height.textProperty().addListener((observable, oldValue, newValue) -> {
            result.setText(Double.toString(updateInputWorker(depth.getText(), height.getText(), width.getText(), weight.getText())));
        });
        depth.textProperty().addListener((observable, oldValue, newValue) -> {
            result.setText(Double.toString(updateInputWorker(depth.getText(), height.getText(), width.getText(), weight.getText())));
        });
        weight.textProperty().addListener((observable, oldValue, newValue) -> {
            result.setText(Double.toString(updateInputWorker(depth.getText(), height.getText(), width.getText(), weight.getText())));
        });

        //comboboxes
        for (ULength u : ULength.values())
            lengthUnitBox.setItems(FXCollections.observableArrayList(ULength.values()));


        lengthProp.addListener((observable, oldValue, newValue) -> {

            wUnit.setText(newValue.toString());
            hUnit.setText(newValue.toString());
            dUnit.setText(newValue.toString());
        });
        lengthProp.bind(lengthUnitBox.valueProperty());

        lengthUnitBox.setValue(ULength.cm);


        for (UWeight u : UWeight.values())
            weightUnitBox.setItems(FXCollections.observableArrayList(UWeight.values()));

        weightProp.addListener((observable, oldValue, newValue) -> {
            weightUnit.setText(newValue.toString());
        });
        weightProp.bind(weightUnitBox.valueProperty());

        weightUnitBox.setValue(UWeight.g);
    }

    /**
     * calculates price and returns it to the output field
     *
     * @param length length from given textfield
     * @param height height from given textfield
     * @param width  width from given textfield
     * @param weight weight from given textfield
     * @return price for output field
     */
    private double updateInputWorker(String length, String height, String width, String weight) {
        Double d_length = Double.parseDouble(length.isEmpty() ? "0.0" : length);
        Double d_height = Double.parseDouble(height.isEmpty() ? "0.0" : height);
        Double d_width = Double.parseDouble(width.isEmpty() ? "0.0" : width);
        Double d_weight = Double.parseDouble(weight.isEmpty() ? "0.0" : weight);

        d_length = convert.numberConversionDimensions(d_length, lengthUnitBox.getValue().toString());
        d_height = convert.numberConversionDimensions(d_height, lengthUnitBox.getValue().toString());
        d_width = convert.numberConversionDimensions(d_width, lengthUnitBox.getValue().toString());
        d_weight = convert.numberConversionWeight(d_weight, weightUnitBox.getValue().toString());

        tempPacket.clear();
        tempPacket.add(new Packet(d_length, d_height, d_width, d_weight, 0.0));

        return shippingCalculator.calcShippingCosts(tempPacket);

        // TODO: move comparisons to Packet.java
        if (d_length <= small.getLength() && d_width <= small.getWidth() && d_height <= small.getHeight() && d_weight < small.getWeight()) {
            return small.getPrize();
        } else if (d_length <= medium.getLength() && d_width <= medium.getWidth() && d_height <= medium.getHeight() && d_weight < medium.getWeight()) {
            return medium.getPrize();
        } else if (d_length <= largeOne.getLength() && d_width <= largeOne.getWidth() && d_height <= largeOne.getHeight()) {
            if (d_weight <= largeOne.getWeight()) {
                return largeOne.getPrize();
            } else if (d_weight <= largeTwo.getWeight()) {
                return largeTwo.getPrize();
            } else if (d_weight <= largeThree.getWeight()) {
                return largeThree.getPrize();
            } else {
                return Double.NaN;
            }
        } else {
            return Double.NaN;
        }
    }

    /**
     * only allows to enter digits and '.' into the input fields
     *
     * @param e given input from textfield enterd by user
     */
    private void inputFilter(KeyEvent e) {
        char c = e.getCharacter().charAt(0);
        if (!(Character.isDigit(c) || c == '.')) {
            e.consume();
        }
    }


    /**
     * Deletes all elements from the superPackage LinkedList
      */
    private void clearSuperPackage() {
        superPackage.clear();
    }

    /**
     * Adds the current temporary calculation package as a new subpackage to the superpackage
     */
    private void addSuperPackage() {
        superPackage.add(tempPacket.get(0));
        Double currentPrize = shippingCalculator.calcShippingCosts(superPackage);
        result1.setText(currentPrize.toString());
    }

}
