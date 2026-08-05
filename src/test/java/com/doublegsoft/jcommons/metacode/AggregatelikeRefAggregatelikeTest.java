package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.CollectionType;
import com.doublegsoft.jcommons.metabean.type.CustomType;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import org.junit.Assert;
import org.junit.Test;

public class AggregatelikeRefAggregatelikeTest extends TestBase {

  private ModelDefinition buildDataModel() {
    ModelDefinition retVal = new ModelDefinition();
    AttributeDefinition attr = null;

    ObjectDefinition category = createPersistentObject(retVal, "category");
    createIdentifiableAttribute(category, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(category, "name", new PrimitiveType("string"));

    ObjectDefinition simple = createPersistentObject(retVal, "simple");
    createIdentifiableAttribute(simple, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(simple, "name", new PrimitiveType("string"));
    createAttributeWithCustomType(simple, "category", category);

    ObjectDefinition another = createPersistentObject(retVal, "another");
    createIdentifiableAttribute(another, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(another, "name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(another, "reference_id", new PrimitiveType("string"));
    attr = another.getAttribute("reference_id");
    attr.setLabelledOption("reference", "value", "id");
    createAttributeWithPrimitiveType(another, "reference_type", new PrimitiveType("string"));
    attr = another.getAttribute("reference_type");
    attr.setLabelledOption("reference", "value", "type");
    createAttribute(another, "status", new PrimitiveType("string"));

    ObjectDefinition fourth = createPersistentObject(retVal, "fourth");
    createIdentifiableAttribute(fourth, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(fourth, "name", new PrimitiveType("string"));
    createAttributeWithCustomType(fourth, "simple", simple);

    ObjectDefinition fifth = createPersistentObject(retVal, "fifth");
    createIdentifiableAttribute(fifth, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(fifth, "name", new PrimitiveType("string"));
    createAttributeWithCustomType(fifth, "category", category);

    return retVal;
  }

  private ObjectDefinition buildAggregate(ModelDefinition dataModel) {
    ObjectDefinition retVal = new ObjectDefinition("aggregate", dataModel);
    ObjectDefinition simple = dataModel.findObjectByName("simple");
    ObjectDefinition category = dataModel.findObjectByName("category");
    ObjectDefinition another = dataModel.findObjectByName("another");

    AttributeDefinition attr = createAttribute(retVal, "simple", new CustomType("simple", simple));
    attr = createAttribute(retVal, "category", new CustomType("category", category));
    attr = createAttribute(retVal, "another", new CustomType("another", another));
    attr.setLabelledOption("conjunction", "source_object", "simple");
    attr.setLabelledOption("conjunction", "source_attribute", "id");
    attr.setLabelledOption("conjunction", "target_object", "another");
    attr.setLabelledOption("conjunction", "target_attribute", "reference_id");

    ObjectDefinition aggregateAnother = dataModel.findObjectByName("aggregate_another");
    CollectionType collType = new CollectionType("");
    collType.setComponentType(aggregateAnother);
    createAttribute(retVal, "anothers", collType);
    return retVal;
  }

  private ObjectDefinition buildAggregateAnother(ModelDefinition dataModel) {
    ObjectDefinition retVal = new ObjectDefinition("aggregate_another", dataModel);
    ObjectDefinition fourth = dataModel.findObjectByName("fourth");
    ObjectDefinition fifth = dataModel.findObjectByName("fifth");
    createAttributeWithCustomType(retVal, "fourth", fourth);
    createAttributeWithCustomType(retVal, "fifth", fifth);
    return retVal;
  }

  @Test
  public void test() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition aggregateAnother = buildAggregateAnother(dataModel);
    ObjectDefinition aggregate = buildAggregate(dataModel);

    TypeDefinition type = new TypeDefinition(aggregate, dataModel);
    FlowDefinition flow = type.getFlow();
    TypeDefinition[] types = flow.getTypes();
//    Assert.assertEquals("composite含有三个类型对象（可持久化）+ 一个aggregate集合",4, types.length);
//
//    TypeDefinition simpleType = types[0];
//    TypeDefinition categoryType = types[1];
//    TypeDefinition anotherType = types[2];
//    TypeDefinition aggregateAnotherType = types[3];
//
//    Assert.assertEquals("aggregate", flow.getRoot().getName());
//    Assert.assertEquals("simple", simpleType.getName());
//    Assert.assertEquals("category", categoryType.getName());
//    Assert.assertEquals("another", anotherType.getName());
//    Assert.assertEquals("aggregate_another", aggregateAnotherType.getName());
//
//    Assert.assertNull("", simpleType.getReference());
//    Assert.assertNotNull("category存在关联对象，因为被直接引用", categoryType.getReference());
//    Assert.assertNotNull("another存在关联对象，定义了conjunction", anotherType.getReference());
  }

}
