package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import org.junit.Assert;
import org.junit.Test;

public class ExtensionMasterTest extends TestBase {

  private ModelDefinition buildDataModel() {
    ModelDefinition retVal = new ModelDefinition();

    ObjectDefinition category = createPersistentObject(retVal, "category");
    createIdentifiableAttribute(category, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(category, "name", new PrimitiveType("string"));

    ObjectDefinition user = createPersistentObject(retVal, "user");
    createIdentifiableAttribute(user, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(user, "name", new PrimitiveType("string"));
    createAttributeWithCustomType(user, "category", category);

    ObjectDefinition onlineUser = new ObjectDefinition("online_user", retVal);
    createAttribute(onlineUser, "token", new PrimitiveType("string"));
    createAttribute(onlineUser, "login_time", new PrimitiveType("datetime"));
    onlineUser.setLabelledOption("extension", "master", "user");
    return retVal;
  }

  @Test
  public void test() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition onlineUser = dataModel.findObjectByName("online_user");
    TypeDefinition type = new TypeDefinition(onlineUser, dataModel);

    FlowDefinition flow = type.getFlow();
    Assert.assertEquals("一个user",
        1, flow.getTypes().length);

    TypeDefinition type0 = flow.getTypes()[0];

    Assert.assertEquals("AREF", type.getReferenceType(type0));

    Assert.assertEquals("user", type0.getName());
    Assert.assertEquals("2个自己的，3个继承而来的", 5, type.getFields().length);
    Assert.assertEquals("token", type.getFields()[0].getName());
    Assert.assertEquals("login_time", type.getFields()[1].getName());
    Assert.assertEquals("id", type.getFields()[2].getName());
    Assert.assertEquals("name", type.getFields()[3].getName());
    Assert.assertEquals("category", type.getFields()[4].getName());
  }

}
