/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.vehicle;

import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.util.ArrayList;
import java.util.List;

@Contract(
        name = "vehicle",
        info = @Info(
                title = "A1 trustSphere",
                description = "A1 trust sphere",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "sabyasachi.bhattacharya@auto1.com",
                        name = "Sabyasachi Bhattacharya",
                        url = "https://auto1.com")))
@Default
public final class VehicleContract implements ContractInterface {

    private final Genson genson = new Genson();

    private enum AssetTransferErrors {
        ASSET_NOT_FOUND,
        ASSET_ALREADY_EXISTS
    }

    /**
     * Creates some initial assets on the ledger.
     *
     * @param ctx the transaction context
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        createVehicle(ctx, "v1");
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Vehicle createVehicle(final Context ctx, String vehicleJson) {
        ChaincodeStub stub = ctx.getStub();
        Vehicle vehicleToBeStored = genson.deserialize(vehicleJson,Vehicle.class);
        checkIfVehicleExists(ctx, vehicleToBeStored.getVehicleId());
        stub.putStringState(vehicleToBeStored.getVehicleId(), vehicleJson);
        return vehicleToBeStored;
    }


    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Vehicle readVehicle(final Context ctx, final String vehicleId) {
        ChaincodeStub stub = ctx.getStub();
        String vehicleJSON = stub.getStringState(vehicleId);

        if(!vehicleExists(ctx,vehicleId)){
            String errorMessage = String.format("Vehicle %s does not exist", vehicleId);
            throw new ChaincodeException(errorMessage);
        }

        Vehicle vehicle = genson.deserialize(vehicleJSON, Vehicle.class);
        return vehicle;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Vehicle addOwner(final Context ctx, final String vehicleId,
                            final String owner) {
        ChaincodeStub stub = ctx.getStub();
        Vehicle existingVehicle = readVehicle(ctx, vehicleId);
        existingVehicle.getOwners().add(owner);
        //Use Genson to convert the Asset into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(existingVehicle);
        stub.putStringState(vehicleId, sortedJson);
        return existingVehicle;
    }


    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Vehicle transferOwner(final Context ctx, final String vehicleId, final String newOwner) {
        ChaincodeStub stub = ctx.getStub();
        Vehicle existingVehicle = readVehicle(ctx, vehicleId);
        //Use a Genson to convert the Asset into string, sort it alphabetically and serialize it into a json string
        existingVehicle.setCurrentOwner(newOwner);
        boolean notAlreadyAdded = existingVehicle.getOwners()
                .stream()
                .noneMatch(owner -> owner.equalsIgnoreCase(newOwner));
        if (notAlreadyAdded) {
            existingVehicle.getOwners().add(newOwner);
        }
        String sortedJson = genson.serialize(existingVehicle);
        stub.putStringState(vehicleId, sortedJson);

        return existingVehicle;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllVehicles(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<Vehicle> queryResults = new ArrayList<>();

        // To retrieve all assets from the ledger use getStateByRange with empty startKey & endKey.
        // Giving empty startKey & endKey is interpreted as all the keys from beginning to end.
        // As another example, if you use startKey = 'asset0', endKey = 'asset9' ,
        // then getStateByRange will retrieve asset with keys between asset0 (inclusive) and asset9 (exclusive) in lexical order.
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result : results) {
            Vehicle vehicle = genson.deserialize(result.getStringValue(), Vehicle.class);
            System.out.println(vehicle);
            queryResults.add(vehicle);
        }

        return genson.serialize(queryResults);
    }

    private void checkIfVehicleExists(Context ctx, String vehicleId) {
        if (vehicleExists(ctx, vehicleId)) {
            String errorMessage = String.format("Vehicle %s already exists", vehicleId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_ALREADY_EXISTS.toString());
        }
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean vehicleExists(final Context ctx, final String vehicleId) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(vehicleId);
        return (assetJSON != null && !assetJSON.isEmpty());
    }
}
