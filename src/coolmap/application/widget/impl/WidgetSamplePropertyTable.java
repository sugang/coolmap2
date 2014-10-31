/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.application.widget.impl;

import coolmap.application.CoolMapMaster;
import coolmap.application.widget.Widget;
import coolmap.data.contology.model.CSamplePropertyMatrix;
import coolmap.utils.graphics.UI;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

/**
 * This widget class displays the imported sample property table and let users group and sort properties
 * @author Keqiang Li
 */
public class WidgetSamplePropertyTable extends Widget {
    private final JXTable _dataTable = new JXTable();
    private final JPanel _container = new JPanel();
    
    
    public WidgetSamplePropertyTable() {
        super("Sample Property Table", W_DATA, L_DATAPORT, UI.getImageIcon("grid"), null);
        
        _dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        _dataTable.addHighlighter(new ColorHighlighter(new HighlightPredicate.ColumnHighlightPredicate(0),
                UI.colorKAMENOZOKI, null));
        _dataTable.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW,
                null, UI.colorKARAKURENAI));
        
        JScrollPane scrollPane = new JScrollPane(_dataTable);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(_container);
        _container.setLayout(new BorderLayout());
        
        _container.add(scrollPane);
    }
    
    private class DataTableModel extends DefaultTableModel {

        public DataTableModel() {
        }

        public DataTableModel(Object[][] data, Object[] columnNames) {
            super(data, columnNames);
        }

        private final HashMap<Integer, Class> columnClass = new HashMap();

        public void setColumnClass(int index, Class cls) {
            columnClass.put(index, cls);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            try {
                Class cls = columnClass.get(new Integer(columnIndex));
                if (cls == null) {
                    return super.getColumnClass(columnIndex);
                } else {
                    return cls;
                }
            } catch (Exception e) {
                return super.getColumnClass(columnIndex); //To change body of generated methods, choose Tools | Templates.
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
//            return super.isCellEditable(row, column); //To change body of generated methods, choose Tools | Templates.
            return false;
        }

    }
    
    private DefaultTableModel _getOntologyAsTableModel(CSamplePropertyMatrix matrix) {
        
        ArrayList<String> tableHeaders = new ArrayList<>();

        ArrayList<String> properties = matrix.getPropNames();
        tableHeaders.add("Samples");
        for (String propType : properties) {
            tableHeaders.add(propType);
        }
        

        String[] headers = new String[tableHeaders.size()];
        tableHeaders.toArray(headers);

        Object[][] data = new Object[matrix.getSampleNames().size()][tableHeaders.size()];
        
        for (int i = 0; i < data.length; i++) {
            ArrayList<String> samplePropertyValues = matrix.getPropertyValuesForSample(matrix.getSampleNames().get(i));
            for (int j = 0; j < tableHeaders.size(); j++) {
                data[i][j] = samplePropertyValues.get(j);
            }
        }

        DataTableModel model = new DataTableModel(data, headers);

        return model;
    }
    
    private void _updateTable(final CSamplePropertyMatrix dataMatrix) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DefaultTableModel model = _getOntologyAsTableModel(dataMatrix);       
                _dataTable.setModel(model);
            }

        });
    }
    
}
