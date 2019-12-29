package ru.informationsystems.objects.constraints;

import ru.informationsystems.objects.population.Population;
import ru.informationsystems.objects.types.FactType;
import ru.informationsystems.objects.types.PowerType;
import ru.informationsystems.objects.types.Predicator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class represents a power type membership constraint.
 * A power type membership constraint is used to indicate that when an element is in a fact type,
 * it must also be in the power type set that belongs to this fact type.
 */
public class PTMembershipConstraint implements Constraint {

    // The power type of this constraint
    private PowerType powerType;
    // The fact type of this constraint
    private FactType factType;
    // The fact type predicator that belongs to the power type
    private Predicator powerTypePredicator;

    public PTMembershipConstraint(PowerType powerType, FactType factType) {
        this.powerType = powerType;
        this.factType = factType;

        // We determine which predicator in the fact type is the one that belongs to the power type
        for (Predicator pred : factType.getPredicators()) {
            if (pred.getBase().getName().equals(powerType.getName())) {
                powerTypePredicator = pred;
                break;
            }
        }
        // If no predicator was found, a mistake was made
        if (powerTypePredicator == null) throw new IllegalArgumentException("The given fact type is not a fact type connected to this power type.");
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
        // We retrieve the population that belongs to the fact type
        List<Map<Predicator, String>> factTypePopulations = population.getFactTypePopulations(factType);

        // We loop through all rows in the population
        for (Map<Predicator, String> factTypePopulation : factTypePopulations) {
            // We determine the value that the power type predicator in this fact type has
            String powerTypeValue = factTypePopulation.entrySet().stream()
                    .filter(e -> e.getKey().equals(powerTypePredicator))
                    .map(Map.Entry::getValue)
                    .findAny()
                    .orElse("");
            powerTypeValue = powerTypeValue.replaceAll("[{}]", "");
            // We split that value into a list of elements that are in the power type set
            List<String> powerTypeValues = Arrays.asList(powerTypeValue.split(","));

            // We loop through all entries in the fact type population
            for (Map.Entry<Predicator, String> entry : factTypePopulation.entrySet()) {
                if (!entry.getKey().equals(powerTypePredicator)) {
                    // We check if the power type set contains the entry value.
                    // If this is not the case, the constraint was violated
                    if (!powerTypeValue.contains(entry.getValue())) {
                        populationIsValid = false;
                        invalidElement = entry.getValue();
                        powerTypeSet = powerTypeValues;
                        return false;
                    }
                }
            }
        }

        return true;
    }

    // Variables used for error printing
    private boolean populationIsValid = true;
    private String invalidElement;
    private List<String> powerTypeSet;

    /**
     * Method for printing the result of the validation.
     * If the validation was unsuccessful, it will print error information.
     */
    @Override
    public void printResult() {
        if (populationIsValid) {
            System.out.println("Power type membership constraint was successfully verified.");
        } else {
            System.out.println("Failed to verify power type membership constraint.");
            System.out.println("Reason: An element was found in a fact type population, that did not occur in the according power type set.");
            System.out.println("Element:");
            System.out.println("\t" + invalidElement);
            System.out.println("Power type set:");
            System.out.println("\t{" + String.join(", ", powerTypeSet) + "}");
        }
    }

    /**
     * Provides the power type's name
     * @return the information, formatted into a string
     */
    @Override
    public String getInformation() {
        return "Membership Constraint over Power Type '" + powerType.getName() + "'";
    }
}
