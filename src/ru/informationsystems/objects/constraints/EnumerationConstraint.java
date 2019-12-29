package ru.informationsystems.objects.constraints;

import ru.informationsystems.objects.population.Population;
import ru.informationsystems.objects.types.LabelType;

import java.util.List;
import java.util.Set;

/**
 * This class represents an enumeration constraint.
 * An enumeration constraint is a constraint that is applied to a label type,
 * to indicate that this type can only contain specified values.
 */
public class EnumerationConstraint implements Constraint {

    // The label type of this constraint
    private LabelType labelType;
    // The allowed label values
    private Set<String> allowedValues;

    public EnumerationConstraint(LabelType labelType, Set<String> allowedValues) {
        this.labelType = labelType;
        this.allowedValues = allowedValues;
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
        // We retrieve the population of the label type
        List<String> pop = population.getLabelTypePopulation(labelType);

        // We loop through all elements in the population, and check if they are in the allowed elements list.
        // If they are not, we return false.
        for (String element : pop) {
            if (!allowedValues.contains(element)) {
                populationIsValid = false;
                invalidElement = element;
                return false;
            }
        }
        return true;
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
            System.out.println("Enumeration constraint was successfully verified.");
        } else {
            System.out.println("Failed to verify enumeration constraint.");
            System.out.println("Reason: An invalid label type element was found.");
            System.out.println("Element:");
            System.out.println("\t" + invalidElement);
            System.out.println("Allowed elements:");
            System.out.println("\t" + String.join(", ", allowedValues));
        }
    }

    /**
     * Provides the allowed values of this constraint
     * @return the information, formatted into a string
     */
    @Override
    public String getInformation() {
        return "Enumeration Constraint with allowed values {" + String.join(", ", allowedValues) + "}";
    }
}
