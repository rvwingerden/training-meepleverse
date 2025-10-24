package io.fluxzero.training.meepleverse.shipping.api;

import io.fluxzero.common.serialization.Revision;
import io.fluxzero.sdk.persisting.eventsourcing.Apply;
import io.fluxzero.training.meepleverse.shipping.api.model.Shipment;
import io.fluxzero.training.meepleverse.shipping.api.model.ShipmentDetails;
import io.fluxzero.training.meepleverse.shipping.api.model.ShipmentId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Revision(1)
public record ShipmentSent(ShipmentId shipmentId, @NotEmpty List<@Valid @NotNull ShipmentDetails> details) implements ShipmentUpdate {

    @Apply
    Shipment apply() {
        return new Shipment(shipmentId, details);
    }
}
