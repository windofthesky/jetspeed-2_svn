/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security;

/**
 * @author <a href="mailto:vkumar@apache.org">Vivek Kumar</a>
 * @version $Id:
 * 
 * PrincipalManagerEventAdapter will allow to extends it self and over-ride desired methods
 * to have custom operation on the events
 */
public class PrincipalManagerEventAdapter implements PrincipalManagerEventListener {

	public void associationAdded(JetspeedPrincipal fromPrincipal,
			JetspeedPrincipal toPrincipal, String associationName) {
		//Do nothing
	}

	public void associationRemoved(JetspeedPrincipal fromPrincipal,
			JetspeedPrincipal toPrincipal, String associationName) {
		//Do nothing		
	}

	public void newPrincipal(JetspeedPrincipal principal) {
		//Do nothing		
	}

	public void removePrincipal(JetspeedPrincipal principal) {
		//Do nothing
	}

	public void updatePrincipal(JetspeedPrincipal principal) {
		//Do nothing
	}

}
