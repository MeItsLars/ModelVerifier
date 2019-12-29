package ru.informationsystems.util;

import ru.informationsystems.objects.InformationStructure;
import ru.informationsystems.objects.types.*;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains a few useful methods that can be applied with an information structure.
 * It also contains a lot of assertion methods, that determine of an element exists, and throw an error if it doesn't.
 */
public class AssertionHandler {

    // The information structure of this assertion handler
    private InformationStructure informationStructure;

    public AssertionHandler(InformationStructure informationStructure) {
        this.informationStructure = informationStructure;
    }

    // This method determines if two object types in an information structure are type related.
    public boolean isTypeRelated(ObjectType objectType1, ObjectType objectType2) {
        Map<ObjectType, Boolean> isExplored = new HashMap<>();
        for (ObjectType objectType : informationStructure.getObjectTypes()) isExplored.put(objectType, false);
        return assertTypeRelatedness(isExplored, objectType1, objectType2, true);
    }

    /**
     * This method determines whether two object types are type related.
     * @param isExplored A map containing, for each object type, if it has been explored yet
     * @param objectType1 The first object type
     * @param objectType2 The second object type
     * @param initial True if this is the initial call to the method, false otherwise
     * @return True if objectType1 and objectType2 are type related, false otherwise.
     */
    private boolean assertTypeRelatedness(Map<ObjectType, Boolean> isExplored, ObjectType objectType1, ObjectType objectType2, boolean initial) {
        // We create a copy of the map. This will be used for derivation rule T2
        Map<ObjectType, Boolean> copy = new HashMap<>(isExplored);

        // Tell the algorithm that this node is now explored.
        isExplored.put(objectType1, true);

        // Case T1: If two object types are equal, they are type related.
        if (objectType1.equals(objectType2)) return true;

        // If object type 2 is already explored, they are not type related.
        if (isExplored.get(objectType2)) return false;

        // Case T2: If A is type related to B, B is type related to A
        // We check if this is the initial call (otherwise we would get an infinite loop), and check their type relatedness.
        if (initial && assertTypeRelatedness(copy, objectType2, objectType1, false)) return true;

        // Case T3: If there exists a Y such that [](objectType1) = [](Y) AND Y ~ objectType2 then they are type related.
        // Check if the object type is an entity type
        if (objectType1 instanceof EntityType) {
            EntityType entityType = (EntityType) objectType1;

            // Loop through all unexplored object types, and check if they are a specialization of objectType1.
            // If they are, and they are type related to objectType2, the result is true.
            for (ObjectType objectType : isExplored.keySet()) {
                if (!isExplored.get(objectType)) {
                    if (entityType.getSpecializations().contains(objectType)
                            && assertTypeRelatedness(isExplored, objectType, objectType2, false)) return true;
                }
            }
        }
        // Loop through all unexplored object types.
        for (ObjectType objectType : isExplored.keySet()) {
            if (!isExplored.get(objectType)) {

                // Check if the object type is an Entity Type
                if (objectType instanceof EntityType) {
                    EntityType entityType = (EntityType) objectType;

                    // Check if objectType1 is a specialization of this object type, and if this object type is
                    // type related to objectType2. If they are, the result is true.
                    if (entityType.getSpecializations().contains(objectType1)
                            && assertTypeRelatedness(isExplored, entityType, objectType2, false)) return true;
                }
            }
        }

        // Case T4: If objectType1 Gen Y and Y ~ objectType 2 then they are type related.
        // Check if objectType1 is an Entity Type
        if (objectType1 instanceof EntityType) {
            EntityType entityType = (EntityType) objectType1;

            // Loop through all unexplored object types. Check if they are generalizations of objectType1.
            // If they are, and they are type related to objectType2, the result is true.
            for (ObjectType objectType : isExplored.keySet()) {
                if (!isExplored.get(objectType)) {
                    if (entityType.getGeneralizations().contains(objectType)
                            && assertTypeRelatedness(isExplored, objectType, objectType2, false)) return true;
                }
            }
        }

        // Case T5: If the entity types of two power types are type related, the power types themselves are type related.
        if (objectType1 instanceof PowerType && objectType2 instanceof PowerType) {
            if (assertTypeRelatedness(isExplored, ((PowerType) objectType1).getElement(), ((PowerType) objectType2).getElement(), false)) return true;
        }

        // Case T6: If the entity types of two sequence types are type related, the sequence types themselves are type related.
        if (objectType1 instanceof SequenceType && objectType2 instanceof SequenceType) {
            if (assertTypeRelatedness(isExplored, ((SequenceType) objectType1).getElement(), ((SequenceType) objectType2).getElement(), false)) return true;
        }

        // This case is for schema types, which we currently ignore
        // Case T7: If O_objectType1 = O_objectType2 then TRUE

        // If none of the derivation rules say that these two types are type related, they are not.
        // We can return false.
        return false;
    }

    /**
     * Checks if the given name parameter exists in the list of object types.
     * If it does, an exception will be thrown.
     * @param name The parameter to be checked
     */
    public void assertNoDuplicates(String name) {
        boolean existDuplicates = informationStructure.getObjectTypes().stream()
                .anyMatch(ot -> ot.getName().equals(name));

        if (existDuplicates) throw new IllegalArgumentException("There already exists an object with name " + name);
    }

    /**
     * Checks if the given name parameter exists in the list of object types.
     * If it does not, an exception will be thrown.
     * @param name The parameter to be checked
     */
    public void assertExists(String name) {
        boolean exists = informationStructure.getObjectTypes().stream()
                .anyMatch(ot -> ot.getName().equals(name));

        if (!exists) throw new IllegalArgumentException("There does not exist an object type with name " + name);
    }

    /**
     * Checks if the given name parameter exists in the list of object types.
     * If it does, it will check if that object type is an instance of the given type.
     * If it is not, an exception will be thrown.
     * @param name The parameter to be checked
     * @param type The class type the parameter should be
     */
    public void assertInstanceOf(String name, Class<? extends ObjectType> type) {
        assertExists(name);
        boolean exists = informationStructure.getObjectTypes().stream()
                .anyMatch(ot -> ot.getName().equals(name) && type.isAssignableFrom(ot.getClass()));

        if (!exists) throw new IllegalArgumentException("There given type " + name + " is not an instance of " + type.getName());
    }

    /**
     * Checks if the given name is already the name of a predicator.
     * If it is, an exception will be thrown.
     * @param name The name of the predicator
     */
    public void assertNoDuplicatePredicator(String name) {
        boolean exists = informationStructure.getObjectTypes().stream()
                .filter(ot -> ot instanceof FactType)
                .map(ot -> (FactType) ot)
                .anyMatch(factType -> factType.getPredicators().stream().anyMatch(p -> p.getName().equals(name)));

        if (exists) throw new IllegalArgumentException("There is already a predicator with name " + name);
    }

    /**
     * Checks if the given name is the name of a predicator.
     * If it isn't, an exception will be thrown.
     * @param name The name of the predicator
     */
    public void assertPredicatorExists(String name) {
        boolean exists = informationStructure.getObjectTypes().stream()
                .filter(ot -> ot instanceof FactType)
                .map(ot -> (FactType) ot)
                .anyMatch(factType -> factType.getPredicators().stream().anyMatch(p -> p.getName().equals(name)));

        if (!exists) throw new IllegalArgumentException("There does not exist a predicator with name " + name);
    }

    /**
     * Checks if the given entity type already has a specialization from the given object type.
     * If it does, an exception will be thrown.
     * @param entityType The entity type that contains the specializations
     * @param objectType The object type that we want to create a specialization from
     */
    public void assertNoDuplicateSpecialization(EntityType entityType, ObjectType objectType) {
        boolean exists = entityType.getSpecializations().contains(objectType);

        if (exists) throw new IllegalArgumentException("There is already a specialization from " + objectType.getName() + " to " + entityType.getName());
    }

    /**
     * Checks if the given entity type already has a generalization from the given object type.
     * If it does, an exception will be thrown.
     * @param entityType The entity type that contains the generalizations
     * @param objectType The object type that we want to create a generalization from
     */
    public void assertNoDuplicateGeneralization(EntityType entityType, ObjectType objectType) {
        boolean exists = entityType.getGeneralizations().contains(objectType);

        if (exists) throw new IllegalArgumentException("There is already a generalization from " + objectType.getName() + " to " + entityType.getName());
    }
}
