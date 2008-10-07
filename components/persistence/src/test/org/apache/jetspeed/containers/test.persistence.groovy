import org.picocontainer.defaults.DefaultPicoContainer
import org.apache.jetspeed.components.util.NanoQuickAssembler

container = new DefaultPicoContainer()

ClassLoader cl = Thread.currentThread().getContextClassLoader()

NanoQuickAssembler.assemble(cl, "org/apache/jetspeed/containers/rdbms.container.groovy", container)

NanoQuickAssembler.assemble(cl, "org/apache/jetspeed/containers/persistence.container.groovy", container)


return container