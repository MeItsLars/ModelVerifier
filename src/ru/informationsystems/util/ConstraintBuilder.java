package ru.informationsystems.util;

import ru.informationsystems.objects.InformationStructure;
import ru.informationsystems.objects.constraints.*;
import ru.informationsystems.objects.types.*;

import java.util.*;

/**
 * Builder class that makes it easy to create a list of constraints
 */
public class ConstraintBuilder {

    // The information structure the constraints apply to
    private InformationStructure informationStructure;
    // An assertion handler for verifying correctness
    private AssertionHandler ah;
    // The set of constraints
    private Set<Constraint> constraints = new HashSet<>();

    public ConstraintBuilder(InformationStructure informationStructure) {
        this.informationStructure = informationStructure;
        this.ah = new AssertionHandler(informationStructure);
    }

    /**
     * Adds a uniqueness constraint
     * @param predicators The predicators this constraint spans
     * @return The builder
     */
    public ConstraintBuilder addUniquenessConstraint(String... predicators) {
        Set<Predicator> preds = new HashSet<>();

        for (String predicator : predicators) {
            ah.assertPredicatorExists(predicator);
            Predicator pred = informationStructure.getPredicator(predicator);
            preds.add(pred);
        }

        UniquenessConstraint uniquenessConstraint = new UniquenessConstraint(preds, ah);
        constraints.add(uniquenessConstraint);

        return this;
    }

    /**
     * Adds an occurrence frequency constraint
     * @param minimum The minimum amount of times an element should occur
     * @param maximum The maximum amount of times an element should occur
     * @param predicators The predicators this constraint spans
     * @return The builder
     */
    public ConstraintBuilder addOccurrenceFrequencyConstraint(int minimum, int maximum, String... predicators) {
        Set<Predicator> preds = new HashSet<>();

        for (String predicator : predicators) {
            ah.assertPredicatorExists(predicator);
            Predicator pred = informationStructure.getPredicator(predicator);
            preds.add(pred);
        }

        OccurrenceFrequencyConstraint occurrenceFrequencyConstraint = new OccurrenceFrequencyConstraint(preds, minimum, maximum, ah);
        constraints.add(occurrenceFrequencyConstraint);

        return this;
    }

    /**
     * Adds a total role constraint
     * @param predicators The predicators this constraint spans
     * @return The builder
     */
    public ConstraintBuilder addTotalRoleConstraint(String... predicators) {
        Set<Predicator> preds = new HashSet<>();

        for (String predicator : predicators) {
            ah.assertPredicatorExists(predicator);
            Predicator pred = informationStructure.getPredicator(predicator);
            preds.add(pred);
        }

        TotalRoleConstraint totalRoleConstraint = new TotalRoleConstraint(preds);
        constraints.add(totalRoleConstraint);

        return this;
    }

    // An enum for representing the type of set constraint
    public enum SetConstr {SUBSET, EQUAL, EXCLUSION}

    /**
     * Adds a set constraint
     * @param setConstr The type of set constraint
     * @param predicatorMatchings The set of predicator matchings
     * @return The builder
     */
    public ConstraintBuilder addSetConstraint(SetConstr setConstr, Map<String, String> predicatorMatchings) {
        Map<Predicator, Predicator> predMatches = new HashMap<>();

        for (Map.Entry<String, String> entry : predicatorMatchings.entrySet()) {
            ah.assertPredicatorExists(entry.getKey());
            ah.assertPredicatorExists(entry.getValue());
            Predicator pred1 = informationStructure.getPredicator(entry.getKey());
            Predicator pred2 = informationStructure.getPredicator(entry.getValue());
            predMatches.put(pred1, pred2);
        }

        SetConstraint setConstraint = new SetConstraint(setConstr, predMatches, ah);
        constraints.add(setConstraint);

        return this;
    }

    /**
     * Adds an enumeration constraint
     * @param labelType The label type of this constraint
     * @param allowedValues The allowed values
     * @return The builder
     */
    public ConstraintBuilder addEnumerationConstraint(String labelType, String... allowedValues) {
        ah.assertExists(labelType);
        ah.assertInstanceOf(labelType, LabelType.class);

        EnumerationConstraint enumerationConstraint = new EnumerationConstraint((LabelType) informationStructure.getObjectType(labelType),
                new HashSet<>(Arrays.asList(allowedValues)));
        constraints.add(enumerationConstraint);
        return this;
    }

    /**
     * Adds a power type exclusion constraint
     * @param powerType The power type this constraint spans
     * @return The builder
     */
    public ConstraintBuilder addPTExclusionConstraint(String powerType) {
        ah.assertExists(powerType);
        ah.assertInstanceOf(powerType, PowerType.class);

        PTExclusionConstraint exclusionConstraint = new PTExclusionConstraint((PowerType) informationStructure.getObjectType(powerType));
        constraints.add(exclusionConstraint);
        return this;
    }

    /**
     * Adds a power type cover constraint
     * @param powerType The power type this constraint spans
     * @return The builder
     */
    public ConstraintBuilder addPTCoverConstraint(String powerType) {
        ah.assertExists(powerType);
        ah.assertInstanceOf(powerType, PowerType.class);

        PTCoverConstraint coverConstraint = new PTCoverConstraint((PowerType) informationStructure.getObjectType(powerType));
        constraints.add(coverConstraint);
        return this;
    }

    /**
     * Adds a power type set cardinality constraint
     * @param powerType The power type this constraint spans
     * @param minimum The minimum number of elements in a set
     * @param maximum The maximum number of elements in a set
     * @return The builder
     */
    public ConstraintBuilder addPTSetCardinalityConstraint(String powerType, int minimum, int maximum) {
        ah.assertExists(powerType);
        ah.assertInstanceOf(powerType, PowerType.class);

        PTSetCardinalityConstraint setCardinalityConstraint = new PTSetCardinalityConstraint((PowerType) informationStructure.getObjectType(powerType), minimum, maximum);
        constraints.add(setCardinalityConstraint);
        return this;
    }

    /**
     * Adds a power type membership constraint
     * @param powerType The power type this constraint spans
     * @param factType The fact type this constraint spans
     * @return The builder
     */
    public ConstraintBuilder addPTMembershipConstraint(String powerType, String factType) {
        ah.assertExists(powerType);
        ah.assertInstanceOf(powerType, PowerType.class);
        ah.assertExists(factType);
        ah.assertInstanceOf(factType, FactType.class);

        PTMembershipConstraint membershipConstraint = new PTMembershipConstraint(
                (PowerType) informationStructure.getObjectType(powerType),
                (FactType) informationStructure.getObjectType(factType));
        constraints.add(membershipConstraint);
        return this;
    }

    /**
     * Adds a specialization total subtype constraint
     * @param entityTypes The set of entity type this constraint spans
     * @return The builder
     */
    public ConstraintBuilder addSpecTotalSubtypeConstraint(String... entityTypes) {
        Set<EntityType> result = new HashSet<>();

        for (String entityType : entityTypes) {
            ah.assertExists(entityType);
            ah.assertInstanceOf(entityType, EntityType.class);
            result.add((EntityType) informationStructure.getObjectType(entityType));
        }

        SpecTotalSubtypeConstraint totalSubtypeConstraint = new SpecTotalSubtypeConstraint(result, informationStructure);
        constraints.add(totalSubtypeConstraint);
        return this;
    }

    /**
     * Adds a specialization exclusion constraint
     * @param entityTypes The set of entity type this constraint spans
     * @return The builder
     */
    public ConstraintBuilder addSpecExclusionConstraint(String... entityTypes) {
        Set<EntityType> result = new HashSet<>();

        for (String entityType : entityTypes) {
            ah.assertExists(entityType);
            ah.assertInstanceOf(entityType, EntityType.class);
            result.add((EntityType) informationStructure.getObjectType(entityType));
        }

        SpecExclusionConstraint specExclusionConstraint = new SpecExclusionConstraint(result);
        constraints.add(specExclusionConstraint);
        return this;
    }

    /**
     * Builds the set of constraints
     * @return The set of constraints
     */
    public Set<Constraint> build() {
        return constraints;
    }
}