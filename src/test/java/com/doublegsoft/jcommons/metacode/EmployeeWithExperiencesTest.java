package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.CustomType;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class EmployeeWithExperiencesTest extends TestBase {

  private ModelDefinition buildDataModel() {
    ModelDefinition retVal = new ModelDefinition();

    ObjectDefinition person = createPersistentObject(retVal, "person");
    createIdentifiableAttribute(person, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(person, "name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(person, "national_id", new PrimitiveType("string"));

    ObjectDefinition employee = createPersistentObject(retVal, "employee");
    createIdentifiableAttribute(employee, "id", new CustomType("person", person));
    createAttributeWithPrimitiveType(employee, "name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(employee, "mobile", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(employee, "hire_date", new PrimitiveType("datetime"));

    ObjectDefinition experience = createPersistentObject(retVal, "experience");
    createIdentifiableAttribute(experience, "id", new CustomType("person", person));
    createAttributeWithCustomType(experience, "employee", employee);
    createAttributeWithPrimitiveType(experience, "start_date", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(experience, "end_date", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(experience, "work", new PrimitiveType("string"));

    createAttributeWithCollectionType(employee, "experiences", "experience", experience);
    return retVal;
  }

  @Test
  public void test() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition employee = dataModel.findObjectByName("employee");
    TypeDefinition type = new TypeDefinition(employee, dataModel);
    List<TypeDefinition> childTypes = type.groupPersistentTypes();
    Assert.assertEquals("employee对象group以后含有两个可持久化类型", 2, childTypes.size());

    Set<ObjectDefinition> refObjs = type.getReferencingObjects();
    Assert.assertEquals("employee对象直接引用了两个对象", 2, refObjs.size());
  }

}
