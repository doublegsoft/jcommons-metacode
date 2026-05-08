package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.CustomType;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

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
    createAttribute(metableEx, "prop_a", new PrimitiveType("string"));
    createAttribute(metableEx, "prop_b", new PrimitiveType("string"));
    createAttribute(metableEx, "prop_c", new PrimitiveType("string"));
    metableEx.setLabelledOption("meta", "", "");
//    metableEx.setLabelledOption("meta", "master", "metable");
//    metableEx.setLabelledOption("meta", "detail", "metable_meta");
    return retVal;
  }

  @Test
  public void test_meta_underline() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition metableEx = dataModel.findObjectByName("metable_");
    TypeDefinition type = new TypeDefinition(metableEx, dataModel);

    FlowDefinition flow = type.getFlow();
    Assert.assertEquals("一个metable、三个metable_meta",
        4, flow.getTypes().length);

    TypeDefinition type0 = flow.getTypes()[0];
    TypeDefinition type1 = flow.getTypes()[1];
    TypeDefinition type2 = flow.getTypes()[2];
    TypeDefinition type3 = flow.getTypes()[3];

    Assert.assertEquals("metable", type0.getName());
    Assert.assertEquals("3个自己的，3个继承而来的", 6, type0.getFields().length);
    Assert.assertEquals("id", type0.getFields()[0].getName());
    Assert.assertEquals("name", type0.getFields()[1].getName());
    Assert.assertEquals("gauge", type0.getFields()[2].getName());
    Assert.assertEquals("prop_a", type0.getFields()[3].getName());
    Assert.assertEquals("prop_b", type0.getFields()[4].getName());
    Assert.assertEquals("prop_c", type0.getFields()[5].getName());

//    Assert.assertEquals("simple", type1.getName());
//    Assert.assertEquals("simple1", type1.getVariable());
//    // 无须关心他的内部属性
//    Assert.assertEquals(0, type1.getFields().length);
//    Assert.assertEquals(0, type1.getReferencingObjects().size());
//
//    Assert.assertEquals("simple", type2.getName());
//    Assert.assertEquals("simple2", type2.getVariable());
  }

  @Test
  public void test_meta_standalone() throws IOException {

  }

}
