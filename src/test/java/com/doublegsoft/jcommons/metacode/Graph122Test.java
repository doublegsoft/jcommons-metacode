package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import org.junit.Assert;
import org.junit.Test;

/**
 *           [ Root ]
 *           /      \
 *      [ Node_A ]  [ Node_B ]
 *      /      \      /      \
 *   [A1]     [A2]  [B1]     [B2]
 */
public class Graph122Test extends TestBase {

  private ModelDefinition buildDataModel() {
    ModelDefinition retVal = new ModelDefinition();
    AttributeDefinition attr = null;

    ObjectDefinition root = createPersistentObject(retVal, "root");
    createIdentifiableAttribute(root, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(root, "name", new PrimitiveType("string"));

    ObjectDefinition nodeA = createPersistentObject(retVal, "node_a");
    createIdentifiableAttribute(nodeA, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(nodeA, "name", new PrimitiveType("string"));
    createAttributeWithCustomType(nodeA, "root", root);

    ObjectDefinition nodeB = createPersistentObject(retVal, "node_b");
    createIdentifiableAttribute(nodeB, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(nodeB, "name", new PrimitiveType("string"));
    createAttributeWithCustomType(nodeB, "root", root);

    ObjectDefinition a1 = createPersistentObject(retVal, "a1");
    createIdentifiableAttribute(a1, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(a1, "name", new PrimitiveType("string"));
    createAttributeWithCustomType(a1, "node_a", nodeA);

    ObjectDefinition a2 = createPersistentObject(retVal, "a2");
    createIdentifiableAttribute(a2, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(a2, "name", new PrimitiveType("string"));
    createAttributeWithCustomType(a2, "node_a", nodeA);

    ObjectDefinition b1 = createPersistentObject(retVal, "b1");
    createIdentifiableAttribute(b1, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(b1, "name", new PrimitiveType("string"));
    createAttributeWithCustomType(b1, "node_b", nodeB);

    ObjectDefinition b2 = createPersistentObject(retVal, "b2");
    createIdentifiableAttribute(b2, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(b2, "name", new PrimitiveType("string"));
    createAttributeWithCustomType(b2, "node_b", nodeB);

    return retVal;
  }

  private ObjectDefinition buildAggregate(ModelDefinition dataModel) {
    ObjectDefinition retVal = new ObjectDefinition("aggregate", dataModel);
    ObjectDefinition root = dataModel.findObjectByName("root");
    ObjectDefinition nodeA = dataModel.findObjectByName("node_a");
    ObjectDefinition nodeB = dataModel.findObjectByName("node_b");
    ObjectDefinition a1 = dataModel.findObjectByName("a1");
    ObjectDefinition a2 = dataModel.findObjectByName("a2");
    ObjectDefinition b1 = dataModel.findObjectByName("b1");
    ObjectDefinition b2 = dataModel.findObjectByName("b2");

    createAttributeWithCustomType(retVal, "root", root);
    createAttributeWithCustomType(retVal, "node_a", nodeA);
    createAttributeWithCustomType(retVal, "node_b", nodeB);
    createAttributeWithCustomType(retVal, "a1", a1);
    createAttributeWithCustomType(retVal, "a2", a2);
    createAttributeWithCustomType(retVal, "b1", b1);
    createAttributeWithCustomType(retVal, "b2", b2);
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
      System.out.println(typeDef.getName() + "包含子节点");
      for (TypeDefinition child : flow.getChildren(typeDef)) {
        System.out.println("  " + child.getName());
      }
    }
  }

}
