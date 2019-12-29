package ru.informationsystems.objects.constraints;

import ru.informationsystems.objects.population.Population;
import ru.informationsystems.objects.types.Predicator;
import ru.informationsystems.util.AssertionHandler;
import ru.informationsystems.util.ConstraintBuilder;
import ru.informationsystems.util.SchemaUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class represents a set constraint. A set constraint determines if a list of predicators
 * should be equal, be a subset of, or be an exclusion of another list of predicators.
 */
public class SetConstraint implements Constraint {

    // The type of set constraint that is used (Equal, Subset, Exclusion)
    private ConstraintBuilder.SetConstr constraintType;
    // The map with predicators, each predicator in the first list maps to another predicator in the second list
    private Map<Predicator, Predicator> predicatorMap;
    // An Assertion Handler, for validating correctness
    private AssertionHandler ah;

    public SetConstraint(ConstraintBuilder.SetConstr constraintType, Map<Predicator, Predicator> predicatorMap, AssertionHandler assertionHandler) {
        this.constraintType = constraintType;
        this.predicatorMap = predicatorMap;
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
        // We retrieve the population of the first set of predicators
        List<Map<Predicator, String>> population1 = SchemaUtils.collapse(population, predicatorMap.keySet(), ah);
        // We retrieve the population of the second set of predicators
        List<Map<Predicator, String>> population2 = SchemaUtils.collapse(population, predicatorMap.values(), ah);

        // We make a case distinction on the different constraint types
        switch (constraintType) {
            default:
            case EQUAL:
                // If they are equal, we check that every population in population1 occurs in population2...
                reason = "An element was found that did not occur in the other population.";
                for (Map<Predicator, String> pop1 : population1) {
                    if (!existsAnEqual(pop1, population2, true)) {
                        invalidElement = pop1;
                        populationIsValid = false;
                        return false;
                    }
                }
                // ...and the other way around
                for (Map<Predicator, String> pop2 : population2) {
                    if (!existsAnEqual(pop2, population1, false)) {
                        invalidElement = pop2;
                        populationIsValid = false;
                        return false;
                    }
                }
                return true;
            case SUBSET:
                // If they are a subset, we check that every population in population1 occurs in population2
                reason = "An element was found that did not occur in the other population.";
                for (Map<Predicator, String> pop1 : population1) {
                    if (!existsAnEqual(pop1, population2, true)) {
                        invalidElement = pop1;
                        populationIsValid = false;
                        return false;
                    }
                }
                return true;
            case EXCLUSION:
                // If they are an exclusion, we check that every population in population1 does not occur in population2...
                reason = "An element was found that occurs in the other population.";
                for (Map<Predicator, String> pop1 : population1) {
                    if (existsAnEqual(pop1, population2, true)) {
                        invalidElement = pop1;
                        populationIsValid = false;
                        return false;
                    }
                }
                // ...and the other way around
                for (Map<Predicator, String> pop2 : population2) {
                    if (existsAnEqual(pop2, population1, false)) {
                        invalidElement = pop2;
                        populationIsValid = false;
                        return false;
                    }
                }
                return true;
        }
    }

    /**
     * This method determines if there is an instance of pop1 in population2.
     * It uses the predicatorMap to determine this. The 'right' boolean determines the direction that the
     * predicatorMap is checked (left to right, or right to left).
     *
     * @param pop1 The row we want to check
     * @param population2 The population that the row could be in
     * @param right Whether we check left to right or right to left
     * @return whether pop1 occurred in population2
     */
    private boolean existsAnEqual(Map<Predicator, String> pop1, List<Map<Predicator, String>> population2, boolean right) {
        // We loop through all subpopulations
        for (Map<Predicator, String> pop2 : population2) {
            // We check if they match using this static util method
            if (SchemaUtils.fullRowsMatch(pop1, pop2, predicatorMap, right)) {
                return true;
            }
        }
        return false;
    }

    // Variables used for error printing
    private boolean populationIsValid = true;
    private String reason;
    private Map<Predicator, String> invalidElement;

    /**
     * Method for printing the result of the validation.
     * If the validation was unsuccessful, it will print error information.
     */
    @Override
    public void printResult() {
        if (populationIsValid) {
            System.out.println("Set constraint was successfully verified.");
        } else {
            System.out.println("Failed to verify set constraint.");
            System.out.println("Reason: " + reason);
            System.out.println("Element:");
            System.out.println("\t{" + invalidElement.entrySet().stream().map(e -> e.getKey().getName() + " > " + e.getValue()).collect(Collectors.joining(", ")) + "}");
        }
    }

    /**
     * Provides the set constraint type and the matchings it has
     * @return the information, formatted into a string
     */
    @Override
    public String getInformation() {
        return "SetConstraint (" + constraintType.name() + ") with matchings {" + predicatorMap.entrySet().stream()
                .map(e -> e.getKey().getName() + " > " + e.getValue().getName())
                .collect(Collectors.joining(", ")) + "}";
    }
}
