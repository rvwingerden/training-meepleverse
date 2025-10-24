package io.fluxzero.training.meepleverse.upcasters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.fluxzero.sdk.common.serialization.casting.Upcast;
import org.springframework.stereotype.Component;

@Component
public class ShipmentUpcaster {

    @Upcast(type = "io.fluxzero.training.meepleverse.shipping.api.ShipmentSent", revision = 0)
    public ObjectNode upcastV0toV1(ObjectNode input) {
        JsonNode details = input.get("details");
        ArrayNode shipments = new ObjectMapper().createArrayNode();
        shipments.add(details);
        input.set("details", shipments);
        return input;
    }

    @Upcast(type = "io.fluxzero.training.meepleverse.shipping.api.model.Shipment", revision = 0)
    public ObjectNode upcastShipmentV0toV1(ObjectNode input) {
        JsonNode details = input.get("details");
        ArrayNode shipments = new ObjectMapper().createArrayNode();
        shipments.add(details);
        input.set("details", shipments);
        return input;
    }
}
