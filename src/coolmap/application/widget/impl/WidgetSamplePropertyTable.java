/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.application.widget.impl;

import coolmap.application.CoolMapMaster;
import coolmap.application.io.actions.spmatrix.ImportContinuousPropertyGroupFromFileAction;
import coolmap.application.io.actions.spmatrix.ImportPropertyNameGroupSettingFromOBOAction;
import coolmap.application.io.actions.spmatrix.ImportPropertyIDGroupSettingFromOBOAction;
import coolmap.application.widget.Widget;
import coolmap.data.contology.model.spmatrix.CSamplePropertyMatrix;
import coolmap.data.contology.model.spmatrix.CategorizedPropertyGroupSetting;
import coolmap.data.contology.model.spmatrix.ContinuousPropertyGroupSetting;
import coolmap.data.contology.model.spmatrix.PropertyGroupSetting;
import coolmap.utils.graphics.UI;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.swing.DefaultRowSorter;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

/**
 * This widget displays the imported sample property table and let users group
 * and sort properties
 *
 * @author Keqiang Li
 */
public class WidgetSamplePropertyTable extends Widget {

    private final JXTable _dataTable = new JXTable();
    private final JPanel _container = new JPanel();
    private final JTableHeader _tableHeader = _dataTable.getTableHeader();
    private CSamplePropertyMatrix _dataMatrix;

    public WidgetSamplePropertyTable() {
        super("Sample Property Table", W_DATA, L_DATAPORT, UI.getImageIcon("grid"), null);

        _dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        _dataTable.addHighlighter(new ColorHighlighter(new HighlightPredicate.ColumnHighlightPredicate(0),
                UI.colorKAMENOZOKI, null));
        _dataTable.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW,
                null, UI.colorKARAKURENAI));

        _dataTable.setColumnModel(new CSamplePropertyTableColumnMode());

        JScrollPane scrollPane = new JScrollPane(_dataTable);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(_container);
        _container.setLayout(new BorderLayout());

        _container.add(scrollPane);

        _tableHeader.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent headerClickEvent) {
                // only response for right click
                if (!SwingUtilities.isRightMouseButton(headerClickEvent)) {
                    return;
                }

                // get where the click happens
                final Point positionClicked = headerClickEvent.getPoint();
                //get on which column user has clicked
                final int col = _dataTable.columnAtPoint(positionClicked);

                // do nothing when user clicks on the first column's header
                if (col <= 0) {
                    return;
                }

                // pop up a menu for user to choose what action to perform
                JPopupMenu tableHeaderPopupMenu = new JPopupMenu();

                boolean isCategorizedProp = _dataMatrix.isCategorizedProp(col - 1);

                if (isCategorizedProp) {
                    JMenuItem importIDGroupItem = new JMenuItem(new ImportPropertyIDGroupSettingFromOBOAction(_dataMatrix.getPropType(col - 1)));
                    importIDGroupItem.setText("Import Group Setting from OBO File (map properties to ids)");
                    tableHeaderPopupMenu.add(importIDGroupItem);

                    JMenuItem importNameGroupItem = new JMenuItem(new ImportPropertyNameGroupSettingFromOBOAction(_dataMatrix.getPropType(col - 1)));
                    importNameGroupItem.setText("Import Group Setting from OBO File (map properties to names)");
                    tableHeaderPopupMenu.add(importNameGroupItem);
                } else {
                    JMenuItem importContGroupItem = new JMenuItem(new ImportContinuousPropertyGroupFromFileAction(_dataMatrix.getPropType(col - 1)));
                    importContGroupItem.setText("Import Group Setting from text File");
                    tableHeaderPopupMenu.add(importContGroupItem);
                }

                JMenuItem viewGroupitem = new JMenuItem("View Groups");
                JMenuItem editGroupItem = new JMenuItem("Edit Group");
                if (isCategorizedProp) {
                    tableHeaderPopupMenu.add(viewGroupitem);
                } else {
                    tableHeaderPopupMenu.add(editGroupItem);
                }

                viewGroupitem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // create a dialog to let user set group info
                        final JDialog dialog = new JDialog(CoolMapMaster.getCMainFrame(), "Viewing Groups");

                        dialog.setLocation(positionClicked);
                        dialog.setLayout(new BorderLayout());

                        PropertyGroupSetting originalSetting = _dataMatrix.getGroupSettingForProperty(col - 1);
                        // no default settings found, should never happen
                        if (originalSetting == null) {
                            return;
                        }

                        CategorizedPropertyGroupSetting setting = (CategorizedPropertyGroupSetting) originalSetting;

                        DefaultMutableTreeNode top = createGroupTree(setting, setting.getRootName());
                        JTree tree = new JTree(top);
                        
                        JScrollPane treeView = new JScrollPane(tree);

                        Dimension d = new Dimension(300, 200);
                        dialog.setSize(d);

                        JButton confirmButton = new JButton("OK");

                        dialog.add(confirmButton, BorderLayout.SOUTH);

                        confirmButton.addMouseListener(new MouseAdapter() {

                            @Override
                            public void mouseClicked(MouseEvent e) {
                                dialog.setVisible(false);
                            }

                        });

                        dialog.add(treeView, BorderLayout.NORTH);
                        dialog.setVisible(true);
                    }
                });

                tableHeaderPopupMenu.show(_tableHeader, headerClickEvent.getX(), headerClickEvent.getY());

                editGroupItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        // create a dialog to let user set group info
                        final JDialog dialog = new JDialog(CoolMapMaster.getCMainFrame(), "Customize Groups");

                        dialog.setLocation(positionClicked);
                        dialog.setLayout(new BorderLayout());

                        PropertyGroupSetting originalSetting = _dataMatrix.getGroupSettingForProperty(col - 1);
                        // no default settings found, should never happen
                        if (originalSetting == null) {
                            return;
                        }
                        if (originalSetting instanceof ContinuousPropertyGroupSetting) {

                            final ContinuousPropertyGroupSetting oldSetting = (ContinuousPropertyGroupSetting) originalSetting;

                            final JTextField editGroup = new JTextField();
                            JTextField minValueField = new JTextField("" + oldSetting.getMin() + ",");
                            JTextField maxValueField = new JTextField("," + oldSetting.getMax());
                            minValueField.setEditable(false);
                            maxValueField.setEditable(false);

                            dialog.add(minValueField, BorderLayout.WEST);
                            dialog.add(maxValueField, BorderLayout.EAST);
                            dialog.add(editGroup, BorderLayout.CENTER);

                            dialog.setSize(300, 100);
                            dialog.setVisible(true);

                            JButton confirmEditButton = new JButton("Change");
                            dialog.add(confirmEditButton, BorderLayout.SOUTH);

                            confirmEditButton.addMouseListener(new MouseAdapter() {

                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    dialog.setVisible(false);

                                    String changedString = editGroup.getText();
                                    ArrayList<Double> newGroupList = new ArrayList<>();

                                    String[] interResult = changedString.split(",");
                                    for (int i = 0; i < interResult.length; ++i) {
                                        interResult[i] = interResult[i].trim();
                                        if (interResult[i].equals("")) {
                                            continue;
                                        }
                                        try {
                                            Double curValue = Double.parseDouble(interResult[i]);
                                            newGroupList.add(curValue);
                                        } catch (NumberFormatException ex) {
                                            return;  // contains invalid input.
                                        }
                                    }

                                    String curPorpType = _dataMatrix.getPropType(col - 1);
                                    ContinuousPropertyGroupSetting newSetting = new ContinuousPropertyGroupSetting(curPorpType, oldSetting.getMin(), oldSetting.getMax());
                                    newSetting.setWithMarks(newGroupList);
                                    _dataMatrix.setPropGroup(curPorpType, newSetting);
                                }
                            });

                        }

                        dialog.setVisible(true);
                    }
                });
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // check for reordering of cloumns everytime mouse released from the table header
                // if reordering occurred, regenerate the ontology
                ArrayList<String> newOrder = new ArrayList<>();

                for (int i = 1; i < _dataTable.getColumnCount(); ++i) {
                    newOrder.add(_dataTable.getColumnName(i));
                }

                _dataMatrix.setPropOrder(newOrder);
            }

        });

    }
    
    private DefaultMutableTreeNode createGroupTree(CategorizedPropertyGroupSetting setting, String rootName) {     
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(setting.getGroup(rootName).getDisplayName());
        Set<String> childrenNodes = setting.getChildren(rootName);
        
        for (String childNode : childrenNodes) {
            DefaultMutableTreeNode newNode = createGroupTree(setting, childNode);
            rootNode.add(newNode);
        }
        
        return rootNode;
    }

    private HashSet<String> _convertStringToHashSet(String str) {
        if (str.length() == 0) {
            return new HashSet<>();
        }
        String[] interResult = str.split(",");
        interResult[0] = interResult[0].substring(1);
        interResult[interResult.length - 1] = interResult[interResult.length - 1].substring(0, interResult[interResult.length - 1].length() - 1);

        HashSet<String> newGroup = new HashSet<>();
        for (int i = 0; i < interResult.length; ++i) {
            interResult[i] = interResult[i].trim();
            newGroup.add(interResult[i]);
        }

        return newGroup;

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

    private DefaultTableModel _getSPMatrixAsTableModel(CSamplePropertyMatrix matrix) {

        ArrayList<String> tableHeaders = new ArrayList<>();

        ArrayList<String> properties = matrix.getPropOrder();
        tableHeaders.add("Samples");
        for (String propType : properties) {
            tableHeaders.add(propType);
        }

        String[] headers = new String[tableHeaders.size()];
        tableHeaders.toArray(headers);

        Object[][] data = new Object[matrix.getSampleNames().size()][tableHeaders.size()];

        for (int i = 0; i < data.length; i++) {
            String curSampleName = matrix.getSampleNames().get(i);
            ArrayList<String> samplePropertyValues = matrix.getPropertyValuesForSample(curSampleName);
            data[i][0] = curSampleName;
            for (int j = 0; j < tableHeaders.size() - 1; j++) {
                data[i][j + 1] = samplePropertyValues.get(j);
            }
        }

        DataTableModel model = new DataTableModel(data, headers);

        return model;
    }

    public void updateTable(final CSamplePropertyMatrix dataMatrix) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                _dataMatrix = dataMatrix;
                DefaultTableModel model = _getSPMatrixAsTableModel(dataMatrix);
                _dataTable.setModel(model);

                DefaultRowSorter sorter = (DefaultRowSorter) _dataTable.getRowSorter();

                for (int i = 1; i < model.getColumnCount(); ++i) {
                    sorter.setSortable(i, false);
                }

            }
        });

    }

    /**
     * rewrite the moveColumn method to prevent user moving the sample column or
     * moving other columns to index 0
     */
    private class CSamplePropertyTableColumnMode extends DefaultTableColumnModel {

        @Override
        public void moveColumn(int columnIndex, int newIndex) {
            if (columnIndex == 0 || newIndex == 0) {
                return;
            }

            super.moveColumn(columnIndex, newIndex);
        }
    }

}
