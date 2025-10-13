package com.netcompany.onboardingexercise1.event.taxcalculationevent;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TaxCalculationEvent {

    @NotNull
    private String taxNumber;

    @NotNull
    private BigDecimal calculatedTax;
}
