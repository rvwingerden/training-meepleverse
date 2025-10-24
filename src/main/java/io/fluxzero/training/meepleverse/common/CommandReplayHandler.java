package io.fluxzero.training.meepleverse.common;

import io.fluxzero.common.MessageType;
import io.fluxzero.sdk.Fluxzero;
import io.fluxzero.sdk.common.exception.TechnicalException;
import io.fluxzero.sdk.tracking.Consumer;
import io.fluxzero.sdk.tracking.handling.HandleError;
import io.fluxzero.sdk.tracking.handling.Trigger;
import io.fluxzero.training.meepleverse.payments.api.ValidatePayment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Consumer(name = "my-dlq-1", minIndex = 1L)
class CommandReplayHandler {

    @HandleError
    void retry(TechnicalException error, @Trigger(messageType = MessageType.COMMAND) ValidatePayment failed) {
        log.error("command-dlq retry being called, for missed command {}", failed);
        Fluxzero.sendCommand(failed);
    }
}
