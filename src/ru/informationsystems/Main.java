package ru.informationsystems;

import ru.informationsystems.objects.InformationStructure;
import ru.informationsystems.objects.Schema;
import ru.informationsystems.objects.constraints.Constraint;
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
        InformationStructure is = new InformationStructureBuilder()
                .addEntityTypes("A", "C", "D", "E")
                .addLabelType("B")
                .addFactTypes("f", "g")
                .addPowerType("F", "E")
                .addPredicator("1", "f", "A")
                .addPredicator("2", "f", "B")
                .addPredicator("3", "g", "D")
                .addPredicator("4", "g", "E")
                .addSpecialization("A", "D")
                .addSpecialization("C", "D")
                .build();

        Set<Constraint> constraints = new ConstraintBuilder(is)
                .addUniquenessConstraint("1", "2")
                .addUniquenessConstraint("3")
                .addUniquenessConstraint("4")
                .addTotalRoleConstraint("1")
                .addEnumerationConstraint("B", "b1", "b2")
                .addSpecTotalSubtypeConstraint("A", "C")
                .addPTCoverConstraint("F")
                .addPTExclusionConstraint("F")
                .build();

        Schema schema = new Schema(is, constraints);

        Population population1 = new PopulationBuilder(schema)
                .populateEntity("A", "d1", "d2")
                .populateLabelType("B", "b1", "b2")
                .populateEntity("C", "d1")
                .populateEntity("D", "d1", "d2")
                .populateEntity("E", "e1", "e2")
                .populatePowerType("F", Arrays.asList(
                        new HashSet<>(Arrays.asList("e1")),
                        new HashSet<>(Arrays.asList("e2"))
                ))
                .populateFactType("f", Arrays.asList(
                        new HashMap<String, String>(){{
                            put("1", "d1");
                            put("2", "b1");
                        }},
                        new HashMap<String, String>(){{
                            put("1", "d2");
                            put("2", "b2");
                        }}
                ))
                .populateFactType("g", Arrays.asList(
                        new HashMap<String, String>(){{
                            put("3", "d1");
                            put("4", "e1");
                        }},
                        new HashMap<String, String>(){{
                            put("3", "d2");
                            put("4", "e2");
                        }}
                ))
                .build();

        Population population2 = new PopulationBuilder(schema)
                .populateEntity("A", "d1", "d2")
                .populateLabelType("B", "b1", "b2", "b3")
                .populateEntity("C", "d1")
                .populateEntity("D", "d1", "d2", "d3")
                .populateEntity("E", "e1", "e2", "e3")
                .populatePowerType("F", Arrays.asList(
                        new HashSet<>(Arrays.asList("e1", "e2")),
                        new HashSet<>(Arrays.asList("e1", "e2"))
                ))
                .populateFactType("f", Arrays.asList(
                        new HashMap<String, String>(){{
                            put("1", "d1");
                            put("2", "b2");
                        }},
                        new HashMap<String, String>(){{
                            put("1", "d1");
                            put("2", "b2");
                        }}
                ))
                .populateFactType("g", Arrays.asList(
                        new HashMap<String, String>(){{
                            put("3", "d1");
                            put("4", "e1");
                        }},
                        new HashMap<String, String>(){{
                            put("3", "d1");
                            put("4", "e1");
                        }}
                ))
                .build();

        schema.validate(population1);
        schema.validate(population2);
    }
}