package Application;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class add a row the to table when adding an item to the portfolio
 *
 * The form will only be accepted if:
 *      - a csv file have been selected (see BrowserListener.java)
 *      - a column has been selected
 *      - a valid investment has been entered (number>0)
 */
public class AddItemListener implements ActionListener {

    private final BrowserListener browserListener;
    private final DefaultTableModel model;
    private final JTable table;
    private final JFrame frame;
    private final JTextField investment;
    private final JTextField itemName;

    public AddItemListener(BrowserListener browserListener, JTable table, DefaultTableModel model, JFrame frame) {
        super();

        this.browserListener = browserListener;
        this.model = model;
        this.table = table;
        this.frame = frame;
        investment = new JTextField();
        itemName   = new JTextField();
    }

    public BrowserListener getBrowserListener() {
        return browserListener;
    }

    public JTextField getInvestment() {
        return investment;
    }

    public JTextField getItemName() {
        return itemName;
    }

    /**
     * Display and error message or add the item the table,
     * depending on the validity of the inputs.
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        // Getting the input
        String csvName = browserListener.getCsvName().getText();
        String columnName = String.valueOf(browserListener.getColumnName().getSelectedItem());
        String investment = this.investment.getText();
        String itemName = this.itemName.getText();

        // Check the inputs
        String errorMessage = checkInput(csvName, columnName);

        // Display the error message or add the item to the table
        if (errorMessage != null) {
            JOptionPane.showMessageDialog(null, new JLabel(errorMessage, JLabel.CENTER), "Invalid input", JOptionPane.ERROR_MESSAGE);
        } else {
            model.addRow(new Object[]{itemName, investment+" $", csvName, columnName, "X"});
            table.setModel(model);
            frame.setVisible(false);
            this.investment.setText("");
        }
    }

    /**
     * Check the validity of the inputs.
     *
     * @param csvName is the csv file path
     * @param columnName is the selected column name
     * @return the error message if invalid inputs.
     */
    private String checkInput(String csvName, String columnName) {

        if (csvName.isEmpty()){
            return "Please enter a csv file";
        }
        if (columnName.isEmpty()){
            return "Please select a column (unexpected error).";
        }

        try {
            if ( !(0<Double.parseDouble(this.investment.getText())) ){
                return "Please enter a valid investment value (>0).";
            }
        } catch (NumberFormatException ignored) {
            return "Please enter a valid investment value (>0).";
        }

        return null;
    }


}