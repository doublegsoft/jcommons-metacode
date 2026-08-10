package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import com.doublegsoft.jcommons.metamodel.dataset.JoinConditionDefinition;
import com.doublegsoft.jcommons.metamodel.dataset.JoinPredicateDefinition;
import org.junit.Assert;
import org.junit.Test;

public class TreelikeTest extends TestBase {

  private ModelDefinition buildDataModel() {
    ModelDefinition retVal = new ModelDefinition();

    // 1. 创建持久化的树节点实体 (tree_node)
    ObjectDefinition treeNode = createPersistentObject(retVal, "tree_node");
    createIdentifiableAttribute(treeNode, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(treeNode, "name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(treeNode, "type", new PrimitiveType("string")); // 树节点类型
    createAttributeWithCustomType(treeNode, "parent", treeNode);                     // 自关联父节点
    createAttributeWithPrimitiveType(treeNode, "sort_number", new PrimitiveType("int"));
    createAttributeWithPrimitiveType(treeNode, "description", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(treeNode, "created_time", new PrimitiveType("datetime"));

    // 2. 创建平铺处理的非持久化复合模型 (plain_tree_node)
    ObjectDefinition plainTreeNode = new ObjectDefinition("plain_tree_node", retVal);

    // 当前节点 ID
    AttributeDefinition attr = createAttribute(plainTreeNode, "node_id", new PrimitiveType("long"));
    attr.setLabelledOption("original", "object", "tree_node");
    attr.setLabelledOption("original", "attribute", "id");

    // 当前节点名称
    attr = createAttribute(plainTreeNode, "node_name", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "tree_node");
    attr.setLabelledOption("original", "attribute", "name");

    // 当前节点类型
    attr = createAttribute(plainTreeNode, "node_type", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "tree_node");
    attr.setLabelledOption("original", "attribute", "type");

    // 父节点 ID (通过别名 parent 关联)
    attr = createAttribute(plainTreeNode, "parent_id", new PrimitiveType("long"));
    attr.setLabelledOption("original", "object", "tree_node");
    attr.setLabelledOption("original", "attribute", "id");
    attr.setLabelledOption("original", "alias", "parent");

    // 父节点名称 (通过别名 parent 关联)
    attr = createAttribute(plainTreeNode, "parent_name", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "tree_node");
    attr.setLabelledOption("original", "attribute", "name");
    attr.setLabelledOption("original", "alias", "parent");

    // 描述
    attr = createAttribute(plainTreeNode, "description", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "tree_node");
    attr.setLabelledOption("original", "attribute", "description");

    return retVal;
  }

  @Test
  public void test_match() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition match = dataModel.findObjectByName("tree_node");
    TypeDefinition type = new TypeDefinition(match, dataModel);

    FlowDefinition flow = new FlowDefinition(type, dataModel);
    TypeDefinition[] types = flow.getTypes();
    Assert.assertEquals("在这个流程中应该有四个类型对象", 2, types.length);
  }
}
