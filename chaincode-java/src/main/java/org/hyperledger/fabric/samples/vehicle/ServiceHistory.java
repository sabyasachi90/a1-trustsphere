package org.hyperledger.fabric.samples.vehicle;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType
public class ServiceHistory {
    @Property()
    private String serviceCenter;
    @Property()
    private Long timeStamp;

    public Long getTimeStamp() {
        return timeStamp;
    }

    public String getServiceCenter() {
        return serviceCenter;
    }

    public void setServiceCenter(String serviceCenter) {
        this.serviceCenter = serviceCenter;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
