package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import org.junit.Assert;
import org.junit.Test;

public class BillAccountAggregateTest extends TestBase {

  private ModelDefinition buildDataModel() {
    ModelDefinition retVal = new ModelDefinition();

    // ==========================================
    // 1. account（客户账户实体）
    // ==========================================
    ObjectDefinition accountObj = createPersistentObject(retVal, "account");
    createIdentifiableAttribute(accountObj, "id", new PrimitiveType("id"));
    createAttributeWithPrimitiveType(accountObj, "account_no", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(accountObj, "customer_name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(accountObj, "type", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(accountObj, "billing_address", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(accountObj, "currency", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(accountObj, "status", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(accountObj, "created_time", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(accountObj, "updated_time", new PrimitiveType("datetime"));

    // ==========================================
    // 2. bill（账单主实体）
    // ==========================================
    ObjectDefinition billObj = createPersistentObject(retVal, "bill");
    createIdentifiableAttribute(billObj, "id", new PrimitiveType("id"));
    createAttributeWithPrimitiveType(billObj, "bill_no", new PrimitiveType("string"));
    createAttributeWithCustomType(billObj, "account", accountObj);
    createAttributeWithPrimitiveType(billObj, "bill_type", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(billObj, "period_start_date", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(billObj, "period_end_date", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(billObj, "issue_date", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(billObj, "due_date", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(billObj, "total_amount", new PrimitiveType("decimal"));
    createAttributeWithPrimitiveType(billObj, "autopay_amount", new PrimitiveType("decimal"));
    createAttributeWithPrimitiveType(billObj, "balance_bf", new PrimitiveType("decimal"));
    createAttributeWithPrimitiveType(billObj, "balance_cf", new PrimitiveType("decimal"));
    createAttributeWithPrimitiveType(billObj, "status", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(billObj, "created_time", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(billObj, "updated_time", new PrimitiveType("datetime"));

    // ==========================================
    // 3. svc_dtl（服务明细实体）
    // ==========================================
    ObjectDefinition svcDtlObj = createPersistentObject(retVal, "svc_dtl");
    createIdentifiableAttribute(svcDtlObj, "id", new PrimitiveType("id"));
    createAttributeWithCustomType(svcDtlObj, "account", accountObj);
    createAttributeWithPrimitiveType(svcDtlObj, "premise_id", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(svcDtlObj, "premise_address", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(svcDtlObj, "supply_nature", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(svcDtlObj, "meter_no", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(svcDtlObj, "start_reading", new PrimitiveType("decimal"));
    createAttributeWithPrimitiveType(svcDtlObj, "end_reading", new PrimitiveType("decimal"));
    createAttributeWithPrimitiveType(svcDtlObj, "reading_type", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(svcDtlObj, "consumption", new PrimitiveType("decimal"));
    createAttributeWithPrimitiveType(svcDtlObj, "subtotal_amount", new PrimitiveType("decimal"));
    createAttributeWithPrimitiveType(svcDtlObj, "created_time", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(svcDtlObj, "updated_time", new PrimitiveType("datetime"));

    // ==========================================
    // 4. bill_item（账单细项实体）
    // ==========================================
    ObjectDefinition billItemObj = createPersistentObject(retVal, "bill_item");
    createIdentifiableAttribute(billItemObj, "id", new PrimitiveType("id"));
    createAttributeWithCustomType(billItemObj, "bill", billObj);
    createAttributeWithCustomType(billItemObj, "svc_dtl", svcDtlObj);
    createAttributeWithPrimitiveType(billItemObj, "item_code", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(billItemObj, "item_name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(billItemObj, "tier_level", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(billItemObj, "unit_price", new PrimitiveType("decimal"));
    createAttributeWithPrimitiveType(billItemObj, "quantity", new PrimitiveType("decimal"));
    createAttributeWithPrimitiveType(billItemObj, "amount", new PrimitiveType("decimal"));
    createAttributeWithPrimitiveType(billItemObj, "start_date", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(billItemObj, "end_date", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(billItemObj, "created_time", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(billItemObj, "updated_time", new PrimitiveType("datetime"));

    // ==========================================
    // 5. bill_message（账单附注实体）
    // ==========================================
    ObjectDefinition billMsgObj = createPersistentObject(retVal, "bill_message");
    createIdentifiableAttribute(billMsgObj, "id", new PrimitiveType("id"));
    createAttributeWithCustomType(billMsgObj, "bill", billObj);
    createAttributeWithPrimitiveType(billMsgObj, "premise_id", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(billMsgObj, "type", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(billMsgObj, "code", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(billMsgObj, "seq_no", new PrimitiveType("integer"));
    createAttributeWithPrimitiveType(billMsgObj, "content", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(billMsgObj, "created_time", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(billMsgObj, "updated_time", new PrimitiveType("datetime"));

    // ==========================================
    // 6. bill_aggregate（账单全景聚合对象）
    // ==========================================
    ObjectDefinition billAggObj = new ObjectDefinition("bill_aggregate", retVal);
    // 1:1 引用
    createAttributeWithCustomType(billAggObj, "bill", billObj);
    createAttributeWithCustomType(billAggObj, "account", accountObj);

    // 1:N 集合引用
    createAttributeWithCollectionType(billAggObj, "svc_dtls", "svc_dtl", svcDtlObj);
    createAttributeWithCollectionType(billAggObj, "bill_items", "bill_item", billItemObj);
    createAttributeWithCollectionType(billAggObj, "bill_messages", "bill_message", billMsgObj);

    return retVal;
  }

  @Test
  public void test_bill_aggretate() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition aggregate = dataModel.findObjectByName("bill_aggregate");

    TypeDefinition typeDef = new TypeDefinition(aggregate, dataModel);
    FlowDefinition flow = typeDef.getFlow();
    TypeDefinition[] types = flow.getTypes();

    TypeDefinition typeObjAccount = null;
    for (TypeDefinition typeObj : types) {
      if (typeObj.getName().equals("account")) {
        typeObjAccount = typeObj;
        break;
      }
    }
    TypeDefinition[] refTypeObjs = flow.getParentsAndSiblings(typeObjAccount);
    Assert.assertEquals(1, refTypeObjs.length);
    Assert.assertEquals("bill", refTypeObjs[0].getName());
  }
}
