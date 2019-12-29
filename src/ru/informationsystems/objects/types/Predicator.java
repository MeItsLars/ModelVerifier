package ru.informationsystems.objects.types;

/**
 * Class for representing predicators
 */
public class Predicator implements Comparable<Predicator> {

    // The name of the predicator
    private String name;
    // The base object type of the predicator
    private ObjectType base;
    // The fact type the predicator belongs to
    private FactType factType;

    public Predicator(String name, ObjectType base, FactType factType) {
        this.name = name;
        this.base = base;
        this.factType = factType;
    }

    /**
     * @return The name of the predicator
     */
    public String getName() {
        return name;
    }

    /**
     * @return The base type of the predicator
     */
    public ObjectType getBase() {
        return base;
    }

    /**
     * @return The fact type this predicator belongs to
     */
    public FactType getFactType() {
        return factType;
    }

    /**
     * Compares two predicators to each other, useful for sorting
     * @param predicator The predicator we want to compare the current predicator to
     * @return The result of the comparison
     */
    @Override
    public int compareTo(Predicator predicator) {
        return name.compareTo(predicator.name);
    }

    /**
     * Method for checking of two predicators are equal.
     * We do this by checking if their names are equal.
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof Predicator) {
            return name.equals(((Predicator) object).getName());
        }
        return false;
    }
}
