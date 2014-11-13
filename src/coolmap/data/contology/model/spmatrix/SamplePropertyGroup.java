/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.data.contology.model.spmatrix;

/**
 *
 * @author Keqiang Li
 * @param <T>
 */
// user customized property group. users may group values from 1 to 100 as group "1-100"
public abstract class SamplePropertyGroup<T> {

    private String _displayName;

    public SamplePropertyGroup(String name) {
        this._displayName = name;
    }
    
    public void setDisplayName(String name) {
       _displayName = name;
    }
    
    public String getDisplayName() {
        return _displayName;
    }
    
    public abstract String getUniqueID();
    
    public abstract boolean contains(T value);
}
