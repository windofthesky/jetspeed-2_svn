/* ========================================================================
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================================================================
 */
import org.picocontainer.defaults.DefaultPicoContainer
import org.apache.jetspeed.locator.JetspeedTemplateLocator
import org.apache.jetspeed.locator.JetspeedTemplateDescriptor
import org.apache.jetspeed.locator.JetspeedLocatorDescriptor
import org.apache.jetspeed.components.ComponentAssemblyTestCase

applicationRoot = ComponentAssemblyTestCase.getApplicationRoot("components/jetspeed", "test")

// create the root container
container = new DefaultPicoContainer()

//
// Template Locator component assembly 
//

roots = [ applicationRoot + "/WEB-INF/templates" ]
omClasses = [ org.apache.jetspeed.locator.JetspeedTemplateDescriptor,
              org.apache.jetspeed.locator.JetspeedLocatorDescriptor ]
defaultType = "email"
container.registerComponentInstance("TemplateLocator", 
                                    new JetspeedTemplateLocator(roots, omClasses, defaultType, applicationRoot))

return container
