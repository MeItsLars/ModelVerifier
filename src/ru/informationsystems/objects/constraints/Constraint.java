package ru.informationsystems.objects.constraints;

import ru.informationsystems.objects.population.Population;

public interface Constraint {

    /**
     * Validates the constraint against the given population.
     * If the population satisfies the constraint, the result will be true.
     * Otherwise, it will be false.
     *
     * @param population The input population we want to validate
     * @return Whether the population satisfies the constraint
     */
    boolean validate(Population population);

    /**
     * Prints constraint result information to the console.
     * If an error was found, this method will give information about the error.
     * If no error was found, it will show this.
     */
    void printResult();

    /**
     * Gives information about the constraint.
     * i.e. the type, relevant predicator info, etc.
     * @return The information, formatted into a string
     */
    String getInformation();
}
