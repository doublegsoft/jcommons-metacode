package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.CustomType;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import org.junit.Assert;
import org.junit.Test;

public class AggregateLikeTest extends TestBase {
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

    return retVal;
  }

  @Test
  public void test() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition aggregate = buildAggregate(dataModel);

    TypeDefinition type = new TypeDefinition(aggregate, dataModel);
    FlowDefinition flow = type.getFlow();
    TypeDefinition[] types = flow.getTypes();
    Assert.assertEquals("composite含有三个类型对象（可持久化）",3, types.length);

    TypeDefinition simpleType = types[0];
    TypeDefinition categoryType = types[1];
    TypeDefinition anotherType = types[2];

    Assert.assertEquals("aggregate", flow.getRoot().getName());
    Assert.assertEquals("simple", simpleType.getName());
    Assert.assertEquals("category", categoryType.getName());
    Assert.assertEquals("another", anotherType.getName());

    Assert.assertNull("", simpleType.getReference());
    Assert.assertNotNull("category存在关联对象，因为被直接引用", categoryType.getReference());
    Assert.assertNotNull("another存在关联对象，定义了conjunction", anotherType.getReference());
  }
}
