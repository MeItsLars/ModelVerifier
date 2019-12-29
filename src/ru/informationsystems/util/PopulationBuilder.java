package ru.informationsystems.util;

import ru.informationsystems.objects.InformationStructure;
import ru.informationsystems.objects.Schema;
import ru.informationsystems.objects.population.Population;
import ru.informationsystems.objects.types.*;

import java.util.*;

/**
 * Builder class that makes it easy to create a population
 */
public class PopulationBuilder {

    // The population that we are creating
    private Population population = new Population();
    // The information structure this population belongs to
    private InformationStructure informationStructure;
    // An assertion handler for verifying correctness
    private AssertionHandler ah;

    public PopulationBuilder(Schema schema) {
        this.informationStructure = schema.getInformationStructure();
        this.ah = new AssertionHandler(informationStructure);
    }

    /**
     * Populates an entity type
     * @param entityType Entity type we want to populate
     * @param values The values of the elements in this entity type
     * @return The builder
     */
    public PopulationBuilder populateEntity(String entityType, String... values) {
        ah.assertExists(entityType);
        ah.assertInstanceOf(entityType, EntityType.class);

        population.addEntityTypePopulation((EntityType) informationStructure.getObjectType(entityType), Arrays.asList(values));

        return this;
    }

    /**
     * Populates a fact type
     * @param factType Fact type we want to populate
     * @param values The values that are in this fact type
     * @return The builder
     */
    public PopulationBuilder populateFactType(String factType, List<Map<String, String>> values) {
        ah.assertExists(factType);
        ah.assertInstanceOf(factType, FactType.class);

        List<Map<Predicator, String>> result = new ArrayList<>();

        for (Map<String, String> value : values) {
            Map<Predicator, String> row = new HashMap<>();

            for (Map.Entry<String, String> entry : value.entrySet()) {
                ah.assertPredicatorExists(entry.getKey());

                row.put(informationStructure.getPredicator(entry.getKey()), entry.getValue());
            }

            result.add(row);
        }

        population.addFactTypePopulation((FactType) informationStructure.getObjectType(factType), result);
        return this;
    }

    /**
     * Populates a label type
     * @param labelType The label type we want to populate
     * @param values The values of the elements of this label type
     * @return The builder
     */
    public PopulationBuilder populateLabelType(String labelType, String... values) {
        ah.assertExists(labelType);
        ah.assertInstanceOf(labelType, LabelType.class);

        population.addLabelTypePopulation((LabelType) informationStructure.getObjectType(labelType), Arrays.asList(values));
        return this;
    }

    /**
     * Populates a power type
     * @param powerType The power type we want to populate
     * @param values The values of the elements of this power type
     * @return The builder
     */
    public PopulationBuilder populatePowerType(String powerType, List<Set<String>> values) {
        //TODO: New implementation; test!
        ah.assertExists(powerType);
        ah.assertInstanceOf(powerType, PowerType.class);

        population.addPowerTypePopulation((PowerType) informationStructure.getObjectType(powerType), values);
        return this;
    }

    /**
     * Builds the population
     * @return The population
     */
    public Population build() {
        return population;
    }
}
