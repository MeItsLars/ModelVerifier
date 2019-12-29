package ru.informationsystems.objects.constraints;

import ru.informationsystems.objects.population.Population;
import ru.informationsystems.objects.types.Predicator;
import ru.informationsystems.util.AssertionHandler;
import ru.informationsystems.util.SchemaUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class represents an occurrence frequency constraint.
 * An occurrence frequency constraint is a constraint that is applied to a list of predicators,
 * to indicate how many times their combination is allowed in any population.
 */
public class OccurrenceFrequencyConstraint implements Constraint {

    // The predicators that this constraint covers
    private Set<Predicator> predicators;
    // The minimum amount of predicator combinations
    private int minimum;
    // The maximum amount of predicator combinations
    private int maximum;
    // An Assertion Handler, for validating correctness
    private AssertionHandler ah;

    public OccurrenceFrequencyConstraint(Set<Predicator> predicators, int minimum, int maximum, AssertionHandler assertionHandler) {
        this.predicators = predicators;
        this.minimum = minimum;
        this.maximum = maximum;
        this.ah = assertionHandler;
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
        // We collapse the population into a single table, containing all the information we need.
        List<Map<Predicator, String>> result = SchemaUtils.collapse(population, predicators, ah);

        // We loop through each entry in the table
        for (int i = 0; i < result.size(); i++) {
            Map<Predicator, String> row = result.get(i);
            // We initialize a variable, that indicates how many times a population occurs
            int occurrences = 0;

            // We loop through all entries in the table
            for (Map<Predicator, String> row2 : result) {
                // If the two rows are the same for all predicators, we increment the occurrences count
                if (SchemaUtils.fullRowsMatch(row, row2, predicators)) {
                    occurrences++;
                }
            }

            // If there were not enough occurrences, or too many occurrences, the population is not valid
            if (occurrences < minimum || occurrences > maximum) {
                this.invalidElement = row;
                this.occurrences = occurrences;
                populationIsValid = false;
                return false;
            }
        }
        return true;
    }

    // Variables used for error printing
    private boolean populationIsValid = true;
    private Map<Predicator, String> invalidElement;
    private int occurrences;

    /**
     * Method for printing the result of the validation.
     * If the validation was unsuccessful, it will print error information.
     */
    @Override
    public void printResult() {
        if (populationIsValid) {
            System.out.println("Occurrence frequency constraint was successfully verified.");
        } else {
            System.out.println("Failed to verify occurrence frequency constraint.");
            System.out.println("Reason: An invalid row was found.");
            System.out.println("Row:");
            System.out.println("\t{" + invalidElement.entrySet().stream().map(e -> e.getKey().getName() + " > " + e.getValue()).collect(Collectors.joining(", ")) + "}");
            System.out.println("Amount of occurrences:");
            System.out.println("\t" + occurrences);
            System.out.println("Expected amount of occurrences:");
            System.out.println("\t" + minimum + " <= o <= " + maximum);
        }
    }

    /**
     * Provides the predicators involved in this constraint
     * @return the string with information
     */
    @Override
    public String getInformation() {
        return "Occurrence Frequency Constraint over predicator(s) {" + predicators.stream()
                .map(Predicator::getName)
                .collect(Collectors.joining(", "))
                + "}";
    }
}
