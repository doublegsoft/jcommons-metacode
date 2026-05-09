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

public class CompleteBloodCountTest extends TestBase {

  private ModelDefinition buildDataModel() {
    ModelDefinition retVal = new ModelDefinition();

    ObjectDefinition examinationReport = createPersistentObject(retVal, "examination_report");
    createIdentifiableAttribute(examinationReport, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(examinationReport, "name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(examinationReport, "create_time", new PrimitiveType("datetime"));

    ObjectDefinition examinationResult = createPersistentObject(retVal, "examination_result");
    createIdentifiableAttribute(examinationResult, "id", new PrimitiveType("long"));
    createAttributeWithCustomType(examinationResult, "examination_report", examinationReport);
    createAttributeWithPrimitiveType(examinationResult, "name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(examinationResult, "value", new PrimitiveType("string"));

    ObjectDefinition completeBloodCount = new ObjectDefinition("complete_blood_count", retVal);
    completeBloodCount.setLabelledOption("pivot", "master", "examination_report");
    completeBloodCount.setLabelledOption("pivot", "detail", "examination_result");
    completeBloodCount.setLabelledOption("pivot", "key", "name");
    completeBloodCount.setLabelledOption("pivot", "value", "value");

    createAttribute(completeBloodCount, "wbc", new PrimitiveType("string"));
    createAttribute(completeBloodCount, "rbc", new PrimitiveType("string"));
    createAttribute(completeBloodCount, "hemoglobin", new PrimitiveType("string"));
    createAttribute(completeBloodCount, "platelets", new PrimitiveType("string"));
    createAttribute(completeBloodCount, "hematocrit", new PrimitiveType("string"));
    return retVal;
  }

  @Test
  public void test() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition completeBloodCount = dataModel.findObjectByName("complete_blood_count");
    TypeDefinition type = new TypeDefinition(completeBloodCount, dataModel);
    Set<ObjectDefinition> refObjs = type.getReferencingObjects();
    Assert.assertEquals("complete_blood_count对象直接引用了两个对象", 2, refObjs.size());

    FlowDefinition flow = new FlowDefinition(type, dataModel);
    Assert.assertEquals("在这个流程中应该有六个类型对象", 2, flow.getTypes().length);
  }

}
