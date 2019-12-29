package ru.informationsystems.objects.types;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for representing fact types
 */
public class FactType extends ObjectType {

    // The list of predicators that this fact type contains
    private List<Predicator> predicators = new ArrayList<>();

    public FactType(String name) {
        super(name);
    }

    /**
     * Adds a predicator to the list of predicators
     * @param predicator The predicator to be added
     */
    public void addPredicator(Predicator predicator) {
        predicators.add(predicator);
    }

    /**
     * @return the list of predicators
     */
    public List<Predicator> getPredicators() {
        return predicators;
    }
}
