package com.ddm.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ddm.server.annotation.base.AutowiredManager;
import com.ddm.server.annotation.base.SynchronizedManager;
import com.ddm.server.annotation.base.TransactionManager;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@TransactionManager
@AutowiredManager
@SynchronizedManager
public @interface Task {

}
