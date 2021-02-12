package Application;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The VaRComputingListener is the listener for the button 'Calculate Portfolio VaR'.
 * This class pick up the data from the table, and give all the necessary parameters
 * for a VaR object to compute the VaR.
 *
 * The parameters include the investments of each item, the confidence level, and
 * all the price values of each item.
 *
 * It also checks if the csv files work together.
 */
public class VaRComputingListener implements ActionListener {

    JTextField confidenceLevel;
    DefaultTableModel model; // The model of the table to pick up the necessary data

    public VaRComputingListener(DefaultTableModel model) {
        super();
        this.model = model;
        confidenceLevel = new JTextField();
    }

    public JTextField getConfidenceLevel() {
        return confidenceLevel;
    }

    /**
     * Function called when button 'Calculate Portfolio VaR' is pressed.
     * Compute the VaR from a VaR object and display the result or error
     * message if happened.
     *
     * Also checks if the csv files work together:
     *      - For the selected columns, all or none the files should have a value for a given row.
     *      - The files have the same number of rows
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        // Checking at least one item in the table
        if (model.getRowCount()==0){
            JOptionPane.showMessageDialog(null, new JLabel("Please add at least one item to calculate the VaR.", JLabel.CENTER), "Invalid input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the invesments and the confidence level and parse to Double
        List<Double> investments = new ArrayList<>();
        Double confidenceLevel = null;

        try {
            confidenceLevel = Double.parseDouble(this.confidenceLevel.getText());
            for (int item=0; item<model.getRowCount(); item++) {
                String investment = (String) model.getValueAt(item, 1);
                investments.add(Double.parseDouble(investment.substring(0, investment.length()-2)));
            }
        } catch (NumberFormatException ignored) {}

        // Initialize a VaR
        VaR var = new VaR(confidenceLevel, investments);

        // Compute the VaR from var
        Integer numberOfDays = null;
        List<List<Double>> prices = new ArrayList<>();
        if (var.getValue().getErrorMessage() == null) {
            try {

                // Gets the historical prices of each item
                for (int item = 0; item < model.getRowCount(); item++) {
                    prices.add(pickUpData(var, item));
                }

                /* Checks that The csv files match. */
                // Checks that all the csv files have the same number of rows
                for (int item=1; item<prices.size(); item++){
                    if (prices.get(item).size() != prices.get(0).size()){
                        var.getValue().setErrorMessage("The csv files don't match. They don't have the same number of rows. Please check.");
                        throw new IllegalArgumentException();
                    }
                }
                // For the selected columns, all or none the files should have a value for a given row.
                for (int price=0; price<prices.get(0).size(); price++){
                    boolean isNull = true;
                    if (prices.get(0).get(price) != null) {
                        isNull = false;
                    }
                    for (int item=1; item<prices.size(); item++){
                        if ((!isNull && (prices.get(item).get(price) == null)) || (isNull && (prices.get(item).get(price) != null))){
                            var.getValue().setErrorMessage("The csv files don't match. For the selected columns, some file(s) have a value at a certain row and others not. Please check.");
                            throw new IllegalArgumentException();
                        }
                    }
                }

                // Calculate the var
                numberOfDays = var.calculateHistoricalVaR(prices);

            } catch (IllegalArgumentException ignored) {}
        }

        // Display the error message or the VaR
        if (var.getValue().getErrorMessage() != null){
            JOptionPane.showMessageDialog(null, new JLabel(var.getValue().getErrorMessage(), JLabel.CENTER), "Invalid input", JOptionPane.ERROR_MESSAGE);
        }
        else{
            String htmlText =
                    "<html><body><div style='text-align:center; margin-bottom:5px;'><p>The historical portfolio VaR analized on "+ numberOfDays +" days at 1 days at "+ confidenceLevel +"% for the investments is:</p>" +
                            "<div style='font-size:20px;border:2px solid green;margin-top: 10px;'>"+ var.getValue().getValue()+" $</div></div>" +
                            "<div style='text-align:right; font-size: 8px; margin-bottom: 10px;'>Note: a positive VaR indicates a loss</div></body></html>";
            JOptionPane.showMessageDialog(null, new JLabel(htmlText, JLabel.CENTER), "Historical Value at Risk", JOptionPane.PLAIN_MESSAGE);
        }
    }


    /**
     * This function picks up the data from the csv file of the item.
     *
     * @param var The VaR object to set an error message if necessary
     * @param item Index of the item in the table model
     * @return the list of the data, keep all rows even if empty
     * @throws IllegalArgumentException if an error occurs with the csv file. Can occur if the csv file is changed after been added in the table.
     */
    private List<Double> pickUpData(VaR var, int item) throws IllegalArgumentException {

        List<Double> prices = new ArrayList<>();
        int columnIndex = -1;

        // Get the column and the csv file names
        String columnName = (String) model.getValueAt(item, 3);
        String csvFileName = (String) model.getValueAt(item, 2);

        // Read the csv file
        try (BufferedReader br = new BufferedReader(new FileReader(csvFileName))) {
            String line = br.readLine(); // skip the first line (headers)

            // Keep the index of the column in the csv file
            if (line != null){
                String[] headers = line.split(",", -1);
                for (int counter=0; counter<headers.length; counter++){
                    if (headers[counter].equals(columnName)){
                        columnIndex = counter;
                    }
                }
            }

            // fill 'prices' with the data, keep all the rows even if empty
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (!data[columnIndex].isEmpty())
                    prices.add(Double.parseDouble(data[columnIndex]));
                else
                    prices.add(null);
            }

        } catch(FileNotFoundException e) {
            var.getValue().setErrorMessage("The CSV file doesn't exist, please check the path.");
            throw new IllegalArgumentException();
        } catch (IOException e) {
            var.getValue().setErrorMessage("Unexpected error reading the file.");
            throw new IllegalArgumentException();
        }

        return prices;
    }

}
