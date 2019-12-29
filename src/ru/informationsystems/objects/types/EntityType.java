package ru.informationsystems.objects.types;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for representing entity types
 */
public class EntityType extends ObjectType {

    // The list of specializations of this entity type
    private List<ObjectType> specializations = new ArrayList<>();
    // The list of generalizations of this entity type
    private List<ObjectType> generalizations = new ArrayList<>();

    public EntityType(String name) {
        super(name);
    }

    /**
     * @return The list of specializations
     */
    public List<ObjectType> getSpecializations() {
        return specializations;
    }

    /**
     * @return The list of generalizations
     */
    public List<ObjectType> getGeneralizations() {
        return generalizations;
    }
}
