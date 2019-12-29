package ru.informationsystems.objects.constraints;

import ru.informationsystems.objects.population.Population;
import ru.informationsystems.objects.types.EntityType;
import ru.informationsystems.objects.types.ObjectType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents a specialization exclusion constraint.
 * A specialization exclusion constraint is used to indicate that two specializations have no
 * common elements.
 */
public class SpecExclusionConstraint implements Constraint {

    // The set of entity types that should exclude each other
    private Set<EntityType> entityTypes;

    public SpecExclusionConstraint(Set<EntityType> entityTypes) {
        this.entityTypes = entityTypes;
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
        // We loop through all entity types
        for (EntityType entityType : entityTypes) {
            // We retrieve their population
            List<String> entityTypePopulation = population.getEntityTypePopulation(entityType);

            // We again loop through all the entity types
            for (EntityType entityType2 : entityTypes) {
                if (entityType != entityType2) {
                    // And also retrieve their population
                    List<String> entityType2Population = population.getEntityTypePopulation(entityType2);

                    // We check if there is an element that occurs in any of the other entity types.
                    // If this is the case, the constraint was validated
                    for (String element : entityTypePopulation) {
                        if (entityType2Population.contains(element)) {
                            populationIsValid = false;
                            invalidElement = element;
                            invalidElementEntityType = entityType.getName();
                            invalidElementComparedEntityType = entityType2.getName();
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    // Variables used for error printing
    private boolean populationIsValid = true;
    private String invalidElement;
    private String invalidElementEntityType;
    private String invalidElementComparedEntityType;

    /**
     * Method for printing the result of the validation.
     * If the validation was unsuccessful, it will print error information.
     */
    @Override
    public void printResult() {
        if (populationIsValid) {
            System.out.println("Specification exclusion constraint was successfully verified.");
        } else {
            System.out.println("Failed to verify specification exclusion constraint.");
            System.out.println("Reason: An element was found that occurs in another entity type population.");
            System.out.println("Element:");
            System.out.println("\t" + invalidElement);
            System.out.println("From entity type:");
            System.out.println("\t" + invalidElementEntityType);
            System.out.println("Duplicate was found in entity type:");
            System.out.println("\t" + invalidElementComparedEntityType);
        }
    }

    /**
     * Provides the entity types this specialization covers
     * @return the information, formatted into a string
     */
    @Override
    public String getInformation() {
        return "Exclusion Constraint over Specialization {" + entityTypes.stream()
                .map(ObjectType::getName).collect(Collectors.joining(", ")) + "}";
    }
}
