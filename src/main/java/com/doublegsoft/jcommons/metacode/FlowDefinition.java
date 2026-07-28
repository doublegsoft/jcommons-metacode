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
      } else if (isAggregateLike && !obj.isLabelled("persistence")) {
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
    AttributeDefinition masterObjIdAttr = masterObj.getIdentifiableAttribute();
    TypeDefinition masterType = new TypeDefinition(masterObj, dataModel);
    types.add(masterType);
    buildReferences(masterType);
    String detailsExpr = obj.getLabelledOption("extension", "details");
    if (detailsExpr != null) {
      String[] objRefExprs = detailsExpr.split(";");
      for (String objRefExpr : objRefExprs) {
        if (Strings.isEmpty(objRefExpr)) {
          continue;
        }
        ObjectDefinition detailObj = null;
        AttributeDefinition detailAttr = null;
        String objName = null;
        if (objRefExpr.contains("(")) {
          objName = objRefExpr.substring(0, objRefExpr.indexOf("("));
          detailObj = dataModel.findObjectByName(objName);
          String attrname = objRefExpr.substring(objRefExpr.indexOf("(") + 1, objRefExpr.indexOf(")"));
          detailAttr = detailObj.getAttribute(attrname);
        } else {
          objName = objRefExpr;
          detailObj = dataModel.findObjectByName(objName);
          for (AttributeDefinition attrInDetail : detailObj.getAttributes()) {
            if (masterObj.getName().equals(attrInDetail.getType().getName())) {
              detailAttr = attrInDetail;
            }
          }
        }
        TypeDefinition detailType = new TypeDefinition(detailObj, dataModel);
        types.add(detailType);
        buildReferences(detailType, detailAttr, masterType);
      }
    }
  }

  private void buildForAggregate() {
    ObjectDefinition rootObj = root.getDefinition();
    ObjectDefinition dataObj = dataModel.findObjectByName(rootObj.getName());
    for (AttributeDefinition attr : dataObj.getAttributes()) {
      ObjectDefinition refObj;
      boolean collection = false;
      if (attr.getType().isCollection()) {
        CollectionType collType = (CollectionType) attr.getType();
        refObj = dataModel.findObjectByName(collType.getComponentType().getName());
        collection = true;
      } else if (attr.getType().isCustom()) {
        refObj = dataModel.findObjectByName(attr.getType().getName());
      } else {
        continue;
      }
      TypeDefinition refType = new TypeDefinition(refObj, dataModel);
      refType.setCollection(collection);
      types.add(refType);
      // 如何解决match和graph那种用aggregate方式去定义的问题
      for (AttributeDefinition rootAttr : rootObj.getAttributes()) {
        if (refObj.getName().equals(rootAttr.getType().getName()) && refType.isVariableNull()) {
          refType.setVariable(rootAttr.getName());
        } else if (refType.isCollection()) {
          refType.setVariable(attr.getName());
        }
      }
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
          compType.setVariable(attr.getName());
        } else {
          TypeDefinition compType = new TypeDefinition(compObj, dataModel);
          compType.setCollection(true);
          types.add(compType);
          buildReferences(compType);
          FieldDefinition field = new FieldDefinition(attr);
          type.addField(field);
          compType.setVariable(attr.getName());
        }
      } else if (attr.isLabelled("conjunction")) {
        // TODO: 一定要想起来在这要干什么
      }
    }
  }

  public TypeDefinition[] getTypes() {
    return getTypes(true);
  }

  // 这个应该是有关联关系的都应该算上
  public TypeDefinition[] getTypes(boolean persistent) {
    List<TypeDefinition> retVal = new ArrayList<>();
    for (TypeDefinition type : types) {
      if (persistent) {
        if (type.isPersistence() || type.getReference() != null) {
          retVal.add(type);
        }
      } else {
        retVal.add(type);
      }
    }
    return retVal.toArray(new TypeDefinition[0]);
  }

  public TypeDefinition[] sortTypes() {
    return sortTypes(true);
  }

  public TypeDefinition[] sortTypes(boolean persistent) {
    Map<String, TypeDefinition> typeMap = new HashMap<>();
    Map<String, List<String>> adjList = new HashMap<>();
    Map<String, Integer> inDegree = new HashMap<>();

    List<TypeDefinition> collections = new ArrayList<>();
    List<TypeDefinition> nonCollections = new ArrayList<>();

    // 1. 初始化基础数据结构（分离 collection 和 non-collection 类型）
    for (TypeDefinition type : types) {
      ObjectDefinition dataObj = type.getDefinition();
      if (persistent) {
        if (!dataObj.isLabelled("persistence")) {
          continue;
        }
      }
      if (type.isCollection()) {
        collections.add(type);
      } else {
        nonCollections.add(type);
        typeMap.put(type.getName(), type);
        adjList.put(type.getName(), new ArrayList<>());
        inDegree.put(type.getName(), 0);
      }
    }
    // 2. 构建邻接表（图）和节点的入度（仅针对 non-collection）
    for (TypeDefinition type : nonCollections) {
      String typeName = type.getName();
      ObjectDefinition dataObj = type.getDefinition();

      for (AttributeDefinition dataAttr : dataObj.getAttributes()) {
        if (dataAttr.getType().isCustom()) {
          String dependencyName = dataAttr.getType().getName();
          // 确保依赖的自定义类型在候选集内
          if (typeMap.containsKey(dependencyName)) {
            // 被依赖项 (B) 指向 依赖项 (A) -> (B -> A)
            adjList.get(dependencyName).add(typeName);
            inDegree.put(typeName, inDegree.get(typeName) + 1);
          }
        }
      }
    }
    // 3. 将所有入度为 0 的节点（仅限 non-collection）放入队列
    Queue<String> queue = new LinkedList<>();
    for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
      if (entry.getValue() == 0) {
        queue.add(entry.getKey());
      }
    }

    // 4. 拓扑排序主循环
    List<TypeDefinition> result = new ArrayList<>();
    while (!queue.isEmpty()) {
      String current = queue.poll();
      result.add(typeMap.get(current));
      // 减少邻居节点的入度
      for (String neighbor : adjList.get(current)) {
        int updatedInDegree = inDegree.get(neighbor) - 1;
        inDegree.put(neighbor, updatedInDegree);
        // 如果入度降为 0，说明其所有前置依赖已处理完，放入队列
        if (updatedInDegree == 0) {
          queue.add(neighbor);
        }
      }
    }

    // 5. 循环依赖（环）检测（仅对比非集合类型的数量）
    if (result.size() != nonCollections.size()) {
      throw new IllegalStateException("检测到数据定义中存在循环依赖，无法构建合法的处理顺序。");
    }

    // 6. 自动将所有 collection 类型追加到最后
    result.addAll(collections);

    return result.toArray(new TypeDefinition[0]);
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

    current.setReference(createJoinCondition(
        sourceAttr, sourceObj, null,
        targetAttr, targetObj, null));
  }

  private void buildReferences(TypeDefinition detail, AttributeDefinition attrRef, TypeDefinition master) {
    ObjectDefinition sourceObj = detail.getDefinition();
    ObjectDefinition masterObj = master.getDefinition();
    detail.setReference(createJoinCondition(
        masterObj.getIdentifiableAttribute(), masterObj, master.getVariable(),
        attrRef, sourceObj, null));
  }

  private void buildReferences(TypeDefinition current) {
    ObjectDefinition currObj = current.getDefinition();
    currObj = dataModel.findObjectByName(currObj.getName());
    // 建立关联关系
    for (int i = 0; i < types.size() - 1; i++) {
      TypeDefinition prevType = types.get(i);
      ObjectDefinition prevObj = prevType.getDefinition();
      prevObj = dataModel.findObjectByName(prevObj.getName());
      boolean isBuilt = buildReferences(currObj, prevObj, current, prevType);
      if (isBuilt) {
        return;
      }
      if (!currObj.isLabelled("persistence")) {
        for (AttributeDefinition currAttr : currObj.getAttributes()) {
          if (currAttr.getType().isCustom()) {
            ObjectDefinition attrAsObj = dataModel.findObjectByName(currAttr.getType().getName());
            if (buildReferences(attrAsObj, prevObj, current, prevType)) {
              return;
            }
          }
        }
      }
      AttributeDefinition[] refAttrs = currObj.getCustomAttributes(prevObj);
      if (refAttrs.length > 0) {
        // 正向引用，可以是多个，比如主客队，或者前置、当前、后置节点等情况
        current.setReference(createJoinCondition(
            prevObj.getIdentifiableAttribute(), prevObj, prevType.getVariable(),
            refAttrs[0], currObj, current.getVariable()));
      } else {
        // 反向引用
        refAttrs = prevObj.getCustomAttributes(currObj);
        for (AttributeDefinition refAttr : refAttrs) {
          if (Strings.isEmpty(current.getVariable()) ||
              refAttr.getName().equals(current.getVariable())) {
            current.setReference(createJoinCondition(
                refAttr, prevObj, prevType.getVariable(),
                currObj.getIdentifiableAttribute(), currObj, current.getVariable()));
            break;
          }
        }
      } // 正向、反向引用
    }
  }

  private boolean buildReferences(ObjectDefinition thisObj, ObjectDefinition anotherObj,
                                  TypeDefinition current, TypeDefinition prevType) {
    AttributeDefinition[] refAttrs = thisObj.getCustomAttributes(anotherObj);
    if (refAttrs.length > 0) {
      // 正向引用，可以是多个，比如主客队，或者前置、当前、后置节点等情况
      current.setReference(createJoinCondition(
          anotherObj.getIdentifiableAttribute(), anotherObj, prevType.getVariable(),
          refAttrs[0], thisObj, current.getVariable()));
      current.getReference().setLeftObjectAlias(prevType.getVariable());
      current.getReference().setRightObjectAlias(current.getVariable());
      return true;
    } else {
      // 反向引用
      refAttrs = anotherObj.getCustomAttributes(thisObj);
      for (AttributeDefinition refAttr : refAttrs) {
        if (refAttr.getName().equals(current.getVariable()) ||
            refAttr.getName().equals(current.getName())) {
          current.setReference(createJoinCondition(
              refAttr, anotherObj, prevType.getVariable(),
              thisObj.getIdentifiableAttribute(), thisObj, current.getVariable()));
          return true;
        }
      }
    }
    return false;
  }

  private JoinConditionDefinition createJoinCondition(AttributeDefinition leftAttr,
                                                      ObjectDefinition leftObj,
                                                      String leftObjAlias,
                                                      AttributeDefinition rightAttr,
                                                      ObjectDefinition rightObj,
                                                      String rightObjAlias) {
    JoinPredicateDefinition joinPredicate = new JoinPredicateDefinition();
    joinPredicate.setLeftAttribute(leftAttr);
    joinPredicate.setLeftObject(leftObj);
    joinPredicate.setLeftObjectAlias(leftObjAlias);
    joinPredicate.setRightAttribute(rightAttr);
    joinPredicate.setRightObject(rightObj);
    joinPredicate.setRightObjectAlias(rightObjAlias);
    return new JoinConditionDefinition(joinPredicate);
  }
}
