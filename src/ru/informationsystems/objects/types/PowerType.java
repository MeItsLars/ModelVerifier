package ru.informationsystems.objects.types;

/**
 * Class for representing power types
 */
public class PowerType extends ObjectType {

    // The entity type this power type spans
    private EntityType entityType;
    // The implicit fact type of this power type
    private FactType implicitFactType;

    public PowerType(String name, EntityType entityType, FactType implicitFactType) {
        super(name);
        this.entityType = entityType;
        this.implicitFactType = implicitFactType;
    }

    /**
     * @return The entity type
     */
    public EntityType getElement() {
        return entityType;
    }

    /**
     * @return The implicit fact type
     */
    public FactType getImplicitFactType() {
        return implicitFactType;
    }
}
