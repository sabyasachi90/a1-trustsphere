/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.vehicle;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@DataType()
public final class Vehicle {

    @Property()
    private final String vehicleId;

    @Property()
    private  ServicingInfo servicingInfo;

    @Property()
    private String currentOwner;

    @Property()
    private List<String> owners;

    public String getVehicleId() {
        return vehicleId;
    }

    public ServicingInfo getServicingInfo() {
        return servicingInfo;
    }

    public void setServicingInfo(ServicingInfo servicingInfo) {
        this.servicingInfo = servicingInfo;
    }

    public List<String> getOwners() {
        return owners;
    }

    public void setOwners(List<String> owners) {
        this.owners = owners;
    }

    public String getCurrentOwner() {
        return currentOwner;
    }

    public void setCurrentOwner(String currentOwner) {
        this.currentOwner = currentOwner;
    }

    public Vehicle(@JsonProperty("vehicleId") final String vehicleId,
                   @JsonProperty("owners") final List<String> owners,
                   @JsonProperty("servicingInfo") final ServicingInfo servicingInfo) {
        this.vehicleId = vehicleId;
        this.owners = owners;
        this.servicingInfo = servicingInfo;
    }

    public Vehicle(@JsonProperty("vehicleId") final String vehicleId) {
        this.vehicleId = vehicleId;
        this.owners = Collections.emptyList();
        this.servicingInfo = null;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Vehicle other = (Vehicle) obj;

        return Objects.deepEquals(
                new String[] {getVehicleId()},
                new String[] {other.getVehicleId()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVehicleId());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [vehicleId=" + vehicleId + "]";
    }
}
