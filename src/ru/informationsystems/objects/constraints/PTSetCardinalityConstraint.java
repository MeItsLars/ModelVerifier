package ru.informationsystems.objects.constraints;

import ru.informationsystems.objects.population.Population;
import ru.informationsystems.objects.types.PowerType;

import java.util.List;
import java.util.Set;

/**
 * This class represents a power type cardinality constraint.
 * A power type cardinality constraint is used to indicate that each set in a power type's population must
 * have at least {minimum} and at most {maximum} elements.
 */
public class PTSetCardinalityConstraint implements Constraint {

    // The power type of this constraint
    private PowerType powerType;
    // The minimum amount of times an element should occur
    private int minimum;
    // The maximum amount of times an element should occur
    private int maximum;

    public PTSetCardinalityConstraint(PowerType powerType, int minimum, int maximum) {
        this.powerType = powerType;
        this.minimum = minimum;
        this.maximum = maximum;
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

        // We use streams, and we check how many times an element occurs in a set.
        // If it is less then the minimum amount, or more than the maximum amount, the population is invalid.
        // Otherwise, it is valid.
        return powerTypePopulation.stream()
                .noneMatch(set -> {
                    int elements = set.size();
                    if (elements < minimum || elements > maximum) {
                        invalidElement = set;
                        populationIsValid = false;
                        return true;
                    } else return false;
                });
    }

    // Variables used for error printing
    private boolean populationIsValid = true;
    private Set<String> invalidElement;

    /**
     * Method for printing the result of the validation.
     * If the validation was unsuccessful, it will print error information.
     */
    @Override
    public void printResult() {
        if (populationIsValid) {
            System.out.println("Power type cardinality constraint was successfully verified.");
        } else {
            System.out.println("Failed to verify power type cardinality constraint.");
            System.out.println("Reason: A set was found with a wrong cardinality.");
            System.out.println("Element:");
            System.out.println("\t{" + String.join(", ", invalidElement) + "}");
            System.out.println("Cardinalities:");
            System.out.println("\tExpected: " + minimum + " <= c <= " + maximum);
            System.out.println("\tActual: " + invalidElement.size());
        }
    }

    /**
     * Provides the power type's name
     * @return the information, formatted into a string
     */
    @Override
    public String getInformation() {
        return "Set Cardinality Constraint over Power Type '" + powerType.getName() + "'";
    }
}
