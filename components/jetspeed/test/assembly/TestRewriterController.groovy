import org.picocontainer.defaults.DefaultPicoContainer
import org.apache.jetspeed.rewriter.JetspeedRewriterController
import org.apache.jetspeed.rewriter.BasicRewriter
import org.apache.jetspeed.rewriter.RulesetRewriterImpl
import org.apache.jetspeed.rewriter.html.SwingParserAdaptor
import org.apache.jetspeed.rewriter.xml.SaxParserAdaptor
import org.apache.jetspeed.components.ComponentAssemblyTestCase

applicationRoot = ComponentAssemblyTestCase.getApplicationRoot("components/jetspeed", "test")

// create the root container
container = new DefaultPicoContainer()

//
// Rewriter Test
//
mapping = applicationRoot + "/WEB-INF/conf/rewriter-rules-mapping.xml"
// rewriters [basic, ruleset] (not required)
rewriterClasses = [ org.apache.jetspeed.rewriter.BasicRewriter,
                    org.apache.jetspeed.rewriter.RulesetRewriterImpl ]
// adaptors [html, xml] (not required)
adaptorClasses = [ org.apache.jetspeed.rewriter.html.SwingParserAdaptor,
                   org.apache.jetspeed.rewriter.xml.SaxParserAdaptor ]
container.registerComponentInstance("RewriterController", 
                                    new JetspeedRewriterController(mapping, rewriterClasses, adaptorClasses))

return container