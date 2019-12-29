package ru.informationsystems.util;

import ru.informationsystems.objects.InformationStructure;
import ru.informationsystems.objects.population.Pair;
import ru.informationsystems.objects.population.Population;
import ru.informationsystems.objects.types.EntityType;
import ru.informationsystems.objects.types.FactType;
import ru.informationsystems.objects.types.ObjectType;
import ru.informationsystems.objects.types.Predicator;

import java.util.*;

/**
 * Util class with a couple of useful methods that apply to information structures and populations in general
 */
public class SchemaUtils {

    /**
     * This function collapses a list of fact types and a population of each of these fact types into a table.
     * This table contains the resulting population, which can then be used to check if it verifies a certain constraint.
     *
     * @param totalPopulation The population of the entire information structure
     * @param predicators The predicators that the constraint spans
     * @param ah The assertion handler we can use for checking type relatedness
     * @return A table representing the collapsed population
     */
    public static List<Map<Predicator, String>> collapse(Population totalPopulation, Collection<Predicator> predicators, AssertionHandler ah) {
// Initialize a resulting table
        List<Map<Predicator, String>> result = new ArrayList<>();

        // Create a new, empty set of already explored fact types
        Set<FactType> exploredFactTypes = new HashSet<>();

        // Find a random predicator in the list of predicators that this constraint spans
        Optional<Predicator> optional = predicators.stream().findAny();
        // If no such predicator exists, we return an empty table
        if (!optional.isPresent()) return result;
        // Otherwise, we retrieve the first (base) predicator
        Predicator basePredicator = optional.get();
        // We retrieve the population of this predicators fact type, and set the current result to be equal to this population.
        List<Map<Predicator, String>> basePopulation = totalPopulation.getFactTypePopulations(basePredicator.getFactType());
        result.addAll(basePopulation);
        // We add the predicators fact type to the explored fact types
        exploredFactTypes.add(basePredicator.getFactType());

        // We create an opt variable, that contains a pair with the predicator that is adjacent to the current set of explored predicators.
        Optional<Pair<Predicator, Predicator>> opt = getAdjacentPredicator(exploredFactTypes, predicators, ah);
        // We check if it's present
        while (opt.isPresent()) {
            // The predicator that is connected to this new predicator
            Predicator fromPred = opt.get().getKey();
            // The new predicator
            Predicator newPred = opt.get().getValue();
            FactType factType = newPred.getFactType();

            // We retrieve the population of this new predicators fact type
            List<Map<Predicator, String>> population = totalPopulation.getFactTypePopulations(factType);

            // We initialize a new result, that will replace the old result
            List<Map<Predicator, String>> newResult = new ArrayList<>();

            // We loop through all rows in the current table
            for (Map<Predicator, String> row : result) {
                // We loop through all rows in the new population
                for (Map<Predicator, String> newRow : population) {
                    // We check if the predicators value of the new population equals the predicators
                    // value of the old population
                    if (newRow.get(newPred).equals(row.get(fromPred))) {
                        // We create a copy of the old row
                        Map<Predicator, String> resultRow = new HashMap<>(row);

                        // We loop through all entries in the new row
                        for (Map.Entry<Predicator, String> entry : newRow.entrySet()) {
                            Predicator predicator = entry.getKey();
                            String value = entry.getValue();

                            if (predicator != newPred) {
                                // We add the new predicator value to the new resulting row
                                resultRow.put(predicator, value);
                            }
                        }

                        // We add the resulting row to the new result
                        newResult.add(resultRow);
                    }
                }
            }

            // We set the result equal to the new result
            result = newResult;

            // We add the newly explored fact type to the explored fact types
            exploredFactTypes.add(factType);
            // We calculate a new adjacent predicator
            opt = getAdjacentPredicator(exploredFactTypes, predicators, ah);
        }
        return result;
    }

    /**
     * This function takes a list of explored fact types, and the result will be an optional of a pair.
     * The function will look for an unexplored fact type that is adjacent to one of the already explored fact types.
     * With adjacent, we mean that the two predicators are type related. If we found a predicator with such a fact type,
     * the function will return a pair containing the original predicator (the one in 'exploredFactTypes') and the
     * adjacent predicator. If no such predicator exists, the function will return an empty optional.
     *
     * @param exploredFactTypes The set of already explored fact types
     * @param predicators The predicators that the constraint spans
     * @param ah The assertion handler we can use for checking type relatedness
     * @return a pair with the two predicators if an adjacent predicator exists, or otherwise an empty optional
     */
    private static Optional<Pair<Predicator, Predicator>> getAdjacentPredicator(Collection<FactType> exploredFactTypes, Collection<Predicator> predicators, AssertionHandler ah) {
        // Loop through all explored fact types and their predicators
        for (FactType factType : exploredFactTypes) {
            for (Predicator pred : factType.getPredicators()) {

                // Loop through all predicators that this UniquenessConstraint spans
                for (Predicator result : predicators) {
                    // Check if they are not yet explored
                    if(exploredFactTypes.stream().noneMatch(ft -> ft.getPredicators().contains(result))) {
                        // Get the predicator's associated fact type
                        FactType resultFactType = result.getFactType();
                        // Loop through all predicators in the fact type
                        for (Predicator result2 : resultFactType.getPredicators()) {
                            // Check if one of the predicators bases is type related to our original predicator's base.
                            // If it is, return the predicator.
                            if (ah.isTypeRelated(result2.getBase(), pred.getBase())) {
                                return Optional.of(new Pair<>(pred, result2));
                            }
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    /**
     * This function completes the lowest common ancestor of two entity types in an information structure
     * @param entityType1 The first entity type
     * @param entityType2 The second entity type
     * @param informationStructure The information structure
     * @return The (optional) lowest common ancestor
     */
    public static Optional<EntityType> getLowestCommonAncestor(EntityType entityType1, EntityType entityType2, InformationStructure informationStructure) {
        // We create a new queue and add the first element
        Queue<EntityType> entityTypes = new LinkedList<>();
        entityTypes.add(entityType1);

        // We loop while the queue is not empty
        while (!entityTypes.isEmpty()) {
            // We retrieve the head
            EntityType head = entityTypes.poll();
            // We check if the entityType2 is a child of the head, if so, we can return the head
            if (isChildOf(entityType2, head)) return Optional.of(head);

            // Otherwise, we loop through all object types
            for (ObjectType objectType : informationStructure.getObjectTypes()) {
                if (objectType instanceof EntityType) {
                    EntityType entityType = (EntityType) objectType;

                    // We check if any of these object types are a parent of the current head
                    for (ObjectType specializationObj : entityType.getSpecializations()) {
                        if (specializationObj instanceof EntityType) {
                            EntityType specialization = (EntityType) specializationObj;
                            if (specialization == head) {
                                // If this is the case, we add this element to the queue
                                entityTypes.add(entityType);
                            }
                        }
                    }
                }
            }
        }
        // No common ancestor was found
        return Optional.empty();
    }

    /**
     * Method that checks if an entity type is a child of another entity type
     * @param child The possible child entity type
     * @param parent The possible parent entity type
     * @return whether the child is a child of the parent
     */
    private static boolean isChildOf(EntityType child, EntityType parent) {
        if (parent == child) return true;

        // We create a queue
        Queue<EntityType> entityTypes = new LinkedList<>();
        entityTypes.add(parent);

        // We loop while the queue is not empty
        while (!entityTypes.isEmpty()) {
            EntityType head = entityTypes.poll();
            // We loop through all specializations of the head
            for (ObjectType specializationObj : head.getSpecializations()) {
                if (specializationObj instanceof EntityType) {
                    // If the specialization is also an entity type, we check if it is the child.
                    // If this is the case, we return true.
                    // Otherwise, we add this specialization to the queue
                    EntityType specialization = (EntityType) specializationObj;
                    if (specialization == child) return true;
                    entityTypes.add(specialization);
                }
            }
        }
        // It was not a child
        return false;
    }

    /**
     * This method checks if the elements of two rows on the given predicators match
     * @param row1 The first row
     * @param row2 The second row
     * @param predicators The predicators that should match
     * @return Whether the rows match on the predicators
     */
    public static boolean fullRowsMatch(Map<Predicator, String> row1, Map<Predicator, String> row2, Collection<Predicator> predicators) {
        // We loop through all predicators
        for (Predicator pred : predicators) {
            // Check if the values for the predicator in both rows are equal.
            // If they are not equal, this means that this row is unique.
            for (Map.Entry<Predicator, String> entry : row1.entrySet()) {
                Predicator p1 = entry.getKey();
                String s1 = entry.getValue();

                for (Map.Entry<Predicator, String> e : row2.entrySet()) {
                    Predicator p2 = e.getKey();
                    String s2 = e.getValue();

                    if (p1.equals(p2) && p2.equals(pred) && !s1.equals(s2)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * This method checks if the elements of two rows on the given predicators match
     * @param row1 The first row
     * @param row2 The second row
     * @param predicators A map, for each prediator indication which other predicator should be connected
     * @param right Whether the map should be read left to right or right to left
     * @return Whether the two rows match on the given predicators map
     */
    public static boolean fullRowsMatch(Map<Predicator, String> row1, Map<Predicator, String> row2, Map<Predicator, Predicator> predicators, boolean right) {
        // We loop through all predicators
        for (Map.Entry<Predicator, Predicator> entry : predicators.entrySet()) {

            // We loop through the first row
            for (Map.Entry<Predicator, String> p1 : row1.entrySet()) {
                // We check if the first row predicator equals the key of the entry
                if (p1.getKey().getName().equals(right ? entry.getKey().getName() : entry.getValue().getName())) {

                    // We loop through the second row
                    for (Map.Entry<Predicator, String> p2 : row2.entrySet()) {
                        // We check if the second row predicator equals the value of the entry
                        if (p2.getKey().getName().equals(right ? entry.getValue().getName() : entry.getKey().getName())) {

                            // We check if the values of the two predicators are equal.
                            // If they are not, we return false.
                            if (!p1.getValue().equals(p2.getValue())) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        // The rows are equal over the given predicators.
        return true;
    }
}
