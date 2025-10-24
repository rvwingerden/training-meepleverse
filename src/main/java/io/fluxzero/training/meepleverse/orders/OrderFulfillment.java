package io.fluxzero.training.meepleverse.orders;

import io.fluxzero.sdk.Fluxzero;
import io.fluxzero.sdk.modeling.EntityId;
import io.fluxzero.sdk.tracking.Consumer;
import io.fluxzero.sdk.tracking.handling.Association;
import io.fluxzero.sdk.tracking.handling.HandleEvent;
import io.fluxzero.sdk.tracking.handling.Stateful;
import io.fluxzero.training.meepleverse.catalog.api.model.ItemId;
import io.fluxzero.training.meepleverse.orders.api.AbortOrder;
import io.fluxzero.training.meepleverse.orders.api.PlaceOrder;
import io.fluxzero.training.meepleverse.orders.api.model.Order;
import io.fluxzero.training.meepleverse.orders.api.model.OrderDetails;
import io.fluxzero.training.meepleverse.orders.api.model.OrderId;
import io.fluxzero.training.meepleverse.payments.api.PaymentAccepted;
import io.fluxzero.training.meepleverse.payments.api.PaymentRejected;
import io.fluxzero.training.meepleverse.payments.api.ValidatePayment;
import io.fluxzero.training.meepleverse.payments.api.model.PaymentId;
import io.fluxzero.training.meepleverse.shipping.api.SendShipment;
import io.fluxzero.training.meepleverse.shipping.api.ShippingFailed;
import io.fluxzero.training.meepleverse.shipping.api.model.ShipmentDetails;
import io.fluxzero.training.meepleverse.shipping.api.model.ShipmentId;
import io.fluxzero.training.meepleverse.supplier.BackorderFailed;
import io.fluxzero.training.meepleverse.supplier.BackorderItems;
import io.fluxzero.training.meepleverse.supplier.BackorderedItemArrived;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Consumer(name = "orders", ignoreSegment = true)
@Value
@Builder(toBuilder = true)
@Stateful
public class OrderFulfillment {

    @Singular
    Set<ItemId> receivedItems;

    @Association
    OrderId orderId;
    @Association
    PaymentId paymentId;

    @HandleEvent
    static OrderFulfillment start(PlaceOrder placeOrder) {
        Fluxzero.sendAndForgetCommand(new ValidatePayment(placeOrder.details().paymentId(), placeOrder.orderId().toString()));
        return OrderFulfillment.builder()
                .orderId(placeOrder.orderId())
                .paymentId(placeOrder.details().paymentId())
                .build();
    }

    @HandleEvent
    void handle(PaymentAccepted event) {
        var order = Fluxzero.<Order>loadEntityValue(event.reference());
        Fluxzero.sendAndForgetCommand(new BackorderItems(order.details().itemIds(), order.orderId(),
                                                         order.details().addressee().fullName()));
    }

    @HandleEvent
    void handle(PaymentRejected event) {
        var order = Fluxzero.<Order>loadEntityValue(event.reference());
        Fluxzero.sendAndForgetCommand(new AbortOrder(order.orderId(), "Payment rejected"));
    }

    @HandleEvent
    void handle(BackorderFailed event) {
        Fluxzero.sendAndForgetCommand(new AbortOrder(event.orderId(), "Backorder failed"));
    }

    @HandleEvent
    OrderFulfillment handle(BackorderedItemArrived event) {
        var order = Fluxzero.loadAggregate(event.orderId()).get();
        var result = toBuilder().receivedItem(event.itemId()).build();
        if (result.receivedItems.containsAll(order.details().itemIds())) {
            Fluxzero.sendAndForgetCommand(new SendShipment(Fluxzero.generateId(ShipmentId.class),
                    new ShipmentDetails(result.receivedItems.stream().toList(), orderId,
                            order.details().addressee())));
        }
        return result;
    }

    @HandleEvent
    void handle(ShippingFailed event) {
        Fluxzero.sendAndForgetCommand(new AbortOrder(event.orderId(), "Shipment failed"));
    }

}
