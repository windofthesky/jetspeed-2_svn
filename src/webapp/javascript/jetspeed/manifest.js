dojo.provide("desktop.manifest");
dojo.require("dojo.string.extras");



dojo.provide("dojo.namespaces.dojo");
dojo.require("dojo.ns");

(function(){
    //mapping of all widget short names to their full package names
    // This is used for widget autoloading - no dojo.require() is necessary.
    // If you use a widget in markup or create one dynamically, then this
    // mapping is used to find and load any dependencies not already loaded.
    // You should use your own namespace for any custom widgets.
    // For extra widgets you use, dojo.declare() may be used to explicitly load them.
    var map = {
        html: {
            "editortable": "jetspeed.widget.EditorTable",
            "multirowheadertable": "jetspeed.widget.MultiRowHeaderTable",
            "portalaccordioncontainer": "jetspeed.widget.PortalAccordionContainer",
            "portalaccordionpane": "jetspeed.widget.PortalAccordionPane",
            "portalmenuoptionlink": "jetspeed.widget.PortalMenuOptionLink",
            "portaltabcontainer": "jetspeed.widget.PortalTabContainer",
            "portaltaskbar": "jetspeed.widget.PortalTaskBar",
            "portaltaskbaritem": "jetspeed.widget.PortalTaskBarItem",
            "portletdefcontainer": "jetspeed.widget.PortletDefContainer",
            "portletwindow": "jetspeed.widget.PortletWindow",
            "portletwindowresizehandle": "jetspeed.widget.PortletWindowResizeHandle",
            "portletdefcontaineritem": "jetspeed.widget.PortletDefContainerItem"
        }
    };

    function jetspeedNamespaceResolver(name, domain){
        if(!domain){ domain="html"; }
        if(!map[domain]){ return null; }
        return map[domain][name];    
    }

    dojo.registerNamespaceResolver("jetspeed", jetspeedNamespaceResolver);
})();




// This is a full custom namespace example
// By convention, myns lives in <dojo root>/../myns/, 
// and myns widgets are in myns.widget
// Convention paths are autodiscovered, and all we would 
// have to do here is register a resolver with 
// dojo.registerNamespaceResolver("myns", <resolver>);
/*
dojo.registerNamespaceManifest("jetspeed", "desktop", "jetspeed", "myns.widget",
    function(name){
        var module = "myns.widget."+dojo.string.capitalize(name);
        dojo.debug("resolver returning '"+module+"' for '"+name+"'"); 
        return module;
    }
);

*/