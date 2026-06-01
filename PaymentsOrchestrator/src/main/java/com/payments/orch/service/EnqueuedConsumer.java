package com.payments.orch.service;

import com.events.billpay.BillPayEnqueued;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.orch.domain.Payment;
import com.payments.orch.domain.PaymentState;
import com.payments.orch.domain.ProcessedEvent;
import com.payments.orch.repository.PaymentRepo;
import com.payments.orch.repository.ProcessedEventRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class EnqueuedConsumer {

  private final PaymentRepo paymentRepo;
  private final ProcessedEventRepo processed;
  private final ObjectMapper om;

  @KafkaListener(topics="billpay.enqueued", groupId="payment-api")
  @Transactional
  public void onMessage(String message) throws Exception {
    var evt = om.readValue(message, BillPayEnqueued.class);
    if (processed.existsByHandlerAndEventId("enqueued", evt.getEventId())) return;

    Payment p = paymentRepo.findById(evt.getPaymentId()).orElse(null);
    if (p != null) {
      p.setState(PaymentState.BATCHED);
      p.setBatchId(evt.getBatchId());
      p.setUpdatedAt(OffsetDateTime.now());
      paymentRepo.save(p);
    }

    processed.save(ProcessedEvent.builder()
        .handler("enqueued")
        .eventId(evt.getEventId())
        .processedAt(OffsetDateTime.now())
        .build());
  }
}