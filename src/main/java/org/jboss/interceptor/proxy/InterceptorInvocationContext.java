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

import javax.interceptor.InvocationContext;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:mariusb@redhat.com">Marius Bogoevici</a>
 */
public class InterceptorInvocationContext implements InvocationContext
{

   private Map<String, Object> contextData = new HashMap<String, Object>();

   private Method method;

   private Object[] parameters;
   
   private Object target;

   private InterceptionChain interceptionChain;

   public InterceptorInvocationContext(InterceptionChain interceptionChain, Object target, Method targetMethod, Object[] parameters)
   {
      this.interceptionChain = interceptionChain;
      this.method = targetMethod;
      this.parameters = parameters;
      this.target = target;
   }

   public Map<String, Object> getContextData()
   {
      return contextData;
   }

   public Method getMethod()
   {
      return method;
   }

   public Object[] getParameters()
   {
      return parameters;
   }

   public Object getTarget()
   {
      return target;
   }

   public Object proceed() throws Exception
   {
      return interceptionChain.invokeNext(this);
   }

   public void setParameters(Object[] params)
   {
      if (method != null)
         this.parameters = params;
   }
}
