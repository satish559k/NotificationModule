package com.NotificationModule.NotificationModule.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NotificationKafkaMessage {
    UUID productId;
    UUID userId;
    Integer quantity;
}
