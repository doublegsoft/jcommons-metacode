package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.CustomType;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class AggregatelikeReverseTest extends TestBase {

  private ModelDefinition buildDataModel() {
    ModelDefinition retVal = new ModelDefinition();
    AttributeDefinition attr = null;

    ObjectDefinition categoryObj = createPersistentObject(retVal, "category");
    createIdentifiableAttribute(categoryObj, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(categoryObj, "name", new PrimitiveType("string"));

    ObjectDefinition personClass = createPersistentObject(retVal, "person_class");
    createIdentifiableAttribute(personClass, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(personClass, "name", new PrimitiveType("string"));

    ObjectDefinition personObj = createPersistentObject(retVal, "person");
    createIdentifiableAttribute(personObj, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(personObj, "name", new PrimitiveType("string"));
    createAttributeWithCustomType(personObj, "person_class", personClass);

    ObjectDefinition rootObj = createPersistentObject(retVal, "root");
    createIdentifiableAttribute(rootObj, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(rootObj, "name", new PrimitiveType("string"));
    createAttributeWithCustomType(rootObj, "category", categoryObj);
    createAttributeWithCustomType(rootObj, "person", personObj);

    ObjectDefinition detailObj = createPersistentObject(retVal, "detail");
    createIdentifiableAttribute(detailObj, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(detailObj, "name", new PrimitiveType("string"));
    createAttribute(detailObj, "status", new PrimitiveType("string"));
    createAttributeWithCustomType(detailObj, "root", rootObj);

    ObjectDefinition rootHolder = createPersistentObject(retVal, "root_holder");
    createIdentifiableAttribute(rootHolder, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(rootHolder, "name", new PrimitiveType("string"));
    createAttributeWithCustomType(rootHolder, "root", rootObj);

    ObjectDefinition rootOwner = createPersistentObject(retVal, "root_owner");
    createIdentifiableAttribute(rootOwner, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(rootOwner, "name", new PrimitiveType("string"));
    createAttributeWithCustomType(rootOwner, "root", rootObj);

    return retVal;
  }

  private ObjectDefinition buildAggregate(ModelDefinition dataModel) {
    ObjectDefinition retVal = new ObjectDefinition("aggregate", dataModel);
    ObjectDefinition rootObj = dataModel.findObjectByName("root");
    ObjectDefinition rootHolder = dataModel.findObjectByName("root_holder");
    ObjectDefinition rootOwner = dataModel.findObjectByName("root_owner");
    ObjectDefinition categoryObj = dataModel.findObjectByName("category");
    ObjectDefinition personObj = dataModel.findObjectByName("person");
    ObjectDefinition personClass = dataModel.findObjectByName("person_class");
    ObjectDefinition detailObj = dataModel.findObjectByName("detail");

    createAttributeWithCustomType(retVal, "root", rootObj);
    createAttributeWithCustomType(retVal, "category", categoryObj);
    createAttributeWithCustomType(retVal, "person", personObj);
    createAttributeWithCustomType(retVal, "person_class", personClass);
    createAttributeWithCustomType(retVal, "root_holder", rootHolder);
    createAttributeWithCustomType(retVal, "root_owner", rootOwner);
    createAttributeWithCollectionType(retVal, "details", "detail", detailObj);
    return retVal;
  }

  @Test
  public void test() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition aggregate = buildAggregate(dataModel);

    TypeDefinition type = new TypeDefinition(aggregate, dataModel);
    FlowDefinition flow = type.getFlow();
    TypeDefinition[] types = flow.getTypes();
    Assert.assertEquals("AGGREGATE含有七个类型对象（可持久化）",7, types.length);
    types = flow.sortTypes();
    for (TypeDefinition typeDef : types) {
      System.out.println(typeDef.getName());
    }
  }

}
