package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.CustomType;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import com.doublegsoft.jcommons.metamodel.dataset.JoinConditionDefinition;
import com.doublegsoft.jcommons.metamodel.dataset.JoinPredicateDefinition;
import org.junit.Assert;
import org.junit.Test;

public class MetableUnderlineTest extends TestBase {

  private ModelDefinition buildDataModel() {
    ModelDefinition retVal = new ModelDefinition();

    ObjectDefinition category = createPersistentObject(retVal, "category");
    createIdentifiableAttribute(category, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(category, "name", new PrimitiveType("string"));

    ObjectDefinition metable = createPersistentObject(retVal, "metable");
    createIdentifiableAttribute(metable, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(metable, "name", new PrimitiveType("string"));
    createAttributeWithCustomType(metable, "category", category);

    ObjectDefinition metableMeta = createPersistentObject(retVal, "metable_meta");
    createIdentifiableAttribute(metableMeta, "metable", new CustomType("metable", metable));
    createIdentifiableAttribute(metableMeta, "property_name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(metableMeta, "property_value", new PrimitiveType("string"));

    ObjectDefinition metableEx = createPersistentObject(retVal, "metable_");
    createAttribute(metableEx, "prop_a", new PrimitiveType("string"));
    createAttribute(metableEx, "prop_b", new PrimitiveType("string"));
    createAttribute(metableEx, "prop_c", new PrimitiveType("string"));
    metableEx.setLabelledOption("meta", "", "");
    return retVal;
  }

  @Test
  public void test() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition metableEx = dataModel.findObjectByName("metable_");
    TypeDefinition type = new TypeDefinition(metableEx, dataModel);

    FlowDefinition flow = type.getFlow();
    Assert.assertEquals("一个metable、一个category，另一个metable_meta",
        3, flow.getTypes().length);

    TypeDefinition type0 = flow.getTypes()[0];
    TypeDefinition type1 = flow.getTypes()[1];
    TypeDefinition type2 = flow.getTypes()[2];

    Assert.assertTrue("meta扩展属性是以集合对象出现", type2.isCollection());

    Assert.assertEquals("metable", type0.getName());
    Assert.assertEquals("3个自己的，3个继承而来的", 6, type0.getFields().length);
    Assert.assertEquals("id", type0.getFields()[0].getName());
    Assert.assertEquals("name", type0.getFields()[1].getName());
    Assert.assertEquals("category", type0.getFields()[2].getName());
    Assert.assertEquals("prop_a", type0.getFields()[3].getName());
    Assert.assertEquals("prop_b", type0.getFields()[4].getName());
    Assert.assertEquals("prop_c", type0.getFields()[5].getName());

    JoinConditionDefinition joinCondition = type2.getReference();
    JoinPredicateDefinition joinPredicate = joinCondition.getJoinPredicates().get(0);
    Assert.assertEquals("metable", joinPredicate.getLeftObject().getName());
    Assert.assertEquals("id", joinPredicate.getLeftAttribute().getName());
    Assert.assertEquals("metable_meta", joinPredicate.getRightObject().getName());
    Assert.assertEquals("metable", joinPredicate.getRightAttribute().getName());
  }

}
