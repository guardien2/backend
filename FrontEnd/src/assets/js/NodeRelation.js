//empty the nodeGraphDiv
function removeNodeGraph(){
 
    document.getElementById("NodeGraphDiv").innerHTML = "";
    
}


(function () {
    
    
    window.drawNodeGraph = function (searchValue) {
        removeNodeGraph();
        alert(searchValue);
        var drag, force, graph, height, l, links, n, nodes, vis, width, _i, _j, _len, _len2, _ref, _ref2;
        width = 1920;
        height = 800;
        /* create the SVG
        */
        vis = d3.select(".nodeGraphClass").append('svg').attr('width', width).attr('height', height);
        /* prepare nodes and links selections
        */
       
        nodes = vis.selectAll('.node');
        links = vis.selectAll('.link');
        /* initialize the force layout
        */
        force = d3.layout.force().size([width, height]).charge(-3000).linkDistance(20).on('tick', (function () {
      /* update nodes and links
      */      nodes.attr('transform', function (d) {
            return "translate(" + d.x + "," + d.y + ")";
        });
            return links.attr('x1', function (d) {
                return d.source.x;
            }).attr('y1', function (d) {
                return d.source.y;
            }).attr('x2', function (d) {
                return d.target.x;
            }).attr('y2', function (d) {
                return d.target.y;
            });
        }));
        /* define a drag behavior to drag nodes
        */
        /* dragged nodes become fixed
        */
        drag = force.drag().on('dragstart', function (d) {
            return d.fixed = true;
        });
        /* create some fake data
        */
        d3.json(searchValue, function (graph) {



            /* resolve node IDs (not optimized at all!)
            */
            _ref = graph.links;
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                l = _ref[_i];
                _ref2 = graph.nodes;
                for (_j = 0, _len2 = _ref2.length; _j < _len2; _j++) {
                    n = _ref2[_j];
                    if (l.source === n.id) {
                        l.source = n;
                        continue;
                    }
                    if (l.target === n.id) {
                        l.target = n;
                        continue;
                    }
                }
            }

            /* create nodes and links
            */
            /* (links are drawn first to make them appear under the nodes)
            */
            /* also, overwrite the selections with their databound version
            */
            links = links.data(graph.links).enter().append('line').attr('class', 'link');
            nodes = nodes.data(graph.nodes).enter().append('g').attr('class', 'node').call(drag);

            nodes.append('circle').attr('r', 12);
            /* draw the label
            */
            nodes.append('text').text(function (d) {
                return d.name;
            }).attr('dy', '0.35em');
            /* run the force layout
            */
            return force.nodes(graph.nodes).links(graph.links).start();
        });
    };


}).call(this);

