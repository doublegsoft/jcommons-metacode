package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.CollectionType;
import com.doublegsoft.jcommons.metamodel.dataset.CompositeRowDefinition;
import com.doublegsoft.jcommons.metamodel.dataset.JoinConditionDefinition;
import com.doublegsoft.jcommons.metamodel.dataset.QualifiedObjectDefinition;
import com.doublegsoft.jcommons.metamodel.root.AggregateRootDefinition;
import com.doublegsoft.jcommons.utils.Inflector;

import java.util.*;

public class TypeDefinition {

  public static final String NO_REF = "NREF";

  public static final String PERSISTENCE_REF = "PREF";

  public static final String ATTRIBUTE_REF = "AREF";

  public static final String COLLECTION_REF = "CREF";

  public static final String ORIGINAL_REF = "OREF";

  public static final String SELF_REF = "SREF";

  private final ModelDefinition dataModel;

  private String variable;

  /**
   * 虚拟的，不一定是真正的data object。
   */
  private final Object definition;

  private final List<FieldDefinition> fields = new ArrayList<>();

  private final List<MethodDefinition> methods = new ArrayList<>();

  private JoinConditionDefinition reference;

  // 是否是属性的直接引用
  private boolean attributeReference;

  // 是否是间接间接引用，用两个字段的引用
  private boolean implicitReference;

  private boolean collection;

  public TypeDefinition(Object definition, ModelDefinition dataModel) {
    if (!(definition instanceof ObjectDefinition) &&
        !(definition instanceof AggregateRootDefinition) &&
        !(definition instanceof CompositeRowDefinition)) {
      throw new IllegalArgumentException("the definition is not accepted, " +
          "just as object definition, aggregate root definition, composite row definition");
    }
    this.definition = definition;
    this.dataModel = dataModel;
    this.generateFields();
  }

  public List<TypeDefinition> groupPersistentTypes() {
    List<TypeDefinition> retVal = new ArrayList<>();
    Map<String, TypeDefinition> existingTypes = new HashMap<>();

    // 处理非集合类型的属性，因为集合类型的属性涉及到直接关联或者间接关联的其他对象
    for (FieldDefinition field : fields) {
      if (!(field.getDefinition() instanceof AttributeDefinition)) {
        continue;
      }
      AttributeDefinition attr = field.getDefinition();
      ObjectDefinition obj = null;
      if (attr.isLabelled("persistence")) {
        // 直接映射
        obj = attr.getParent();
      } else if (attr.isLabelled("original")) {
        // 间接映射
        String origObjName = attr.getLabelledOption("original", "object");
        obj = dataModel.findObjectByName(origObjName);
      } else if (attr.getType().isCollection()) {
        CollectionType collType = (CollectionType) attr.getType();
        obj = dataModel.findObjectByName(collType.getComponentType().getName());
      }
      if (obj == null) {
        continue;
      }
      TypeDefinition existingType = existingTypes.get(obj.getName());
      if (existingType == null) {
        existingType = new TypeDefinition(obj, dataModel);
        retVal.add(existingType);
        existingTypes.put(obj.getName(), existingType);
      }
      existingType.fields.add(field);
    }
    return retVal;
  }

  public List<FieldDefinition> getFieldsWithCustomType() {
    List<FieldDefinition> retVal = new ArrayList<>();
    for (FieldDefinition field : fields) {
      if (field.hasCustomType()) {
        fields.add(field);
      }
    }
    return retVal;
  }

  public List<FieldDefinition> getFieldsWithCollectionType() {
    List<FieldDefinition> retVal = new ArrayList<>();
    for (FieldDefinition field : fields) {
      if (field.hasCollectionType()) {
        fields.add(field);
      }
    }
    return retVal;
  }

  public List<FieldDefinition> getFieldsWithPrimitiveType() {
    List<FieldDefinition> retVal = new ArrayList<>();
    for (FieldDefinition field : fields) {
      if (field.hasPrimitiveType()) {
        fields.add(field);
      }
    }
    return retVal;
  }

  public List<FieldDefinition> getFieldsWithCollectionTypeAndCustomComponentType() {
    // TODO
    List<FieldDefinition> retVal = new ArrayList<>();
    return retVal;
  }

  public List<FieldDefinition> getFieldsWithComponentType(ObjectDefinition componentType) {
    // TODO
    List<FieldDefinition> retVal = new ArrayList<>();
    return retVal;
  }

  public Set<ObjectDefinition> getReferencingCustomTypes() {
    Set<ObjectDefinition> retVal = new HashSet<>();
    for (FieldDefinition field : fields) {
      if (field.hasCustomType()) {
        retVal.add(dataModel.findObjectByName(field.getType().getName()));
      } else if (field.hasCollectionType() &&
          field.getComponentType() instanceof ObjectDefinition) {
        retVal.add(dataModel.findObjectByName(field.getComponentType().getName()));
      }
    }
    return retVal;
  }

  public Set<ObjectDefinition> getReferencingObjects() {
    Set<ObjectDefinition> retVal = getReferencingCustomTypes();
    if (definition instanceof ObjectDefinition) {
      ObjectDefinition obj = (ObjectDefinition) definition;
      if (obj.isLabelled("pivot")) {
        String masterObjName = obj.getLabelledOption("pivot", "master");
        String detailObjName = obj.getLabelledOption("pivot", "detail");
        retVal.add(dataModel.findObjectByName(masterObjName));
        retVal.add(dataModel.findObjectByName(detailObjName));
      } else if (obj.isLabelled("projection")) {
        String masterObjName = obj.getLabelledOption("projection", "master");
        String detailObjName = obj.getLabelledOption("projection", "detail");
        retVal.add(dataModel.findObjectByName(masterObjName));
        retVal.add(dataModel.findObjectByName(detailObjName));
      }
      if (obj.isLabelled("meta")) {
        String masterObjName = obj.getLabelledOption("meta", "master");
        String detailObjName = obj.getLabelledOption("meta", "detail");
        retVal.add(dataModel.findObjectByName(masterObjName));
        retVal.add(dataModel.findObjectByName(detailObjName));
      }
      if (obj.isLabelled("extension")) {
        String extensionObjName = obj.getLabelledOption("extension", "object");
        retVal.add(dataModel.findObjectByName(extensionObjName));
      }
    }
    return retVal;
  }

  public boolean isPersistence() {
    if (definition instanceof ObjectDefinition) {
      ObjectDefinition obj = dataModel.findObjectByName(getName());
      return obj.isLabelled("persistence");
    }
    return false;
  }

  public boolean isComposite() {
    if (definition instanceof ObjectDefinition) {
      ObjectDefinition obj = (ObjectDefinition) definition;
      return obj.isLabelled("composite");
    }
    return false;
  }

  public boolean isAggregate() {
    if (definition instanceof ObjectDefinition) {
      ObjectDefinition obj = (ObjectDefinition) definition;
      return obj.isLabelled("aggregate");
    }
    return false;
  }

  public boolean isPivot() {
    if (definition instanceof ObjectDefinition) {
      ObjectDefinition obj = (ObjectDefinition) definition;
      return obj.isLabelled("pivot");
    }
    return false;
  }

  public boolean isMeta() {
    if (definition instanceof ObjectDefinition) {
      ObjectDefinition obj = (ObjectDefinition) definition;
      return obj.isLabelled("meta");
    }
    return false;
  }

  public boolean isExtension() {
    if (definition instanceof ObjectDefinition) {
      ObjectDefinition obj = (ObjectDefinition) definition;
      return obj.isLabelled("extension");
    }
    return false;
  }

  public boolean isAttributeReference() {
    return attributeReference;
  }

  public void setAttributeReference(boolean attributeReference) {
    this.attributeReference = attributeReference;
  }

  public AttributeDefinition[] getIdentifiableAttributes() {
    if (definition instanceof ObjectDefinition) {
      ObjectDefinition obj = (ObjectDefinition) definition;
      ObjectDefinition dataObj = dataModel.findObjectByName(obj.getName());
      return dataObj.getIdentifiableAttributes();
    }
    return new AttributeDefinition[0];
  }

  public FlowDefinition getDataObjects() {
    FlowDefinition retVal = new FlowDefinition(this, dataModel);
    return retVal;
  }

  public FieldDefinition[] getFields() {
    return fields.toArray(new FieldDefinition[0]);
  }

  public void addField(FieldDefinition field) {
    fields.add(field);
  }

  public String getVariable() {
    return variable;
  }

  public void setVariable(String variable) {
    this.variable = variable;
  }

  @SuppressWarnings("unchecked")
  public <T> T getDefinition() {
    return (T) definition;
  }

  public JoinConditionDefinition getReference() {
    return reference;
  }

  public void setReference(JoinConditionDefinition reference) {
    this.reference = reference;
  }

  public AttributeDefinition getLeftAttributeFromReference() {
    if (reference == null) {
      return null;
    }
    return reference.getJoinPredicates().get(0).getLeftAttribute();
  }

  public AttributeDefinition getRightAttributeFromReference() {
    if (reference == null) {
      return null;
    }
    return reference.getJoinPredicates().get(0).getRightAttribute();
  }

  public FieldDefinition getFieldByDefinition(Object definition) {
    for (FieldDefinition field : fields) {
      if (field.getDefinition().equals(definition)) {
        return field;
      }
    }
    return null;
  }

  public FieldDefinition getFieldByName(String name) {
    for (FieldDefinition field : fields) {
      if (field.getName().equals(name)) {
        return field;
      }
    }
    return null;
  }

  public boolean isCollection() {
    return collection;
  }

  public void setCollection(boolean collection) {
    this.collection = collection;
  }

  public boolean isAggregateRoot() {
    return definition instanceof AggregateRootDefinition;
  }

  public boolean isCompositeRow() {
    return definition instanceof CompositeRowDefinition;
  }

  public boolean isDataObject() {
    return definition instanceof ObjectDefinition;
  }

  public AttributeDefinition getIdentifiableAttribute() {
    if (definition instanceof ObjectDefinition) {
      ObjectDefinition dataObj = dataModel.findObjectByName(getName());
      return dataObj.getIdentifiableAttribute();
    }
    return null;
  }

  public String getPlural() {
    if (definition instanceof ObjectDefinition) {
      ObjectDefinition obj = (ObjectDefinition) definition;
      ObjectDefinition dataObj = dataModel.findObjectByName(obj.getName());
      if (dataObj.getLabelledOption("name", "plural") != null) {
        return dataObj.getLabelledOption("name", "plural");
      }
      return Inflector.getInstance().pluralize(dataObj.getName());
    }
    return null;
  }

  public AttributeDefinition getAttribute(String name) {
    if (definition instanceof ObjectDefinition) {
      ObjectDefinition dataObj = dataModel.findObjectByName(getName());
      return dataObj.getAttribute(name);
    }
    return null;
  }

  public FlowDefinition getFlow() {
    return new FlowDefinition(this, dataModel);
  }

  public String getLabel() {
    if (definition instanceof ObjectDefinition) {
      ObjectDefinition obj = (ObjectDefinition) definition;
      if (obj.isLabelled("name")) {
        return obj.getLabelledOption("name", "label");
      }
    } else if (definition instanceof CompositeRowDefinition) {
      CompositeRowDefinition row = (CompositeRowDefinition) definition;
      QualifiedObjectDefinition qualObj = row.getQualifiedObjects().get(0);
      return qualObj.getObject().getLabelledOption("name", "label");
    } else if (definition instanceof AggregateRootDefinition) {
      AggregateRootDefinition root = (AggregateRootDefinition) definition;
      root.getRoot().getLabelledOption("name", "label");
    }
    throw new RuntimeException("unknown definition type: \"" + definition.getClass() + "\"");
  }

  public String getName() {
    if (definition instanceof ObjectDefinition) {
      ObjectDefinition obj = (ObjectDefinition) definition;
      return obj.getName();
    } else if (definition instanceof CompositeRowDefinition) {
      CompositeRowDefinition row = (CompositeRowDefinition) definition;
      QualifiedObjectDefinition qualObj = row.getQualifiedObjects().get(0);
      return qualObj.getObject().getName();
    } else if (definition instanceof AggregateRootDefinition) {
      AggregateRootDefinition root = (AggregateRootDefinition) definition;
      root.getRoot().getName();
    }
    throw new RuntimeException("unknown definition type: \"" + definition.getClass() + "\"");
  }

  public String getReferenceType(TypeDefinition another) {
    ObjectDefinition thisObj = (ObjectDefinition) definition;
    ObjectDefinition anotherObj = (ObjectDefinition) another.definition;
    String thisObjName = thisObj.getName();
    if (thisObjName.endsWith("_")) {
      thisObjName = thisObjName.substring(0, thisObjName.length() - 1);
    } else if (thisObj.isLabelled("meta")) {
      thisObjName = thisObj.getLabelledOption("meta", "master");
    }
    // 属性引用
    if (thisObjName.equals(anotherObj.getName())) {
      return SELF_REF;
    }
    if (thisObj.isLabelled("meta")) {
      if (anotherObj.getName().equals(thisObj.getLabelledOption("meta", "detail"))) {
        return COLLECTION_REF;
      } else if (anotherObj.getName().equals(thisObjName + "_meta")) {
        return COLLECTION_REF;
      }
    }
    if (thisObj.isLabelled("pivot")) {
      if (anotherObj.getName().equals(thisObj.getLabelledOption("pivot", "detail"))) {
        return COLLECTION_REF;
      }
    }
    String retVal = getReferenceType(thisObj, anotherObj);
    if (NO_REF.equals(retVal)) {
      if (thisObj.isLabelled("meta")) {
        ObjectDefinition thisDataObj = dataModel.findObjectByName(thisObjName);
        retVal = getReferenceType(thisDataObj, anotherObj);
      } else if (thisObj.isLabelled("pivot")) {
        ObjectDefinition thisDataObj = dataModel.findObjectByName(thisObjName);
        retVal = getReferenceType(thisDataObj, anotherObj);
      }
    }
    return retVal;
  }

  private String getReferenceType(ObjectDefinition thisObj, ObjectDefinition anotherObj) {
    for (AttributeDefinition attr : thisObj.getAttributes()) {
      if (attr.getType().getName().equals(anotherObj.getName())) {
        if (attr.isLabelled("persistence")) {
          return PERSISTENCE_REF;
        } else {
          return ATTRIBUTE_REF;
        }
      }
      if (attr.getType().isCollection()) {
        CollectionType collType = (CollectionType) attr.getType();
        if (collType.getComponentType().getName().equals(anotherObj.getName())) {
          return COLLECTION_REF;
        }
      }
      if (attr.isLabelled("original")) {
        String origObjName = thisObj.getLabelledOption("original", "object");
        if (origObjName.equals(anotherObj.getName())) {
          return ORIGINAL_REF;
        }
      }
    }
    return NO_REF;
  }

  private void generateFields() {
    Set<String> existingFieldNames = new HashSet<>();
    if (definition instanceof ObjectDefinition) {
      ObjectDefinition obj = (ObjectDefinition) definition;
      for (AttributeDefinition attr : obj.getAttributes()) {
        FieldDefinition field = new FieldDefinition(attr);
        fields.add(field);
      }
      if (obj.isLabelled("pivot")) {
        String masterObjName = obj.getLabelledOption("pivot", "master");
        ObjectDefinition masterObj = dataModel.findObjectByName(masterObjName);
        addFields(masterObj, existingFieldNames);
      }
      if (obj.isLabelled("meta")) {
        String masterObjName = obj.getLabelledOption("meta", "master");
        if (masterObjName == null) {
          masterObjName = obj.getName().substring(0, obj.getName().length() - 1);
        }
        ObjectDefinition masterObj = dataModel.findObjectByName(masterObjName);
        addFields(masterObj, existingFieldNames);
      }
      if (obj.isLabelled("extension")) {
        String extensionObjName = obj.getLabelledOption("extension", "master");
        ObjectDefinition extensionObj = dataModel.findObjectByName(extensionObjName);
        addFields(extensionObj, existingFieldNames);
      }
    } else if (definition instanceof CompositeRowDefinition) {
      // TODO
    } else if (definition instanceof AggregateRootDefinition) {
      // TODO
    }
  }

  private void addFields(ObjectDefinition obj, Set<String> existingFieldNames) {
    for (AttributeDefinition attr : obj.getAttributes()) {
      if (!existingFieldNames.contains(attr.getName())) {
        FieldDefinition field = new FieldDefinition(attr);
        fields.add(field);
      }
      existingFieldNames.add(attr.getName());
    }
  }

}
