import org.apache.jetspeed.components.util.NanoQuickAssembler
import org.apache.jetspeed.pipeline.JetspeedPipeline
import org.apache.jetspeed.prefs.impl.PreferencesProviderImpl
import org.apache.jetspeed.security.impl.SecurityProviderImpl

// create the root container
container = new DefaultPicoContainer()

pipelineClass = org.apache.jetspeed.pipeline.JetspeedPipeline
pipelineDirectory = "/WEB-INF/conf/pipelines/"
pipelineDescriptor = "portlet-pipeline"

ClassLoader cl = Thread.currentThread().getContextClassLoader()

NanoQuickAssembler.assemble(cl, "org/apache/jetspeed/containers/rdbms.container.groovy", container)

NanoQuickAssembler.assemble(cl, "org/apache/jetspeed/containers/persistence.container.groovy", container)

//
// Preferences.
//
NanoQuickAssembler.assemble(cl, "org/apache/jetspeed/prefs/containers/prefs.container.groovy", container)

//
// Security
//
NanoQuickAssembler.assemble(cl, "org/apache/jetspeed/security/containers/security.container.groovy", container)

// Instantiate the Preferences provider.
container.getComponentInstanceOfType(PreferencesProviderImpl)

// Instantiate the Security provider.
container.getComponentInstanceOfType(SecurityProviderImpl)


return container


