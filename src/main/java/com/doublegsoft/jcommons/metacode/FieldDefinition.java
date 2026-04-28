package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;

public class FieldDefinition {

  private final Object definition;

  public FieldDefinition(Object definition) {
    if (!(definition instanceof AttributeDefinition)) {
      throw new IllegalArgumentException("the definition is not accepted, just as attribute definition");
    }
    this.definition = definition;
  }

  public boolean isIdentifiable() {
    if (definition instanceof AttributeDefinition) {
      AttributeDefinition attr = (AttributeDefinition) definition;
      return attr.isIdentifiable();
    }
    return false;
  }

  public boolean isCustomType() {
    if (definition instanceof AttributeDefinition) {
      AttributeDefinition attr = (AttributeDefinition) definition;
      return attr.getType().isCustom();
    }
    return false;
  }

  public boolean isPrimitiveType() {
    if (definition instanceof AttributeDefinition) {
      AttributeDefinition attr = (AttributeDefinition) definition;
      return attr.getType().isPrimitive();
    }
    return false;
  }

  public boolean isCollectionType() {
    if (definition instanceof AttributeDefinition) {
      AttributeDefinition attr = (AttributeDefinition) definition;
      return attr.getType().isCollection();
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  public <T> T getDefinition() {
    return (T) definition;
  }

}
