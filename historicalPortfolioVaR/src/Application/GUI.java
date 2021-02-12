package Application;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;


/**
 * This class is the displayed GUI.
 */
public class GUI implements ActionListener {

    private final JFrame frameItem;

    public GUI() {

        ///////////////////////////////////////////////////////////////

        /* Creating the main frame*/

        JFrame frame = new JFrame();
        frame.setSize(940,500);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 50, 30));

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Instrument name");
        model.addColumn("Investment*");
        model.addColumn("File name*");
        model.addColumn("CSV column*");
        model.addColumn("");
        JTable table = new JTable(model);
        table.setDefaultEditor(Object.class, null);
        table.getColumnModel().getColumn(0).setPreferredWidth(140);
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.getColumnModel().getColumn(2).setPreferredWidth(400);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(50);
        DefaultTableCellRenderer deleteRenderer = new DefaultTableCellRenderer();
        deleteRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(4).setCellRenderer(deleteRenderer);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(Color.LIGHT_GRAY);

        Action delete = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                JTable table = (JTable)e.getSource();
                int modelRow = Integer.parseInt( e.getActionCommand() );
                ((DefaultTableModel)table.getModel()).removeRow(modelRow);
            }
        };

        ButtonColumn buttonColumn = new ButtonColumn(table, delete, 4);
        buttonColumn.setMnemonic(KeyEvent.VK_D);

        JButton computingButton = new JButton("Calculate Portfolio VaR");
        VaRComputingListener varComputingListener = new VaRComputingListener(model);
        computingButton.addActionListener(varComputingListener);

        JScrollPane pane = new JScrollPane(table);

        JButton addItemButton = new JButton("Add item to portfolio");
        addItemButton.addActionListener(this);

        JLabel labelMandatoryField = new JLabel("* mandatory field");
        labelMandatoryField.setFont(new Font("Serif", Font.ITALIC, 11));
        JLabel labelConfidence = new JLabel("Enter the confidence level :");
        JLabel labelConfUnit   = new JLabel("%");

        GridBagConstraints c = new GridBagConstraints();
        panel.setLayout(new GridBagLayout());

        c.gridwidth = 10;
        c.gridy = 0;
        c.ipady = 100;
        c.ipadx = 700;
        panel.add(pane, c);
        c.gridy = 1;
        c.ipady = 0;
        c.ipadx = 0;
        c.insets = new Insets(0,620,0,0);
        panel.add(labelMandatoryField, c);
        c.gridy = 2;
        c.ipady = 0;
        c.ipadx = 0;
        c.insets = new Insets(0,0,0,0);
        panel.add(addItemButton, c);

        c.insets = new Insets(30,0,0,0);
        c.gridy = 3;
        c.ipady = 0;
        c.ipadx = 0;
        panel.add(labelConfidence, c);
        c.insets = new Insets(10,320,10,0);
        c.gridwidth = 1;
        c.gridy = 4;
        c.ipady = 15;
        c.ipadx = 80;
        panel.add(varComputingListener.getConfidenceLevel(), c);
        c.insets = new Insets(0,5,1,0);
        c.ipady = 15;
        c.ipadx = 50;
        panel.add(labelConfUnit, c);

        c.gridwidth = 10;
        c.gridx = 0;
        c.gridy = 5;
        c.ipady = 20;
        c.ipadx = 50;
        c.insets = new Insets(10,0,0,0);
        panel.add(computingButton, c);

        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Historical VaR Calculator");
        frame.setVisible(true);

        ///////////////////////////////////////////////////////////////

        /* Creating the frame for adding an item */

        frameItem = new JFrame();
        frameItem.setVisible(false);
        frameItem.setSize(500, 425);
        frameItem.setLocationRelativeTo(null);

        JLabel labelItemName   = new JLabel("Enter an item name (optional):");
        JLabel labelCSV        = new JLabel("Select a csv file:");
        JLabel labelColumn     = new JLabel("Select the column name of the prices in the csv file:");
        JLabel labelInvestment = new JLabel("Enter the desired investment:");
        JLabel labelInvestUnit = new JLabel("$");

        JButton addItem = new JButton("Add item");
        AddItemListener addItemListener = new AddItemListener(new BrowserListener(), table, model, frameItem);
        addItem.addActionListener(addItemListener);
        JButton browser = new JButton("Select CSV File");
        BrowserListener browserListener = addItemListener.getBrowserListener();
        browser.addActionListener(browserListener);

        browserListener.getColumnName().setBackground(Color.WHITE);
        browserListener.getCsvName().setBackground(Color.WHITE);

        c = new GridBagConstraints();

        JPanel panelItem = new JPanel();
        panelItem.setBorder(BorderFactory.createEmptyBorder(30, 30, 50, 30));
        panelItem.setLayout(new GridBagLayout());

        c.gridy = 0;
        c.gridwidth = 5;
        panelItem.add(labelItemName, c);
        c.insets = new Insets(10,0,20,0);
        c.gridy = 1;
        c.ipady = 15;
        c.ipadx = 150;
        panelItem.add(addItemListener.getItemName(), c);
        c.insets = new Insets(0,0,0,0);
        c.ipady = 0;
        c.ipadx = 0;
        c.gridy = 2;
        panelItem.add(labelCSV, c);
        c.insets = new Insets(10,0,20,0);
        c.gridwidth = 1;
        c.gridy = 3;
        c.ipady = 15;
        c.ipadx = 250;
        panelItem.add(browserListener.getCsvName(), c);
        c.insets = new Insets(10,20,20,0);
        c.ipady = 0;
        c.ipadx = 0;
        panelItem.add(browser, c);

        c.insets = new Insets(0,0,10,0);
        c.gridy = 4;
        c.ipady = 0;
        c.ipadx = 0;
        c.gridwidth = 5;
        panelItem.add(labelColumn, c);
        c.insets = new Insets(0,0,20,0);
        c.gridy = 5;
        c.ipady = 10;
        c.ipadx = 100;
        panelItem.add(browserListener.getColumnName(), c);

        c.insets = new Insets(0,0,10,0);
        c.gridwidth = 5;
        c.gridy = 6;
        c.ipady = 0;
        c.ipadx = 0;
        panelItem.add(labelInvestment, c);
        c.insets = new Insets(0,160,10,0);
        c.gridy = 7;
        c.ipady = 15;
        c.ipadx = 80;
        c.gridwidth = 1;
        panelItem.add(addItemListener.getInvestment(), c);
        c.insets = new Insets(0,-95,15,0);
        c.ipady = 15;
        c.ipadx = 50;
        panelItem.add(labelInvestUnit, c);

        c.gridx = 0;
        c.gridy = 8;
        c.ipady = 10;
        c.ipadx = 30;
        c.insets = new Insets(10,0,0,0);
        c.gridwidth = 5;
        panelItem.add(addItem, c);

        frameItem.add(panelItem);
        frameItem.setTitle("Add Item to Portfolio");
    }

    public static void main(String[] args) {
        new GUI();
    }

    /**
     * Make visible  the frame to add an item by clicking on button
     * 'Add item to portfolio', and prevent from opening multiple
     * 'Add item to portfolio' frames.
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (!frameItem.isVisible()) {
            frameItem.setVisible(true);
        }
    }

}
