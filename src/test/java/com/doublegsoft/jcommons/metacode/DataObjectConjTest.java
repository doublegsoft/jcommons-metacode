package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import org.junit.Assert;
import org.junit.Test;

public class DataObjectConjTest extends TestBase {

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

    ObjectDefinition conj = createPersistentObject(retVal, "conj");
    createIdentifiableAttribute(conj, "simple", simple);
    createIdentifiableAttribute(conj, "another", another);



    AttributeDefinition collAttr = createAttributeWithCollectionType(simple, "anothers", "another", another);
    collAttr.setLabelledOption("conjunction", "object", "conj");

    return retVal;
  }

  @Test
  public void test() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition simple = dataModel.findObjectByName("simple");
    TypeDefinition type = new TypeDefinition(simple, dataModel);
    FlowDefinition flow = type.getFlow();
    TypeDefinition[] types = flow.getTypes();
    Assert.assertEquals("composite含有三个类型对象（可持久化）",4, types.length);

    TypeDefinition conjType = types[2];
    TypeDefinition anotherType = types[3];

    Assert.assertTrue(conjType.isCollection());
    Assert.assertTrue(anotherType.isCollection());
  }

}
