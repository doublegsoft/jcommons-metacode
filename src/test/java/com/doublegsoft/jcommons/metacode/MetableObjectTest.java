package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.CustomType;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import org.junit.Assert;
import org.junit.Test;

public class MetableObjectTest extends TestBase {

  private ModelDefinition buildDataModel() {
    ModelDefinition retVal = new ModelDefinition();

    ObjectDefinition metable = createPersistentObject(retVal, "metable");
    createIdentifiableAttribute(metable, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(metable, "name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(metable, "gauge", new PrimitiveType("string"));

    ObjectDefinition metableMeta = createPersistentObject(retVal, "metable_meta");
    createIdentifiableAttribute(metableMeta, "metable", new CustomType("metable", metable));
    createIdentifiableAttribute(metableMeta, "property_name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(metableMeta, "property_value", new PrimitiveType("string"));

    ObjectDefinition metableEx = createPersistentObject(retVal, "metable_");
    createAttributeWithPrimitiveType(metableEx, "prop_a", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(metableEx, "prop_b", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(metableEx, "prop_c", new PrimitiveType("string"));
    metableEx.setLabelledOption("meta", "master", "metable");
    metableEx.setLabelledOption("meta", "detail", "metable_meta");
    return retVal;
  }

  @Test
  public void test() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition metableEx = dataModel.findObjectByName("metable_");
    TypeDefinition type = new TypeDefinition(metableEx, dataModel);

    FlowDefinition flow = new FlowDefinition(type, dataModel);
    Assert.assertEquals("在这个流程中应该有两个类型对象", 3, flow.getTypes().length);

//    TypeDefinition type0 = flow.getTypes()[0];
//    TypeDefinition type1 = flow.getTypes()[1];
//    TypeDefinition type2 = flow.getTypes()[2];
//
//    Assert.assertEquals("another", type0.getName());
//    Assert.assertEquals(4, type0.getFields().length);
//    Assert.assertEquals(1, type0.getReferencingObjects().size());
//
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
