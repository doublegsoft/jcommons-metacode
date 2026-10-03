package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import org.junit.Assert;
import org.junit.Test;

@Deprecated
public class AggregatelikeIndirectCollectionAttributeTest extends TestBase {

  /**
   * 构建底层数据模型（持久化实体及关联定义）
   */
  private ModelDefinition buildDataModel() {
    ModelDefinition retVal = new ModelDefinition();

    // 1. automated_process（流程定义）
    ObjectDefinition processObj = createPersistentObject(retVal, "automated_process");
    createIdentifiableAttribute(processObj, "id", new PrimitiveType("id"));
    createAttributeWithPrimitiveType(processObj, "code", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(processObj, "name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(processObj, "version", new PrimitiveType("integer"));
    createAttributeWithPrimitiveType(processObj, "description", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(processObj, "status", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(processObj, "trigger_type", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(processObj, "created_at", new PrimitiveType("datetime"));

    // 2. automated_process_task（流程任务定义）
    ObjectDefinition taskObj = createPersistentObject(retVal, "automated_process_task");
    createIdentifiableAttribute(taskObj, "id", new PrimitiveType("id"));
    createAttributeWithCustomType(taskObj, "process", processObj);
    createAttributeWithPrimitiveType(taskObj, "code", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(taskObj, "name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(taskObj, "type", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(taskObj, "action_type", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(taskObj, "executor", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(taskObj, "retry_policy", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(taskObj, "assignee_type", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(taskObj, "assignee_expression", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(taskObj, "form_key", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(taskObj, "expression", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(taskObj, "timeout_seconds", new PrimitiveType("integer"));

    // 反向关联：流程包含任务集合
    createAttributeWithCollectionType(processObj, "tasks", "task", taskObj);

    // 3. automated_process_transition（任务流转连线）
    ObjectDefinition transObj = createPersistentObject(retVal, "automated_process_transition");
    createAttributeWithCustomType(transObj, "process", processObj);
    createAttributeWithCustomType(transObj, "source", taskObj);
    createAttributeWithCustomType(transObj, "target", taskObj);
    createAttributeWithPrimitiveType(transObj, "condition", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(transObj, "priority", new PrimitiveType("integer"));

    // 反向关联：任务节点流出的连线集合
    createAttributeWithCollectionType(taskObj, "transitions", "transition", transObj);

    // 4. automated_process_instance（流程实例）
    ObjectDefinition processInstObj = createPersistentObject(retVal, "automated_process_instance");
    createIdentifiableAttribute(processInstObj, "id", new PrimitiveType("id"));
    createAttributeWithCustomType(processInstObj, "automated_process", processObj);
    createAttributeWithPrimitiveType(processInstObj, "business_key", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(processInstObj, "status", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(processInstObj, "started_by", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(processInstObj, "started_time", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(processInstObj, "end_time", new PrimitiveType("datetime"));

    // 5. automated_task_instance（任务执行实例）
    ObjectDefinition taskInstObj = createPersistentObject(retVal, "automated_task_instance");
    createIdentifiableAttribute(taskInstObj, "id", new PrimitiveType("id"));
    createAttributeWithCustomType(taskInstObj, "process_instance", processInstObj);
    createAttributeWithCustomType(taskInstObj, "task", taskObj);
    createAttributeWithPrimitiveType(taskInstObj, "status", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(taskInstObj, "assignee", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(taskInstObj, "action", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(taskInstObj, "comment", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(taskInstObj, "retry_count", new PrimitiveType("integer"));
    createAttributeWithPrimitiveType(taskInstObj, "started_time", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(taskInstObj, "end_time", new PrimitiveType("datetime"));

    // 反向关联：流程实例包含任务实例轨迹集合
    createAttributeWithCollectionType(processInstObj, "task_instances", "task_instance", taskInstObj);

    // 6. automated_transition_instance（任务流转实例）
    ObjectDefinition transInstObj = createPersistentObject(retVal, "automated_transition_instance");
    createIdentifiableAttribute(transInstObj, "id", new PrimitiveType("id"));
    createAttributeWithCustomType(transInstObj, "process_instance", processInstObj);
    createAttributeWithCustomType(transInstObj, "source_task_instance", taskInstObj);
    createAttributeWithCustomType(transInstObj, "target_task_instance", taskInstObj);
    createAttributeWithCustomType(transInstObj, "source_task", taskObj);
    createAttributeWithCustomType(transInstObj, "target_task", taskObj);
    createAttributeWithPrimitiveType(transInstObj, "status", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(transInstObj, "condition_expression", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(transInstObj, "evaluation_context", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(transInstObj, "transition_time", new PrimitiveType("datetime"));

    // 7. automated_process_variable（流程变量）
    ObjectDefinition processVarObj = createPersistentObject(retVal, "automated_process_variable");
    createIdentifiableAttribute(processVarObj, "id", new PrimitiveType("id"));
    createAttributeWithCustomType(processVarObj, "automated_process_instance", processInstObj);
    createAttributeWithPrimitiveType(processVarObj, "name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(processVarObj, "type", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(processVarObj, "value", new PrimitiveType("text"));

    return retVal;
  }

  /**
   * 构建【流程定义聚合对象】(automated_process_aggregate)
   */
  private ObjectDefinition buildProcessAggregate(ModelDefinition dataModel) {
    ObjectDefinition retVal = new ObjectDefinition("automated_process_aggregate", dataModel);
    ObjectDefinition processObj = dataModel.findObjectByName("automated_process");
    ObjectDefinition taskObj = dataModel.findObjectByName("automated_process_task");
    ObjectDefinition transObj = dataModel.findObjectByName("automated_process_transition");

    createAttributeWithCustomType(retVal, "process", processObj);
    createAttributeWithCollectionType(retVal, "tasks", "task", taskObj);
    createAttributeWithCollectionType(retVal, "transitions", "transition", transObj);
    return retVal;
  }

  /**
   * 构建【流程实例聚合对象】(automated_process_instance_aggregate)
   */
  private ObjectDefinition buildProcessInstanceAggregate(ModelDefinition dataModel) {
    ObjectDefinition retVal = new ObjectDefinition("automated_process_instance_aggregate", dataModel);
    ObjectDefinition processInstObj = dataModel.findObjectByName("automated_process_instance");
    ObjectDefinition taskInstObj = dataModel.findObjectByName("automated_task_instance");
    ObjectDefinition taskObj = dataModel.findObjectByName("automated_process_task");
    ObjectDefinition transInstObj = dataModel.findObjectByName("automated_transition_instance");

    createAttributeWithCustomType(retVal, "process_instance", processInstObj);
    createAttributeWithCollectionType(retVal, "task_instances", "task_instance", taskInstObj);
    AttributeDefinition attrTasks = createAttributeWithCollectionType(retVal, "tasks", "task", taskObj);
    attrTasks.setLabelledOption("conjunction", "source_object", "automated_task_instance");
    attrTasks.setLabelledOption("conjunction", "source_attribute", "process_instance");
    attrTasks.setLabelledOption("conjunction", "target_object", "automated_task_instance");
    attrTasks.setLabelledOption("conjunction", "target_attribute", "task");
    createAttributeWithCollectionType(retVal, "transition_instances", "transition_instance", transInstInstOrObj(transInstObj));
    return retVal;
  }

  private ObjectDefinition transInstInstOrObj(ObjectDefinition obj) {
    return obj;
  }

  @Test
  public void testProcessAggregate() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition aggregate = buildProcessAggregate(dataModel);

    TypeDefinition type = new TypeDefinition(aggregate, dataModel);
    FlowDefinition flow = type.getFlow();
    TypeDefinition[] types = flow.getTypes();
    // 流程定义聚合包含：automated_process, automated_process_task, automated_process_transition 3个实体类型
    Assert.assertEquals("流程定义AGGREGATE含有三个类型对象", 3, types.length);
    types = flow.sortTypes();
    for (TypeDefinition typeDef : types) {
      System.out.println(typeDef.getName());
    }
  }

  @Test
  public void testProcessInstanceAggregate() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition aggregate = buildProcessInstanceAggregate(dataModel);

    TypeDefinition typeDef = new TypeDefinition(aggregate, dataModel);
    FlowDefinition flow = typeDef.getFlow();
    TypeDefinition[] types = flow.getTypes();
    // 流程实例聚合包含：automated_process_instance, automated_task_instance, automated_process_task, automated_transition_instance 4个实体类型
    Assert.assertEquals("流程实例AGGREGATE含有四个类型对象", 4, types.length);
    types = flow.sortTypes();

    TypeDefinition typeAutomatedTaskInstance = types[1];
    TypeDefinition typeAutomatedProcessTask = types[2];
    TypeDefinition typeAutomatedTransitionInstance = types[3];

    Assert.assertEquals("CREF", typeDef.getReferenceType(typeAutomatedTaskInstance));
    Assert.assertEquals("CREF", typeDef.getReferenceType(typeAutomatedProcessTask));
    Assert.assertEquals("CREF", typeDef.getReferenceType(typeAutomatedTransitionInstance));

    Assert.assertNotNull(typeAutomatedTaskInstance.getReference());
    Assert.assertNotNull(typeAutomatedProcessTask.getReference());
    Assert.assertNotNull(typeAutomatedTransitionInstance.getReference());
  }

}
