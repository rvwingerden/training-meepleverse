package io.fluxzero.training.meepleverse.upcasters;

import io.fluxzero.common.FileUtils;
import io.fluxzero.common.api.Data;
import io.fluxzero.sdk.common.serialization.jackson.JacksonSerializer;
import io.fluxzero.sdk.test.TestFixture;
import io.fluxzero.training.meepleverse.shipping.api.ShipmentSent;
import org.junit.jupiter.api.Test;

import static io.fluxzero.sdk.configuration.DefaultFluxzero.builder;
import static io.fluxzero.sdk.test.TestFixture.create;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ShipmentUpcasterTest {
    private final TestFixture testFixture =
            create(builder().replaceSerializer(new JacksonSerializer(singletonList(new ShipmentUpcaster()))));


    @Test
    void testUpcastCreateVisitFrom0To1() {
        ShipmentSent original = testFixture.getFluxzero().serializer().deserialize(new Data<>(
                FileUtils.loadFile(ShipmentUpcasterTest.class, "shipment-rev-0.json").getBytes(),
                "io.fluxzero.training.meepleverse.shipping.api.ShipmentSent", 0, null));

        ShipmentSent expected = testFixture.getFluxzero().serializer().deserialize(new Data<>(
                FileUtils.loadFile(ShipmentUpcasterTest.class, "shipment-rev-1.json").getBytes(),
                "io.fluxzero.training.meepleverse.shipping.api.ShipmentSent", 1, null));

        assertEquals(expected, original);
    }

}