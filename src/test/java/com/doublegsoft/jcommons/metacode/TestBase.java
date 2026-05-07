package com.doublegsoft.jcommons.metacode;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.ObjectDefinition;
import com.doublegsoft.jcommons.metabean.type.CollectionType;
import com.doublegsoft.jcommons.metabean.type.CustomType;
import com.doublegsoft.jcommons.metabean.type.ObjectType;
import com.doublegsoft.jcommons.metabean.type.PrimitiveType;

public class TestBase {

  protected ObjectDefinition createPersistentObject(ModelDefinition model,
                                                    String objname) {
    ObjectDefinition retVal = new ObjectDefinition(objname, model);
    retVal.setLabelledOption("persistence", "name", objname);
    retVal.setLabelledOption("name", "label", objname);
    return retVal;
  }

  protected AttributeDefinition createAttribute(ObjectDefinition parent,
                                                String attrname,
                                                ObjectType attrtype) {
    AttributeDefinition retVal = new AttributeDefinition(attrname, parent);
    retVal.setLabelledOption("name", "label", attrname);
    retVal.setType(attrtype);
    return retVal;
  }

  protected void createIdentifiableAttribute(ObjectDefinition parent,
                                             String attrname,
                                             ObjectType attrtype) {
    AttributeDefinition retVal = new AttributeDefinition(attrname, parent);
    retVal.setLabelledOption("persistence", "name", attrname);
    retVal.setLabelledOption("name", "label", attrname);
    retVal.getConstraint().setIdentifiable(true);
    retVal.setType(attrtype);
  }

  protected void createAttributeWithPrimitiveType(ObjectDefinition parent,
                                                  String attrname,
                                                  PrimitiveType attrtype) {
    AttributeDefinition retVal = new AttributeDefinition(attrname, parent);
    retVal.setLabelledOption("persistence", "name", attrname);
    retVal.setLabelledOption("name", "label", attrname);
    retVal.setType(attrtype);
  }

  protected void createAttributeWithCustomType(ObjectDefinition parent,
                                               String attrname,
                                               ObjectDefinition type) {
    AttributeDefinition retVal = new AttributeDefinition(attrname, parent);
    retVal.setLabelledOption("persistence", "name", attrname);
    retVal.setLabelledOption("name", "label", attrname);
    retVal.setType(new CustomType(type.getName(), type));
  }

  protected void createAttributeWithCollectionType(ObjectDefinition parent,
                                                   String attrname,
                                                   String singular,
                                                   ObjectDefinition obj) {
    AttributeDefinition retVal = new AttributeDefinition(attrname, parent);
    retVal.setLabelledOption("name", "label", attrname);
    retVal.setLabelledOption("name", "singular", singular);
    CollectionType collType = new CollectionType(obj.getName());
    collType.setComponentType(obj);
    retVal.setType(collType);
  }

}
