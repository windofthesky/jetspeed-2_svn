import org.picocontainer.defaults.DefaultPicoContainer
import org.apache.jetspeed.cache.file.FileCache

// create the root container
container = new DefaultPicoContainer()

Long scanRate = 10
cacheSize = 20
container.registerComponentInstance(FileCache, new FileCache(scanRate, cacheSize))

return container