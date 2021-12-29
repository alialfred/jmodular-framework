package com.alisoftclub.frameworks.modular.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TimerEvent {

    String name() default "timer-task";

    int timeout() default 1000;

    boolean daemon() default false;

    int priority() default 5;
}
