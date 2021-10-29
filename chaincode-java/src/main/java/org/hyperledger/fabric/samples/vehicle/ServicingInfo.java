package org.hyperledger.fabric.samples.vehicle;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.List;

@DataType
public class ServicingInfo {
    @Property
    private List<ServiceHistory> serviceHistories;

    public List<ServiceHistory> getServiceHistories() {
        return serviceHistories;
    }

    public void setServiceHistories(List<ServiceHistory> serviceHistories) {
        this.serviceHistories = serviceHistories;
    }
}

