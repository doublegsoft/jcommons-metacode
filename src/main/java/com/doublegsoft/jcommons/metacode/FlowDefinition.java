package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metamodel.ValueDefinition;

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
    types.add(new TypeDefinition(masterObj, dataModel));

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
      TypeDefinition fieldOwner = new TypeDefinition(detailObj, dataModel);
      for (AttributeDefinition refAttr : detailRefAttrs) {
        FieldDefinition refAttrField = new FieldDefinition(refAttr);
        ValueDefinition value = new ValueDefinition();
        value.setAttributeValue(masterIdAttrs[0]);
        refAttrField.setValue(value);
        fieldOwner.addField(refAttrField);
      }
      FieldDefinition keyAttrField = new FieldDefinition(keyAttr);
      ValueDefinition keyAttrValue = new ValueDefinition();
      keyAttrValue.setString(attr.getName());
      keyAttrField.setValue(keyAttrValue);
      fieldOwner.addField(keyAttrField);

      FieldDefinition valAttrField = new FieldDefinition(valAttr);
      ValueDefinition valAttrValue = new ValueDefinition();
      keyAttrValue.setAttributeValue(attr);
      keyAttrField.setValue(valAttrValue);
      fieldOwner.addField(valAttrField);
      types.add(fieldOwner);
    }
  }

  private void buildForMeta() {
    ObjectDefinition obj = root.getDefinition();
    if (!obj.isLabelled("meta")) {
      return;
    }
    String masterObjName = obj.getLabelledOption("meta", "master");
    String detailObjName = obj.getLabelledOption("meta", "detail");
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
    types.add(new TypeDefinition(masterObj, dataModel));

    if (masterIdAttrs.length == 0) {
      throw new DefinitionException("\"" + detailObjName + "\"没有标识属性定义！");
    }
    if (detailRefAttrs.length == 0) {
      throw new DefinitionException("\"" + detailObjName + "\"没有属性引用到\"" + masterObjName + "\"定义！");
    }
    for (AttributeDefinition attr : masterObj.getAttributes()) {
      // TODO
    }
    for (AttributeDefinition attr : obj.getAttributes()) {
      if (attr.isLabelled("persistence") || attr.isLabelled("original")) {
        continue;
      }
      TypeDefinition fieldOwner = new TypeDefinition(detailObj, dataModel);
      for (AttributeDefinition refAttr : detailRefAttrs) {
        FieldDefinition refAttrField = new FieldDefinition(refAttr);
        ValueDefinition value = new ValueDefinition();
        value.setAttributeValue(masterIdAttrs[0]);
        refAttrField.setValue(value);
        fieldOwner.addField(refAttrField);
      }
      FieldDefinition keyAttrField = new FieldDefinition(keyAttr);
      ValueDefinition keyAttrValue = new ValueDefinition();
      keyAttrValue.setString(attr.getName());
      keyAttrField.setValue(keyAttrValue);
      fieldOwner.addField(keyAttrField);

      FieldDefinition valAttrField = new FieldDefinition(valAttr);
      ValueDefinition valAttrValue = new ValueDefinition();
      keyAttrValue.setAttributeValue(attr);
      keyAttrField.setValue(valAttrValue);
      fieldOwner.addField(valAttrField);
      types.add(fieldOwner);
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
    if (obj.isLabelled("pivot") || obj.isLabelled("meta") || obj.isLabelled("extension")) {
      return;
    }
    Map<String, TypeDefinition> existingTypes = new HashMap<>();
    for (AttributeDefinition attr : obj.getAttributes()) {
      TypeDefinition type = null;
      if (attr.isLabelled("persistence")) {
        // 自然的数据对象
        type = existingTypes.get(attr.getParent().getName());
        if (!existingTypes.containsKey(attr.getParent().getName())) {
          type = new TypeDefinition(new ObjectDefinition(
              attr.getParent().getName(), dummyModel), dataModel);
          existingTypes.put(attr.getParent().getName(), type);
          types.add(type);
        }
        FieldDefinition field = new FieldDefinition(attr);
        type.addField(field);
      } else if (attr.isLabelled("original")) {
        // 衍生的领域对象
        String origObjName = attr.getLabelledOption("original", "object");
        String origAttrName = attr.getLabelledOption("original", "attribute");
        ObjectDefinition origObj = dataModel.findObjectByName(origObjName);
        AttributeDefinition origObjAttr = origObj.getAttribute(origAttrName);
        type = existingTypes.get(origObjName);
        if (!existingTypes.containsKey(origObjName)) {
          type = new TypeDefinition(new ObjectDefinition(origObjName, dummyModel), dataModel);
          existingTypes.put(origObjName, type);
          types.add(type);
        }
        FieldDefinition field = new FieldDefinition(origObjAttr);
        type.addField(field);
      } else if (attr.getType().isCollection()) {
        if (attr.isLabelled("conjunction")) {

        } else {

        }
      }
    }
  }

  public TypeDefinition[] getTypes() {
    return types.toArray(new TypeDefinition[0]);
  }
}
