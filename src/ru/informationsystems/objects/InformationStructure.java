package ru.informationsystems.objects;

import ru.informationsystems.objects.types.*;
import ru.informationsystems.util.InformationStructurePrinter;

import java.util.*;

/**
 * Class for representing an information structure
 */
public class InformationStructure {

    // The set of object types involved in this information structure
    private Set<ObjectType> objectTypes = new HashSet<>();

    /**
     * Adds an object type to the information structure
     * @param objectType The object type to be added
     */
    public void addObjectType(ObjectType objectType) {
        objectTypes.add(objectType);
    }

    /**
     * Retrieves the set of object types of this information structure
     * @return The set of object types
     */
    public Set<ObjectType> getObjectTypes() {
        return objectTypes;
    }

    /**
     * Gets the object type with the given name.
     * If it does not exists, an error is thrown.
     * @param name The name of the object type
     * @return The object type
     */
    public ObjectType getObjectType(String name) {
        Optional<ObjectType> objectType = objectTypes.stream()
                .filter(ot -> ot.getName().equals(name))
                .findAny();

        if (!objectType.isPresent()) throw new IllegalArgumentException("There does not exist an object type with name " + name);
        return objectType.get();
    }

    /**
     * Gets the predicator with the given name.
     * If it does not exist, an error is thrown.
     * @param name The name of the predicator
     * @return The predicator
     */
    public Predicator getPredicator(String name) {
        Optional<Predicator> predicator = objectTypes.stream()
                .filter(ot -> ot instanceof FactType)
                .map(ot -> (FactType) ot)
                .map(factType -> factType.getPredicators().stream().filter(pred -> pred.getName().equals(name)).findAny())
                .filter(Optional::isPresent)
                .findAny().get();

        if (!predicator.isPresent()) throw new IllegalArgumentException("There does not exist a predicator with name " + name);
        return predicator.get();
    }

    /**
     * Prints this information structure to the console
     */
    public void print() {
        InformationStructurePrinter.print(this);
    }
}
