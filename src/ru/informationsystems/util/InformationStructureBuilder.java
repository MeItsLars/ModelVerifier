package ru.informationsystems.util;

import ru.informationsystems.objects.InformationStructure;
import ru.informationsystems.objects.types.*;

/**
 * Builder class that makes it easy to create an information structure
 */
public class InformationStructureBuilder {

    // The current information structure
    private InformationStructure informationStructure = new InformationStructure();
    // An assertion handler for verifying correctness
    private AssertionHandler ah;

    public InformationStructureBuilder() {
        this.ah = new AssertionHandler(informationStructure);
    }

    /**
     * Adds an entity type to the information structure
     * @param name The name of the entity type
     * @return The builder
     */
    public InformationStructureBuilder addEntityType(String name) {
        ah.assertNoDuplicates(name);
        informationStructure.addObjectType(new EntityType(name));
        return this;
    }

    /**
     * Adds a list of entity types to the information structure
     * @param names The names of the entity types
     * @return The builder
     */
    public InformationStructureBuilder addEntityTypes(String... names) {
        if (names == null || names.length == 0) return this;
        for (String name : names) addEntityType(name);
        return this;
    }

    /**
     * Adds a label type to the information structure
     * @param name The name of the label type
     * @return The builder
     */
    public InformationStructureBuilder addLabelType(String name) {
        informationStructure.addObjectType(new LabelType(name));
        return this;
    }

    /**
     * Adds a list of label types to the information structure
     * @param names The names of the label types
     * @return The builder
     */
    public InformationStructureBuilder addLabelTypes(String... names) {
        if (names == null || names.length == 0) return this;
        for (String name : names) addLabelType(name);
        return this;
    }

    /**
     * Adds a fact type to the information structure
     * @param name The name of the fact type
     * @return The builder
     */
    public InformationStructureBuilder addFactType(String name) {
        ah.assertNoDuplicates(name);
        informationStructure.addObjectType(new FactType(name));
        return this;
    }

    /**
     * Adds a list of fact types to the information structure
     * @param names The names of the fact types
     * @return The builder
     */
    public InformationStructureBuilder addFactTypes(String... names) {
        if (names == null || names.length == 0) return this;
        for (String name : names) addFactType(name);
        return this;
    }

    /**
     * Adds a predicator to the information structure
     * @param name The name of the predicator
     * @param factTypeName The associated fact type
     * @param objectTypeName The associated base object type
     * @return The builder
     */
    public InformationStructureBuilder addPredicator(String name, String factTypeName, String objectTypeName) {
        ah.assertInstanceOf(factTypeName, FactType.class);
        ah.assertInstanceOf(objectTypeName, ObjectType.class);

        FactType factType = (FactType) informationStructure.getObjectType(factTypeName);
        ObjectType objectType = informationStructure.getObjectType(objectTypeName);

        ah.assertNoDuplicatePredicator(name);

        factType.addPredicator(new Predicator(name, objectType, factType));
        return this;
    }

    /**
     * Adds a power type to the information structure
     * @param name The name of the power type
     * @param objectTypeName The object type this power type spans
     * @return The builder
     */
    public InformationStructureBuilder addPowerType(String name, String objectTypeName) {
        ah.assertNoDuplicates(name);
        // Implicit fact type:
        addFactType("e_{" + name + "}");

        informationStructure.addObjectType(new PowerType(name, (EntityType) informationStructure.getObjectType(objectTypeName),
                (FactType) informationStructure.getObjectType("e_{" + name + "}")));

        // Implicit fact type predicators:
        addPredicator("e^P_{" + name + "}", "e_{" + name + "}", name);
        addPredicator("e^E_{" + name + "}", "e_{" + name + "}", objectTypeName);

        return this;
    }

    /**
     * Adds a sequence type to the information structure
     * @param name The name of the sequence type
     * @param objectTypeName The object type this sequence type spans
     * @return The builder
     */
    public InformationStructureBuilder addSequenceType(String name, String objectTypeName) {
        ah.assertNoDuplicates(name);
        informationStructure.addObjectType(new SequenceType(name, informationStructure.getObjectType(objectTypeName)));
        return this;
    }

    /**
     * Adds a specialization to the information structure
     * @param fromObjectType The object type that 'the arrow starts at'
     * @param toEntityType The Entity type that 'the arrow ends at'
     * @return The builder
     */
    public InformationStructureBuilder addSpecialization(String fromObjectType, String toEntityType) {
        ah.assertExists(fromObjectType);
        ah.assertExists(toEntityType);

        ObjectType objectType = informationStructure.getObjectType(fromObjectType);
        EntityType entityType = (EntityType) informationStructure.getObjectType(toEntityType);

        ah.assertNoDuplicateSpecialization(entityType, objectType);

        entityType.getSpecializations().add(objectType);
        return this;
    }

    /**
     * Adds a generalization to the information structure
     * @param fromObjectType The object type that 'the arrow starts at'
     * @param toEntityType The Entity type that 'the arrow ends at'
     * @return The builder
     */
    public InformationStructureBuilder addGeneralization(String fromObjectType, String toEntityType) {
        ah.assertExists(fromObjectType);
        ah.assertExists(toEntityType);

        ObjectType objectType = informationStructure.getObjectType(fromObjectType);
        EntityType entityType = (EntityType) informationStructure.getObjectType(toEntityType);

        ah.assertNoDuplicateGeneralization(entityType, objectType);

        entityType.getGeneralizations().add(objectType);
        return this;
    }

    /**
     * Builds this information structure
     * @return the information structure
     */
    public InformationStructure build() {
        return informationStructure;
    }
}
