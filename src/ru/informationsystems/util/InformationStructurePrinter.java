package ru.informationsystems.util;

import ru.informationsystems.objects.InformationStructure;
import ru.informationsystems.objects.types.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for printing information structure
 */
public class InformationStructurePrinter {

    // The mathematical characters for each of these characters
    private static final String P = new String(Character.toChars(0x1D4AB));
    private static final String F = new String(Character.toChars(0x02131));
    private static final String S = new String(Character.toChars(0x1D4AE));
    private static final String E = new String(Character.toChars(0x02130));
    private static final String O = new String(Character.toChars(0x1D4AA));
    private static final String G = new String(Character.toChars(0x1D4A2));
    private static final String C = new String(Character.toChars(0x1D49E));
    private static final String L = new String(Character.toChars(0x1D4A7));

    /**
     * Prints the information structure in a nice format, that is specified in the lecture notes.
     * @param informationStructure The information structure
     */
    public static void print(InformationStructure informationStructure) {
        // The lists of types we want to print
        List<String> predicators = new ArrayList<>();
        List<String> factTypes = new ArrayList<>();
        List<String> sequenceTypes = new ArrayList<>();
        List<String> entityTypes = new ArrayList<>();
        List<String> objTypes = new ArrayList<>();
        List<String> powerTypes = new ArrayList<>();
        List<String> schemaTypes = new ArrayList<>();
        List<String> labelTypes = new ArrayList<>();

        // We sort the object types
        List<ObjectType> sortedObjectTypes = new ArrayList<>(informationStructure.getObjectTypes());
        Collections.sort(sortedObjectTypes);

        // We loop through all object types, and add them to their list
        for (ObjectType objectType : sortedObjectTypes) {
            objTypes.add(objectType.getName());

            if (objectType instanceof FactType) {
                FactType factType = (FactType) objectType;
                factTypes.add(factType.getName());
                predicators.addAll(factType.getPredicators().stream().map(Predicator::getName).collect(Collectors.toSet()));
            } else if (objectType instanceof SequenceType) {
                sequenceTypes.add(objectType.getName());
            } else if (objectType instanceof EntityType) {
                entityTypes.add(objectType.getName());
            } else if (objectType instanceof PowerType) {
                powerTypes.add(objectType.getName());
            } else if (objectType instanceof LabelType) {
                labelTypes.add(objectType.getName());
            }
        }

        // We sort the predicators
        Collections.sort(predicators);

        // We print all values
        System.out.println("This information structure is defined by:");
        print(P, predicators);
        print(F, factTypes);
        print(S, sequenceTypes);
        print(E, entityTypes);
        print(O, objTypes);
        print(G, powerTypes);
        print(C, schemaTypes);
        print(L, labelTypes);
    }

    /**
     * Simpel method for printing a type
     * @param prefix The prefix character
     * @param list The list of elements
     */
    private static void print(String prefix, List<String> list) {
        System.out.println(prefix + " = {" + String.join(", ", list) + "}");
    }
}
