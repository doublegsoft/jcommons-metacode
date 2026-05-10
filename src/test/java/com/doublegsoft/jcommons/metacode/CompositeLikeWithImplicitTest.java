package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import org.junit.Assert;
import org.junit.Test;

public class CompositeLikeWithImplicitTest extends TestBase {

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

    ObjectDefinition simpleEx = new ObjectDefinition("simple_", retVal);
    attr = createAttribute(simpleEx, "reference_type", new PrimitiveType("string"));
    attr.getConstraint().setDefaultValue("SIMPLE");
    attr = createAttribute(simpleEx, "status", new PrimitiveType("string"));
    attr.getConstraint().setDefaultValue("YEP");
    return retVal;
  }

  private ObjectDefinition buildComposite(ModelDefinition dataModel) {
    ObjectDefinition retVal = new ObjectDefinition("composite", dataModel);
    AttributeDefinition attr = createAttribute(retVal, "simple_id", new PrimitiveType("long"));
    attr.setLabelledOption("original", "object", "simple");
    attr.setLabelledOption("original", "attribute", "id");
    attr = createAttribute(retVal, "simple_name", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "simple");
    attr.setLabelledOption("original", "attribute", "name");
    attr = createAttribute(retVal, "category_name", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "category");
    attr.setLabelledOption("original", "attribute", "name");

    attr = createAttribute(retVal, "another_id", new PrimitiveType("long"));
    attr.setLabelledOption("original", "object", "another");
    attr.setLabelledOption("original", "attribute", "id");
    attr.setLabelledOption("conjunction", "source_object", "simple");
    attr.setLabelledOption("conjunction", "source_attribute", "id");
    attr.setLabelledOption("conjunction", "target_object", "another");
    attr.setLabelledOption("conjunction", "target_attribute", "reference_id");
    attr = createAttribute(retVal, "another_name", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "another");
    attr.setLabelledOption("original", "attribute", "name");
    attr.setLabelledOption("conjunction", "source_object", "simple");
    attr.setLabelledOption("conjunction", "source_attribute", "id");
    attr.setLabelledOption("conjunction", "target_object", "another");
    attr.setLabelledOption("conjunction", "target_attribute", "reference_id");
    return retVal;
  }

  @Test
  public void test() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition composite = buildComposite(dataModel);

    TypeDefinition type = new TypeDefinition(composite, dataModel);
    FlowDefinition flow = type.getFlow();
    TypeDefinition[] types = flow.getTypes();
    Assert.assertEquals("composite含有三个类型对象（可持久化）",3, types.length);

    Assert.assertEquals("composite", flow.getRoot().getName());
    Assert.assertEquals("simple", types[0].getName());
    Assert.assertEquals("category", types[1].getName());
    Assert.assertEquals("another", types[2].getName());
  }

}
