/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.application.widget.impl;

import coolmap.application.CoolMapMaster;
import coolmap.application.widget.Widget;
import coolmap.data.contology.spmatrix.CSamplePropertyMatrix;
import coolmap.data.contology.spmatrix.CategorizedPropertyGroupSetting;
import coolmap.data.contology.spmatrix.ContinuousPropertyGroupSetting;
import coolmap.data.contology.spmatrix.PropertyGroupSetting;
import coolmap.data.contology.spmatrix.SamplePropertyGroup;
import coolmap.utils.graphics.UI;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.DefaultRowSorter;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

/**
 * This widget class displays the imported sample property table and let users
 * group and sort properties
 *
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

        // when the header is double clicked, show dialog to let user set group for properties
        _dataTable.getTableHeader().addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                Point pt = e.getPoint();
                final int col = _dataTable.columnAtPoint(pt);
                if (e.getClickCount() != 2 || col <= 0) {
                    return;
                }

                // create a dialog to let user set group info
                final JDialog dialog = new JDialog(CoolMapMaster.getCMainFrame(), "Customize Groups");
                dialog.setLocation(pt);
                dialog.setLayout(new BorderLayout());

                PropertyGroupSetting originalSetting = CoolMapMaster.getFirst().getGroupSettingForProperty(col - 1);
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

                    dialog.setLocation(e.getPoint());
                    dialog.setSize(300, 100);
                    dialog.setVisible(true);

                    JButton confirmEditButton = new JButton("Change");
                    dialog.add(confirmEditButton, BorderLayout.SOUTH);
                    
                    confirmEditButton.addMouseListener(new MouseListener() {

                        @Override
                        public void mouseClicked(MouseEvent e) {
                            dialog.setVisible(false);
                            
                            String changedString = editGroup.getText();
                            ArrayList<Double> newGroupList = new ArrayList<>();
                            
                            String[] interResult = changedString.split(",");
                            for (int i = 0; i < interResult.length; ++i) {
                                interResult[i] = interResult[i].trim();
                                try {
                                    Double curValue = Double.parseDouble(interResult[i]);
                                    newGroupList.add(curValue);
                                } catch (NumberFormatException ex) {
                                }                              
                            }
                            
                            String curPorpType = CoolMapMaster.getFirst().getPropType(col - 1);
                            ContinuousPropertyGroupSetting newSetting = new ContinuousPropertyGroupSetting(curPorpType, oldSetting.getMin(), oldSetting.getMax());
                            newSetting.setWithMarks(newGroupList);
                            CoolMapMaster.getFirst().setPropGroup(curPorpType, newSetting);
                        }

                        @Override
                        public void mousePressed(MouseEvent e) {
                            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {
                            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }
                    });

                    

                } else {

                    CategorizedPropertyGroupSetting setting = (CategorizedPropertyGroupSetting) originalSetting;
                    ArrayList<SamplePropertyGroup> groups = setting.getGroups();

                    final ArrayList<String> data = new ArrayList<>();
                    //Object data[][] = new Object[groups.size()][1];

                    for (int i = 0; i < groups.size(); ++i) {
                        data.add(groups.get(i).customizedName);
                    }

                    final JList list = new JList(data.toArray());

                    list.addMouseListener(new MouseListener() {

                        @Override
                        public void mouseClicked(MouseEvent e) {

                            final int index = list.getSelectedIndex();
                            if (e.getClickCount() != 2) {
                                return;
                            }

                            final JDialog editDialog = new JDialog(dialog, "Modifying Group");
                            final JTextField editGroup = new JTextField(data.get(index));

                            editDialog.setLayout(new BorderLayout());
                            editDialog.add(editGroup, BorderLayout.NORTH);

                            editDialog.setLocation(e.getPoint());
                            editDialog.setSize(200, 100);
                            editDialog.setVisible(true);

                            JButton confirmEditButton = new JButton("Change");
                            editDialog.add(confirmEditButton, BorderLayout.SOUTH);

                            confirmEditButton.addMouseListener(new MouseListener() {

                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    editDialog.setVisible(false);
                                    String changedString = editGroup.getText();
                                    ArrayList<String> newData = _generateNewData(data, index, changedString);

                                    data.clear();
                                    data.addAll(newData);
                                    list.setListData(data.toArray());
                                }

                                @Override
                                public void mousePressed(MouseEvent e) {
                                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                                }

                                @Override
                                public void mouseReleased(MouseEvent e) {
                                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                                }

                                @Override
                                public void mouseEntered(MouseEvent e) {
                                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                                }

                                @Override
                                public void mouseExited(MouseEvent e) {
                                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                                }
                            });
                            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }

                        @Override
                        public void mousePressed(MouseEvent e) {
                            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {
                            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }
                    });

                    JPanel panel = new JPanel(new BorderLayout());
                    JScrollPane scrollPane1 = new JScrollPane(list);

                    panel.add(scrollPane1, BorderLayout.NORTH);
                    //dialog.pack();
                    Dimension d = new Dimension(300, 200);
                    dialog.setSize(d);
                    dialog.add(panel);

                    JButton confirmButton = new JButton("Confirm");

                    panel.add(confirmButton, BorderLayout.SOUTH);

                    confirmButton.addMouseListener(new MouseListener() {

                        @Override
                        public void mouseClicked(MouseEvent e) {
                            dialog.setVisible(false);

                            ArrayList<HashSet> newSettings = new ArrayList<>();

                            for (int i = 0; i < data.size(); ++i) {
                                newSettings.add(_convertStringToHashSet(data.get(i)));

                            }

                            CoolMapMaster.getFirst().setCatePropGroup(col - 1, newSettings);

                        }

                        @Override
                        public void mousePressed(MouseEvent e) {
                            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {
                            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }
                    });
                }

                //dialog.set
                dialog.setVisible(true);
                // CoolMapMaster.lookupPropertyAtIndex(col);

            }

            @Override
            public void mousePressed(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
    }

    private ArrayList<String> _generateNewData(ArrayList<String> data, int index, String changedString) {
        String originalString = data.get(index);
        HashSet<String> originalGroup = _convertStringToHashSet(originalString);

        HashSet<String> changedGroup = _convertStringToHashSet(changedString);
        if (originalGroup.equals(changedGroup)) {
            return data;
        }

        ArrayList<String> newDataList = new ArrayList<>();
        for (int i = 0; i < data.size(); ++i) {
            if (i != index) {
                HashSet<String> curGroup = _convertStringToHashSet(data.get(i));
                curGroup.removeAll(changedGroup);
                if (curGroup.size() > 0) {
                    newDataList.add(curGroup.toString());
                }
            } else {
                if (changedGroup.size() > 0) {
                    newDataList.add(changedGroup.toString());
                }
            }
        }

        originalGroup.removeAll(changedGroup);
        if (originalGroup.size() > 0) {
            newDataList.add(originalGroup.toString());
        }

        return newDataList;

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
                DefaultTableModel model = _getOntologyAsTableModel(dataMatrix);
                _dataTable.setModel(model);

                DefaultRowSorter sorter = (DefaultRowSorter) _dataTable.getRowSorter();
                //sorter.setSortable(0, false);

                for (int i = 1; i < model.getColumnCount(); ++i) {
                    sorter.setSortable(i, false);
                }
            }
        });
    }

}
