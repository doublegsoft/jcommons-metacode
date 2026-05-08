package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.CollectionType;
import com.doublegsoft.jcommons.metamodel.ValueDefinition;
import com.doublegsoft.jcommons.metamodel.dataset.JoinConditionDefinition;
import com.doublegsoft.jcommons.metamodel.dataset.JoinPredicateDefinition;
import com.doublegsoft.jcommons.utils.Strings;

import java.util.*;

public class FlowDefinition {

  private final List<TypeDefinition> types = new ArrayList<>();

  private final ModelDefinition dummyModel = new ModelDefinition();

  private final TypeDefinition root;

  private final ModelDefinition dataModel;

  public FlowDefinition(TypeDefinition root, ModelDefinition dataModel) {
    this.root = root;
    this.dataModel = dataModel;
    build();
  }

  public void addObjectIntoFlow(ObjectDefinition obj) {
    TypeDefinition type = new TypeDefinition(obj, dataModel);
    types.add(type);
  }

  public void addAttributeIntoFlow(AttributeDefinition attr) {
    ObjectDefinition obj = new ObjectDefinition(attr.getName(), dummyModel);
    TypeDefinition type = new TypeDefinition(obj, dataModel);
    types.add(type);
  }

  private void build() {
    if (root.getDefinition() instanceof ObjectDefinition) {
      buildForPivot();
      buildForMeta();
      buildForExtension();
      buildForObject();
    }
  }

  private void buildForPivot() {
    ObjectDefinition obj = root.getDefinition();
    if (!obj.isLabelled("pivot")) {
      return;
    }
    String masterObjName = obj.getLabelledOption("pivot", "master");
    String detailObjName = obj.getLabelledOption("pivot", "detail");
    String keyAttrName = obj.getLabelledOption("pivot", "key");
    String valAttrName = obj.getLabelledOption("pivot", "value");
    ObjectDefinition masterObj = dataModel.findObjectByName(masterObjName);
    ObjectDefinition detailObj = dataModel.findObjectByName(detailObjName);
    AttributeDefinition keyAttr = detailObj.getAttribute(keyAttrName);
    AttributeDefinition valAttr = detailObj.getAttribute(valAttrName);
    AttributeDefinition[] masterIdAttrs = detailObj.getIdentifiableAttributes();
    AttributeDefinition[] detailRefAttrs = detailObj.getCustomAttributes(masterObj);
    TypeDefinition masterType = new TypeDefinition(masterObj, dataModel);
    types.add(masterType);

    if (masterIdAttrs.length == 0) {
      throw new DefinitionException("\"" + detailObjName + "\"没有标识属性定义！");
    }
    if (detailRefAttrs.length == 0) {
      throw new DefinitionException("\"" + detailObjName + "\"没有属性引用到\"" + masterObjName + "\"定义！");
    }
    for (AttributeDefinition attr : obj.getAttributes()) {
      if (attr.isLabelled("persistence") || attr.isLabelled("original")) {
        continue;
      }
      TypeDefinition detailType = new TypeDefinition(detailObj, dataModel);
      for (AttributeDefinition refAttr : detailRefAttrs) {
        FieldDefinition refAttrField = new FieldDefinition(refAttr);
        ValueDefinition value = new ValueDefinition();
        value.setAttributeValue(masterIdAttrs[0]);
        refAttrField.setValue(value);
        detailType.addField(refAttrField);
      }
      FieldDefinition keyAttrField = new FieldDefinition(keyAttr);
      ValueDefinition keyAttrValue = new ValueDefinition();
      keyAttrValue.setString(attr.getName());
      keyAttrField.setValue(keyAttrValue);
      detailType.addField(keyAttrField);

      FieldDefinition valAttrField = new FieldDefinition(valAttr);
      ValueDefinition valAttrValue = new ValueDefinition();
      keyAttrValue.setAttributeValue(attr);
      keyAttrField.setValue(valAttrValue);
      detailType.addField(valAttrField);
      types.add(detailType);
    }
  }

  private void buildForMeta() {
    ObjectDefinition obj = root.getDefinition();
    if (!obj.isLabelled("meta")) {
      return;
    }
    String masterObjName = obj.getLabelledOption("meta", "master");
    String detailObjName = obj.getLabelledOption("meta", "detail");
    if (masterObjName == null) {
      masterObjName = obj.getName().substring(0, obj.getName().length() - 1);
    }
    if (detailObjName == null) {
      detailObjName = masterObjName + "_meta";
    }
    String keyAttrName = obj.getLabelledOption("meta", "key");
    if (keyAttrName == null) {
      keyAttrName = "property_name";
    }
    String valAttrName = obj.getLabelledOption("meta", "value");
    if (valAttrName == null) {
      valAttrName = "property_value";
    }
    ObjectDefinition masterObj = dataModel.findObjectByName(masterObjName);
    ObjectDefinition detailObj = dataModel.findObjectByName(detailObjName);
    AttributeDefinition keyAttr = detailObj.getAttribute(keyAttrName);
    AttributeDefinition valAttr = detailObj.getAttribute(valAttrName);
    AttributeDefinition[] masterIdAttrs = detailObj.getIdentifiableAttributes();
    AttributeDefinition[] detailRefAttrs = detailObj.getCustomAttributes(masterObj);
    TypeDefinition masterType = new TypeDefinition(masterObj, dataModel);
    types.add(masterType);

    if (masterIdAttrs.length == 0) {
      throw new DefinitionException("\"" + detailObjName + "\"没有标识属性定义！");
    }
    if (detailRefAttrs.length == 0) {
      throw new DefinitionException("\"" + detailObjName + "\"没有属性引用到\"" + masterObjName + "\"定义！");
    }
    if (keyAttr == null) {
      throw new DefinitionException("\"在meta对象没有找到作为key的字段！");
    }
    if (valAttr == null) {
      throw new DefinitionException("\"在meta对象没有找到作为value的字段！");
    }

    for (FieldDefinition rootField : root.getFields()) {
      boolean existing = false;
      for (FieldDefinition masterField : masterType.getFields()) {
        if (masterField.getName().equals(rootField.getName())) {
          existing = true;
          break;
        }
      }
      if (!existing) {
        masterType.addField(rootField);
      }
    }

    for (AttributeDefinition attr : obj.getAttributes()) {
      if (attr.isLabelled("persistence") || attr.isLabelled("original")) {
        continue;
      }
      TypeDefinition detailType = new TypeDefinition(detailObj, dataModel);
      types.add(detailType);
    }
  }

  private void buildForExtension() {
    ObjectDefinition obj = root.getDefinition();
    if (!obj.isLabelled("extension")) {
      return;
    }
    String masterObjName = obj.getLabelledOption("extension", "master");
    ObjectDefinition masterObj = dataModel.findObjectByName(masterObjName);
    String objectsExpr = obj.getLabelledOption("extension", "objects");
    String[] objRefExprs = objectsExpr.split(";");
    for (String objRefExpr : objRefExprs) {
      String objName = objRefExpr.substring(0, objRefExpr.indexOf("("));
      String attrName = objRefExpr.substring(objRefExpr.indexOf("(") + 1, objRefExpr.indexOf(")"));
      ObjectDefinition extensionObj = dataModel.findObjectByName(objName);
      AttributeDefinition extensionObjAttr = extensionObj.getAttribute(attrName);
    }
  }

  private void buildForObject() {
    ObjectDefinition obj = root.getDefinition();
    if (obj.isLabelled("pivot") ||
        obj.isLabelled("meta") ||
        obj.isLabelled("extension")) {
      return;
    }
    Map<String, TypeDefinition> existingTypes = new HashMap<>();
    Set<AttributeDefinition> existingOrigAttrs = new HashSet<>();
    for (AttributeDefinition attr : obj.getAttributes()) {
      TypeDefinition type = null;
      type = existingTypes.get(attr.getParent().getName());
      if (type == null) {
        type = new TypeDefinition(new ObjectDefinition(
            attr.getParent().getName(), dummyModel), dataModel);
        existingTypes.put(attr.getParent().getName(), type);
        types.add(type);
        buildReferences(type);
      }
      if (attr.isLabelled("persistence")) {
        // 数据对象
        if (attr.getType().isCustom()) {
          TypeDefinition refType = new TypeDefinition(new ObjectDefinition(
              attr.getType().getName(), dummyModel), dataModel);
          refType.setAttributeReference(true);
          refType.setVariable(attr.getName());
          // 注意：用属性名称作为key
          existingTypes.put(attr.getName(), refType);
          types.add(refType);
          buildReferences(refType);
        }
        FieldDefinition field = new FieldDefinition(attr);
        type.addField(field);
      } else if (attr.isLabelled("original")) {
        // 合成对象、聚合对象
        String origObjName = attr.getLabelledOption("original", "object");
        String origAttrName = attr.getLabelledOption("original", "attribute");
        String origAlias = attr.getLabelledOption("original", "alias");
        String prefixAlias = "";
        if (origAlias != null) {
          prefixAlias = origAlias + "_";
        }
        ObjectDefinition origObj = dataModel.findObjectByName(origObjName);
        AttributeDefinition origObjAttr = origObj.getAttribute(origAttrName);
        type = existingTypes.get(prefixAlias + origObjName);
        if (type == null) {
          type = new TypeDefinition(new ObjectDefinition(origObjName, dummyModel), dataModel);
          existingTypes.put(prefixAlias + origObjName, type);
          types.add(type);
          if (!Strings.isEmpty(origAlias)) {
            type.setVariable(origAlias);
          }
          buildReferences(type);
        }
        FieldDefinition field = new FieldDefinition(origObjAttr);
        type.addField(field);
      } else if (attr.getType().isCollection()) {
        CollectionType collType = (CollectionType) attr.getType();
        ObjectDefinition compObj = (ObjectDefinition) collType.getComponentType();
        if (attr.isLabelled("conjunction")) {
          String conjObjName = attr.getLabelledOption("conjunction", "object");
          ObjectDefinition conjObj = dataModel.findObjectByName(conjObjName);
          TypeDefinition conjType = new TypeDefinition(conjObj, dataModel);
          conjType.setCollection(true);
          TypeDefinition compType = new TypeDefinition(compObj, dataModel);
          compType.setCollection(true);
          types.add(conjType);
          buildReferences(conjType);
          types.add(compType);
          buildReferences(compType);
        } else {
          TypeDefinition compType = new TypeDefinition(compObj, dataModel);
          compType.setCollection(true);
          types.add(compType);
          buildReferences(compType);
          FieldDefinition field = new FieldDefinition(attr);
          type.addField(field);
        }
      }
    }
  }

  public TypeDefinition[] getTypes() {
    return getTypes(true);
  }

  public TypeDefinition[] getTypes(boolean persistent) {
    List<TypeDefinition> retVal = new ArrayList<>();
    for (TypeDefinition type : types) {
      if (persistent) {
        if (type.isPersistence()) {
          retVal.add(type);
        }
      } else {
        retVal.add(type);
      }
    }
    return retVal.toArray(new TypeDefinition[0]);
  }

  private void buildReferences(TypeDefinition current) {
    ObjectDefinition currObj = current.getDefinition();
    currObj = dataModel.findObjectByName(currObj.getName());
    if (types.size() > 1) {
      // 建立关联关系
      for (int i = 0; i < types.size() - 1; i++) {
        TypeDefinition prevType = types.get(i);
        ObjectDefinition prevObj = prevType.getDefinition();
        prevObj = dataModel.findObjectByName(prevObj.getName());
        AttributeDefinition[] refAttrs = currObj.getCustomAttributes(prevObj);
        if (refAttrs.length > 0) {
          JoinPredicateDefinition joinPredicate = new JoinPredicateDefinition();
          joinPredicate.setLeftAttribute(prevObj.getIdentifiableAttribute());
          joinPredicate.setLeftObject(prevObj);
          joinPredicate.setRightAttribute(refAttrs[0]);
          joinPredicate.setRightObject(currObj);
          JoinConditionDefinition joinCondition = new JoinConditionDefinition(joinPredicate);
          current.setReference(joinCondition);
        } else {
          refAttrs = prevObj.getCustomAttributes(currObj);
          for (AttributeDefinition refAttr : refAttrs) {
            if (Strings.isEmpty(current.getVariable()) ||
                refAttr.getName().equals(current.getVariable())) {
              JoinPredicateDefinition joinPredicate = new JoinPredicateDefinition();
              joinPredicate.setLeftAttribute(refAttr);
              joinPredicate.setLeftObject(prevObj);
              joinPredicate.setRightAttribute(currObj.getIdentifiableAttribute());
              joinPredicate.setRightObject(currObj);
              JoinConditionDefinition joinCondition = new JoinConditionDefinition(joinPredicate);
              current.setReference(joinCondition);
              break;
            }
          }
        }
      }
    }
  }
}
