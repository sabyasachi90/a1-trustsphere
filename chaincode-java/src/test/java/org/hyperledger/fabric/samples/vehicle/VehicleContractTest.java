/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.vehicle;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.Mockito.*;

public final class VehicleContractTest {

    private final class MockKeyValue implements KeyValue {

        private final String key;
        private final String value;

        MockKeyValue(final String key, final String value) {
            super();
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public String getStringValue() {
            return this.value;
        }

        @Override
        public byte[] getValue() {
            return this.value.getBytes();
        }

    }

    private final class MockAssetResultsIterator implements QueryResultsIterator<KeyValue> {

        private final List<KeyValue> assetList;

        MockAssetResultsIterator() {
            super();

            assetList = new ArrayList<KeyValue>();

            assetList.add(new MockKeyValue("v1",
                    "{ \"vehicleId\": \"v1\"}"));
        }

        @Override
        public Iterator<KeyValue> iterator() {
            return assetList.iterator();
        }

        @Override
        public void close() throws Exception {
            // do nothing
        }

    }


    @Nested
    class InvokeReadAssetTransaction {

        @Test
        public void whenAssetExists() {
            VehicleContract contract = new VehicleContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("v1"))
                    .thenReturn("{ \"vehicleId\": \"v1\"}");

            Vehicle vehicle = contract.readVehicle(ctx, "v1");

            assertThat(vehicle).isEqualTo(new Vehicle("v1"));
        }
    }

    @Test
    @Disabled("for init ledger method")
    void invokeInitLedgerTransaction() {
        VehicleContract contract = new VehicleContract();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);

        contract.initLedger(ctx);

        InOrder inOrder = inOrder(stub);
        inOrder.verify(stub).putStringState("v1", "{\"currentOwner\":null,\"owners\":[\"owner1\",\"owner2\"],\"servicingInfo\":null," +
                "\"vehicleId\":\"v1\"}");

    }

    @Nested
    class InvokeCreateAssetTransaction {

        @Test
        public void whenAssetExists() {
            VehicleContract contract = new VehicleContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("v1"))
                    .thenReturn("{ \"vehicleId\": \"v1\"}");

            Throwable thrown = catchThrowable(() -> {
                contract.createVehicle(ctx, "{ \"vehicleId\": \"v1\"}");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Vehicle v1 already exists");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ASSET_ALREADY_EXISTS".getBytes());
        }


        @Test
        public void whenAssetDoesNotExist() {
            VehicleContract contract = new VehicleContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("v1")).thenReturn("");

            Vehicle vehicle = contract.createVehicle(ctx, "{ \"vehicleId\": \"v1\"}");

            assertThat(vehicle).isEqualTo(new Vehicle("v1"));
        }
    }


    @Nested
    class TransferAssetTransaction {

        @Test
        public void whenAssetExists() {
            VehicleContract contract = new VehicleContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("v1"))
                    .thenReturn("{ \"vehicleId\": \"v1\",  \"owners\": [\"owner1\"] }");

            Vehicle vehicle = contract.transferOwner(ctx, "v1", "Dr Evil");

            assertThat(vehicle).isEqualTo(new Vehicle("v1",List.of("owner1","Dr Evil"),null));
        }

       /* @Test
        public  void test(){
            List<String> list = new ArrayList<>();
            list.add("a");
            list.add("b");
            Vehicle vehicle = new Vehicle("v1",list,null);
            Genson genson = new Genson();
           String json = genson.serialize(vehicle);
            Vehicle v = genson.deserialize(json,Vehicle.class);
            System.out.println(v);
        }*/

        @Test
        public void whenAssetDoesNotExist() {
            VehicleContract contract = new VehicleContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("v1")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                contract.transferOwner(ctx, "v1", "Dr Evil");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Vehicle v1 does not exist");
        }
    }

    @Nested
    class UpdateAssetTransaction {

        @Test
        public void whenAssetExists() {
            VehicleContract contract = new VehicleContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("v1"))
                    .thenReturn("{ \"vehicleId\": \"v1\",  \"owners\": [\"owner1\"]}");

            Vehicle vehicle = contract.addOwner(ctx, "v1", "owner2");

            assertThat(vehicle).isEqualTo(new Vehicle("v1", List.of("owner1","owner2"),null));
        }
    }
}
