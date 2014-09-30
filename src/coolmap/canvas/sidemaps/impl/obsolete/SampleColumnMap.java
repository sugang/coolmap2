/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.canvas.sidemaps.impl.obsolete;

import coolmap.canvas.listeners.CViewListener;
import coolmap.canvas.misc.MatrixCell;
import coolmap.canvas.sidemaps.ColumnMap;
import coolmap.data.CoolMapObject;
import coolmap.data.cmatrixview.model.VNode;
import coolmap.utils.graphics.UI;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.JComponent;

/**
 *
 * @author gangsu
 */
public class SampleColumnMap extends ColumnMap<Object, Object> implements CViewListener {

    @Override
    public void nameChanged(CoolMapObject object) {
    }

    @Override
    public void viewFilterChanged(CoolMapObject object) {
    }

//    @Override
//    public void subSelectionRowChanged(CoolMapObject object) {
//    }
//
//    @Override
//    public void subSelectionColumnChanged(CoolMapObject object) {
//    }
    public SampleColumnMap(CoolMapObject object) {
        super(object);
    }

    @Override
    public void justifyView() {
    }

    @Override
    public void viewRendererChanged(CoolMapObject object) {
    }

    @Override
    public void render(Graphics2D g2D, CoolMapObject<Object, Object> object, int fromRow, int toRow, int fromCol, int toCol, float zoomX, float zoomY, int width, int height) {
        g2D.setColor(Color.BLACK);
        g2D.fillRect(0, 0, width, height);
        super.render(g2D, object, fromRow, toRow, fromCol, toCol, zoomX, zoomY, width, height);
    }

    @Override
    public boolean canRender(CoolMapObject coolMapObject) {
        if (Object.class.isAssignableFrom(coolMapObject.getViewClass())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public JComponent getConfigUI() {
        return null;
    }

    @Override
    public void renderColumn(Graphics2D g2D, CoolMapObject<Object, Object> object, VNode node, int anchorX, int anchorY, int cellWidth, int cellHeight) {
        g2D.setColor(UI.randomColor());
        g2D.fillRect(anchorX, anchorY, cellWidth, cellHeight);
    }

    @Override
    public void activeCellChanged(CoolMapObject obj, MatrixCell oldCell, MatrixCell newCell) {
        //System.out.println("Column respond to active cell change" + oldCell + " " + newCell);
    }

    @Override
    public void selectionChanged(CoolMapObject obj) {
        //System.out.println("Column respond to selection change");
    }

    @Override
    protected void prepareRender(Graphics2D g2D) {
    }

    @Override
    protected void prePaint(Graphics2D g2D, CoolMapObject<Object, Object> object, int width, int height) {
    }

    @Override
    protected void postPaint(Graphics2D g2D, CoolMapObject<Object, Object> object, int width, int height) {
    }

    @Override
    public void aggregatorUpdated(CoolMapObject object) {
    }

    @Override
    public void rowsChanged(CoolMapObject object) {
    }

    @Override
    public void columnsChanged(CoolMapObject object) {
    }

    @Override
    public void baseMatrixChanged(CoolMapObject object) {
    }

    @Override
    public void mapAnchorMoved(CoolMapObject object) {
    }

    @Override
    public void mapZoomChanged(CoolMapObject object) {
    }

//    @Override
//    public void stateStorageUpdated(CoolMapObject object) {
//    }
    @Override
    public void gridChanged(CoolMapObject object) {
    }
}
