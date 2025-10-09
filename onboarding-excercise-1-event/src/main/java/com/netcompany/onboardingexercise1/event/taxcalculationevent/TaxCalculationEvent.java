package com.netcompany.onboardingexercise1.event.taxcalculationevent;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TaxCalculationEvent {
    private String taxNumber;
    private BigDecimal calculatedTax;
}
