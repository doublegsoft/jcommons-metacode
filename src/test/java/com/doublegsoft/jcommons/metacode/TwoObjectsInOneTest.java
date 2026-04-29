package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.CustomType;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class TwoObjectsInOneTest extends TestBase {

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

    ObjectDefinition personAndEmployee = new ObjectDefinition("person_and_employee", retVal);
    AttributeDefinition attr = createAttribute(personAndEmployee, "person_id", new PrimitiveType("long"));
    attr.setLabelledOption("original", "object", "person");
    attr.setLabelledOption("original", "attribute", "id");

    attr = createAttribute(personAndEmployee, "person_name", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "person");
    attr.setLabelledOption("original", "attribute", "name");

    attr = createAttribute(personAndEmployee, "national_id", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "person");
    attr.setLabelledOption("original", "attribute", "national_id");

    attr = createAttribute(personAndEmployee, "mobile", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "employee");
    attr.setLabelledOption("original", "attribute", "mobile");

    attr = createAttribute(personAndEmployee, "hire_date", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "employee");
    attr.setLabelledOption("original", "attribute", "hire_date");

    return retVal;
  }

  @Test
  public void test() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition personAndEmployee = dataModel.findObjectByName("person_and_employee");
    TypeDefinition type = new TypeDefinition(personAndEmployee, dataModel);

    FlowDefinition flow = new FlowDefinition(type, dataModel);
    Assert.assertEquals("在这个流程中应该有两个类型对象", 2, flow.getTypes().length);

    TypeDefinition personType = flow.getTypes()[0];
    Assert.assertEquals("person应该有三个属性", 3, personType.getFields().length);

    TypeDefinition employeeType = flow.getTypes()[1];
    Assert.assertEquals("employee应该有两个属性", 2, employeeType.getFields().length);
  }

}
