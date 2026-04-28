package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metamodel.dataset.CompositeRowDefinition;
import com.doublegsoft.jcommons.metamodel.root.AggregateRootDefinition;

import java.util.ArrayList;
import java.util.List;

public class TypeDefinition {

  private final Object definition;

  private final List<FieldDefinition> fields = new ArrayList<>();

  private final List<MethodDefinition> methods = new ArrayList<>();

  public TypeDefinition(Object definition) {
    if (!(definition instanceof ObjectDefinition) &&
        !(definition instanceof AggregateRootDefinition) &&
        !(definition instanceof CompositeRowDefinition)) {
      throw new IllegalArgumentException("the definition is not accepted, " +
          "just as object definition, aggregate root definition, composite row definition");
    }
    this.definition = definition;
  }

  public List<FieldDefinition> getFieldsWithCustomType() {
    // TODO
    List<FieldDefinition> retVal = new ArrayList<>();
    return retVal;
  }

  public List<FieldDefinition> getFieldsWithCollectionType() {
    // TODO
    List<FieldDefinition> retVal = new ArrayList<>();
    return retVal;
  }

  public List<FieldDefinition> getFieldsWithComponentType(ObjectDefinition componentType) {
    // TODO
    List<FieldDefinition> retVal = new ArrayList<>();
    return retVal;
  }

  public List<FieldDefinition> getFieldsWithPrimitiveType() {
    // TODO
    List<FieldDefinition> retVal = new ArrayList<>();
    return retVal;
  }

  public List<FieldDefinition> getIdentifiableFields() {
    // TODO
    List<FieldDefinition> retVal = new ArrayList<>();
    return retVal;
  }

  public FieldDefinition getIdentifiableField() {
    // TODO
    return null;
  }

  @SuppressWarnings("unchecked")
  public <T> T getDefinition() {
    return (T) definition;
  }

}
