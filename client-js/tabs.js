//remove um grupo de layers do mapa
function removeLayerGroup(layerGroup) {
    if(layerGroup != null){
        if(MyApp.Map.hasLayer(layerGroup)){
            MyApp.Map.removeLayer(layerGroup);
        }
    }
}

//adiciona um grupo de layers ao mapa
function addLayerGroup(layerGroup) {
    if(layerGroup != null){
        layerGroup.addTo(MyApp.Map);
        MyApp.Map.fitBounds(layerGroup.getBounds());
        return true;
    }
    else{
        return false;
    }
}


//quando o utilizador muda para o separador 'Planear' é necessario adicionar os marcadores e
//ajustar a area do mapa
function addStartAndEndMarkerTab1(){
    var lats = [];
    var lngs = [];

    if(MyApp.Markers.startAtMarker != null){
        lats.push(MyApp.Markers.startAtMarker.getLatLng().lat);
        lngs.push(MyApp.Markers.startAtMarker.getLatLng().lng);
        if(!MyApp.Map.hasLayer(MyApp.Markers.startAtMarker)){
            MyApp.Markers.startAtMarker.addTo(MyApp.Map);
        }
    }

    if(MyApp.Markers.endAtMarker != null){
        lats.push(MyApp.Markers.endAtMarker.getLatLng().lat);
        lngs.push(MyApp.Markers.endAtMarker.getLatLng().lng);
        if(!MyApp.Map.hasLayer(MyApp.Markers.endAtMarker)){
            MyApp.Markers.endAtMarker.addTo(MyApp.Map);
        }
    }

    if(MyApp.Markers.startAtMarker != null || MyApp.Markers.endAtMarker != null){
        var bounds = getBounds(lats, lngs);
        MyApp.Map.fitBounds(bounds);
    }
}


//funcao que remove os marcadores do separador 1
function removeStartAndEndMarkerOfTab1(){
    if(MyApp.Markers.startAtMarker != null && MyApp.Map.hasLayer(MyApp.Markers.startAtMarker)){
        MyApp.Map.removeLayer(MyApp.Markers.startAtMarker);
    }

    if(MyApp.Markers.endAtMarker != null && MyApp.Map.hasLayer(MyApp.Markers.endAtMarker)){
        MyApp.Map.removeLayer(MyApp.Markers.endAtMarker);
    }
}

//funcao que remove os marcadores do separador 2
function removeStartAndEndMarkerOfTab2(){
    if(MyApp.MarkersTab2.startAtMarker != null && MyApp.Map.hasLayer(MyApp.MarkersTab2.startAtMarker)){
        MyApp.Map.removeLayer(MyApp.MarkersTab2.startAtMarker);
    }

    if(MyApp.MarkersTab2.endAtMarker != null && MyApp.Map.hasLayer(MyApp.MarkersTab2.endAtMarker)){
        MyApp.Map.removeLayer(MyApp.MarkersTab2.endAtMarker);
    }
}

//funcao que dado um array de latitudes e longitudes devolve os limites
//util para determinar a area visivel do mapa
function getBounds(lats, lngs){
    var maxlat = Math.max.apply(Math, lats);
    var maxlng = Math.max.apply(Math, lngs);

    var minlat = Math.min.apply(Math, lats);
    var minlng = Math.min.apply(Math, lngs);

    //southwest
    var sw = new L.LatLng(minlat,minlng);
    //northeast
    var ne = new L.LatLng(maxlat,maxlng);

    return new L.LatLngBounds(sw, ne);
}

//quando um troco e seleccionado, muda-se a cor e aumenta-se o tamanho
function highlightSelectedLayer(layerGroup, layer, event) {
    //The CTRL key was not pressed
    if(event.originalEvent.ctrlKey == false){
        layer.setStyle(MyApp.SelectedLayer);

        if(event.originalEvent.shiftKey == false){
            layerGroup.eachLayer(function (l) {
                if(l != layer){
                    l.setStyle({color: l.color, weight: 7});
                }
            });
        }
        else{
           highlightImplicitLayers(layer);
        }
    }
    else{
        //The CTRL key was pressed
        if(layer.options.weight == MyApp.SelectedLayer.weight){
            //desselecciona a layer
            layer.setStyle({color: layer.color, weight: 7});
        }
        else{
            layer.setStyle(MyApp.SelectedLayer);
        }
    }
}

//funcao chamada qunado o troco é seleccionado com shift
function highlightImplicitLayers(layer){
    //sera que o utilizador ja seleccionou um troco anteriormente?
    var idLayerAlreadySelected = null;
    var idSelectedLayerWithShift = layer.id;

    //a primeira que aparece seleccionada no array, i.e pode nao ser a primeira seleccionada
    var firstSelectedLayer = null;

    for(var index in MyApp.Layers.layersGroupStreetEdges._layers) {
       if(MyApp.Layers.layersGroupStreetEdges._layers[index].options.weight == MyApp.SelectedLayer.weight){
           if(MyApp.Layers.layersGroupStreetEdges._layers[index].id == idSelectedLayerWithShift){
               firstSelectedLayer = idSelectedLayerWithShift;
           }
           else{
                idLayerAlreadySelected = MyApp.Layers.layersGroupStreetEdges._layers[index].id;
                if(firstSelectedLayer == null){
                    firstSelectedLayer = idLayerAlreadySelected;
                }
                break;
           }
       }
   }

   if(idLayerAlreadySelected != null){
       var startId;
       var stopId;

       if(firstSelectedLayer == idLayerAlreadySelected){
           startId = idLayerAlreadySelected;
           stopId = idSelectedLayerWithShift;
       }
       else{
           startId = idSelectedLayerWithShift;
           stopId = idLayerAlreadySelected;
       }

       var isToHighlight = false;

       //e necessario fazer o highlight das layers intermedias
       for(var index in MyApp.Layers.layersGroupStreetEdges._layers) {
           if(MyApp.Layers.layersGroupStreetEdges._layers[index].id == stopId){
               break;
           }
           else if(isToHighlight == true){
               if(MyApp.Layers.layersGroupStreetEdges._layers[index].options.color != MyApp.WalkLegColor){
                    MyApp.Layers.layersGroupStreetEdges._layers[index].setStyle(MyApp.SelectedLayer);
               }
           }
           else if(MyApp.Layers.layersGroupStreetEdges._layers[index].id == startId){
               isToHighlight = true;
           }
       }
   }
}

function returnIdsOfSelectedStreetEdges(){
    var array = new Array();

    MyApp.Layers.layersGroupStreetEdges.eachLayer(function (layer) {
        if(layer.options.weight == MyApp.SelectedLayer.weight){
            array.push(layer.id);
        }
    });

    return array;
}


jQuery(document).ready(function($){
        var tabOpts = {
            disabled: [1],
            collapsible: true,
            //active: false,

            activate: function(event ,ui){
                switch(ui.newTab.index()){
                    case 0:
                        addStartAndEndMarkerTab1();
                        removeLayerGroup(MyApp.Layers.layersGroupStreetEdges);
                        removeStartAndEndMarkerOfTab2();
                        jQuery('.streetEdgesLegend').hide();

                        if(jQuery("#elevationLegend").length || jQuery("#safetyLegend").length){
                            addLayerGroup(MyApp.Layers.featureGroupStreetEdges);
                        }
                        else{
                            if(addLayerGroup(MyApp.Layers.layersGroupLegs)){
                                if(MyApp.LastPlannedTrip.hasWalkMode){
                                    jQuery('.legLegend').show();
                                }
                            }
                        }
                        break;
                    case 1:
                        removeStartAndEndMarkerOfTab1();
                        removeLayerGroup(MyApp.Layers.layersGroupLegs);
                        removeLayerGroup(MyApp.Layers.featureGroupStreetEdges);
                        jQuery('.legLegend').hide();
                        addTripsToDropDownList();
                        break;
                }
            }
	};
        $("#myTabs").tabs(tabOpts);
});


function getRandomColor() {
    var letters = '0123456789ABCDEF'.split('');
    var color = '#';
    for (var i = 0; i < 6; i++ ) {
        color += letters[Math.round(Math.random() * 15)];
    }
    return color;
}
