package viewer;

import org.sqlite.SQLiteDataSource;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.*;

public class SQLiteViewer extends JFrame {

    JComboBox tables = new JComboBox();
    JTextArea query = new JTextArea();
    DefaultTableModel tableModel = new DefaultTableModel();
    JTable table = new JTable(tableModel);
    JButton execute = new JButton("Execute");


    public void setComponentsEnabled(boolean value) {
        query.setEnabled(value);
        execute.setEnabled(value);
    }

    public SQLiteViewer() {
        super("SQLite Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 900);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);

        JTextField fileName = new JTextField();
        fileName.setName("FileNameTextField");
        fileName.setBounds(20,20, 510,30);
        //fileName.setText("C:/java/hyperskill.org/!files/base.db");
        add(fileName);

        fileName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                change();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                change();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                change();
            }

            public void change() {
                setComponentsEnabled(true);
            }
        });


        JButton openFile = new JButton("Open");
        openFile.setName("OpenFileButton");
        openFile.setBounds(550, 20, 100, 30);
        add(openFile);

        openFile.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String databaseName = fileName.getText();

                File file = new File(databaseName);
                if (!file.exists()) {
                    JOptionPane.showMessageDialog(new JFrame(), "File doesn't exist!");
                    setComponentsEnabled(false);
                    return;
                }

                String url = "jdbc:sqlite:" + databaseName;
                SQLiteDataSource dataSource = new SQLiteDataSource();
                dataSource.setUrl(url);
                try (Connection con = dataSource.getConnection()) {
                    try (Statement statement = con.createStatement()) {
                        try (ResultSet tableName = statement.executeQuery("SELECT name FROM sqlite_master WHERE type ='table' AND name NOT LIKE 'sqlite_%';")) {
                            setComponentsEnabled(true);
                            tables.removeAllItems();
                            while (tableName.next()) {
                                String name = tableName.getString("name");
                                tables.addItem(name);
                            }
                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

            }
        });

        //JComboBox tables = new JComboBox();
        tables.setName("TablesComboBox");
        tables.setBounds(20,60, 630,30);
        add(tables);

        tables.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox) e.getSource();
                String item = (String) box.getSelectedItem();
                query.setText("SELECT * FROM " + item + ";");
            }
        });

        //JTextArea query = new JTextArea();
        query.setName("QueryTextArea");
        query.setBounds(20,100, 510,130);
        add(query);

        //JButton execute = new JButton("Execute");
        execute.setName("ExecuteQueryButton");
        execute.setBounds(550, 100, 100, 30);
        add(execute);

        execute.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String databaseName = fileName.getText();
                String executeQuery = query.getText();
                String url = "jdbc:sqlite:" + databaseName;
                SQLiteDataSource dataSource = new SQLiteDataSource();
                dataSource.setUrl(url);
                try (Connection con = dataSource.getConnection()) {
                    try (Statement statement = con.createStatement()) {
                        try (ResultSet tableName = statement.executeQuery(executeQuery)) {
                            tableModel.setColumnCount(0);
                            tableModel.setRowCount(0);

                            ResultSetMetaData meta = tableName.getMetaData();
                            int cols = meta.getColumnCount();
                            for (int i = 1; i <= cols; i++) {
                                tableModel.addColumn(meta.getColumnName(i));
                            }

                            Object[] obj = new Object[cols];

                            while (tableName.next()) {
                                for (int i = 0; i < cols; i++) {
                                    obj[i] = tableName.getString(i + 1);
                                }
                                tableModel.addRow(obj);
                            }
                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

            }
        });

        //JTable table = new JTable(array, columnsHeader);
        table.setName("Table");
        table.setBounds(20,250, 630,590);
        add(table);

        setComponentsEnabled(false);

        setVisible(true);
    }
}
