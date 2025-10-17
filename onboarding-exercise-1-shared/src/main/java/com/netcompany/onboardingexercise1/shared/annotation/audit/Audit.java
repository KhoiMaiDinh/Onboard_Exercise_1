package com.netcompany.onboardingexercise1.shared.annotation.audit;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Audit {
    String action() default "";
}
