package ru.informationsystems.objects.population;

import ru.informationsystems.objects.types.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class for representing a population of an information structure
 */
public class Population {

    // The populations of entity types
    private Map<EntityType, List<String>> entityTypePopulations = new HashMap<>();
    // The populations of label types
    private Map<LabelType, List<String>> labelTypePopulations = new HashMap<>();
    // The populations of fact types
    private Map<FactType, List<Map<Predicator, String>>> factTypePopulations = new HashMap<>();
    // The populations of power types
    private Map<PowerType, List<Set<String>>> powerTypePopulations = new HashMap<>();

    /**
     * Retrieves the population of an entity type
     * @param entityType The entity type we want to retrieve the population of
     * @return the population that belongs to this entity type
     */
    public List<String> getEntityTypePopulation(EntityType entityType) {
        return entityTypePopulations.get(entityType);
    }

    /**
     * Retrieves the population of a label type
     * @param labelType The label type we want to retrieve the population of
     * @return the population that belongs to this label type
     */
    public List<String> getLabelTypePopulation(LabelType labelType) {
        return labelTypePopulations.get(labelType);
    }

    /**
     * Retrieves the population of a fact type
     * @param factType The fact type we want to retrieve the population of
     * @return the population that belongs to this fact type
     */
    public List<Map<Predicator, String>> getFactTypePopulations(FactType factType) {
        return factTypePopulations.get(factType);
    }

    /**
     * Retrieves the population of a power type
     * @param powerType The power type we want to retrieve the population of
     * @return the population that belongs to this power type
     */
    public List<Set<String>> getPowerTypePopulation(PowerType powerType) {
        return powerTypePopulations.get(powerType);
    }

    /**
     * Adds a population to the list of entity type populations
     * @param entityType The entity type that the population belongs to
     * @param population The population of this entity type
     */
    public void addEntityTypePopulation(EntityType entityType, List<String> population) {
        entityTypePopulations.put(entityType, population);
    }

    /**
     * Adds a population to the list of label type populations
     * @param labelType The label type that the population belongs to
     * @param population The population of this label type
     */
    public void addLabelTypePopulation(LabelType labelType, List<String> population) {
        labelTypePopulations.put(labelType, population);
    }

    /**
     * Adds a population to the list of fact type populations
     * @param factType The fact type that the population belongs to
     * @param population The population of this fact type
     */
    public void addFactTypePopulation(FactType factType, List<Map<Predicator, String>> population) {
        factTypePopulations.put(factType, population);
    }

    /**
     * Adds a population to the list of power type populations
     * @param powerType The power type that the population belongs to
     * @param population The population of this power type
     */
    public void addPowerTypePopulation(PowerType powerType, List<Set<String>> population) {
        powerTypePopulations.put(powerType, population);
    }
}
