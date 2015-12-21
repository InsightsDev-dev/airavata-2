/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.apache.airavata.model.task;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2015-12-21")
public class DataStagingTaskModel implements org.apache.thrift.TBase<DataStagingTaskModel, DataStagingTaskModel._Fields>, java.io.Serializable, Cloneable, Comparable<DataStagingTaskModel> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("DataStagingTaskModel");

  private static final org.apache.thrift.protocol.TField SOURCE_FIELD_DESC = new org.apache.thrift.protocol.TField("source", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField DESTINATION_FIELD_DESC = new org.apache.thrift.protocol.TField("destination", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("type", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField TRANSFER_START_TIME_FIELD_DESC = new org.apache.thrift.protocol.TField("transferStartTime", org.apache.thrift.protocol.TType.I64, (short)4);
  private static final org.apache.thrift.protocol.TField TRANSFER_END_TIME_FIELD_DESC = new org.apache.thrift.protocol.TField("transferEndTime", org.apache.thrift.protocol.TType.I64, (short)5);
  private static final org.apache.thrift.protocol.TField TRANSFER_RATE_FIELD_DESC = new org.apache.thrift.protocol.TField("transferRate", org.apache.thrift.protocol.TType.STRING, (short)6);
  private static final org.apache.thrift.protocol.TField PROCESS_INPUT_FIELD_DESC = new org.apache.thrift.protocol.TField("processInput", org.apache.thrift.protocol.TType.STRUCT, (short)7);
  private static final org.apache.thrift.protocol.TField PROCESS_OUTPUT_FIELD_DESC = new org.apache.thrift.protocol.TField("processOutput", org.apache.thrift.protocol.TType.STRUCT, (short)8);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new DataStagingTaskModelStandardSchemeFactory());
    schemes.put(TupleScheme.class, new DataStagingTaskModelTupleSchemeFactory());
  }

  private String source; // required
  private String destination; // required
  private DataStageType type; // required
  private long transferStartTime; // optional
  private long transferEndTime; // optional
  private String transferRate; // optional
  private org.apache.airavata.model.application.io.InputDataObjectType processInput; // optional
  private org.apache.airavata.model.application.io.OutputDataObjectType processOutput; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    SOURCE((short)1, "source"),
    DESTINATION((short)2, "destination"),
    /**
     * 
     * @see DataStageType
     */
    TYPE((short)3, "type"),
    TRANSFER_START_TIME((short)4, "transferStartTime"),
    TRANSFER_END_TIME((short)5, "transferEndTime"),
    TRANSFER_RATE((short)6, "transferRate"),
    PROCESS_INPUT((short)7, "processInput"),
    PROCESS_OUTPUT((short)8, "processOutput");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // SOURCE
          return SOURCE;
        case 2: // DESTINATION
          return DESTINATION;
        case 3: // TYPE
          return TYPE;
        case 4: // TRANSFER_START_TIME
          return TRANSFER_START_TIME;
        case 5: // TRANSFER_END_TIME
          return TRANSFER_END_TIME;
        case 6: // TRANSFER_RATE
          return TRANSFER_RATE;
        case 7: // PROCESS_INPUT
          return PROCESS_INPUT;
        case 8: // PROCESS_OUTPUT
          return PROCESS_OUTPUT;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __TRANSFERSTARTTIME_ISSET_ID = 0;
  private static final int __TRANSFERENDTIME_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  private static final _Fields optionals[] = {_Fields.TRANSFER_START_TIME,_Fields.TRANSFER_END_TIME,_Fields.TRANSFER_RATE,_Fields.PROCESS_INPUT,_Fields.PROCESS_OUTPUT};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.SOURCE, new org.apache.thrift.meta_data.FieldMetaData("source", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.DESTINATION, new org.apache.thrift.meta_data.FieldMetaData("destination", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.TYPE, new org.apache.thrift.meta_data.FieldMetaData("type", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, DataStageType.class)));
    tmpMap.put(_Fields.TRANSFER_START_TIME, new org.apache.thrift.meta_data.FieldMetaData("transferStartTime", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.TRANSFER_END_TIME, new org.apache.thrift.meta_data.FieldMetaData("transferEndTime", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.TRANSFER_RATE, new org.apache.thrift.meta_data.FieldMetaData("transferRate", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.PROCESS_INPUT, new org.apache.thrift.meta_data.FieldMetaData("processInput", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, org.apache.airavata.model.application.io.InputDataObjectType.class)));
    tmpMap.put(_Fields.PROCESS_OUTPUT, new org.apache.thrift.meta_data.FieldMetaData("processOutput", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, org.apache.airavata.model.application.io.OutputDataObjectType.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(DataStagingTaskModel.class, metaDataMap);
  }

  public DataStagingTaskModel() {
  }

  public DataStagingTaskModel(
    String source,
    String destination,
    DataStageType type)
  {
    this();
    this.source = source;
    this.destination = destination;
    this.type = type;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public DataStagingTaskModel(DataStagingTaskModel other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetSource()) {
      this.source = other.source;
    }
    if (other.isSetDestination()) {
      this.destination = other.destination;
    }
    if (other.isSetType()) {
      this.type = other.type;
    }
    this.transferStartTime = other.transferStartTime;
    this.transferEndTime = other.transferEndTime;
    if (other.isSetTransferRate()) {
      this.transferRate = other.transferRate;
    }
    if (other.isSetProcessInput()) {
      this.processInput = new org.apache.airavata.model.application.io.InputDataObjectType(other.processInput);
    }
    if (other.isSetProcessOutput()) {
      this.processOutput = new org.apache.airavata.model.application.io.OutputDataObjectType(other.processOutput);
    }
  }

  public DataStagingTaskModel deepCopy() {
    return new DataStagingTaskModel(this);
  }

  @Override
  public void clear() {
    this.source = null;
    this.destination = null;
    this.type = null;
    setTransferStartTimeIsSet(false);
    this.transferStartTime = 0;
    setTransferEndTimeIsSet(false);
    this.transferEndTime = 0;
    this.transferRate = null;
    this.processInput = null;
    this.processOutput = null;
  }

  public String getSource() {
    return this.source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public void unsetSource() {
    this.source = null;
  }

  /** Returns true if field source is set (has been assigned a value) and false otherwise */
  public boolean isSetSource() {
    return this.source != null;
  }

  public void setSourceIsSet(boolean value) {
    if (!value) {
      this.source = null;
    }
  }

  public String getDestination() {
    return this.destination;
  }

  public void setDestination(String destination) {
    this.destination = destination;
  }

  public void unsetDestination() {
    this.destination = null;
  }

  /** Returns true if field destination is set (has been assigned a value) and false otherwise */
  public boolean isSetDestination() {
    return this.destination != null;
  }

  public void setDestinationIsSet(boolean value) {
    if (!value) {
      this.destination = null;
    }
  }

  /**
   * 
   * @see DataStageType
   */
  public DataStageType getType() {
    return this.type;
  }

  /**
   * 
   * @see DataStageType
   */
  public void setType(DataStageType type) {
    this.type = type;
  }

  public void unsetType() {
    this.type = null;
  }

  /** Returns true if field type is set (has been assigned a value) and false otherwise */
  public boolean isSetType() {
    return this.type != null;
  }

  public void setTypeIsSet(boolean value) {
    if (!value) {
      this.type = null;
    }
  }

  public long getTransferStartTime() {
    return this.transferStartTime;
  }

  public void setTransferStartTime(long transferStartTime) {
    this.transferStartTime = transferStartTime;
    setTransferStartTimeIsSet(true);
  }

  public void unsetTransferStartTime() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __TRANSFERSTARTTIME_ISSET_ID);
  }

  /** Returns true if field transferStartTime is set (has been assigned a value) and false otherwise */
  public boolean isSetTransferStartTime() {
    return EncodingUtils.testBit(__isset_bitfield, __TRANSFERSTARTTIME_ISSET_ID);
  }

  public void setTransferStartTimeIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __TRANSFERSTARTTIME_ISSET_ID, value);
  }

  public long getTransferEndTime() {
    return this.transferEndTime;
  }

  public void setTransferEndTime(long transferEndTime) {
    this.transferEndTime = transferEndTime;
    setTransferEndTimeIsSet(true);
  }

  public void unsetTransferEndTime() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __TRANSFERENDTIME_ISSET_ID);
  }

  /** Returns true if field transferEndTime is set (has been assigned a value) and false otherwise */
  public boolean isSetTransferEndTime() {
    return EncodingUtils.testBit(__isset_bitfield, __TRANSFERENDTIME_ISSET_ID);
  }

  public void setTransferEndTimeIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __TRANSFERENDTIME_ISSET_ID, value);
  }

  public String getTransferRate() {
    return this.transferRate;
  }

  public void setTransferRate(String transferRate) {
    this.transferRate = transferRate;
  }

  public void unsetTransferRate() {
    this.transferRate = null;
  }

  /** Returns true if field transferRate is set (has been assigned a value) and false otherwise */
  public boolean isSetTransferRate() {
    return this.transferRate != null;
  }

  public void setTransferRateIsSet(boolean value) {
    if (!value) {
      this.transferRate = null;
    }
  }

  public org.apache.airavata.model.application.io.InputDataObjectType getProcessInput() {
    return this.processInput;
  }

  public void setProcessInput(org.apache.airavata.model.application.io.InputDataObjectType processInput) {
    this.processInput = processInput;
  }

  public void unsetProcessInput() {
    this.processInput = null;
  }

  /** Returns true if field processInput is set (has been assigned a value) and false otherwise */
  public boolean isSetProcessInput() {
    return this.processInput != null;
  }

  public void setProcessInputIsSet(boolean value) {
    if (!value) {
      this.processInput = null;
    }
  }

  public org.apache.airavata.model.application.io.OutputDataObjectType getProcessOutput() {
    return this.processOutput;
  }

  public void setProcessOutput(org.apache.airavata.model.application.io.OutputDataObjectType processOutput) {
    this.processOutput = processOutput;
  }

  public void unsetProcessOutput() {
    this.processOutput = null;
  }

  /** Returns true if field processOutput is set (has been assigned a value) and false otherwise */
  public boolean isSetProcessOutput() {
    return this.processOutput != null;
  }

  public void setProcessOutputIsSet(boolean value) {
    if (!value) {
      this.processOutput = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case SOURCE:
      if (value == null) {
        unsetSource();
      } else {
        setSource((String)value);
      }
      break;

    case DESTINATION:
      if (value == null) {
        unsetDestination();
      } else {
        setDestination((String)value);
      }
      break;

    case TYPE:
      if (value == null) {
        unsetType();
      } else {
        setType((DataStageType)value);
      }
      break;

    case TRANSFER_START_TIME:
      if (value == null) {
        unsetTransferStartTime();
      } else {
        setTransferStartTime((Long)value);
      }
      break;

    case TRANSFER_END_TIME:
      if (value == null) {
        unsetTransferEndTime();
      } else {
        setTransferEndTime((Long)value);
      }
      break;

    case TRANSFER_RATE:
      if (value == null) {
        unsetTransferRate();
      } else {
        setTransferRate((String)value);
      }
      break;

    case PROCESS_INPUT:
      if (value == null) {
        unsetProcessInput();
      } else {
        setProcessInput((org.apache.airavata.model.application.io.InputDataObjectType)value);
      }
      break;

    case PROCESS_OUTPUT:
      if (value == null) {
        unsetProcessOutput();
      } else {
        setProcessOutput((org.apache.airavata.model.application.io.OutputDataObjectType)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case SOURCE:
      return getSource();

    case DESTINATION:
      return getDestination();

    case TYPE:
      return getType();

    case TRANSFER_START_TIME:
      return getTransferStartTime();

    case TRANSFER_END_TIME:
      return getTransferEndTime();

    case TRANSFER_RATE:
      return getTransferRate();

    case PROCESS_INPUT:
      return getProcessInput();

    case PROCESS_OUTPUT:
      return getProcessOutput();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case SOURCE:
      return isSetSource();
    case DESTINATION:
      return isSetDestination();
    case TYPE:
      return isSetType();
    case TRANSFER_START_TIME:
      return isSetTransferStartTime();
    case TRANSFER_END_TIME:
      return isSetTransferEndTime();
    case TRANSFER_RATE:
      return isSetTransferRate();
    case PROCESS_INPUT:
      return isSetProcessInput();
    case PROCESS_OUTPUT:
      return isSetProcessOutput();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof DataStagingTaskModel)
      return this.equals((DataStagingTaskModel)that);
    return false;
  }

  public boolean equals(DataStagingTaskModel that) {
    if (that == null)
      return false;

    boolean this_present_source = true && this.isSetSource();
    boolean that_present_source = true && that.isSetSource();
    if (this_present_source || that_present_source) {
      if (!(this_present_source && that_present_source))
        return false;
      if (!this.source.equals(that.source))
        return false;
    }

    boolean this_present_destination = true && this.isSetDestination();
    boolean that_present_destination = true && that.isSetDestination();
    if (this_present_destination || that_present_destination) {
      if (!(this_present_destination && that_present_destination))
        return false;
      if (!this.destination.equals(that.destination))
        return false;
    }

    boolean this_present_type = true && this.isSetType();
    boolean that_present_type = true && that.isSetType();
    if (this_present_type || that_present_type) {
      if (!(this_present_type && that_present_type))
        return false;
      if (!this.type.equals(that.type))
        return false;
    }

    boolean this_present_transferStartTime = true && this.isSetTransferStartTime();
    boolean that_present_transferStartTime = true && that.isSetTransferStartTime();
    if (this_present_transferStartTime || that_present_transferStartTime) {
      if (!(this_present_transferStartTime && that_present_transferStartTime))
        return false;
      if (this.transferStartTime != that.transferStartTime)
        return false;
    }

    boolean this_present_transferEndTime = true && this.isSetTransferEndTime();
    boolean that_present_transferEndTime = true && that.isSetTransferEndTime();
    if (this_present_transferEndTime || that_present_transferEndTime) {
      if (!(this_present_transferEndTime && that_present_transferEndTime))
        return false;
      if (this.transferEndTime != that.transferEndTime)
        return false;
    }

    boolean this_present_transferRate = true && this.isSetTransferRate();
    boolean that_present_transferRate = true && that.isSetTransferRate();
    if (this_present_transferRate || that_present_transferRate) {
      if (!(this_present_transferRate && that_present_transferRate))
        return false;
      if (!this.transferRate.equals(that.transferRate))
        return false;
    }

    boolean this_present_processInput = true && this.isSetProcessInput();
    boolean that_present_processInput = true && that.isSetProcessInput();
    if (this_present_processInput || that_present_processInput) {
      if (!(this_present_processInput && that_present_processInput))
        return false;
      if (!this.processInput.equals(that.processInput))
        return false;
    }

    boolean this_present_processOutput = true && this.isSetProcessOutput();
    boolean that_present_processOutput = true && that.isSetProcessOutput();
    if (this_present_processOutput || that_present_processOutput) {
      if (!(this_present_processOutput && that_present_processOutput))
        return false;
      if (!this.processOutput.equals(that.processOutput))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_source = true && (isSetSource());
    list.add(present_source);
    if (present_source)
      list.add(source);

    boolean present_destination = true && (isSetDestination());
    list.add(present_destination);
    if (present_destination)
      list.add(destination);

    boolean present_type = true && (isSetType());
    list.add(present_type);
    if (present_type)
      list.add(type.getValue());

    boolean present_transferStartTime = true && (isSetTransferStartTime());
    list.add(present_transferStartTime);
    if (present_transferStartTime)
      list.add(transferStartTime);

    boolean present_transferEndTime = true && (isSetTransferEndTime());
    list.add(present_transferEndTime);
    if (present_transferEndTime)
      list.add(transferEndTime);

    boolean present_transferRate = true && (isSetTransferRate());
    list.add(present_transferRate);
    if (present_transferRate)
      list.add(transferRate);

    boolean present_processInput = true && (isSetProcessInput());
    list.add(present_processInput);
    if (present_processInput)
      list.add(processInput);

    boolean present_processOutput = true && (isSetProcessOutput());
    list.add(present_processOutput);
    if (present_processOutput)
      list.add(processOutput);

    return list.hashCode();
  }

  @Override
  public int compareTo(DataStagingTaskModel other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetSource()).compareTo(other.isSetSource());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSource()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.source, other.source);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetDestination()).compareTo(other.isSetDestination());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDestination()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.destination, other.destination);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetType()).compareTo(other.isSetType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.type, other.type);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetTransferStartTime()).compareTo(other.isSetTransferStartTime());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTransferStartTime()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.transferStartTime, other.transferStartTime);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetTransferEndTime()).compareTo(other.isSetTransferEndTime());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTransferEndTime()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.transferEndTime, other.transferEndTime);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetTransferRate()).compareTo(other.isSetTransferRate());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTransferRate()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.transferRate, other.transferRate);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetProcessInput()).compareTo(other.isSetProcessInput());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetProcessInput()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.processInput, other.processInput);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetProcessOutput()).compareTo(other.isSetProcessOutput());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetProcessOutput()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.processOutput, other.processOutput);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("DataStagingTaskModel(");
    boolean first = true;

    sb.append("source:");
    if (this.source == null) {
      sb.append("null");
    } else {
      sb.append(this.source);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("destination:");
    if (this.destination == null) {
      sb.append("null");
    } else {
      sb.append(this.destination);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("type:");
    if (this.type == null) {
      sb.append("null");
    } else {
      sb.append(this.type);
    }
    first = false;
    if (isSetTransferStartTime()) {
      if (!first) sb.append(", ");
      sb.append("transferStartTime:");
      sb.append(this.transferStartTime);
      first = false;
    }
    if (isSetTransferEndTime()) {
      if (!first) sb.append(", ");
      sb.append("transferEndTime:");
      sb.append(this.transferEndTime);
      first = false;
    }
    if (isSetTransferRate()) {
      if (!first) sb.append(", ");
      sb.append("transferRate:");
      if (this.transferRate == null) {
        sb.append("null");
      } else {
        sb.append(this.transferRate);
      }
      first = false;
    }
    if (isSetProcessInput()) {
      if (!first) sb.append(", ");
      sb.append("processInput:");
      if (this.processInput == null) {
        sb.append("null");
      } else {
        sb.append(this.processInput);
      }
      first = false;
    }
    if (isSetProcessOutput()) {
      if (!first) sb.append(", ");
      sb.append("processOutput:");
      if (this.processOutput == null) {
        sb.append("null");
      } else {
        sb.append(this.processOutput);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSetSource()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'source' is unset! Struct:" + toString());
    }

    if (!isSetDestination()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'destination' is unset! Struct:" + toString());
    }

    if (!isSetType()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'type' is unset! Struct:" + toString());
    }

    // check for sub-struct validity
    if (processInput != null) {
      processInput.validate();
    }
    if (processOutput != null) {
      processOutput.validate();
    }
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class DataStagingTaskModelStandardSchemeFactory implements SchemeFactory {
    public DataStagingTaskModelStandardScheme getScheme() {
      return new DataStagingTaskModelStandardScheme();
    }
  }

  private static class DataStagingTaskModelStandardScheme extends StandardScheme<DataStagingTaskModel> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, DataStagingTaskModel struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // SOURCE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.source = iprot.readString();
              struct.setSourceIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // DESTINATION
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.destination = iprot.readString();
              struct.setDestinationIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.type = org.apache.airavata.model.task.DataStageType.findByValue(iprot.readI32());
              struct.setTypeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // TRANSFER_START_TIME
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.transferStartTime = iprot.readI64();
              struct.setTransferStartTimeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // TRANSFER_END_TIME
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.transferEndTime = iprot.readI64();
              struct.setTransferEndTimeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // TRANSFER_RATE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.transferRate = iprot.readString();
              struct.setTransferRateIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 7: // PROCESS_INPUT
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.processInput = new org.apache.airavata.model.application.io.InputDataObjectType();
              struct.processInput.read(iprot);
              struct.setProcessInputIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 8: // PROCESS_OUTPUT
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.processOutput = new org.apache.airavata.model.application.io.OutputDataObjectType();
              struct.processOutput.read(iprot);
              struct.setProcessOutputIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, DataStagingTaskModel struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.source != null) {
        oprot.writeFieldBegin(SOURCE_FIELD_DESC);
        oprot.writeString(struct.source);
        oprot.writeFieldEnd();
      }
      if (struct.destination != null) {
        oprot.writeFieldBegin(DESTINATION_FIELD_DESC);
        oprot.writeString(struct.destination);
        oprot.writeFieldEnd();
      }
      if (struct.type != null) {
        oprot.writeFieldBegin(TYPE_FIELD_DESC);
        oprot.writeI32(struct.type.getValue());
        oprot.writeFieldEnd();
      }
      if (struct.isSetTransferStartTime()) {
        oprot.writeFieldBegin(TRANSFER_START_TIME_FIELD_DESC);
        oprot.writeI64(struct.transferStartTime);
        oprot.writeFieldEnd();
      }
      if (struct.isSetTransferEndTime()) {
        oprot.writeFieldBegin(TRANSFER_END_TIME_FIELD_DESC);
        oprot.writeI64(struct.transferEndTime);
        oprot.writeFieldEnd();
      }
      if (struct.transferRate != null) {
        if (struct.isSetTransferRate()) {
          oprot.writeFieldBegin(TRANSFER_RATE_FIELD_DESC);
          oprot.writeString(struct.transferRate);
          oprot.writeFieldEnd();
        }
      }
      if (struct.processInput != null) {
        if (struct.isSetProcessInput()) {
          oprot.writeFieldBegin(PROCESS_INPUT_FIELD_DESC);
          struct.processInput.write(oprot);
          oprot.writeFieldEnd();
        }
      }
      if (struct.processOutput != null) {
        if (struct.isSetProcessOutput()) {
          oprot.writeFieldBegin(PROCESS_OUTPUT_FIELD_DESC);
          struct.processOutput.write(oprot);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class DataStagingTaskModelTupleSchemeFactory implements SchemeFactory {
    public DataStagingTaskModelTupleScheme getScheme() {
      return new DataStagingTaskModelTupleScheme();
    }
  }

  private static class DataStagingTaskModelTupleScheme extends TupleScheme<DataStagingTaskModel> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, DataStagingTaskModel struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeString(struct.source);
      oprot.writeString(struct.destination);
      oprot.writeI32(struct.type.getValue());
      BitSet optionals = new BitSet();
      if (struct.isSetTransferStartTime()) {
        optionals.set(0);
      }
      if (struct.isSetTransferEndTime()) {
        optionals.set(1);
      }
      if (struct.isSetTransferRate()) {
        optionals.set(2);
      }
      if (struct.isSetProcessInput()) {
        optionals.set(3);
      }
      if (struct.isSetProcessOutput()) {
        optionals.set(4);
      }
      oprot.writeBitSet(optionals, 5);
      if (struct.isSetTransferStartTime()) {
        oprot.writeI64(struct.transferStartTime);
      }
      if (struct.isSetTransferEndTime()) {
        oprot.writeI64(struct.transferEndTime);
      }
      if (struct.isSetTransferRate()) {
        oprot.writeString(struct.transferRate);
      }
      if (struct.isSetProcessInput()) {
        struct.processInput.write(oprot);
      }
      if (struct.isSetProcessOutput()) {
        struct.processOutput.write(oprot);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, DataStagingTaskModel struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.source = iprot.readString();
      struct.setSourceIsSet(true);
      struct.destination = iprot.readString();
      struct.setDestinationIsSet(true);
      struct.type = org.apache.airavata.model.task.DataStageType.findByValue(iprot.readI32());
      struct.setTypeIsSet(true);
      BitSet incoming = iprot.readBitSet(5);
      if (incoming.get(0)) {
        struct.transferStartTime = iprot.readI64();
        struct.setTransferStartTimeIsSet(true);
      }
      if (incoming.get(1)) {
        struct.transferEndTime = iprot.readI64();
        struct.setTransferEndTimeIsSet(true);
      }
      if (incoming.get(2)) {
        struct.transferRate = iprot.readString();
        struct.setTransferRateIsSet(true);
      }
      if (incoming.get(3)) {
        struct.processInput = new org.apache.airavata.model.application.io.InputDataObjectType();
        struct.processInput.read(iprot);
        struct.setProcessInputIsSet(true);
      }
      if (incoming.get(4)) {
        struct.processOutput = new org.apache.airavata.model.application.io.OutputDataObjectType();
        struct.processOutput.read(iprot);
        struct.setProcessOutputIsSet(true);
      }
    }
  }

}

