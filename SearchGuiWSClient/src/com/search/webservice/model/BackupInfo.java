package com.search.webservice.model;

public class BackupInfo  implements java.io.Serializable {
    private java.util.Calendar dateCreated;

    private java.lang.Long fileSize;

    private java.lang.Boolean hasBackup;

    private java.lang.String ruleId;

    public BackupInfo() {
    }

    public BackupInfo(
           java.util.Calendar dateCreated,
           java.lang.Long fileSize,
           java.lang.Boolean hasBackup,
           java.lang.String ruleId) {
           this.dateCreated = dateCreated;
           this.fileSize = fileSize;
           this.hasBackup = hasBackup;
           this.ruleId = ruleId;
    }


    /**
     * Gets the dateCreated value for this BackupInfo.
     * 
     * @return dateCreated
     */
    public java.util.Calendar getDateCreated() {
        return dateCreated;
    }


    /**
     * Sets the dateCreated value for this BackupInfo.
     * 
     * @param dateCreated
     */
    public void setDateCreated(java.util.Calendar dateCreated) {
        this.dateCreated = dateCreated;
    }


    /**
     * Gets the fileSize value for this BackupInfo.
     * 
     * @return fileSize
     */
    public java.lang.Long getFileSize() {
        return fileSize;
    }


    /**
     * Sets the fileSize value for this BackupInfo.
     * 
     * @param fileSize
     */
    public void setFileSize(java.lang.Long fileSize) {
        this.fileSize = fileSize;
    }


    /**
     * Gets the hasBackup value for this BackupInfo.
     * 
     * @return hasBackup
     */
    public java.lang.Boolean getHasBackup() {
        return hasBackup;
    }


    /**
     * Sets the hasBackup value for this BackupInfo.
     * 
     * @param hasBackup
     */
    public void setHasBackup(java.lang.Boolean hasBackup) {
        this.hasBackup = hasBackup;
    }


    /**
     * Gets the ruleId value for this BackupInfo.
     * 
     * @return ruleId
     */
    public java.lang.String getRuleId() {
        return ruleId;
    }


    /**
     * Sets the ruleId value for this BackupInfo.
     * 
     * @param ruleId
     */
    public void setRuleId(java.lang.String ruleId) {
        this.ruleId = ruleId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BackupInfo)) return false;
        BackupInfo other = (BackupInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.dateCreated==null && other.getDateCreated()==null) || 
             (this.dateCreated!=null &&
              this.dateCreated.equals(other.getDateCreated()))) &&
            ((this.fileSize==null && other.getFileSize()==null) || 
             (this.fileSize!=null &&
              this.fileSize.equals(other.getFileSize()))) &&
            ((this.hasBackup==null && other.getHasBackup()==null) || 
             (this.hasBackup!=null &&
              this.hasBackup.equals(other.getHasBackup()))) &&
            ((this.ruleId==null && other.getRuleId()==null) || 
             (this.ruleId!=null &&
              this.ruleId.equals(other.getRuleId())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getDateCreated() != null) {
            _hashCode += getDateCreated().hashCode();
        }
        if (getFileSize() != null) {
            _hashCode += getFileSize().hashCode();
        }
        if (getHasBackup() != null) {
            _hashCode += getHasBackup().hashCode();
        }
        if (getRuleId() != null) {
            _hashCode += getRuleId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BackupInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://model.manager.search.com", "BackupInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateCreated");
        elemField.setXmlName(new javax.xml.namespace.QName("http://model.manager.search.com", "dateCreated"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fileSize");
        elemField.setXmlName(new javax.xml.namespace.QName("http://model.manager.search.com", "fileSize"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hasBackup");
        elemField.setXmlName(new javax.xml.namespace.QName("http://model.manager.search.com", "hasBackup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ruleId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://model.manager.search.com", "ruleId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
