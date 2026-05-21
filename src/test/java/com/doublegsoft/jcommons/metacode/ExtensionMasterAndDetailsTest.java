package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.CustomType;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import com.doublegsoft.jcommons.metamodel.dataset.JoinConditionDefinition;
import org.junit.Assert;
import org.junit.Test;

public class ExtensionMasterAndDetailsTest extends TestBase {

  private ModelDefinition buildDataModel() {
    ModelDefinition retVal = new ModelDefinition();

    ObjectDefinition category = createPersistentObject(retVal, "category");
    createIdentifiableAttribute(category, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(category, "name", new PrimitiveType("string"));

    ObjectDefinition article = createPersistentObject(retVal, "article");
    createIdentifiableAttribute(article, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(article, "name", new PrimitiveType("string"));
    createAttributeWithCustomType(article, "category", category);

    ObjectDefinition articleEngagement = createPersistentObject(retVal, "article_engagement");
    createIdentifiableAttribute(articleEngagement, "id", new CustomType("article", article));
    createAttributeWithPrimitiveType(articleEngagement, "view_count", new PrimitiveType("int"));
    createAttributeWithPrimitiveType(articleEngagement, "forward_count", new PrimitiveType("int"));
    createAttributeWithPrimitiveType(articleEngagement, "like_count", new PrimitiveType("int"));

    ObjectDefinition positiveReview = createPersistentObject(retVal, "positive_review");
    createIdentifiableAttribute(positiveReview, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(positiveReview, "count", new PrimitiveType("int"));
    createAttributeWithPrimitiveType(positiveReview, "reference_id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(positiveReview, "reference_type", new PrimitiveType("string"));

    ObjectDefinition extension = new ObjectDefinition("article_extension", retVal);
    createAttributeWithPrimitiveType(positiveReview, "placeholder", new PrimitiveType("string"));

    extension.setLabelledOption("extension", "master", "article");
    extension.setLabelledOption("extension", "details", "article_engagement;positive_review(reference_id)");
    return retVal;
  }

  @Test
  public void test() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition extension = dataModel.findObjectByName("article_extension");
    TypeDefinition type = new TypeDefinition(extension, dataModel);

    FlowDefinition flow = type.getFlow();
    Assert.assertEquals("article, article_engagement, positive_review", 3, flow.getTypes().length);

    TypeDefinition typePositiveReview = flow.getTypes()[2];
    Assert.assertNotNull(typePositiveReview.getReference());

    JoinConditionDefinition joinCondition = typePositiveReview.getReference();
    Assert.assertEquals("article", joinCondition.getLeftObject().getName());
    Assert.assertEquals("positive_review", joinCondition.getRightObject().getName());
  }
}
