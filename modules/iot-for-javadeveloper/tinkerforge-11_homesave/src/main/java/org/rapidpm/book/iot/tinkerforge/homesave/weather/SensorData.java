package org.rapidpm.book.iot.tinkerforge.homesave.weather;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

/**
* Created by Sven Ruppert on 06.01.2015.
*/
public class SensorData implements Externalizable {
  private String masterUID;
  private String uid;
  private String time;
  private int rawValue;


  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeUTF(masterUID);
    out.writeUTF(uid);
    out.writeUTF(time);
    out.writeInt(rawValue);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    masterUID = in.readUTF();
    uid = in.readUTF();
    time = in.readUTF();
    rawValue = in.readInt();
  }

  public SensorData() {
  }

  public SensorData(String masterUID, String uid, String time, int rawValue) {
    this.masterUID = masterUID;
    this.uid = uid;
    this.time = time;
    this.rawValue = rawValue;
//      System.out.println("new  => " + this.toString());
  }

  private SensorData(Builder builder) {
    setMasterUID(builder.masterUID);
    setUid(builder.uid);
    setTime(builder.time);
    setRawValue(builder.rawValue);
  }

  public static Builder newBuilder() {
    return new Builder();
  }


  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("SensorData{");
    sb.append("masterUID='").append(masterUID).append('\'');
    sb.append(", uid='").append(uid).append('\'');
    sb.append(", time='").append(time).append('\'');
    sb.append(", rawValue=").append(rawValue);
    sb.append('}');
    return sb.toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(masterUID, uid, time, rawValue);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final SensorData other = (SensorData) obj;
    return Objects.equals(this.masterUID, other.masterUID) && Objects.equals(this.uid, other.uid) && Objects.equals(this.time, other.time) && Objects.equals(this.rawValue, other.rawValue);
  }

  public String getMasterUID() {
    return masterUID;
  }

  public void setMasterUID(String masterUID) {
    this.masterUID = masterUID;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public int getRawValue() {
    return rawValue;
  }

  public void setRawValue(int rawValue) {
    this.rawValue = rawValue;
  }


  public static final class Builder {
    private String masterUID;
    private String uid;
    private String time;
    private int rawValue;

    private Builder() {
    }

    public Builder masterUID(String masterUID) {
      this.masterUID = masterUID;
      return this;
    }

    public Builder uid(String uid) {
      this.uid = uid;
      return this;
    }

    public Builder time(String time) {
      this.time = time;
      return this;
    }

    public Builder rawValue(int rawValue) {
      this.rawValue = rawValue;
      return this;
    }

    public SensorData build() {
      return new SensorData(this);
    }
  }
}
