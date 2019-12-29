package ru.informationsystems.objects.types;

/**
 * Abstract class for representing object types.
 * This class contains a name, since each object type has a name.
 */
public abstract class ObjectType implements Comparable<ObjectType> {

    // The name of this object type
    private String name;

    public ObjectType(String name) {
        this.name = name;
    }

    /**
     * @return the name of the object type.
     */
    public String getName() {
        return name;
    }

    /**
     * Compares two object types to each other, useful for sorting
     * @param objectType the object type we want to compare the current object type to
     * @return the result of the comparison
     */
    @Override
    public int compareTo(ObjectType objectType) {
        return name.compareTo(objectType.name);
    }
}
