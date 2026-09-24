package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import com.doublegsoft.jcommons.metamodel.dataset.JoinConditionDefinition;
import com.doublegsoft.jcommons.metamodel.dataset.JoinPredicateDefinition;
import org.junit.Assert;
import org.junit.Test;

public class PlainlikeIndirectTest extends TestBase {

  /**
   * 构建认知资产元数据模型
   */
  private ModelDefinition buildDataModel() {
    ModelDefinition retVal = new ModelDefinition();

    // =========================================================================
    // 1. third_object: 知识入口 (tn_cca_cognent)
    // =========================================================================
    ObjectDefinition thirdObject = createPersistentObject(retVal, "third_object");
    createIdentifiableAttribute(thirdObject, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(thirdObject, "name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(thirdObject, "type", new PrimitiveType("string")); // enum[OBJ,SCN,ERR,CAP]
    createAttributeWithPrimitiveType(thirdObject, "title", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(thirdObject, "description", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(thirdObject, "status", new PrimitiveType("string")); // enum[AC,AR]
    createAttributeWithPrimitiveType(thirdObject, "created_time", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(thirdObject, "updated_time", new PrimitiveType("datetime"));

    // =========================================================================
    // 2. second_object: 标签 (tn_cogntg) -> 关联 third_object
    // =========================================================================
    ObjectDefinition secondObject = createPersistentObject(retVal, "second_object");
    createIdentifiableAttribute(secondObject, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(secondObject, "name", new PrimitiveType("string"));
    createAttributeWithCustomType(secondObject, "third_object", thirdObject); // &third_object(id)
    createAttributeWithPrimitiveType(secondObject, "description", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(secondObject, "created_time", new PrimitiveType("datetime"));

    // =========================================================================
    // 3. fourth_object: 概念 (tn_cca_cogncep) -> 关联 third_object
    // =========================================================================
    ObjectDefinition fourthObject = createPersistentObject(retVal, "fourth_object");
    createIdentifiableAttribute(fourthObject, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(fourthObject, "name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(fourthObject, "title", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(fourthObject, "definition", new PrimitiveType("string"));
    createAttributeWithCustomType(fourthObject, "third_object", thirdObject); // &third_object(id)
    createAttributeWithPrimitiveType(fourthObject, "stability", new PrimitiveType("string")); // enum[LW,MD,HG]
    createAttributeWithPrimitiveType(fourthObject, "created_time", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(fourthObject, "updated_time", new PrimitiveType("datetime"));

    // =========================================================================
    // 4. first_object: 资产 (tn_cca_cognasst) -> 关联 second_object
    // =========================================================================
    ObjectDefinition firstObject = createPersistentObject(retVal, "first_object");
    createIdentifiableAttribute(firstObject, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(firstObject, "name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(firstObject, "type", new PrimitiveType("string")); // enum[FL,VD,IM,CD,DC,LN]
    createAttributeWithPrimitiveType(firstObject, "title", new PrimitiveType("string"));
    createAttributeWithCustomType(firstObject, "second_object", secondObject); // &second_object(id)
    createAttributeWithPrimitiveType(firstObject, "location", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(firstObject, "description", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(firstObject, "hash", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(firstObject, "size", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(firstObject, "mime", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(firstObject, "duration", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(firstObject, "created_time", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(firstObject, "updated_time", new PrimitiveType("datetime"));

    // =========================================================================
    // 5. fifth_object: 主题 (tn_cca_cogntop)
    // =========================================================================
    ObjectDefinition fifthObject = createPersistentObject(retVal, "fifth_object");
    createIdentifiableAttribute(fifthObject, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(fifthObject, "name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(fifthObject, "title", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(fifthObject, "summary", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(fifthObject, "abstraction_level", new PrimitiveType("string")); // enum[LW,MD,HG]
    createAttributeWithPrimitiveType(fifthObject, "created_time", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(fifthObject, "updated_time", new PrimitiveType("datetime"));

    // =========================================================================
    // 6. plainlike_object: 复合平铺对象 (@composite)
    // =========================================================================
    ObjectDefinition plainlikeObject = new ObjectDefinition("plainlike_object", retVal);
    AttributeDefinition attr;

    // 映射 first_object 属性
    attr = createAttribute(plainlikeObject, "first_object_id", new PrimitiveType("long"));
    attr.setLabelledOption("original", "object", "first_object");
    attr.setLabelledOption("original", "attribute", "id");

    attr = createAttribute(plainlikeObject, "first_object_name", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "first_object");
    attr.setLabelledOption("original", "attribute", "name");

    // 映射 second_object 属性
    attr = createAttribute(plainlikeObject, "second_object_id", new PrimitiveType("long"));
    attr.setLabelledOption("original", "object", "second_object");
    attr.setLabelledOption("original", "attribute", "id");

    attr = createAttribute(plainlikeObject, "second_object_name", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "second_object");
    attr.setLabelledOption("original", "attribute", "name");

    // 映射 third_object 属性
    attr = createAttribute(plainlikeObject, "third_object_id", new PrimitiveType("long"));
    attr.setLabelledOption("original", "object", "third_object");
    attr.setLabelledOption("original", "attribute", "id");

    attr = createAttribute(plainlikeObject, "third_object_name", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "third_object");
    attr.setLabelledOption("original", "attribute", "name");

    // 映射 fourth_object 属性
    attr = createAttribute(plainlikeObject, "fourth_object_id", new PrimitiveType("long"));
    attr.setLabelledOption("original", "object", "fourth_object");
    attr.setLabelledOption("original", "attribute", "id");

    attr = createAttribute(plainlikeObject, "fourth_object_name", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "fourth_object");
    attr.setLabelledOption("original", "attribute", "name");

    return retVal;
  }

//  @Test
//  public void test_first_object() throws Exception {
//    ModelDefinition dataModel = buildDataModel();
//    ObjectDefinition firstObj = dataModel.findObjectByName("first_object");
//    TypeDefinition type = new TypeDefinition(firstObj, dataModel);
//
//    FlowDefinition flow = new FlowDefinition(type, dataModel);
//    TypeDefinition[] types = flow.getTypes();
//
//    // first_object -> second_object -> third_object <- fourth_object
//    Assert.assertEquals("在关联流中应有4个类型对象", 4, types.length);
//
//    ObjectDefinition rootObj = dataModel.findObjectByName(((ObjectDefinition)types[0].getDefinition()).getName());
//    TypeDefinition secondType = types[1];
//    ObjectDefinition secondObj = dataModel.findObjectByName(((ObjectDefinition)secondType.getDefinition()).getName());
//
//    JoinConditionDefinition joinCondition = secondType.getReference();
//    JoinPredicateDefinition joinPredicate = joinCondition.getJoinPredicates().get(0);
//    Assert.assertEquals(rootObj.getAttribute("second_object"), joinPredicate.getLeftAttribute());
//    Assert.assertEquals(secondObj.getIdentifiableAttribute(), joinPredicate.getRightAttribute());
//  }

  @Test
  public void test_plainlike_object() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition plainlikeObj = dataModel.findObjectByName("plainlike_object");
    TypeDefinition type = new TypeDefinition(plainlikeObj, dataModel);

    FlowDefinition flow = new FlowDefinition(type, dataModel);
    TypeDefinition[] types = flow.getTypes();

    // 覆盖了 first_object, second_object, third_object, fourth_object 4个对象
    Assert.assertEquals("平铺对象中应解析出4个关联类型对象", 4, types.length);

    TypeDefinition fourthObjType = types[3];
    Assert.assertEquals("fourth_object", fourthObjType.getName());
    Assert.assertNotNull(fourthObjType.getReference());

    JoinConditionDefinition fourthObjRef = fourthObjType.getReference();
    JoinPredicateDefinition fourthObjPred = fourthObjRef.getJoinPredicates().get(0);
    System.out.println(fourthObjPred.getLeftObject().getName());
    System.out.println(fourthObjPred.getRightObject().getName());

    ObjectDefinition firstObj = dataModel.findObjectByName("first_object");
    ObjectDefinition secondObj = dataModel.findObjectByName("second_object");

    // 验证 first_object 与 second_object 之间的关联
    TypeDefinition secondType = types[1];
    JoinConditionDefinition join1 = secondType.getReference();
    JoinPredicateDefinition pred1 = join1.getJoinPredicates().get(0);
    Assert.assertEquals(firstObj.getAttribute("second_object"), pred1.getLeftAttribute());
    Assert.assertEquals(secondObj.getIdentifiableAttribute(), pred1.getRightAttribute());
  }
}
