package Application;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the action listener when selecting a csv file.
 *
 * It checks several things in the csv file and validate the
 * action only if csv file fulfill the some criteria.
 * It then only offers to selection the columns fitting some criteria.
 *
 * A column will be available to selection only if:
 *      - all the data in the column is parsable to a Double (except empty cell)
 *      - there is not a day without data between two days with data (calculated VaR would be corrupted)
 *      - at least two rows contain data (following rows given the last point)
 *
 * The file will be accepted if
 *      - the file name ends with '.csv'
 *      - the file is not empty (so contains a header line)
 *      - At least one row is available to selection (see previous list)
 */
public class BrowserListener implements ActionListener {



    private final JTextField csvName;
    private final JComboBox<String> columnName;


    public JComboBox<String> getColumnName() {
        return columnName;
    }

    public JTextField getCsvName() {
        return csvName;
    }

    public BrowserListener() {
        super();
        csvName = new JTextField();
        csvName.setEditable(false);
        columnName = new JComboBox<>();
    }

    /**
     * Handle the selection of a csv file, and validate only if
     * the selection is valid (see criteria is the comment of the class).
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open CSV file");
        int response = fileChooser.showOpenDialog(null);

        if (response == JFileChooser.APPROVE_OPTION) {
            String fileName = fileChooser.getSelectedFile().getAbsolutePath();

            if ((5<fileName.length()) && fileName.endsWith(".csv")) { // checking if this is a csv file

                // Reading the file
                try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

                    String line = br.readLine();

                    if (line != null) { // checking the file is not empty

                        // Getting the header line
                        String[] headers = line.split(",");

                        // Pick up only the valid columns as prices (on our example pick all except 'Date')
                        List<String> validColumns = new ArrayList<>();
                        for (int counter = 0; counter < headers.length; counter++) {

                            Double previousPrice = null;
                            Double nextPrice = null;
                            boolean twoDataInColumn = false;

                            BufferedReader br2 = new BufferedReader(new FileReader(fileName));
                            br2.readLine(); // skip the header line
                            String line2;

                            while ((line2 = br2.readLine()) != null) {
                                String[] data = line2.split(",", -1);

                                try {
                                    // checking if there is not a day without data between two days with data
                                    if ((previousPrice != null) && (nextPrice == null) && (!data[counter].isEmpty()))
                                        break;

                                    if (!data[counter].isEmpty()){
                                        previousPrice = nextPrice;
                                        nextPrice = Double.parseDouble(data[counter]);
                                    }
                                    else {
                                        nextPrice = null;
                                    }

                                    // Checking if at least two rows are valid
                                    if ((previousPrice != null) && (nextPrice != null))
                                        twoDataInColumn = true;


                                } catch (NumberFormatException ignored) { // if one row is not empty or parsable to string, reject it
                                    break;
                                }


                            }

                            if (twoDataInColumn && (line2 == null)){
                                validColumns.add(headers[counter]);
                            }

                        }

                        // Checking if at least one column is available to selection and display the available columns
                        if (validColumns.size()>0) {
                            csvName.setText(fileName);
                            columnName.setModel(new DefaultComboBoxModel<>(validColumns.toArray(new String[0])));
                        }
                        else {
                            JOptionPane.showMessageDialog(null, "Not enough data in the file to compute a VaR, please check.", "Invalid input", JOptionPane.ERROR_MESSAGE);
                        }

                    } else {
                        JOptionPane.showMessageDialog(null, "The CSV file is empty, please enter a valid file.", "Invalid input", JOptionPane.ERROR_MESSAGE);
                    }


                } catch (FileNotFoundException e) {
                    JOptionPane.showMessageDialog(null, "The CSV file doesn't exist, please check the path.", "Invalid input", JOptionPane.ERROR_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Unexpected error reading the file.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            else{
                JOptionPane.showMessageDialog(null, "Please enter a valid CSV file name.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        }


    }
}
