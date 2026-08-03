package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import com.doublegsoft.jcommons.metamodel.dataset.JoinConditionDefinition;
import com.doublegsoft.jcommons.metamodel.dataset.JoinPredicateDefinition;
import org.junit.Assert;
import org.junit.Test;

public class PlainlikeTest extends TestBase {

  private ModelDefinition buildDataModel() {
    ModelDefinition retVal = new ModelDefinition();

    ObjectDefinition team = createPersistentObject(retVal, "team");
    createIdentifiableAttribute(team, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(team, "name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(team, "logo", new PrimitiveType("string"));

    ObjectDefinition stadium = createPersistentObject(retVal, "stadium");
    createIdentifiableAttribute(stadium, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(stadium, "name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(stadium, "capacity", new PrimitiveType("int"));
    createAttributeWithPrimitiveType(stadium, "address", new PrimitiveType("string"));

    ObjectDefinition match = createPersistentObject(retVal, "match");
    createIdentifiableAttribute(match, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(match, "match_time", new PrimitiveType("datetime"));
    createAttributeWithCustomType(match, "stadium", stadium);
    createAttributeWithCustomType(match, "home", team);
    createAttributeWithPrimitiveType(match, "home_score", new PrimitiveType("string"));
    createAttributeWithCustomType(match, "away", team);
    createAttributeWithPrimitiveType(match, "away_score", new PrimitiveType("int"));

    ObjectDefinition matchRow = new ObjectDefinition("match_row", retVal);
    AttributeDefinition attr = createAttribute(matchRow, "match_id", new PrimitiveType("long"));
    attr.setLabelledOption("original", "object", "match");
    attr.setLabelledOption("original", "attribute", "id");

    attr = createAttribute(matchRow, "match_time", new PrimitiveType("datetime"));
    attr.setLabelledOption("original", "object", "match");
    attr.setLabelledOption("original", "attribute", "match_time");

    attr = createAttribute(matchRow, "stadium_name", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "stadium");
    attr.setLabelledOption("original", "attribute", "name");

    attr = createAttribute(matchRow, "home_team", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "team");
    attr.setLabelledOption("original", "attribute", "name");
    attr.setLabelledOption("original", "alias", "home");

    attr = createAttribute(matchRow, "home_score", new PrimitiveType("int"));
    attr.setLabelledOption("original", "object", "match");
    attr.setLabelledOption("original", "attribute", "home_score");

    attr = createAttribute(matchRow, "away_team", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "team");
    attr.setLabelledOption("original", "attribute", "name");
    attr.setLabelledOption("original", "alias", "away");

    attr = createAttribute(matchRow, "away_score", new PrimitiveType("int"));
    attr.setLabelledOption("original", "object", "match");
    attr.setLabelledOption("original", "attribute", "away_score");

    return retVal;
  }

  @Test
  public void test_match() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition match = dataModel.findObjectByName("match");
    TypeDefinition type = new TypeDefinition(match, dataModel);

    FlowDefinition flow = new FlowDefinition(type, dataModel);
    TypeDefinition[] types = flow.getTypes();
    Assert.assertEquals("在这个流程中应该有四个类型对象", 4, types.length);

    ObjectDefinition matchObj = types[0].getDefinition();
    matchObj = dataModel.findObjectByName(matchObj.getName());

    TypeDefinition home = types[2];

    ObjectDefinition teamObj = home.getDefinition();
    teamObj = dataModel.findObjectByName(teamObj.getName());

    JoinConditionDefinition joinCondition = home.getReference();
    JoinPredicateDefinition joinPredicate = joinCondition.getJoinPredicates().get(0);
    Assert.assertEquals(matchObj.getAttribute("home"), joinPredicate.getLeftAttribute());
    Assert.assertEquals(teamObj.getIdentifiableAttribute(), joinPredicate.getRightAttribute());

    TypeDefinition away = types[3];
    teamObj = away.getDefinition();
    teamObj = dataModel.findObjectByName(teamObj.getName());

    joinCondition = away.getReference();
    joinPredicate = joinCondition.getJoinPredicates().get(0);

    Assert.assertEquals(matchObj.getAttribute("away"), joinPredicate.getLeftAttribute());
    Assert.assertEquals(teamObj.getIdentifiableAttribute(), joinPredicate.getRightAttribute());
  }

  @Test
  public void test_match_row() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition matchRow = dataModel.findObjectByName("match_row");
    TypeDefinition type = new TypeDefinition(matchRow, dataModel);

    FlowDefinition flow = new FlowDefinition(type, dataModel);
    Assert.assertEquals("在这个流程中应该有四个类型对象", 4, flow.getTypes().length);

    ObjectDefinition matchObj = flow.getTypes()[0].getDefinition();
    matchObj = dataModel.findObjectByName(matchObj.getName());

    TypeDefinition home = flow.getTypes()[2];

    ObjectDefinition teamObj = home.getDefinition();
    teamObj = dataModel.findObjectByName(teamObj.getName());

    JoinConditionDefinition joinCondition = home.getReference();
    JoinPredicateDefinition joinPredicate = joinCondition.getJoinPredicates().get(0);
    Assert.assertEquals(matchObj.getAttribute("home"), joinPredicate.getLeftAttribute());
    Assert.assertEquals(teamObj.getIdentifiableAttribute(), joinPredicate.getRightAttribute());

    TypeDefinition away = flow.getTypes()[3];
    teamObj = away.getDefinition();
    teamObj = dataModel.findObjectByName(teamObj.getName());

    joinCondition = away.getReference();
    joinPredicate = joinCondition.getJoinPredicates().get(0);

    Assert.assertEquals(matchObj.getAttribute("away"), joinPredicate.getLeftAttribute());
    Assert.assertEquals(teamObj.getIdentifiableAttribute(), joinPredicate.getRightAttribute());
  }
}
