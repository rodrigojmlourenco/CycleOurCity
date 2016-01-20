function initMap(lat, lon, zoom){
    var osmUrl='http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
    var osmAttrib='Map data © OpenStreetMap contributors, Routing powered by OpenTripPlanner';
    var osm = new L.TileLayer(osmUrl, {minZoom: 8, maxZoom: 18, attribution: osmAttrib});

    var ocmUrl = 'http://tile.opencyclemap.org/cycle/{z}/{x}/{y}.png';
    var ocmAttrib='Map data © OpenCycleMap contributors, Routing powered by OpenTripPlanner';
    var ocm = new L.tileLayer(ocmUrl, {minZoom: 8, maxZoom: 18, attribution: ocmAttrib});

    var osmMapQuestUrl = 'http://{s}.mqcdn.com/tiles/1.0.0/osm/{z}/{x}/{y}.png';
    var subDomains = ['otile1','otile2','otile3','otile4'];
    var mapQuestAttrib = 'Map data © MapQuest contributors, Routing powered by OpenTripPlanner';

    var osmMapQuest = new L.TileLayer(osmMapQuestUrl, {minZoom: 8, maxZoom: 18, attribution: mapQuestAttrib, subdomains: subDomains});

    //var osmSatelliteMapQuestUrl = 'http://{s}.mqcdn.com/tiles/1.0.0/sat/{z}/{x}/{y}.png'
    //var osmSatelliteMapQuest = new L.TileLayer(osmSatelliteMapQuestUrl, {maxZoom: 18, attribution: mapQuestAttrib, subdomains: subDomains});

    var baseMaps = {
        "OpenCycleMap": ocm,
        "OpenStreetMap": osm,
        "OSM MapQuest": osmMapQuest
        //"Satélite MapQuest": osmSatelliteMapQuest
    };

    MyApp.Map = L.map('map', {layers: ocm, boxZoom: false, doubleClickZoom: false});

    MyApp.Map.setView(new L.LatLng(lat, lon), zoom);

    MyApp.SwitchLayerControl = L.control.layers(baseMaps);
    MyApp.SwitchLayerControl.addTo(MyApp.Map);

    //Quando o utilizador clica no botão direito
    MyApp.Map.on('contextmenu', function(e) {
        MyApp.LastPointClicked = e;
    });


    //Quando o utilizador navega pelo mapa..
    //map.on('mousemove', showCurrentCoordinates);


    var legLegend = L.control({position: 'bottomright'});

    legLegend.onAdd = function (map) {
        var div = L.DomUtil.create("div", 'info legend legLegend');
        div.innerHTML = '<b>Atenção: este trajeto inclui partes para fazer a pé, assinaladas a amarelo.</b></br>';
        return div;
    };

    MyApp.Map.addControl(legLegend);


    var streetEdgesLegend = L.control({position: 'bottomright'});

    streetEdgesLegend.onAdd = function (map) {
        var div = L.DomUtil.create("div", 'info legend streetEdgesLegend');

        div.innerHTML = '<i style="background:' + MyApp.StreetEdgeColor + '"></i>' + "Troços classificáveis";

        return div;
    };

    MyApp.Map.addControl(streetEdgesLegend);

    /* var streetEdgesSafetyColorsLegend = L.control({position: 'bottomright'});

    streetEdgesSafetyColorsLegend.onAdd = function (map) {
        var div = L.DomUtil.create("div", 'info legend safetyColorsStreetEdgesLegend');

        for(var i = 5; i >= 0; i--){
            div.innerHTML += '<i style="background:' + MyApp.RatingColors.colors[i] + '"></i>' + MyApp.RatingScales.safety[i] + "</br>";
        }

        return div;
    };

    MyApp.Map.addControl(streetEdgesSafetyColorsLegend);

    var streetEdgesElevationColorsLegend = L.control({position: 'bottomright'});

    streetEdgesElevationColorsLegend.onAdd = function (map) {
        var div = L.DomUtil.create("div", 'info legend elevationColorsStreetEdgesLegend');

        for(var i = 0; i < MyApp.RatingScales.elevation.length; i++){
            div.innerHTML += '<i style="background:' + MyApp.RatingColors.colors[i] + '"></i>' + MyApp.RatingScales.elevation[i] + "</br>";
        }

        return div;
    };

    MyApp.Map.addControl(streetEdgesElevationColorsLegend); */

    // create an orange rectangle
    L.rectangle(MyApp.bounds, {color: "#ff7800", weight: 3, clickable:false}).addTo(MyApp.Map);

    // zoom the map to the rectangle bounds
    //MyApp.Map.fitBounds(MyApp.bounds);
    //map.addControl(popupInfoControl);
}


function addStreetEdgesToMap(streetEdges){
    MyApp.Polylines.polylineStreetEdges = new Array();

    var streetEdge;

    var flagWalkMode = false;

    for(var s in streetEdges){
        streetEdge = streetEdges[s];

        var polylineStreetEdge = new L.EncodedPolyline(streetEdge.Geometry);

        polylineStreetEdge.options.weight = 7;
        if(streetEdge.BicycleMode == '0'){
            flagWalkMode = true;
            polylineStreetEdge.options.color = MyApp.WalkLegColor;
        }
        else{
            polylineStreetEdge.options.color = MyApp.StreetEdgeColor;
        }

        polylineStreetEdge.id = streetEdge.IdStreetEdge;
        polylineStreetEdge.color = polylineStreetEdge.options.color;

        if(streetEdge.BicycleMode != '0'){
            polylineStreetEdge.on('click', function(e) {
                showFormToRateIfNotExists(this.id);
                highlightSelectedLayer(MyApp.Layers.layersGroupStreetEdges, this, e);
            });
        }

        MyApp.Polylines.polylineStreetEdges.push(polylineStreetEdge);
    }

    MyApp.Layers.layersGroupStreetEdges = L.featureGroup(MyApp.Polylines.polylineStreetEdges);
    addLayerGroup(MyApp.Layers.layersGroupStreetEdges);

    if(flagWalkMode){
        if(jQuery('#walkStreetEdge').size() == 0){
            jQuery('.streetEdgesLegend').append('<p id="walkStreetEdge"><i style="background:' + MyApp.WalkLegColor + '"></i>' + "Troços percorridos a pé (não classificáveis)</p>");
        }
    }
    else{
        jQuery('#walkStreetEdge').remove();
    }

    jQuery('.streetEdgesLegend').show();
}


// Esta função é para comentar depois
function addRatedStreetEdgesToMap(){
    jQuery.ajax({
        type: "POST",
        url: "RatedStreetEdges.php",
        data: {},
        beforeSend : function() {
           jQuery.blockUI({css: {
                            border: 'none',
                            padding: '15px',
                            backgroundColor: '#000',
                            '-webkit-border-radius': '10px',
                            '-moz-border-radius': '10px',
                            opacity: .5,
                            color: '#fff'
                            }, message: "A carregar arcos classificados" });
        },
        dataType: "json",
        success: function(streetEdges){
            var polylineStreetEdges = new Array();

            var streetEdge;

            for (var i=0; i < streetEdges.ratedStreetEdges.length; i++){
                streetEdge = streetEdges.ratedStreetEdges[i];
                var polylineStreetEdge = new L.EncodedPolyline(streetEdge.Geometry);


                polylineStreetEdge.options.weight = 7;
                polylineStreetEdge.options.color = MyApp.WalkLegColor;
                polylineStreetEdge.id = 5;
                polylineStreetEdge.color = polylineStreetEdge.options.color;
                /*if(streetEdge.BicycleMode != '0'){
                    polylineStreetEdge.on('click', function(e) {
                        showFormToRateIfNotExists(this.id);
                        highlightSelectedLayer(MyApp.Layers.layersGroupStreetEdges, this, e);
                    });
                } */

                polylineStreetEdges.push(polylineStreetEdge);
            }

            console.log(polylineStreetEdges.length);
            var layersGroupStreetEdges = L.featureGroup(polylineStreetEdges);
            addLayerGroup(layersGroupStreetEdges);
            jQuery.unblockUI();
        }
   });
}


function isLastLayer(numSelectedLayers){
  //numero de layers para classificar
  var numLayers = 0;

  for(var index in MyApp.Layers.layersGroupStreetEdges._layers)
      if(MyApp.Layers.layersGroupStreetEdges._layers[index].options.color != MyApp.WalkLegColor)
           numLayers++;

  return numLayers == numSelectedLayers)
}
