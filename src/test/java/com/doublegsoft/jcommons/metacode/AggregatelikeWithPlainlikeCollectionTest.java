package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import org.junit.Assert;
import org.junit.Test;

public class AggregatelikeWithPlainlikeCollectionTest extends TestBase {

  /**
   * 构建底层数据模型（持久化实体、关联定义以及 task_instance_row 投影定义）
   */
  private ModelDefinition buildDataModel() {
    ModelDefinition retVal = new ModelDefinition();

    // ==========================================
    // 1. automated_process（流程定义）
    // ==========================================
    ObjectDefinition processObj = createPersistentObject(retVal, "automated_process");
    createIdentifiableAttribute(processObj, "id", new PrimitiveType("id"));
    createAttributeWithPrimitiveType(processObj, "code", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(processObj, "name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(processObj, "version", new PrimitiveType("integer"));
    createAttributeWithPrimitiveType(processObj, "description", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(processObj, "status", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(processObj, "trigger_type", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(processObj, "created_at", new PrimitiveType("datetime"));

    // ==========================================
    // 2. automated_process_task（流程任务定义）
    // ==========================================
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

    // 反向关联：流程包含任务集合 tasks: &automated_process_task(id)[]
    createAttributeWithCollectionType(processObj, "tasks", "task", taskObj);

    // ==========================================
    // 3. automated_process_transition（任务流转连线）
    // ==========================================
    ObjectDefinition transObj = createPersistentObject(retVal, "automated_process_transition");
    createAttributeWithCustomType(transObj, "process", processObj);
    createAttributeWithCustomType(transObj, "source", taskObj);
    createAttributeWithCustomType(transObj, "target", taskObj);
    createAttributeWithPrimitiveType(transObj, "condition", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(transObj, "priority", new PrimitiveType("integer"));

    // 反向关联：任务节点流出的连线集合 transitions: &automated_process_transition(source)[]
    createAttributeWithCollectionType(taskObj, "transitions", "transition", transObj);

    // ==========================================
    // 4. automated_process_instance（流程实例）
    // ==========================================
    ObjectDefinition processInstObj = createPersistentObject(retVal, "automated_process_instance");
    createIdentifiableAttribute(processInstObj, "id", new PrimitiveType("id"));
    createAttributeWithCustomType(processInstObj, "automated_process", processObj);
    createAttributeWithPrimitiveType(processInstObj, "business_key", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(processInstObj, "status", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(processInstObj, "started_by", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(processInstObj, "started_time", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(processInstObj, "end_time", new PrimitiveType("datetime"));

    // ==========================================
    // 5. automated_task_instance（任务执行实例）
    // ==========================================
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

    // ==========================================
    // 6. automated_transition_instance（任务流转实例）
    // ==========================================
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

    // ==========================================
    // 7. automated_process_variable（流程变量）
    // ==========================================
    ObjectDefinition processVarObj = createPersistentObject(retVal, "automated_process_variable");
    createIdentifiableAttribute(processVarObj, "id", new PrimitiveType("id"));
    createAttributeWithCustomType(processVarObj, "automated_process_instance", processInstObj);
    createAttributeWithPrimitiveType(processVarObj, "name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(processVarObj, "type", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(processVarObj, "value", new PrimitiveType("text"));

    // ==========================================
    // 8. task_instance_row（任务实例行投影）
    // ==========================================
    ObjectDefinition taskInstRowObj = new ObjectDefinition("task_instance_row", retVal);
    createOriginalAttribute(taskInstRowObj, "automated_task_instance_id", "id", "automated_task_instance", "id");
    createOriginalAttribute(taskInstRowObj, "process_instance_id", "id", "automated_task_instance", "process_instance");
    createOriginalAttribute(taskInstRowObj, "status", "string", "automated_task_instance", "status");
    createOriginalAttribute(taskInstRowObj, "assignee", "string", "automated_task_instance", "assignee");
    createOriginalAttribute(taskInstRowObj, "action", "string", "automated_task_instance", "action");
    createOriginalAttribute(taskInstRowObj, "comment", "string", "automated_task_instance", "comment");
    createOriginalAttribute(taskInstRowObj, "retry_count", "integer", "automated_task_instance", "retry_count");
    createOriginalAttribute(taskInstRowObj, "started_time", "datetime", "automated_task_instance", "started_time");
    createOriginalAttribute(taskInstRowObj, "end_time", "datetime", "automated_task_instance", "end_time");

    createOriginalAttribute(taskInstRowObj, "code", "string", "automated_process_task", "code");
    createOriginalAttribute(taskInstRowObj, "task_id", "id", "automated_process_task", "id");
    createOriginalAttribute(taskInstRowObj, "name", "string", "automated_process_task", "name");
    createOriginalAttribute(taskInstRowObj, "executor", "string", "automated_process_task", "executor");
    createOriginalAttribute(taskInstRowObj, "assignee_expression", "string", "automated_process_task", "assignee_expression");
    createOriginalAttribute(taskInstRowObj, "expression", "string", "automated_process_task", "expression");

    return retVal;
  }

  /**
   * 辅助构建带有 @original(object='...', attribute='...') 标签属性的方法
   */
  private AttributeDefinition createOriginalAttribute(ObjectDefinition obj, String name, String type,
                                                      String origObject, String origAttribute) {
    createAttributeWithPrimitiveType(obj, name, new PrimitiveType(type));
    AttributeDefinition retVal = obj.getAttributes()[obj.getAttributes().length - 1];
    retVal.setLabelledOption("original", "object", origObject);
    retVal.setLabelledOption("original", "attribute", origAttribute);
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
    ObjectDefinition taskInstRowObj = dataModel.findObjectByName("task_instance_row");
    ObjectDefinition transInstObj = dataModel.findObjectByName("automated_transition_instance");

    createAttributeWithCustomType(retVal, "process_instance", processInstObj);
    // task_instances: &task_instance_row(process_instance_id)[]
    createAttributeWithCollectionType(retVal, "task_instances", "task_instance", taskInstRowObj);
    // transition_instances: &automated_transition_instance(process_instance)[]
    createAttributeWithCollectionType(retVal, "transition_instances", "transition_instance", transInstObj);
    return retVal;
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
    TypeDefinition[] types = flow.sortTypes();

    Assert.assertEquals(3, types.length);
    for (TypeDefinition t : types) {
      System.out.println(t.getName());
    }
  }

}
