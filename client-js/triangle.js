/* Author: Nelson Nunes*/

bikeTriangle = {

    div : null,
    cursor_size : 19,

    triangleTimeFactor: null,
    triangleSlopeFactor: null,
    triangleSafetyFactor: null,

    timeFactor: 0.34,
    slopeFactor: 0.33,
    safetyFactor: 0.33,

    onChanged : null,

    initialize : function(divID) {
        this.div = document.getElementById(divID);
        this.render();
    },

    render : function() {

        var this_ = this;

        var width = jQuery(this.div).width(), height = jQuery(this.div).height();
        var tri_side = 2 * (height - this.cursor_size) * 1/Math.sqrt(3);
        var margin = this.cursor_size/2;

        //console.log()

        var canvas = Raphael(jQuery(this.div).attr('id'), width, height);

        var bg = canvas.rect(0,0,width,height).attr({
              stroke: 'none',
              fill: '#eee'
          });

        var triangle = canvas.path(["M",margin+tri_side/2,margin,"L",margin+tri_side,height-margin,"L",margin,height-margin,"z"]);


        triangle.attr({fill:"#ddd", stroke:"none"});

        var labelSize = "18px";

        var safeFill = "#bbe070";
        var safeFill2 = "#77b300";
        var safeName = "Menos Tráfego"; //locale.bikeTriangle.safeName;
        var safeSym = "MT"; //locale.bikeTriangle.safeSym;

        var hillFill = "#8cc4ff";
        var hillFill2 = "#61a7f2";
        var hillName = "Plano"; //locale.bikeTriangle.hillName;
        var hillSym = "P"; //locale.bikeTriangle.hillSym;

        var timeFill = "#ffb2b2";
        var timeFill2 = "#f27979";
        var timeName = "Curto"; //locale.bikeTriangle.timeName;
        var timeSym = "C"; //locale.bikeTriangle.timeSym;

        var labelT = canvas.text(margin + tri_side/2, margin+24, timeSym);
        labelT.attr({fill:timeFill2, "font-size":labelSize, "font-weight":"bold"});

        var labelH = canvas.text(margin + 22, height-margin-14, hillSym);
        labelH.attr({fill:hillFill2, "font-size":labelSize, "font-weight":"bold"});

        var labelS = canvas.text(margin + tri_side - 22, height-margin-14, safeSym);
        labelS.attr({fill:safeFill2, "font-size":labelSize, "font-weight":"bold"});

        var barLeft = margin*2 + tri_side;
        var barWidth = width - margin*3 - tri_side;
        var barHeight = (height-margin*4)/3;

        var timeBar = canvas.rect(barLeft, margin, barWidth*.333, barHeight);
        timeBar.attr({fill:timeFill, stroke:"none"});

        var topoBar = canvas.rect(barLeft, margin*2+barHeight, barWidth*.333, barHeight);
        topoBar.attr({fill:hillFill, stroke:"none"});

        var safetyBar = canvas.rect(barLeft, margin*3 + barHeight*2, barWidth*.333, barHeight);
        safetyBar.attr({fill:safeFill, stroke:"none"});

        var timeLabel = canvas.text(barLeft + barWidth/2, margin+barHeight/2, timeName + ": 33%");
        timeLabel.attr({"font-size":"15px", opacity:1});

        var topoLabel = canvas.text(barLeft + barWidth/2, margin*2+barHeight+barHeight/2, hillName + ": 33%");
        topoLabel.attr({"font-size":"15px", opacity:1});

        var safetyLabel = canvas.text(barLeft + barWidth/2, margin*3+barHeight*2+barHeight/2, safeName + ": 33%");
        safetyLabel.attr({"font-size":"15px", opacity:1});

        var cx = margin+tri_side/2, cy = height-margin-(1/Math.sqrt(3))*(tri_side/2);
        var cursorVert = canvas.rect(cx-.5, cy-this.cursor_size/2-2, 1, this.cursor_size+4).attr({
            fill: "rgb(0,0,0)",
            stroke: "none"
        });
        var cursorHoriz = canvas.rect(cx-this.cursor_size/2-2, cy-.5, this.cursor_size+4, 1).attr({
            fill: "rgb(0,0,0)",
            stroke: "none"
        });
        var cursor = canvas.circle(cx, cy, this.cursor_size/2).attr({
            fill: "rgb(128,128,128)",
            stroke: "none",
            opacity: 0.25
        });

        var time, topo, safety;

        var thisBT = this;
        var animTime = 250;
        var start = function () {
            // storing original coordinates
            this.ox = this.attr("cx");
            this.oy = this.attr("cy");
            timeBar.animate({opacity: .25}, animTime);
            topoBar.animateWith(timeBar, {opacity: .25}, animTime);
            safetyBar.animateWith(timeBar, {opacity: .25}, animTime);

        },
        move = function (dx, dy) {
            // move will be called with dx and dy
            var nx = this.ox + dx, ny = this.oy + dy;
            if(ny > height-margin) ny = height-margin;
            if(ny < margin) ny = margin;
            var offset = (ny-margin)/(height-margin*2) * tri_side/2;
            if(nx < margin + (tri_side/2) - offset) nx = margin + (tri_side/2) - offset;
            if(nx > margin + (tri_side/2) + offset) nx = margin + (tri_side/2) + offset;

            time = ((height-2*margin)-(ny-margin))/(height-2*margin);
            topo = thisBT.distToSegment(nx, ny, margin+tri_side/2, margin, margin+tri_side, height-margin)/(height-2*margin);
            safety = 1- time - topo;

            //console.log(Math.round(time*100)/100 + " " + Math.round(topo*100)/100 + " " + Math.round(safety*100)/100);

            timeBar.attr({width: barWidth*time});
            topoBar.attr({width: barWidth*topo});
            safetyBar.attr({width: barWidth*safety});
            timeLabel.attr("text", timeName + ": "+Math.round(time*100)+"%");
            topoLabel.attr("text", hillName + ": " +Math.round(topo*100)+"%");
            safetyLabel.attr("text", safeName + ": " +Math.round(safety*100)+"%");

            this.attr({cx: nx, cy: ny});
            cursorVert.attr({x: nx-.5, y: ny-thisBT.cursor_size/2-2});
            cursorHoriz.attr({x: nx-thisBT.cursor_size/2-2, y: ny-.5});
        },
        up = function () {

            // restoring state
            timeBar.animate({opacity: 1}, animTime);
            topoBar.animateWith(timeBar, {opacity: 1}, animTime);
            safetyBar.animateWith(timeBar, {opacity: 1}, animTime);

            // was seeing really odd small numbers in scientific notation when topo neared zero so added this
            if(topo < 0.005) {
                topo = 0.0;
            }

            thisBT.timeFactor = time;
            thisBT.slopeFactor = topo;
            thisBT.safetyFactor = safety;
            if(this_.onChanged && typeof(this_.onChanged) === "function") {
                this_.onChanged();
            }
        };

        cursor.drag(move, start, up);
        cursor.mouseover(function() {
            this.animate({opacity: 0.5}, animTime);
        });
        cursor.mouseout(function() {
            this.animate({opacity: 0.25}, animTime);
        });

    },

    distance : function(x1, y1, x2, y2) {
        return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
    },

    distToSegment : function(px, py, x1, y1, x2, y2) {
        var r, dx ,dy;
        dx = x2 - x1;
        dy = y2 - y1;
        r = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);
        return this.distance(px, py, (1 - r) * x1 + r * x2, (1 - r) * y1 + r * y2);
    },

    getFactors : function() {
        return {
                triangleTimeFactor : this.timeFactor,
                triangleSlopeFactor : this.slopeFactor,
                triangleSafetyFactor : this.safetyFactor
        }
    }

};

bikeTriangle.initialize("bikeTriangle");

function ArrayToURL(array) {
  var pairs = [];
  for (var key in array)
    if (array.hasOwnProperty(key))
      pairs.push(encodeURIComponent(key) + '=' + encodeURIComponent(array[key]));
  return pairs.join('&');
}
