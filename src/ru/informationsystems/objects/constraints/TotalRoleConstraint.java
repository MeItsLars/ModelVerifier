package ru.informationsystems.objects.constraints;

import ru.informationsystems.objects.population.Population;
import ru.informationsystems.objects.types.EntityType;
import ru.informationsystems.objects.types.ObjectType;
import ru.informationsystems.objects.types.PowerType;
import ru.informationsystems.objects.types.Predicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class represents a total role constraint.
 * A total role constraint is used to indicate that each element in the population of a set of predicators should occur
 * in the union of their base types.
 */
public class TotalRoleConstraint implements Constraint {

    // The set of predicators
    private Set<Predicator> predicators;

    public TotalRoleConstraint(Set<Predicator> predicators) {
        this.predicators = predicators;
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
        //TODO: Test this stuff

        // We retrieve the total set of elements of the base types populations of each of the predicators
        Set<String> totalLeftUnion = predicators.stream()
                .map(pred -> {
                    ObjectType base = pred.getBase();
                    if (base instanceof EntityType) return population.getEntityTypePopulation((EntityType) base);
                    else if (base instanceof PowerType) {
                        return population.getPowerTypePopulation((PowerType) base).stream()
                                .map(set -> "{" + String.join(", ", set) + "}")
                                .collect(Collectors.toList());
                    } else return new ArrayList<String>();
                })
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        // We retrieve the total set of elements of the populations of each of the predicators
        Set<String> totalRightUnion = predicators.stream()
                .map(pred -> {
                    List<Map<Predicator, String>> pop = population.getFactTypePopulations(pred.getFactType());
                    return pop.stream()
                            .map(map -> map.get(pred))
                            .collect(Collectors.toSet());
                })
                .flatMap(Set::stream)
                .collect(Collectors.toSet());


        if (totalLeftUnion.equals(totalRightUnion)) {
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
            System.out.println("Total role constraint was successfully verified.");
        } else {
            System.out.println("Failed to verify total role constraint.");
            System.out.println("Reason: An element was found that occurs in the set of base type populations, but not in the set of predicator populations.");
        }
    }

    /**
     * Provides the predicators that this constraint spans
     * @return the information, formatted into a string
     */
    @Override
    public String getInformation() {
        return "Total Role Constraint over predicator(s) {" + predicators.stream()
                .map(Predicator::getName)
                .collect(Collectors.joining(", "))
                + "}";
    }
}
