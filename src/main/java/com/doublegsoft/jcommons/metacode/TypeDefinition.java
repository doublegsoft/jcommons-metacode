package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.CollectionType;
import com.doublegsoft.jcommons.metabean.type.ObjectType;
import com.doublegsoft.jcommons.metamodel.dataset.CompositeRowDefinition;
import com.doublegsoft.jcommons.metamodel.root.AggregateRootDefinition;

import java.util.*;

public class TypeDefinition {

  private final ModelDefinition dataModel;

  private final Object definition;

  private final List<FieldDefinition> fields = new ArrayList<>();

  private final List<MethodDefinition> methods = new ArrayList<>();

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

  public List<FieldDefinition> getIdentifiableFields() {
    List<FieldDefinition> retVal = new ArrayList<>();
    for (FieldDefinition field : fields) {
      if (field.isIdentifiable()) {
        retVal.add(field);
      }
    }
    return retVal;
  }

  public FieldDefinition getIdentifiableField() {
    List<FieldDefinition> retVal = getIdentifiableFields();
    if (retVal.isEmpty()) {
      return null;
    }
    return retVal.get(0);
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
      ObjectDefinition obj = (ObjectDefinition) definition;
      return obj.isLabelled("persistence");
    }
    return false;
  }

  public boolean isAggregate() {
    return false;
  }

  public boolean isPivot() {
    return false;
  }

  public boolean isMeta() {
    return false;
  }

  public boolean isExtension() {
    return false;
  }

  /**
   * 获取当前定义对应的“根数据对象”（Root Data Object）。
   * <p>
   * 逻辑说明：
   * 1. 仅当 definition 是 ObjectDefinition 时才进行处理；
   * 2. 遍历该对象下的所有属性（AttributeDefinition）；
   * 3. 查找带有 "persistence" 标签的属性：
   *    - 一般表示该属性与持久化层（数据库）相关；
   * 4. 一旦找到这样的属性，则返回其所属的父对象（即当前对象本身，作为根数据对象）。
   * <p>
   * 设计意图：
   * - 用于识别一个模型是否绑定了持久化定义；
   * - 若存在 persistence 标记，则认为该对象是“可落库的根对象”；
   * <p>
   * 返回值：
   * - 若找到带有 "persistence" 标签的属性，则返回其父 ObjectDefinition；
   * - 否则返回 null，表示当前定义不是持久化根对象。
   * <p>
   * 注意：
   * - 当前实现只要存在任意一个带 "persistence" 标签的属性即认为是 root，
   *   未校验是否为主键或主表标识；
   * - 若未来需要更严格语义（如判断主键、主表等），建议扩展该逻辑。
   */
  public ObjectDefinition getRootDataObject() {
    if (definition instanceof ObjectDefinition) {
      ObjectDefinition obj = (ObjectDefinition) definition;
      for (AttributeDefinition attr : obj.getAttributes()) {
        if (attr.isLabelled("persistence")) {
          return attr.getParent();
        }
      }
    }
    return null;
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


  @SuppressWarnings("unchecked")
  public <T> T getDefinition() {
    return (T) definition;
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
        ObjectDefinition masterObj = dataModel.findObjectByName(masterObjName);
        addFields(masterObj, existingFieldNames);
      }
      if (obj.isLabelled("extension")) {
        String extensionObjName = obj.getLabelledOption("extension", "object");
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
