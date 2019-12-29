package ru.informationsystems.objects;

import ru.informationsystems.objects.constraints.Constraint;
import ru.informationsystems.objects.population.Population;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Class for representing a schema
 */
public class Schema {

    // The information structure of this schema
    private InformationStructure informationStructure;
    // The set of constraints that apply to this schema
    private Set<Constraint> constraints;

    public Schema(InformationStructure informationStructure, Set<Constraint> constraints) {
        this.informationStructure = informationStructure;
        this.constraints = constraints;
    }

    /**
     * @return The information structure of this schema
     */
    public InformationStructure getInformationStructure() {
        return informationStructure;
    }

    /**
     * @return The constraints of this schema
     */
    public Set<Constraint> getConstraints() {
        return constraints;
    }

    /**
     * This method validates the information structure with the given population against the set of constraints.
     * The results of this validation are printed to the console.
     * @param population
     */
    public void validate(Population population) {
        // We print the information structure
        informationStructure.print();

        System.out.println("Validating schema constraints...\n");

        int constraintsSize = constraints.size();
        int index = 1;
        int amountOfValidatedConstraints = 0;

        // We create a list of resulting messages
        List<String> resultMessage = new ArrayList<>();
        // We loop through each constraint
        for (Constraint constraint : constraints) {
            // We retrieve the result of the constraint validation
            boolean result = constraint.validate(population);
            // We add a table format to the list of results
            resultMessage.add(String.format("| %-5d | %-80s |   %2s \t  |", index, constraint.getInformation(), result ? "✓" : "✗"));
            // If the constraint was validated, increment this counter
            if (result) amountOfValidatedConstraints++;
            // Print the information of this constraint
            System.out.println(">> Constraint " + index + " <<");
            constraint.printResult();
            System.out.println();
            index++;
        }

        // Print a summary all information in a nicely formatted table
        System.out.println("+-------+----------------------------------------------------------------------------------+----------+");
        System.out.format("| %s | %-80s | %s   |\n", "INDEX", "CONSTRAINT INFORMATION", "RESULT");
        System.out.println("+-------+----------------------------------------------------------------------------------+----------+");
        resultMessage.forEach(System.out::println);
        System.out.println("+-------+----------------------------------------------------------------------------------+----------+");
        System.out.println("All constraints were checked.");
        System.out.println(amountOfValidatedConstraints + " / " + constraintsSize + " constraints were valid.");
    }
}
