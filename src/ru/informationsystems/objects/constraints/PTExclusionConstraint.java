package ru.informationsystems.objects.constraints;

import ru.informationsystems.objects.population.Population;
import ru.informationsystems.objects.types.PowerType;

import java.util.List;
import java.util.Set;

/**
 * This class represents power type exclusion constraint.
 * A power type exclusion constraint is used to indicate that each element in a powertype's entity type must
 * occur in at most one of the sets in the power type.
 */
public class PTExclusionConstraint implements Constraint {

    // The power type of this constraint
    private PowerType powerType;

    public PTExclusionConstraint(PowerType powerType) {
        this.powerType = powerType;
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
        // We retrieve the population that belongs to this power type
        List<Set<String>> powerTypePopulation = population.getPowerTypePopulation(powerType);
        // We retrieve the population that belongs to the entity type
        List<String> entityTypePopulation = population.getEntityTypePopulation(powerType.getElement());

        // We use streams to check if there is an element that occurs in more than one set.
        // If this is the case, the result of this stream will be false.
        // If all elements occur in a set, the result will be true.
        return entityTypePopulation.stream()
                .noneMatch(element -> {
                    long result = powerTypePopulation.stream()
                            .flatMap(Set::stream)
                            .filter(elt -> elt.equals(element))
                            .count();

                    if (result > 1) {
                        populationIsValid = false;
                        invalidElement = element;
                    }
                    return result > 1;
                });
    }

    // Variables used for error printing
    private boolean populationIsValid = true;
    private String invalidElement;

    /**
     * Method for printing the result of the validation.
     * If the validation was unsuccessful, it will print error information.
     */
    @Override
    public void printResult() {
        if (populationIsValid) {
            System.out.println("Power type exclusion constraint was successfully verified.");
        } else {
            System.out.println("Failed to verify power type exclusion constraint.");
            System.out.println("Reason: An element was found that occurs in two or more power type sets.");
            System.out.println("Element:");
            System.out.println("\t" + invalidElement);
        }
    }

    /**
     * Provides the power type's name
     * @return the information, formatted into a string
     */
    @Override
    public String getInformation() {
        return "Exclusion Constraint over Power Type '" + powerType.getName() + "'";
    }
}
