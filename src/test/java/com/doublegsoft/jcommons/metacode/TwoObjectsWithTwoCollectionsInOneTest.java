package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.CustomType;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import com.doublegsoft.jcommons.metamodel.dataset.JoinConditionDefinition;
import com.doublegsoft.jcommons.metamodel.dataset.JoinPredicateDefinition;
import org.junit.Assert;
import org.junit.Test;

public class TwoObjectsWithTwoCollectionsInOneTest extends TestBase {

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

    ObjectDefinition work_experience = createPersistentObject(retVal, "work_experience");
    createIdentifiableAttribute(work_experience, "id", new PrimitiveType("long"));
    createAttributeWithCustomType(work_experience, "employee", employee);
    createAttributeWithPrimitiveType(work_experience, "start_date", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(work_experience, "end_date", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(work_experience, "company", new PrimitiveType("string"));

    ObjectDefinition education_experience = createPersistentObject(retVal, "education_experience");
    createIdentifiableAttribute(education_experience, "id", new PrimitiveType("long"));
    createAttributeWithCustomType(education_experience, "employee", employee);
    createAttributeWithPrimitiveType(education_experience, "start_date", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(education_experience, "end_date", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(education_experience, "school", new PrimitiveType("string"));

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

    createAttributeWithCollectionType(personAndEmployee, "work_experiences", work_experience);
    createAttributeWithCollectionType(personAndEmployee, "education_experiences", education_experience);

    return retVal;
  }

  @Test
  public void test() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition personAndEmployee = dataModel.findObjectByName("person_and_employee");
    TypeDefinition type = new TypeDefinition(personAndEmployee, dataModel);

    FlowDefinition flow = new FlowDefinition(type, dataModel);
    Assert.assertEquals("在这个流程中应该有两个类型对象", 4, flow.getTypes().length);

    TypeDefinition personType = flow.getTypes()[0];
    Assert.assertEquals("person应该有三个属性", 3, personType.getFields().length);

    TypeDefinition employeeType = flow.getTypes()[1];
    Assert.assertEquals("employee应该有两个属性", 2, employeeType.getFields().length);
    Assert.assertNotNull("employee和person的关联关系已经建立", employeeType.getReference());
    JoinConditionDefinition joinCondition = employeeType.getReference();
    JoinPredicateDefinition joinPredicate = joinCondition.getJoinPredicates().get(0);

    ObjectDefinition personObj = personType.getDefinition();
    personObj = dataModel.findObjectByName(personObj.getName());
    ObjectDefinition employeeObj = employeeType.getDefinition();
    employeeObj = dataModel.findObjectByName(employeeObj.getName());
    Assert.assertEquals("左边的属性是person的标识属性",
        personObj.getIdentifiableAttribute(), joinPredicate.getLeftAttribute());
    Assert.assertEquals("右边的属性是employee的标识属性，因为他和person是一对一的关系",
        employeeObj.getIdentifiableAttribute(), joinPredicate.getRightAttribute());

    TypeDefinition workExperienceType = flow.getTypes()[2];
    Assert.assertTrue("work_experience是个集合对象", workExperienceType.isCollection());
  }

}
