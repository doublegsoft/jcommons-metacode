package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;
import org.junit.Assert;
import org.junit.Test;

public class RbacTest extends TestBase {

  private ModelDefinition buildDataModel() {
    ModelDefinition retVal = new ModelDefinition();

    // 1. sys_user 实体
    ObjectDefinition sysUser = createPersistentObject(retVal, "sys_user");
    createIdentifiableAttribute(sysUser, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(sysUser, "username", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(sysUser, "password_hash", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(sysUser, "nickname", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(sysUser, "email", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(sysUser, "status", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(sysUser, "created_time", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(sysUser, "updated_time", new PrimitiveType("datetime"));

    // 2. sys_role 实体
    ObjectDefinition sysRole = createPersistentObject(retVal, "sys_role");
    createIdentifiableAttribute(sysRole, "id", new PrimitiveType("long"));
    createAttributeWithPrimitiveType(sysRole, "code", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(sysRole, "name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(sysRole, "description", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(sysRole, "status", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(sysRole, "created_time", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(sysRole, "updated_time", new PrimitiveType("datetime"));

    // 3. sys_permission 实体
    ObjectDefinition sysPermission = createPersistentObject(retVal, "sys_permission");
    createIdentifiableAttribute(sysPermission, "id", new PrimitiveType("long"));
    createAttributeWithCustomType(sysPermission, "parent", sysPermission);
    createAttributeWithPrimitiveType(sysPermission, "code", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(sysPermission, "name", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(sysPermission, "type", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(sysPermission, "path", new PrimitiveType("string"));
    createAttributeWithPrimitiveType(sysPermission, "created_time", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(sysPermission, "updated_time", new PrimitiveType("datetime"));

    // 4. sys_user_role 实体
    ObjectDefinition sysUserRole = createPersistentObject(retVal, "sys_user_role");
    createIdentifiableAttribute(sysUserRole, "id", new PrimitiveType("long"));
    createAttributeWithCustomType(sysUserRole, "user", sysUser);
    createAttributeWithCustomType(sysUserRole, "role", sysRole);
    createAttributeWithPrimitiveType(sysUserRole, "created_time", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(sysUserRole, "updated_time", new PrimitiveType("datetime"));

    // 5. sys_role_permission 实体
    ObjectDefinition sysRolePermission = createPersistentObject(retVal, "sys_role_permission");
    createIdentifiableAttribute(sysRolePermission, "id", new PrimitiveType("long"));
    createAttributeWithCustomType(sysRolePermission, "role", sysRole);
    createAttributeWithCustomType(sysRolePermission, "permission", sysPermission);
    createAttributeWithPrimitiveType(sysRolePermission, "created_time", new PrimitiveType("datetime"));
    createAttributeWithPrimitiveType(sysRolePermission, "updated_time", new PrimitiveType("datetime"));

    // 6. user_role_permission_detail 复合视图 (Composite)
    ObjectDefinition detail = new ObjectDefinition("user_role_permission_detail", retVal);
    AttributeDefinition attr;

    // 用户信息 (sys_user)
    attr = createAttribute(detail, "user_id", new PrimitiveType("long"));
    attr.setLabelledOption("original", "object", "sys_user");
    attr.setLabelledOption("original", "attribute", "id");

    attr = createAttribute(detail, "username", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "sys_user");
    attr.setLabelledOption("original", "attribute", "username");

    attr = createAttribute(detail, "nickname", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "sys_user");
    attr.setLabelledOption("original", "attribute", "nickname");

    // 用户角色关系 (sys_user_role)
    attr = createAttribute(detail, "user_role_id", new PrimitiveType("long"));
    attr.setLabelledOption("original", "object", "sys_user_role");
    attr.setLabelledOption("original", "attribute", "id");

    // 角色信息 (sys_role)
    attr = createAttribute(detail, "role_id", new PrimitiveType("long"));
    attr.setLabelledOption("original", "object", "sys_role");
    attr.setLabelledOption("original", "attribute", "id");

    attr = createAttribute(detail, "role_code", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "sys_role");
    attr.setLabelledOption("original", "attribute", "code");

    attr = createAttribute(detail, "role_name", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "sys_role");
    attr.setLabelledOption("original", "attribute", "name");

    // 角色权限关系 (sys_role_permission)
    attr = createAttribute(detail, "role_permission_id", new PrimitiveType("long"));
    attr.setLabelledOption("original", "object", "sys_role_permission");
    attr.setLabelledOption("original", "attribute", "id");

    // 权限信息 (sys_permission)
    attr = createAttribute(detail, "permission_id", new PrimitiveType("long"));
    attr.setLabelledOption("original", "object", "sys_permission");
    attr.setLabelledOption("original", "attribute", "id");

    attr = createAttribute(detail, "permission_code", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "sys_permission");
    attr.setLabelledOption("original", "attribute", "code");

    attr = createAttribute(detail, "permission_name", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "sys_permission");
    attr.setLabelledOption("original", "attribute", "name");

    attr = createAttribute(detail, "permission_path", new PrimitiveType("string"));
    attr.setLabelledOption("original", "object", "sys_permission");
    attr.setLabelledOption("original", "attribute", "path");

    return retVal;
  }

  @Test
  public void test() throws Exception {
    ModelDefinition dataModel = buildDataModel();
    ObjectDefinition compositeObj = dataModel.findObjectByName("user_role_permission_detail");

    TypeDefinition type = new TypeDefinition(compositeObj, dataModel);
    FlowDefinition flow = type.getFlow();
    TypeDefinition[] types = flow.getTypes();
    Assert.assertEquals("COMPOSITE含有五个类型对象（可持久化）",5, types.length);
    types = flow.sortTypes();
    for (TypeDefinition typeDef : types) {
      System.out.println(typeDef.getName() + ": " + typeDef.getReferences().size());
    }
  }
}
