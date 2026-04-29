package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.CustomType;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class ChineseProductTest extends TestBase {

  private ModelDefinition buildDataModel() {
    ModelDefinition retVal = new ModelDefinition();

    ObjectDefinition product = createPersistentObject(retVal, "product");
    createIdentifiableAttribute(product, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(product, "name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(product, "gauge", new PrimitiveType("string"));

    ObjectDefinition productMeta = createPersistentObject(retVal, "product_meta");
    createIdentifiableAttribute(productMeta, "product", new CustomType("product", product));
    createIdentifiableAttribute(productMeta, "property_name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(productMeta, "property_value", new PrimitiveType("string"));

    ObjectDefinition chineseProduct = createPersistentObject(retVal, "chinese_product");
    chineseProduct.setLabelledOption("meta", "master", "product");

    createAttribute(chineseProduct, "产地", new PrimitiveType("string"));
    createAttribute(chineseProduct, "国标", new PrimitiveType("string"));
    createAttribute(chineseProduct, "等级", new PrimitiveType("string"));

    return retVal;
  }

  @Test
  public void test() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition employee = dataModel.findObjectByName("chinese_product");
    TypeDefinition type = new TypeDefinition(employee, dataModel);

    FlowDefinition flow = new FlowDefinition(type, dataModel);
    Assert.assertEquals("在这个流程中应该有六个类型对象", 4, flow.getTypes().length);
  }
}
