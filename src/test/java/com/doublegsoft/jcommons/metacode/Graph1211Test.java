package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import org.junit.Assert;
import org.junit.Test;

/**
 *           [ Root ]            <-- 第一层 (1个)
 *          /      \
 *     [ Node_A ]  [ Node_B ]   <-- 第二层 (2个)
 *                    |
 *                 [ Node_C ]   <-- 第三层 (1个，位于右侧分支下)
 *                    |
 *                 [ Node_D ]   <-- 第四层 (1个)
 */
public class Graph1211Test extends TestBase {

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

    ObjectDefinition  nodeC = createPersistentObject(retVal, "node_c");
    createIdentifiableAttribute(nodeC, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(nodeC, "name", new PrimitiveType("string"));
    createAttributeWithCustomType(nodeC, "node_a", nodeB);

    ObjectDefinition nodeD = createPersistentObject(retVal, "node_d");
    createIdentifiableAttribute(nodeD, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(nodeD, "name", new PrimitiveType("string"));
    createAttributeWithCustomType(nodeD, "node_c", nodeC);

    return retVal;
  }

  private ObjectDefinition buildAggregate(ModelDefinition dataModel) {
    ObjectDefinition retVal = new ObjectDefinition("aggregate", dataModel);
    ObjectDefinition root = dataModel.findObjectByName("root");
    ObjectDefinition nodeA = dataModel.findObjectByName("node_a");
    ObjectDefinition nodeB = dataModel.findObjectByName("node_b");
    ObjectDefinition nodeC = dataModel.findObjectByName("node_c");
    ObjectDefinition nodeD = dataModel.findObjectByName("node_d");

    createAttributeWithCustomType(retVal, "root", root);
    createAttributeWithCustomType(retVal, "node_a", nodeA);
    createAttributeWithCustomType(retVal, "node_b", nodeB);
    createAttributeWithCustomType(retVal, "node_c", nodeC);
    createAttributeWithCustomType(retVal, "node_d", nodeD);
    return retVal;
  }

  @Test
  public void test() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition aggregate = buildAggregate(dataModel);

    TypeDefinition type = new TypeDefinition(aggregate, dataModel);
    FlowDefinition flow = type.getFlow();
    TypeDefinition[] types = flow.getTypes();
    Assert.assertEquals("AGGREGATE含有五个类型对象（可持久化）",5, types.length);
    types = flow.sortTypes();
    for (TypeDefinition typeDef : types) {
      System.out.println(typeDef.getName() + "包含子节点");
      for (TypeDefinition child : flow.getChildren(typeDef)) {
        System.out.println("  " + child.getName());
      }
      if ("root".equals(typeDef.getName())) {
        Assert.assertEquals("root包含node_a和node_b", 2, flow.getChildren(typeDef).length);
      }
    }
  }

}
