package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.CollectionType;
import com.doublegsoft.jcommons.metabean.type.ObjectType;
import com.doublegsoft.jcommons.metamodel.ValueDefinition;
import com.doublegsoft.jcommons.metamodel.dataset.JoinConditionDefinition;

public class FieldDefinition {

  private final Object definition;

  private ValueDefinition value;

  private JoinConditionDefinition joinCondition;

  private boolean collection;

  public FieldDefinition(Object definition) {
    if (!(definition instanceof AttributeDefinition)) {
      throw new IllegalArgumentException("the definition is not accepted, just as attribute definition");
    }
    this.definition = definition;
  }

  public String getName() {
    if (definition instanceof AttributeDefinition) {
      AttributeDefinition attr = (AttributeDefinition) definition;
      return attr.getName();
    }
    return null;
  }

  public boolean isIdentifiable() {
    if (definition instanceof AttributeDefinition) {
      AttributeDefinition attr = (AttributeDefinition) definition;
      return attr.isIdentifiable();
    }
    return false;
  }

  public boolean hasPrimitiveType() {
    if (definition instanceof AttributeDefinition) {
      AttributeDefinition attr = (AttributeDefinition) definition;
      return attr.getType().isPrimitive();
    }
    return false;
  }

  public boolean hasCustomType() {
    if (definition instanceof AttributeDefinition) {
      AttributeDefinition attr = (AttributeDefinition) definition;
      return attr.getType().isCustom();
    }
    return false;
  }

  public boolean hasCollectionType() {
    if (definition instanceof AttributeDefinition) {
      AttributeDefinition attr = (AttributeDefinition) definition;
      return attr.getType().isCollection();
    }
    return false;
  }

  public Object getDefaultValue() {
    if (definition instanceof AttributeDefinition) {
      AttributeDefinition attr = (AttributeDefinition) definition;
      return attr.getConstraint().getDefaultValue();
    }
    return false;
  }

  public ObjectType getType() {
    if (definition instanceof AttributeDefinition) {
      AttributeDefinition attr = (AttributeDefinition) definition;
      return attr.getType();
    }
    return null;
  }

  public ObjectType getComponentType() {
    if (definition instanceof AttributeDefinition) {
      AttributeDefinition attr = (AttributeDefinition) definition;
      CollectionType colltype = (CollectionType) attr.getType();
      return colltype.getComponentType();
    }
    return null;
  }

  public ValueDefinition getValue() {
    return value;
  }

  public void setValue(ValueDefinition value) {
    this.value = value;
  }

  public boolean isCollection() {
    return collection;
  }

  public void setCollection(boolean collection) {
    this.collection = collection;
  }

  public JoinConditionDefinition getJoinCondition() {
    return joinCondition;
  }

  public void setJoinCondition(JoinConditionDefinition joinCondition) {
    this.joinCondition = joinCondition;
  }

  @SuppressWarnings("unchecked")
  public <T> T getDefinition() {
    return (T) definition;
  }

}
