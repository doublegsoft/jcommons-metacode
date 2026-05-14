package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.CollectionType;
import com.doublegsoft.jcommons.metabean.type.CustomType;
import com.doublegsoft.jcommons.metabean.type.ObjectType;
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

  public TypeDefinition getRoot() {
    return root;
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
      ObjectDefinition obj = root.getDefinition();
      ObjectDefinition dataObj = dataModel.findObjectByName(obj.getName());
      boolean isAggregateLike = true;
      for (AttributeDefinition attr : dataObj.getAttributes()) {
        if (!attr.getType().isCustom() && !attr.getType().isCollection()) {
          isAggregateLike = false;
          break;
        }
      }
      if (dataObj.isLabelled("pivot")) {
        buildForPivot();
      } else if (dataObj.isLabelled("meta") || obj.isLabelled("meta")) {
        buildForMeta();
      } else if (dataObj.isLabelled("extension")) {
        buildForExtension();
      } else if (isAggregateLike) {
        buildForAggregate();
      } else {
        // composite or plain
        buildForObject();
      }
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
    if (keyAttr == null) {
      throw new DefinitionException("\"在pivot对象的定义中没有找到作为key的字段！");
    }
    if (valAttr == null) {
      throw new DefinitionException("\"在pivot对象的定义中没有找到作为value的字段！");
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

    for (FieldDefinition field : masterType.getFields()) {
      if (field.getDefinition() instanceof AttributeDefinition) {
        AttributeDefinition attr = field.getDefinition();
        if (attr.getType().isCustom()) {
          ObjectDefinition refObj = dataModel.findObjectByName(attr.getType().getName());
          TypeDefinition refType = new TypeDefinition(refObj, dataModel);
          types.add(refType);
          buildReferences(refType);
        }
      }
    }

    TypeDefinition detailType = new TypeDefinition(detailObj, dataModel);
    detailType.setCollection(true);
    types.add(detailType);
    buildReferences(detailType);
  }

  private void buildForMeta() {
    ObjectDefinition obj = root.getDefinition();
    if (!obj.isLabelled("meta")) {
      return;
    }
    String masterObjName = obj.getLabelledOption("meta", "master");
    String detailObjName = obj.getLabelledOption("meta", "detail");
    if (masterObjName == null) {
      if (obj.getName().endsWith("_")) {
        masterObjName = obj.getName().substring(0, obj.getName().length() - 1);
      } else {
        masterObjName = obj.getName();
      }
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
    buildReferences(masterType);

    if (masterIdAttrs.length == 0) {
      throw new DefinitionException("\"" + detailObjName + "\"没有标识属性定义！");
    }
    if (detailRefAttrs.length == 0) {
      throw new DefinitionException("\"" + detailObjName + "\"没有属性引用到\"" + masterObjName + "\"定义！");
    }
    if (keyAttr == null) {
      throw new DefinitionException("\"在meta对象的定义中没有找到作为key的字段！");
    }
    if (valAttr == null) {
      throw new DefinitionException("\"在meta对象的定义中没有找到作为value的字段！");
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

    for (FieldDefinition field : masterType.getFields()) {
      if (field.getDefinition() instanceof AttributeDefinition) {
        AttributeDefinition attr = field.getDefinition();
        if (attr.getType().isCustom()) {
          ObjectDefinition refObj = dataModel.findObjectByName(attr.getType().getName());
          TypeDefinition refType = new TypeDefinition(refObj, dataModel);
          types.add(refType);
          buildReferences(refType);
        }
      }
    }

    TypeDefinition detailType = new TypeDefinition(detailObj, dataModel);
    detailType.setCollection(true);
    types.add(detailType);
    buildReferences(detailType);
  }

  private void buildForExtension() {
    ObjectDefinition obj = root.getDefinition();
    if (!obj.isLabelled("extension")) {
      return;
    }
    String masterObjName = obj.getLabelledOption("extension", "master");
    ObjectDefinition masterObj = dataModel.findObjectByName(masterObjName);
    String detailsExpr = obj.getLabelledOption("extension", "details");
    if (detailsExpr != null) {
      String[] objRefExprs = detailsExpr.split(";");
      for (String objRefExpr : objRefExprs) {
        if (Strings.isEmpty(objRefExpr)) {
          continue;
        }
        String objName = objRefExpr.substring(0, objRefExpr.indexOf("("));
        String attrName = objRefExpr.substring(objRefExpr.indexOf("(") + 1, objRefExpr.indexOf(")"));
        ObjectDefinition extensionObj = dataModel.findObjectByName(objName);
        AttributeDefinition extensionObjAttr = extensionObj.getAttribute(attrName);
      }
    }
  }

  private void buildForAggregate() {
    ObjectDefinition obj = root.getDefinition();
    ObjectDefinition dataObj = dataModel.findObjectByName(obj.getName());
    for (AttributeDefinition attr : dataObj.getAttributes()) {
      ObjectDefinition refObj = dataModel.findObjectByName(attr.getType().getName());
      TypeDefinition refType = new TypeDefinition(refObj, dataModel);
      types.add(refType);
      buildReferences(refType, attr);
    }
  }

  private void buildForObject() {
    ObjectDefinition obj = root.getDefinition();
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
        ObjectDefinition compObj = null;
        // FIXME
        ObjectType componentType = collType.getComponentType();
        if (componentType instanceof CustomType) {
          compObj = dataModel.findObjectByName(componentType.getName());
        } else if (componentType instanceof ObjectDefinition) {
          compObj = (ObjectDefinition) componentType;
        }
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
      } else if (attr.isLabelled("conjunction")) {
        // TODO
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

  private void buildReferences(TypeDefinition current, AttributeDefinition attrRef) {
    if (attrRef == null || !attrRef.isLabelled("conjunction")) {
      buildReferences(current);
      return;
    }
    String sourceObjName = attrRef.getLabelledOption("conjunction", "source_object");
    String sourceAttrName = attrRef.getLabelledOption("conjunction", "source_attribute");
    String targetObjName = attrRef.getLabelledOption("conjunction", "target_object");
    String targetAttrName = attrRef.getLabelledOption("conjunction", "target_attribute");

    ObjectDefinition sourceObj = dataModel.findObjectByName(sourceObjName);
    AttributeDefinition sourceAttr = dataModel.findAttributeByNames(sourceObjName, sourceAttrName);
    ObjectDefinition targetObj = dataModel.findObjectByName(targetObjName);
    AttributeDefinition targetAttr = dataModel.findAttributeByNames(targetObjName, targetAttrName);

    current.setReference(createJoinCondition(sourceAttr, sourceObj, targetAttr, targetObj));
  }

  private void buildReferences(TypeDefinition current) {
    ObjectDefinition currObj = current.getDefinition();
    currObj = dataModel.findObjectByName(currObj.getName());
    // 建立关联关系
    for (int i = 0; i < types.size() - 1; i++) {
      TypeDefinition prevType = types.get(i);
      ObjectDefinition prevObj = prevType.getDefinition();
      prevObj = dataModel.findObjectByName(prevObj.getName());
      AttributeDefinition[] refAttrs = currObj.getCustomAttributes(prevObj);
      if (refAttrs.length > 0) {
        // 正向引用，可以是多个，比如主客队，或者前置、当前、后置节点等情况
        current.setReference(createJoinCondition(prevObj.getIdentifiableAttribute(), prevObj,
            refAttrs[0], currObj));
      } else {
        // 反向引用
        refAttrs = prevObj.getCustomAttributes(currObj);
        for (AttributeDefinition refAttr : refAttrs) {
          if (Strings.isEmpty(current.getVariable()) ||
              refAttr.getName().equals(current.getVariable())) {
            current.setReference(createJoinCondition(refAttr, prevObj,
                currObj.getIdentifiableAttribute(), currObj));
            break;
          }
        }
      } // 正向、反向引用
    }
  }

  private JoinConditionDefinition createJoinCondition(AttributeDefinition leftAttr,
                                                      ObjectDefinition leftObj,
                                                      AttributeDefinition rightAttr,
                                                      ObjectDefinition rightObj) {
    JoinPredicateDefinition joinPredicate = new JoinPredicateDefinition();
    joinPredicate.setLeftAttribute(leftAttr);
    joinPredicate.setLeftObject(leftObj);
    joinPredicate.setRightAttribute(rightAttr);
    joinPredicate.setRightObject(rightObj);
    return new JoinConditionDefinition(joinPredicate);
  }
}
