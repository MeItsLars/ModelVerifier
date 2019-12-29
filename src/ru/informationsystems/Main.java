package ru.informationsystems;

import ru.informationsystems.objects.InformationStructure;
import ru.informationsystems.objects.Schema;
import ru.informationsystems.objects.constraints.Constraint;
import ru.informationsystems.objects.population.Pair;
import ru.informationsystems.objects.population.Population;
import ru.informationsystems.util.ConstraintBuilder;
import ru.informationsystems.util.InformationStructureBuilder;
import ru.informationsystems.util.PopulationBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        InformationStructure informationStructure = new InformationStructureBuilder()
                .addEntityTypes("A", "B", "C", "D", "E")
                .addSpecialization("B", "A")
                .addSpecialization("C", "A")
                .addSpecialization("D", "C")
                .addSpecialization("E", "C")
                .build();

        Set<Constraint> constraints = new ConstraintBuilder(informationStructure)
                //.addUniquenessConstraint("p", "s")
                //.addUniquenessConstraint("q", "r")
                //.addOccurrenceFrequencyConstraint(1, 1, "p", "s")
                //.addOccurrenceFrequencyConstraint(1, 1, "q", "r")
                //.addTotalRoleConstraint("q", "r")
                /*.addSetConstraint(ConstraintBuilder.SetConstr.SUBSET, new HashMap<String, String>(){{
                    put("u", "w");
                    put("q", "s");
                }})*/
                //.addLabelTypeConstraint("AL", "a1", "a2", "a3")
                //.addLabelTypeConstraint("BL", "b1", "b2", "b3")
                //.addPTExclusionConstraint("Convoy")
                //.addPTCoverConstraint("Convoy")
                //.addPTMembershipConstraint("Convoy", "flagship")
                //.addSpecTotalSubtypeConstraint("B", "C")
                //.addSpecTotalSubtypeConstraint("B", "D")
                .addSpecExclusionConstraint("B", "C")
                .addSpecExclusionConstraint("B", "D")
                .build();

        Schema schema = new Schema(informationStructure, constraints);

        Population population = new PopulationBuilder(schema)
                .populateEntity("A", "a1", "a2", "a3")
                .populateEntity("B", "a1", "a2")
                .populateEntity("C", "a3")
                .populateEntity("D", "a3")
                .build();

        schema.validate(population);
    }
}