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

package org.jboss.interceptor.reader;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jboss.interceptor.spi.metadata.ClassMetadata;
import org.jboss.interceptor.spi.metadata.InterceptorMetadata;
import org.jboss.interceptor.spi.metadata.MethodMetadata;
import org.jboss.interceptor.spi.model.InterceptionType;


/**
 * @author <a href="mailto:mariusb@redhat.com">Marius Bogoevici</a>
 */
public class SimpleInterceptorMetadata implements InterceptorMetadata, Serializable
{
   
   private static final long serialVersionUID = 1247010247012491L;

   private ClassMetadata<?> interceptorClass;

   private Map<InterceptionType, List<MethodMetadata>> interceptorMethodMap;

   private boolean targetClass;

   public SimpleInterceptorMetadata(ClassMetadata<?> interceptorClass, boolean targetClass, Map<InterceptionType, List<MethodMetadata>> interceptorMethodMap)
   {
      this.interceptorClass = interceptorClass;
      this.targetClass = targetClass;
      this.interceptorMethodMap = interceptorMethodMap;
   }

   public List<MethodMetadata> getInterceptorMethods(InterceptionType interceptionType)
   {
      if (interceptorMethodMap != null)
      {
         List<MethodMetadata> methods = interceptorMethodMap.get(interceptionType);
         return methods == null ? Collections.<MethodMetadata>emptyList() : methods;
      }
      else
      {
         return Collections.<MethodMetadata>emptyList();
      }
   }

   public boolean isTargetClass()
   {
      return targetClass;
   }

   private Object writeReplace()
   {
     return new MetadataSerializationProxy(interceptorClass, targetClass);
   }

   private static class MetadataSerializationProxy implements Serializable
   {

      private ClassMetadata<?> classMetadata;
      private boolean targetClass;

      MetadataSerializationProxy(ClassMetadata<?> classMetadata, boolean targetClass)
      {
         this.classMetadata = classMetadata;
         this.targetClass = targetClass;
      }

      public Object readResolve()
      {
         return targetClass?
               InterceptorMetadataUtils.readMetadataForTargetClass(classMetadata):
               InterceptorMetadataUtils.readMetadataForInterceptorClass(classMetadata);
      }
   }
}
