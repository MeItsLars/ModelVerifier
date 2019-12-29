package ru.informationsystems.objects.constraints;

import ru.informationsystems.objects.population.Pair;
import ru.informationsystems.objects.population.Population;
import ru.informationsystems.objects.types.FactType;
import ru.informationsystems.objects.types.Predicator;
import ru.informationsystems.util.AssertionHandler;
import ru.informationsystems.util.SchemaUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents a uniqueness constraint.
 * A uniqueness constraint is used to indicate that a set of values, derived from a set of predicators,
 * can only occur once in a population.
 */
public class UniquenessConstraint implements Constraint {

    // The set of predicators
    private Set<Predicator> predicators;
    // An assertion handler, used for verifying correctness
    private AssertionHandler ah;

    public UniquenessConstraint(Set<Predicator> predicators, AssertionHandler assertionHandler) {
        this.predicators = predicators;
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
        // We retrieve the population from the collapse method
        List<Map<Predicator, String>> result = SchemaUtils.collapse(population, predicators, ah);

        // We loop through each entry in the table
        for (int i = 0; i < result.size(); i++) {
            Map<Predicator, String> row = result.get(i);

            // For each entry, we loop through all the entries that are following
            for (int j = i + 1; j < result.size(); j++) {
                Map<Predicator, String> row2 = result.get(j);

                duplicatePopulation1 = row;
                duplicatePopulation2 = row2;

                if (SchemaUtils.fullRowsMatch(row, row2, predicators)) return false;
            }
        }
        populationIsValid = true;
        return true;
    }

    // Variables used for error printing
    private boolean populationIsValid = false;
    private Map<Predicator, String> duplicatePopulation1;
    private Map<Predicator, String> duplicatePopulation2;

    /**
     * Method for printing the result of the validation.
     * If the validation was unsuccessful, it will print error information.
     */
    @Override
    public void printResult() {
        if (populationIsValid) {
            System.out.println("Uniqueness constraint was successfully verified.");
        } else {
            System.out.println("Failed to verify uniqueness constraint.");
            System.out.println("Reason: A duplicate population was found.");
            System.out.println("Population 1:");
            duplicatePopulation1.forEach((p, s) -> System.out.println("\t" + p.getName() + ": " + s));
            System.out.println("Population 2:");
            duplicatePopulation2.forEach((p, s) -> System.out.println("\t" + p.getName() + ": " + s));
        }
    }

    /**
     * Provides the predicators involved in this constraint
     * @return the string with information
     */
    @Override
    public String getInformation() {
        return "Uniqueness Constraint over predicator(s) {" + predicators.stream()
                .map(Predicator::getName)
                .collect(Collectors.joining(", "))
                + "}";
    }
}
