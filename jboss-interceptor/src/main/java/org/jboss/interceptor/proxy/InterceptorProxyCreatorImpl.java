/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.interceptor.proxy;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import org.jboss.interceptor.model.InterceptionType;
import org.jboss.interceptor.model.InterceptorClassMetadata;
import org.jboss.interceptor.model.InterceptionModel;
import org.jboss.interceptor.registry.InterceptorRegistry;
import org.jboss.interceptor.registry.InterceptorClassMetadataRegistry;
import org.jboss.interceptor.InterceptorException;
import org.jboss.interceptor.util.InterceptionUtils;
import org.jboss.interceptor.util.proxy.TargetInstanceProxy;

import javax.interceptor.AroundInvoke;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.util.*;

import sun.reflect.ReflectionFactory;

/**
 * @author <a href="mailto:mariusb@redhat.com">Marius Bogoevici</a>
 */
public class InterceptorProxyCreatorImpl implements InterceptorProxyCreator
{

   private List<InterceptorRegistry<Class<?>, ?>> interceptorRegistries;

   private List<InterceptionHandlerFactory<?>> interceptionHandlerFactories;

   public InterceptorProxyCreatorImpl(List<InterceptorRegistry<Class<?>, ?>> interceptorRegistries, List<InterceptionHandlerFactory<?>> interceptionHandlerFactories)
   {
      this.interceptorRegistries = interceptorRegistries;
      this.interceptionHandlerFactories = interceptionHandlerFactories;
   }

   public InterceptorProxyCreatorImpl(InterceptorRegistry<Class<?>, ?> interceptorRegistries, InterceptionHandlerFactory<?> interceptionHandlerFactories)
   {
      this.interceptorRegistries = Collections.<InterceptorRegistry<Class<?>, ?>>singletonList(interceptorRegistries);
      this.interceptionHandlerFactories = Collections.<InterceptionHandlerFactory<?>>singletonList(interceptionHandlerFactories);
   }


   public <T> T createProxyFromInstance(final Object target, Class<T> proxifiedClass, Class<?>[] constructorTypes, Object[] constructorArguments)
   {
      MethodHandler interceptorMethodHandler = getMethodHandler(target, proxifiedClass);
      return createProxyInstance(createProxyClassWithHandler(proxifiedClass, interceptorMethodHandler), interceptorMethodHandler);
   }

   public <T> T createProxyInstance(Class<T> proxyClass, MethodHandler interceptorMethodHandler)
   {
      try
      {
         ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
         Constructor<T> c = reflectionFactory.newConstructorForSerialization(proxyClass, Object.class.getDeclaredConstructor());
         T proxyObject = c.newInstance();
         if (interceptorMethodHandler != null)
         {
            ((ProxyObject) proxyObject).setHandler(interceptorMethodHandler);
         }
         return proxyObject;
      }
      catch (Exception e)
      {
         throw new InterceptorException(e);
      }
   }

   public static <T> Class<T> createProxyClass(Class<T> proxyClass)
   {
      ProxyFactory proxyFactory = new ProxyFactory();
      if (proxyClass != null)
      {
         proxyFactory.setSuperclass(proxyClass);
      }
      proxyFactory.setInterfaces(new Class<?>[]{LifecycleMixin.class, TargetInstanceProxy.class});
      Class<T> clazz = proxyFactory.createClass();
      return clazz;
   }

   public static <T> Class<T> createProxyClassWithHandler(Class<T> proxyClass, MethodHandler methodHandler)
   {
      ProxyFactory proxyFactory = new ProxyFactory();
      if (proxyClass != null)
      {
         proxyFactory.setSuperclass(proxyClass);
      }
      proxyFactory.setInterfaces(new Class<?>[]{LifecycleMixin.class, TargetInstanceProxy.class});
      proxyFactory.setHandler(methodHandler);
      Class<T> clazz = proxyFactory.createClass();
      return clazz;
   }


   public <T> MethodHandler getMethodHandler(Object target, Class<T> proxyClass, boolean includeTargetClass)
   {
      return new InterceptorMethodHandler(target, proxyClass, getModelsFor(proxyClass), interceptionHandlerFactories, includeTargetClass);
   }

   public <T> MethodHandler getMethodHandler(Object target, Class<T> proxyClass)
   {
      return this.getMethodHandler(target, proxyClass, true);
   }

   private <T> List<InterceptionModel<Class<?>, ?>> getModelsFor(Class<T> proxyClass)
   {
      List<InterceptionModel<Class<?>, ?>> interceptionModels = new ArrayList<InterceptionModel<Class<?>, ?>>();
      for (InterceptorRegistry interceptorRegistry : interceptorRegistries)
      {
         interceptionModels.add(interceptorRegistry.getInterceptionModel(proxyClass));
      }
      return interceptionModels;
   }

   public <T> T createProxyFromInstance(final Object target, Class<T> proxyClass) throws IllegalAccessException, InstantiationException
   {
      return createProxyFromInstance(target, proxyClass, new Class[0], new Object[0]);
   }

}


