package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import org.junit.Assert;
import org.junit.Test;

public class SimpleWithExplicitCollectionsTest extends TestBase {

  private ModelDefinition buildDataModel() {
    ModelDefinition retVal = new ModelDefinition();

    ObjectDefinition simple = createPersistentObject(retVal, "simple");
    createIdentifiableAttribute(simple, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(simple, "name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(simple, "gauge", new PrimitiveType("string"));

    ObjectDefinition first = createPersistentObject(retVal, "first");
    createIdentifiableAttribute(first, "id", new PrimitiveType("long"));
    createAttributeWithCustomType(first, "simple", simple);
    createAttributeWithPrimitiveType(first, "name", new PrimitiveType("string"));

    ObjectDefinition second = createPersistentObject(retVal, "second");
    createIdentifiableAttribute(second, "id", new PrimitiveType("long"));
    createAttributeWithCustomType(second, "simple", simple);
    createAttributeWithPrimitiveType(second, "name", new PrimitiveType("string"));

    createAttributeWithCollectionType(simple, "first_objs", "first_obj", first);
    createAttributeWithCollectionType(simple, "second_objs", "second_obj", second);
    return retVal;
  }

  @Test
  public void test() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition simple = dataModel.findObjectByName("simple");
    TypeDefinition type = new TypeDefinition(simple, dataModel);

    FlowDefinition flow = new FlowDefinition(type, dataModel);
    Assert.assertEquals("在这个流程中应该有两个类型对象", 3, flow.getTypes().length);

    TypeDefinition type0 = flow.getTypes()[0];
    TypeDefinition type1 = flow.getTypes()[1];
    TypeDefinition type2 = flow.getTypes()[2];

    Assert.assertEquals("simple", type0.getName());
    Assert.assertEquals(5, type0.getFields().length);
    Assert.assertEquals(2, type0.getReferencingObjects().size());

//    Assert.assertEquals("simple", type1.getName());
//    Assert.assertEquals("simple1", type1.getVariable());
//    // 无须关心他的内部属性
//    Assert.assertEquals(0, type1.getFields().length);
//    Assert.assertEquals(0, type1.getReferencingObjects().size());
//
//    Assert.assertEquals("simple", type2.getName());
//    Assert.assertEquals("simple2", type2.getVariable());
  }

}
