package com.netcompany.onboardingexercise1.rest.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BatchConsumption<T> {
    List<T> eventsBatch;

    Integer messagesLeft;

    Boolean hasMore;

}
