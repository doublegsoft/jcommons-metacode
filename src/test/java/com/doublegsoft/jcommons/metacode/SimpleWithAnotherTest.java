package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.CustomType;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import org.junit.Assert;
import org.junit.Test;

public class SimpleWithAnotherTest extends TestBase {

  private ModelDefinition buildDataModel() {
    ModelDefinition retVal = new ModelDefinition();

    ObjectDefinition simple = createPersistentObject(retVal, "simple");
    createIdentifiableAttribute(simple, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(simple, "name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(simple, "gauge", new PrimitiveType("string"));

    ObjectDefinition another = createPersistentObject(retVal, "another");
    createIdentifiableAttribute(another, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(another, "name", new PrimitiveType("string"));
    createAttributeWithCustomType(another, "simple1", simple);
    createAttributeWithCustomType(another, "simple2", simple);

    return retVal;
  }

  @Test
  public void test() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition employee = dataModel.findObjectByName("another");
    TypeDefinition type = new TypeDefinition(employee, dataModel);

    FlowDefinition flow = new FlowDefinition(type, dataModel);
    Assert.assertEquals("在这个流程中应该有两个类型对象", 3, flow.getTypes().length);

    TypeDefinition type0 = flow.getTypes()[0];
    TypeDefinition type1 = flow.getTypes()[1];
    TypeDefinition type2 = flow.getTypes()[2];

    Assert.assertEquals("another", type0.getName());
    Assert.assertEquals(4, type0.getFields().length);
    Assert.assertEquals(1, type0.getReferencingObjects().size());

    Assert.assertEquals("simple", type1.getName());
    Assert.assertEquals("simple1", type1.getVariable());
    // 无须关心他的内部属性
    Assert.assertEquals(0, type1.getFields().length);
    Assert.assertEquals(0, type1.getReferencingObjects().size());

    Assert.assertEquals("simple", type2.getName());
    Assert.assertEquals("simple2", type2.getVariable());
  }

}
