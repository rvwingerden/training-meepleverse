package io.fluxzero.training.meepleverse.replayers;

import io.fluxzero.sdk.tracking.Consumer;
import io.fluxzero.sdk.tracking.handling.HandleDocument;
import io.fluxzero.training.meepleverse.shipping.api.model.Shipment;
import org.springframework.stereotype.Component;

@Component
@Consumer(name = "replayShipmentDocuments")
public class ReplayShipmentDocuments {

    @HandleDocument(documentClass = Shipment.class)
    Shipment handle(Shipment upcastedShipment) {
        return upcastedShipment;
    }
}
