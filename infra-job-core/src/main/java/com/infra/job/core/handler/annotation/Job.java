package com.infra.job.core.handler.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Job {

    String value();

    String init() default "";

    String destroy() default "";

}
