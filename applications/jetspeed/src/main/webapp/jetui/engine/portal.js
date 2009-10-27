//Use loader to grab the modules needed
YUI(yuiConfig).use('console', 'dd', 'anim', 'io', 'cookie', 'json', 'widget', function(Y) {
	//new Y.Console().render(); 
    //Make this an Event Target so we can bubble to it
    var Portal = function() {
        Portal.superclass.constructor.apply(this, arguments);
    };
    Portal.NAME = 'portal';
    Portal.prototype.desktopMode = false;
    Portal.prototype.jstbLeft;
    Portal.prototype.jstbRight;
    Portal.prototype.isMoving;
    Y.extend(Portal, Y.Base);
    var portal = new Portal();
    portal.isMoving = false;
    var goingUp = false, goingRight = false, lastY = 0, lastX = 0;   
    
    ////////////////////////////////////////////////////
    // the Portlet Class
    function Portlet(config) {
        Portlet.superclass.constructor.call(this, config);
    };
    Y.extend(Portlet, Y.Base, {
    	initializer : function(cfg) { 
   	 	},
        destructor : function(cfg) { 
   	 	}     	    	
    });    
    Portlet.NAME = "portlet";
    Portlet.ATTRS = {
    	"name" : { value: "undefined" }, 
        "id" : { value: "0" },
        "toolbar" : { value : false },
        "detached" : { value : false }
    };
	Portlet.prototype.info = function() {
		Y.log("name: " + this.get("name"));
		Y.log("id  : " + this.get("id"));		
		Y.log("toolbar  : " + this.get("toolbar"));		
		Y.log("---------");
    };
    
    ////////////////////////////////////////////////////    
    // Create Navigator Portlet
    var navigator = new Portlet();
    navigator.set("name", "j2-admin::JetspeedNavigator");
    navigator.set("id", "_JetspeedNavigator");
    navigator.set("toolbar", true);
    navigator.set("detached", false);
    var toolbox = new Portlet();
    toolbox.set("name", "j2-admin::JetspeedToolbox");
    toolbox.set("id", "_JetspeedToolbox");
    toolbox.set("toolbar", true);
    toolbox.set("detached", false);

//    var toggleMoveMode = function(e) { 
//	    window.location = window.location + "?toggle=move";
//    }       
//    Y.on("click", toggleMoveMode, "#jstbMoveMode");
    
    ////////////////////////////////////////////////////    
    // setup toolbar docking area and togglers    
    var lhsToggler = Y.get('#jstbLeftToggle');
    var rhsToggler = Y.get('#jstbRightToggle');    
    // add fx plugin to docking area
    portal.jstbLeft = Y.get('#jstbLeft').plug(Y.Plugin.NodeFX, {
        from: { width: 1 },
        to: {
            width: function(node) { // dynamic in case of change
                return 200; //node.get('scrollWidth'); // get expanded height (offsetHeight may be zero)
            }
        },
        easing: Y.Easing.easeOut,
        duration: 0.3
    });    
    portal.jstbRight = Y.get('#jstbRight').plug(Y.Plugin.NodeFX, {
        from: { width: 1 },
        to: {
            width: function(node) { // dynamic in case of change
                return 200; //node.get('scrollWidth'); // get expanded height (offsetHeight may be zero)
            }
        },
        easing: Y.Easing.easeOut,
        duration: 0.3
    });        
    var onClickToolbar = function(e) {
//    	nav.setStyle('z-index', '500');
    }

    var onClickToggler = function(e) {
    	var id = e.target.getAttribute('id');
    	var toggler = lhsToggler;
    	var toolbar = portal.jstbLeft;
    	var compareStyle = 'jstbToggle1';
    	if (id.indexOf('Left') == -1)
    	{
    		toggler = rhsToggler;
    		toolbar = portal.jstbRight;
        	var compareStyle = 'jstbToggle2';    		
    	}
    	portal.toggleToolbar(toolbar, toggler, compareStyle);    	    	
    };
    lhsToggler.on('click', onClickToggler);
    rhsToggler.on('click', onClickToggler);

    ////////////////////////////////////////////////////       
    // drag and drop
    var nav = Y.get('#jsNavigator');
    nav.data = navigator;
    var ddNav = new Y.DD.Drag({
        node: nav,
        groups: ['toolbars'],
        dragMode: 'point'                
    }).plug(Y.Plugin.DDProxy, { 
      	 moveOnEnd: false         	    	
    });    
    ddNav.addHandle('.PTitle');
    nav.on('click', onClickToolbar);

    var jetspeedZone = Y.get('#jetspeedZone');
    var jzDrop = new Y.DD.Drop({
        node: jetspeedZone,
        groups: ['toolbars']        
    });
    
    var tb = Y.get('#jsToolbox');
    tb.data = toolbox;
    var ddToolbox = new Y.DD.Drag({
        node: tb,
        groups: ['toolbars'],                 
        dragMode: 'point'        
    }).plug(Y.Plugin.DDProxy, { 
      	 moveOnEnd: false         	    	
    });    
    ddToolbox.addHandle('.PTitle');
    tb.on('click', onClickToolbar);
    
    var drop = new Y.DD.Drop({
        node: Y.get('#jstbLeft'),
        groups: ['toolbars']
    });
    var drop = new Y.DD.Drop({
        node: Y.get('#jstbRight'),
        groups: ['toolbars']        
    });
    
    var draggablePortlets = Y.Node.all('.portal-layout-cell');    
    draggablePortlets.each(function(v, k) {
        var p = new Portlet();
        p.set("name", v.getAttribute("name"));
        p.set("id", v.getAttribute("id"));
        p.set("toolbar", false);
        p.set("detached", false);
        v.data = p;
        var ddNav = new Y.DD.Drag({
            node: v,
            groups: ['portlets'],
            dragMode: 'intersect'                    
        }).plug(Y.Plugin.DDProxy, { 
          	 moveOnEnd: false         	    	
        });    
        ddNav.addHandle('.PTitle');
    	var drop = new Y.DD.Drop({
            node: v,
            groups: ['portlets', 'toolbars']            
        });        
    });
    
    var dropLayoutColumns = Y.Node.all('.portal-layout-column');
    dropLayoutColumns.each(function(v, k) {
    	var drop = new Y.DD.Drop({
            node: v,
            groups: ['portlets']            
        });            	
    });
    
    Portal.prototype.toggleToolbar = function(toolbar, toggler, compareStyle) {
        toggler.toggleClass('jstbToggle1');
        toggler.toggleClass('jstbToggle2');
        var currentStyle = toggler.getAttribute('class');
        var nodelist = toolbar.get('children');
        if (currentStyle == compareStyle)
        {
            nodelist.setStyle('display', 'block');        	
        }
        else
        {
            nodelist.setStyle('display', 'none');        	
        }	        
        toolbar.fx.set('reverse', !toolbar.fx.get('reverse')); // toggle reverse 
        toolbar.fx.run();
	};
	    
    
	Y.DD.DDM.on('drag:drophit', function(e) {
        var drop = e.drop.get('node'),
            drag = e.drag.get('node');
        if (drag.data.get("toolbar"))
        {        	
        	Y.log("drop hit of toolbar: " + drop.getAttribute('id'));
            if (drop == portal.jstbLeft || drop == portal.jstbRight)
            {
	        	drag.setStyle('position', '');
				drag.setStyle('top', '');
				drag.setStyle('left', '');        		
				drop.appendChild(drag);
            }
            else
            {            		
				var dragParent = drag.get('parentNode');
            	drag.setStyle('position', 'absolute');
				drag.setStyle('top', e.drag.region.top + "px");
				drag.setStyle('left', e.drag.region.left + "px");        		
				jetspeedZone.appendChild(drag);
				if (dragParent.get("children").size() == 0)
				{        	        
				    if (dragParent == portal.jstbLeft) {
						portal.toggleToolbar(dragParent, lhsToggler, "jstbToggle1");
				    }
				    else  if (dragParent == portal.jstbRight) {
				    	portal.toggleToolbar(dragParent, rhsToggler, "jstbToggle2");        	
				    }
				}              				
            }
        }
    });
	
	  Portal.prototype.movePortlet = function(e)
	  {
        var drop = e.drop.get('node'),
            drag = e.drag.get('node');
        var dragParent = drag.get('parentNode');
        var dropParent = drop.get('parentNode');

		Y.log("HIT: " + drop.data.get('id'));
        
        if (dropParent == portal.jstbLeft || dropParent == portal.jstbRight)
        {
          if (!dropParent.contains(drag)) {
        	  dropParent.appendChild(drag);
          }
        }
        else
        {
        	if (goingUp)
        	{
    			Y.log("going UP");
        		// var next = drop.get('previousSibling');
    			var prev = drop.previous();
                if (prev == null)
                {
                	//drag.remove();                	
                	dropParent.prepend(drag);                	
                }
                else
                {
        			//drag.remove();
                	dropParent.insertBefore(drag, drop);
                }        		
        	}
        	else
        	{
        		var next = drop.next();
                if (next == null) 
                {
        			Y.log("going down APPEND");
        			//drag.remove();
        			dropParent.appendChild(drag);
                }
                else
                {
        			Y.log("going down: " + next); //next.data.get('name'));
        			//drag.remove();
        			dropParent.insertBefore(drag, next);
                }
        	}
        }
    };    

    Y.DD.DDM.on('drag:end', function(e) {
        var drag = e.target;
        if (drag.target) {
            drag.target.set('locked', false);
        }
        if (drag.get('node').data.get("toolbar"))
        {
            drag.get('node').setStyle('visibility', '');
        }
        else
        {
            var x = drag.get('node').all('div');
            if (x != null)
            {
            	x.setStyle('visibility', '');
            }
        }        
        //drag.get('node').setStyle('border', '');                
        drag.get('node').removeClass('moving');
        drag.get('dragNode').set('innerHTML', '');
    });
    
    Y.DD.DDM.on('drag:start', function(e) {
        var drag = e.target;
//        if (drag.target) {
//            drag.target.set('locked', true);
//        }
        var dragNode = drag.get('dragNode');
        dragNode.set('innerHTML', drag.get('node').get('innerHTML'));
        dragNode.setStyle('opacity','.5');
        dragNode.setAttribute('class', 'portlet purpleplanet');

        if (drag.get('node').data.get("toolbar"))
        {
        	drag.get('node').setStyle('visibility', 'hidden');        	
        }
        else
        {
        	var x = drag.get('node').all('div');
        	if (x != null)
        	{
        		x.setStyle('visibility', 'hidden');
        	}
        }
        drag.get('node').setStyle('border', '1px dotted #black');        
        drag.get('node').addClass('moving');
        
        lastX = drag.mouseXY[0];
        lastY = drag.mouseXY[1];
        //Y.log("starting drag " + lastX +  " , " + lastY);
    });

    Y.DD.DDM.on('drag:over', function(e) {
    	if (portal.isMoving)
    		return;
    	
    	var x = e.drag.mouseXY[0],
    		y = e.drag.mouseXY[1];
    	
    	if (y == lastY)
    	{    		
    	}
    	else if (y < lastY) {
            goingUp = true;
            
        } else {
            goingUp = false;
        }
        lastY = y;
        if (x < lastX) {
            goingRight = false;
        } else {
            goingRight = true;
        }        
        lastX = x;
        
        if (e.drag.get('node').data.get("toolbar"))
        {        
            var drop = e.drop.get('node'),
            drag = e.drag.get('node');            
            var dragParent = drag.get('parentNode');
            var dropParent = drop.get('parentNode');        	
            if (drop == portal.jstbLeft || drop == portal.jstbRight)
            {
				if (!drop.contains(drag)) {
					  drop.appendChild(drag);
				}
				// close up the toolbar leaving from
				if (dragParent.get("children").size() == 0)
				{        	        
				    if (dragParent == portal.jstbLeft) {
						portal.toggleToolbar(dragParent, lhsToggler, "jstbToggle1");
				    }
				    else  if (dragParent == portal.jstbRight) {
				    	portal.toggleToolbar(dragParent, rhsToggler, "jstbToggle2");        	
				    }
				}              
            }        	
            else // jetspeed drop zone
            {
            	//Y.log("hovering over the zone");
            }
        }
        else
        {
	    	var region = e.drop.get('node').get('region');
	    	var srcRegion = e.drag.get('node').get('region');
	    	if (y >= srcRegion.top && y <= srcRegion.bottom && x >= srcRegion.left && x <= srcRegion.right)
	    	{
//	    		Y.log("dragging over src");
	    	}	    	
	    	else if (y >= region.top && y <= region.bottom && x >= region.left && x <= region.right)
	    	{
	    		Y.log("**** HIT");
	    		portal.isMoving = true;
	    		portal.movePortlet(e); 
	    		portal.isMoving = false;
	    	}
        }
		//Y.log("x,y = " + x + "," + y);
    	
    });
    
//    Y.DD.DDM.on('drag:drag', function(e) {
//        var x = e.target.mouseXY[0];
//    	var y = e.target.mouseXY[1];
//        if (y < lastY) {
//            goingUp = true;
//        } else {
//            goingUp = false;
//        }
//        if (x < lastX) {
//            goingRight = false;
//        } else {
//            goingRight = true;
//        }        
//        lastX = x;
//        Y.log("DRAG: x = " + x + " y " + y );
//    });
    
//    Y.DD.DDM.on('drop:enter', function(e) {
//    
//    	//var region = e.drop.region;
//    	var region = e.drop.get('node').get('region');     	
//    	Y.log("region = " + region.top + "," + region.bottom + " : " + region.left + ","  +region.right);
    
//        if (!e.drag || !e.drop || (e.drop !== e.target)) {
//            return false;
//        }
        //var id = e.drop.get('node').data.get('id');
        //Y.log("entering: " + p);
        
//        if (e.drop.get('node').get('tagName').toLowerCase() === 'li') {
//            if (e.drop.get('node').hasClass('item')) {
//                _moveMod(e.drag, e.drop);
//            }
//        }
//    });    
    
    
});
