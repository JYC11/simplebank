package org.example;

import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.meta.Definition;
import org.jooq.meta.TableDefinition;

public class CustomGeneratorStrategy extends DefaultGeneratorStrategy {

    @Override
    public String getJavaClassName(Definition definition, Mode mode) {
        String className = super.getJavaClassName(definition, mode);

        // Add J prefix to all classes
        className = "J" + className;

        // Add specific suffixes based on the mode
        if (mode == Mode.POJO) {
            if (!className.endsWith("Pojo")) {
                className += "Pojo";
            }
        } else if (mode == Mode.DEFAULT && definition instanceof TableDefinition) {
            // This is for table classes
            if (!className.endsWith("Table")) {
                className += "Table";
            }
        }

        return className;
    }
}