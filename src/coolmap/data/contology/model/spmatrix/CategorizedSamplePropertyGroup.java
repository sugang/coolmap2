/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.data.contology.model.spmatrix;

import java.util.HashSet;

/**
 *
 * @author Keqiang Li
 */
public class CategorizedSamplePropertyGroup extends SamplePropertyGroup<String> {

    private final HashSet<String> _group;

    public CategorizedSamplePropertyGroup(String name) {
        super(name);
        _group = new HashSet<>();
    }

    public void addValue(String value) {
        _group.add(value);
    }

    public void removeValue(String value) {
        if (_group.contains(value)) {
            _group.remove(value);
        }
    }

    public void addAll(HashSet<String> values) {
        _group.addAll(values);
    }

    public void clear() {
        _group.clear();
    }

    public int getSize() {
        return _group.size();
    }

    @Override
    public boolean contains(String value) {
        return _group.contains(value);
    }
    
    @Override
    public String toString()
    {
        return _group.toString();
    }

}
