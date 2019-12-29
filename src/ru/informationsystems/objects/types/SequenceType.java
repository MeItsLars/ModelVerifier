package ru.informationsystems.objects.types;

/**
 * Class for representing sequence types
 */
public class SequenceType extends ObjectType {

    // The object type this sequence type spans
    private ObjectType objectType;

    public SequenceType(String name, ObjectType objectType) {
        super(name);
        this.objectType = objectType;
    }

    /**
     * @return The object type this sequence type spans
     */
    public ObjectType getElement() {
        return objectType;
    }
}
