package ru.informationsystems.objects.constraints;

import ru.informationsystems.objects.population.Population;
import ru.informationsystems.objects.types.PowerType;

import java.util.List;
import java.util.Set;

/**
 * This class represents a power type cover constraint.
 * A power type cover constraint is used to indicate that each element in a powertype's entity type must
 * occur in at least one of the sets in the power type.
 */
public class PTCoverConstraint implements Constraint {

    // The power type of this constraint
    private PowerType powerType;

    public PTCoverConstraint(PowerType powerType) {
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

        // We use streams to check if there is an element that does not occur in a set.
        // If this is the case, the result of this stream will be false.
        // If all elements occur in a set, the result will be true.
        return entityTypePopulation.stream()
                .noneMatch(element -> {
                    long result = powerTypePopulation.stream()
                            .flatMap(Set::stream)
                            .filter(elt -> elt.equals(element))
                            .count();

                    if (result == 0) {
                        populationIsValid = false;
                        invalidElement = element;
                    }
                    return result == 0;
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
            System.out.println("Power type cover constraint was successfully verified.");
        } else {
            System.out.println("Failed to verify power type cover constraint.");
            System.out.println("Reason: An element was found that does not occur in any power type set.");
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
        return "Cover Constraint over Power Type '" + powerType.getName() + "'";
    }
}
