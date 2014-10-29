/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.application.widget.impl;

import com.google.common.collect.Range;
import coolmap.application.CoolMapMaster;
import coolmap.application.listeners.ActiveCoolMapChangedListener;
import coolmap.application.widget.Widget;
import coolmap.canvas.listeners.CViewListener;
import coolmap.canvas.misc.MatrixCell;
import coolmap.data.CoolMapObject;
import coolmap.data.cmatrixview.model.VNode;
import coolmap.data.listeners.CObjectListener;
import coolmap.utils.graphics.UI;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import static javax.swing.JTable.AUTO_RESIZE_OFF;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import temp.RowHeader;

/**
 * This widget class displays the imported sample property table and let users group and sort properties
 * @author Keqiang Li
 */
public class WidgetSamplePropertyTable extends Widget implements CObjectListener, CViewListener, ActiveCoolMapChangedListener{
    private final SamplePropertyDataTable _dataTable;
    private final JPanel _container = new JPanel();
    
    private JScrollPane scrollPane;
    private JTable rowHeader;
    
    private CoolMapObject _activeObject;
    
    private boolean _sorterTrigger = false;
    
    public WidgetSamplePropertyTable() {
        super("Sample Property Table", W_DATA, L_DATAPORT, UI.getImageIcon("grid"), null);
        CoolMapMaster.getActiveCoolMapObjectListenerDelegate().addCObjectListener(this);
        CoolMapMaster.getActiveCoolMapObjectListenerDelegate().addCViewListener(this);
        CoolMapMaster.addActiveCoolMapChangedListener(this);
        
        _dataTable = new SamplePropertyDataTable();
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(_container);
        _container.setLayout(new BorderLayout());
        
        scrollPane = new JScrollPane(_dataTable);
        rowHeader = new RowHeader(_dataTable);
        scrollPane.setRowHeaderView(rowHeader);
        scrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, rowHeader.getTableHeader());
        
        _container.add(scrollPane);
    }

    @Override
    public void aggregatorUpdated(CoolMapObject object) {
          //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void rowsChanged(CoolMapObject object) {
          //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void columnsChanged(CoolMapObject object) {
          //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void baseMatrixChanged(CoolMapObject object) {
          //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void viewRendererChanged(CoolMapObject object) {
          //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void viewFilterChanged(CoolMapObject object) {
          //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void nameChanged(CoolMapObject object) {
          //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void selectionChanged(CoolMapObject object) {
          //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mapAnchorMoved(CoolMapObject object) {
          //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void activeCellChanged(CoolMapObject object, MatrixCell oldCell, MatrixCell newCell) {
          //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mapZoomChanged(CoolMapObject object) {
          //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void gridChanged(CoolMapObject object) {
          //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void activeCoolMapChanged(CoolMapObject oldObject, CoolMapObject activeCoolMapObject) {
        _activeObject = activeCoolMapObject;
        _updateData();
    }
    
    private void _updateData()
    {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                _replaceTableModel();
            }
        });
    }
    
    private void _replaceTableModel() {
        try {
            CoolMapObject object = _activeObject;
            if (object == null) {
                _dataTable.setModel(new WidgetSamplePropertyTable.DataTableModel());
                return;
            }
            //need to make sure it's sortable
            //The selections will be

            //secure column labels
            Object[] columnLabels = new Object[object.getViewNumColumns() + 1];
//            _dataTable.clearColumnClasses();
            columnLabels[0] = "Row Nodes";
            for (int i = 0; i < object.getViewNumColumns(); i++) {
                columnLabels[i + 1] = object.getViewNodeColumn(i);
//                if(Double.class.isAssignableFrom(object.getViewClass())){
//                    _dataTable.setColumnClass(i+1, Double.class);
//                }
                if (Thread.interrupted()) {
                    return;
                }
            }

            //create
            Object[][] data = new Object[object.getViewNumRows()][object.getViewNumColumns() + 1];

            for (int i = 0; i < object.getViewNumRows(); i++) {
                data[i][0] = object.getViewNodeRow(i);
                for (int j = 0; j < object.getViewNumColumns(); j++) {
//                    data[i][j+1]
                    data[i][j + 1] = object.getViewValue(i, j);
                    if (Thread.interrupted()) {
                        return;
                    }
                }
            }

//            DataTableModel model = new DataTableModel();
            if (Thread.interrupted()) {
                return;
            }

            //Then set table model
            WidgetSamplePropertyTable.DataTableModel model = new WidgetSamplePropertyTable.DataTableModel(data, columnLabels);

            //
            for (int i = 1; i < columnLabels.length; i++) {
                if (Double.class.isAssignableFrom(object.getViewClass())) {
                    model.setColumnClass(i, null);
                }
            }
            if (Thread.interrupted()) {
                return;
            }

            _dataTable.setModel(model);

            //Remove
//        _dataTable.getColumnModel().removeColumn(_dataTable.getColumn("Row Nodes"));
            //add
            _dataTable.getRowSorter().addRowSorterListener(new RowSorterListener() {

                @Override
                public void sorterChanged(RowSorterEvent e) {
                    if (e.getType() == RowSorterEvent.Type.SORTED) {
//                        System.out.println("sort changed");

                        rowHeader.repaint();

                        //replace nodes
                        if (_activeObject == null) {
                            return;
                        }
                        List treeNodes = _activeObject.getViewTreeNodesRow();
                        ArrayList<VNode> nodes = new ArrayList<VNode>();

                        for (int i = 0; i < _dataTable.getRowCount(); i++) {
                            nodes.add((VNode) _dataTable.getValueAt(i, 0));
                        }
                        //System.out.println(nodes);
                        _sorterTrigger = true;
                        _activeObject.replaceRowNodes(nodes, treeNodes);

                    }

                }
            });
        } catch (Exception e) {
            System.err.println("Minor issue when attempting to update table model. Possibly due to render cancelation");
        }
    }
    
    private class DataTableModel extends DefaultTableModel {

        public DataTableModel() {
        }

        public DataTableModel(Object[][] data, Object[] columnNames) {
            super(data, columnNames);
        }

        private final HashMap<Integer, Class> columnClass = new HashMap();

        public void setColumnClass(int index, Class cls) {
            columnClass.put(new Integer(index), cls);
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
    
    private class SamplePropertyDataTable extends JTable {

//        private final HashMap<Integer, Class> columnClassMap = new HashMap<Integer, Class>();
        private boolean columnDragging = false;
        private DecimalFormat format = new DecimalFormat("#.###");

        public SamplePropertyDataTable() {

            setAutoCreateRowSorter(true);
            setRowSelectionAllowed(true);
            setColumnSelectionAllowed(true);
            setAutoResizeMode(AUTO_RESIZE_OFF);

            setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); //To change body of generated methods, choose Tools | Templates.

                    if (value != null && Double.class.isAssignableFrom(value.getClass())) {
                        label.setText(format.format(value));
                    }

                    if (!isSelected) {
                        int modelIndex = table.convertColumnIndexToModel(column);

                        if (modelIndex == 0) {
                            label.setBackground(UI.colorLightGreen0);
                            label.setFont(_dataTable.getTableHeader().getFont());
                        } else {
                            label.setBackground(UI.colorWhite);
                            label.setFont(UIManager.getFont("Table.font"));
                        }
                    }

                    return label;
                }

            });

            getColumnModel().addColumnModelListener(new TableColumnModelListener() {

                @Override
                public void columnAdded(TableColumnModelEvent e) {
                }

                @Override
                public void columnRemoved(TableColumnModelEvent e) {
                }

                @Override
                public void columnMoved(TableColumnModelEvent e) {
                    columnDragging = true;
                    if (columnValue == -1) {
                        columnValue = e.getFromIndex();
                    }

                    columnNewValue = e.getToIndex();

                    //System.out.println(columnValue + " " + columnNewValue);
                }

                @Override
                public void columnMarginChanged(ChangeEvent e) {
                }

                @Override
                public void columnSelectionChanged(ListSelectionEvent e) {
                }
            });

            getTableHeader().addMouseListener(new MouseAdapter() {

                @Override
                public void mouseReleased(MouseEvent e) {

                    if (columnValue != -1 && (columnValue == 0 || columnNewValue == 0)) {
                        _dataTable.moveColumn(columnNewValue, columnValue);
                    }

                    if (columnValue >= 1 && columnNewValue >= 1 && columnValue != columnNewValue) {

                        //To change body of generated methods, choose Tools | Templates.
                        if (columnDragging) {
//                        System.out.println("Drag completed");
                            reorderColumns(columnValue, columnNewValue);
                        }
                        columnDragging = false;
                    }

                    //reset them both
                    columnValue = -1;
                    columnNewValue = -1;
                }

                @Override
                public void mouseExited(MouseEvent e) {
//                    super.mouseExited(e); //To change body of generated methods, choose Tools | Templates.
//                    mouseReleased(e);
                }

            });
        }

        private int columnValue = -1;
        private int columnNewValue = -1;

        private void reorderColumns(int fromIndex, int toIndex) {
            //Note the state here must be saved
            //System.out.println(_dataTable.getColumnModel().getColumn(0).getHeaderValue());
//            if(_activeObject == null){
//                return;
//            }
//            
//            ArrayList<VNode> columnNodes = new ArrayList<VNode>(_activeObject.getViewNumColumns());
//            
//            for(int i=0; i< _dataTable.getColumnModel().getColumnCount(); i++){
//                try{
//                    //columnNodes.add((VNode)_dataTable.getColumnModel().getColumn(i).getHeaderValue());
//                    System.out.println(_dataTable.getColumnModel().getColumn(i).getIdentifier().getClass());
//                }
//                catch(Exception e){
//                    e.printStackTrace();
//                }
//            }
//            
//            System.out.println(columnNodes);
            fromIndex = fromIndex - 1;
            toIndex = toIndex - 1;

            if (toIndex > fromIndex) {
                toIndex = toIndex + 1;
            }

//            System.out.println(fromIndex + " " + toIndex);
           // if (_activeObject == null) {
            //    return;
            //}

            ArrayList<Range<Integer>> selectedColumns = new ArrayList<>(1);
            selectedColumns.add(Range.closedOpen(fromIndex, fromIndex + 1));
            //_activeObject.multiShiftColumns(selectedColumns, toIndex);

        }
    }
    
}
