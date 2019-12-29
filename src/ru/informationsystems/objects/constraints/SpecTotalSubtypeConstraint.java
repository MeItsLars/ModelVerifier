package ru.informationsystems.objects.constraints;

import ru.informationsystems.objects.InformationStructure;
import ru.informationsystems.objects.population.Population;
import ru.informationsystems.objects.types.EntityType;
import ru.informationsystems.objects.types.ObjectType;
import ru.informationsystems.util.SchemaUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents a specialization total subtype constraint.
 * A specialization total subtype constraint is used to indicate that one or more entity types together
 * have the same population as their lowest common ancestor.
 */
public class SpecTotalSubtypeConstraint implements Constraint {

    // The lowest common ancestor of the entity types
    private EntityType lowestCommonAncestor;
    // The set of entity types
    private Set<EntityType> entityTypes;

    public SpecTotalSubtypeConstraint(Set<EntityType> entityTypes, InformationStructure informationStructure) {
        this.entityTypes = entityTypes;

        // We loop through all entity types, and keep calculating the lowest common ancestor of the current entity type
        // and the previous lowest common ancestor
        Queue<EntityType> queue = new LinkedList<>(entityTypes);
        EntityType lowestCommonAncestor = queue.poll();
        while (!queue.isEmpty()) {
            EntityType head = queue.poll();

            Optional<EntityType> optional = SchemaUtils.getLowestCommonAncestor(head, lowestCommonAncestor, informationStructure);
            if (!optional.isPresent()) throw new IllegalArgumentException("No common ancestor in the given entity type list.");
            else lowestCommonAncestor = optional.get();
        }
        if (lowestCommonAncestor == null) throw new IllegalArgumentException("No common ancestor in the given entity type list.");

        // In the end, the lowest common ancestor is set
        this.lowestCommonAncestor = lowestCommonAncestor;
    }

    /**
     * Validates the constraint against a given population.
     * The result will be true if the population satisfies the constraint, and false otherwise.
     *
     * @param population The input population we want to validate
     * @return the result of the validation
     */
    @Override
    public boolean validate(Population population) {
        // We retrieve the population of the ancestor entity type, and put it in a set
        Set<String> ancestorPopulation = new HashSet<>(population.getEntityTypePopulation(lowestCommonAncestor));

        // We use streams to take the union of the populations of the other entity types
        Set<String> entityTypesPopulation = entityTypes.stream()
                .map(population::getEntityTypePopulation)
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        // We return if they were equal.
        if (ancestorPopulation.equals(entityTypesPopulation)) {
            return true;
        } else {
            populationIsValid = false;
            return false;
        }
    }

    // Variables used for error printing
    private boolean populationIsValid = true;

    /**
     * Method for printing the result of the validation.
     * If the validation was unsuccessful, it will print error information.
     */
    @Override
    public void printResult() {
        if (populationIsValid) {
            System.out.println("Specialization total subtype constraint was successfully verified.");
        } else {
            System.out.println("Failed to verify specialization total subtype constraint.");
            System.out.println("Reason: An element was found in the lowest common ancestor that did not occur in any of the entity types.");
        }
    }

    /**
     * Provides the entity types this specialization covers
     * @return the information, formatted into a string
     */
    @Override
    public String getInformation() {
        return "Total Subtype Constraint over Specialization {" + entityTypes.stream()
                .map(ObjectType::getName).collect(Collectors.joining(", ")) + "}";
    }
}
