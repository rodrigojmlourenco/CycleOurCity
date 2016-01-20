//função que solicita a pesquisa de um trajecto
function planTrip(planWithRatings){
    var fromLatLng = MyApp.Markers.startAtMarker.getLatLng().lat + ',' + MyApp.Markers.startAtMarker.getLatLng().lng;
    var toLatLng = MyApp.Markers.endAtMarker.getLatLng().lat + ',' + MyApp.Markers.endAtMarker.getLatLng().lng;

    var factors_url = ArrayToURL(bikeTriangle.getFactors());

    var url;

    if(planWithRatings){
        url = MyApp.URL + factors_url + '&_dc=1368806699670&arriveBy=false&time=17%3A02&ui_date=17-05-2013&mode=BICYCLE&optimize=TRIANGLE&maxWalkDistance=5000&walkSpeed=0.833&date=2013-05-17&callback=?&toPlace=' + toLatLng + '&fromPlace=' + fromLatLng + "&routingWithUserFeedback=true";
    }
    else{
        url = MyApp.URL + factors_url + '&_dc=1368806699670&arriveBy=false&time=17%3A02&ui_date=17-05-2013&mode=BICYCLE&optimize=TRIANGLE&maxWalkDistance=5000&walkSpeed=0.833&date=2013-05-17&callback=?&toPlace=' + toLatLng + '&fromPlace=' + fromLatLng + "&routingWithUserFeedback=false";
    }

    //bloqueia ui e só desbloqueia depois do trajecto estar guardado
    jQuery.blockUI({css: {  border: 'none',
                            padding: '15px',
                            backgroundColor: '#000',
                            '-webkit-border-radius': '10px',
                            '-moz-border-radius': '10px',
                            opacity: .5,
                            color: '#fff'
                         },
                    message: "A planear o trajecto..."});

    var urls = new Array(url);
    var jxhr = [];
    var doneCount = 0;
    var arrayJSON = new Array();

    jQuery.each(urls, function (i, url) {
        jxhr.push(
            jQuery.getJSON(url, function (json) {
                arrayJSON[doneCount] = json;
                doneCount++;

                if(doneCount == urls.length) {
                    var plannedTrip;

                    plannedTrip = arrayJSON[0];

                    processPlannedTripWithRatings(plannedTrip);

                    if(plannedTrip.plan != null){
                        var defaultTripName;
                        if(plannedTrip.plan.from.name != "" && plannedTrip.plan.from.name != "road"){
                            defaultTripName = "Partida: " + plannedTrip.plan.from.name;
                        }
                        else if(plannedTrip.plan.to.name != "" && plannedTrip.plan.from.name != "road"){
                            defaultTripName = "Chegada: " + plannedTrip.plan.to.name;
                        }
                        else{
                            //var date = new Date();
                            //defaultTripName = date.getDate() + "/" + (date.getMonth() + 1) + "/" + date.getFullYear();
                            defaultTripName = "Partida e Chegada desconhecidas"
                        }

                        saveTrip(defaultTripName, plannedTrip);
                    }
                }
            }).done(function(){console.log("success"); }).fail(function() {jQuery('#routing-result').remove(); addPlannerError(plannerNotAvailableError); jQuery.unblockUI(); })
        );
    });
}

function processPlannedTripWithRatings(plannedTripWithRatings){
    if(MyApp.Layers.layersGroupLegs != null){
        MyApp.Layers.layersGroupLegs.clearLayers();
    }
    if(MyApp.Layers.featureGroupStreetEdges != null){
        MyApp.Layers.featureGroupStreetEdges.clearLayers();
    }

    MyApp.LastPlannedTrip.response = plannedTripWithRatings;
    MyApp.LastPlannedTrip.hasWalkMode = false;
    jQuery('.legLegend').hide();

    var flagTripHasWalkMode = false;

    var itinerary;

    if(plannedTripWithRatings.plan != null && plannedTripWithRatings.plan.itineraries[0].streetEdges.length > 0 ){
        itinerary = plannedTripWithRatings.plan.itineraries[0];
        if(plannedTripWithRatings.plan.itineraries.length > 1){
            //alert("Numero de itinerarios: " + plannedTripWithRatings.plan.itineraries.length);
        }

        var legs;

        if(itinerary.legs instanceof Array){
            legs = itinerary.legs;
        }
        else{
            legs = new Array();
            legs.push(itinerary.legs);
        }

        var polylineLegs = new Array();

        for(var l in legs){
            var leg = legs[l];

            var polylineLeg = new L.EncodedPolyline(leg.legGeometry.points);

            polylineLeg.options.weight = 7;

            if(legs[l].mode == "BICYCLE"){
                polylineLeg.options.color = MyApp.BicycleLegColor;
            }
            else{
                polylineLeg.options.color = MyApp.WalkLegColor;
                flagTripHasWalkMode = true;
                MyApp.LastPlannedTrip.hasWalkMode = true;
            }

            polylineLeg.id = l;
            polylineLeg.geometry = leg.legGeometry.points;
            polylineLeg.color = polylineLeg.options.color;

            polylineLegs.push(polylineLeg);
        }

        MyApp.Layers.layersGroupLegs = L.featureGroup(polylineLegs);
        //addLayerGroup(MyApp.Layers.layersGroupLegs);

        var streetedges = itinerary.streetEdges;
        var polylineStreetEdges = new Array();

        for(var s in streetedges){
            var streetedge = streetedges[s];

            var polylineStreetEdge = new L.EncodedPolyline(streetedge.edgeGeometry.points);

            polylineStreetEdge.options.fillOpacity = 1;
            polylineStreetEdge.options.opacity = 1.0;
            polylineStreetEdge.options.weight = 7;
            polylineStreetEdge.options.color = MyApp.BicycleLegColor;
            polylineStreetEdge.id = s;
            polylineStreetEdge.geometry = streetedge.edgeGeometry.points;
            polylineStreetEdge.color = polylineStreetEdge.options.color;
            polylineStreetEdge.elevationClassification = streetedge.elevationId;
            polylineStreetEdge.safetyClassification = streetedge.safetyId;

            polylineStreetEdges.push(polylineStreetEdge);
        }

        MyApp.Layers.featureGroupStreetEdges = L.featureGroup(polylineStreetEdges);

        jQuery('#routing-result').remove();
        jQuery('#tab1').append("<div id='routing-result'><hr></div>");

        addView(plannedTripWithRatings);
        addTripDetailsToTripTab(plannedTripWithRatings, flagTripHasWalkMode);
    }
    else{
        jQuery('#routing-result').remove();
        if(plannedTripWithRatings.error == null){
            addPlannerError("O local de partida está demasiado perto do local de chegada.");
        }
        else{
            addPlannerError(plannedTripWithRatings.error.msg);
        }
        //alert('Erro: ' + plannedTripWithRatings.error.msg);
        jQuery.unblockUI();
    }
}

function addView(plannedTripWithRatings){
    var safetyFactor = parseFloat(plannedTripWithRatings.requestParameters.triangleSafetyFactor);
    var elevationFactor = parseFloat(plannedTripWithRatings.requestParameters.triangleSlopeFactor);

    if(safetyFactor >= elevationFactor){
        addLegendDropdownlist(2);
        showTripWithSafetyClassifications();
    }
    else{
        addLegendDropdownlist(1);
        showTripWithElevationClassifications();
    }
}

function addTripDetailsToTripTab(plannedTripWithtRatings, flagTripHasWalkMode){
    var distance = 0;

    for(var i=0; i < plannedTripWithtRatings.plan.itineraries[0].legs.length; i++) {
       distance += plannedTripWithtRatings.plan.itineraries[0].legs[i].distance; /* a distancia ja vem em metros */
    }

    var duration = 0;

    for(var i=0; i < plannedTripWithtRatings.plan.itineraries[0].legs.length; i++) {
       duration += plannedTripWithtRatings.plan.itineraries[0].legs[i].duration;
    }

    var divLeg = "<p class='leg' style='display:none'>Tempo total: " + MillisecondsToHoursMinutes(duration) +", Distância Total: " + Math.round(100*distance)/100+" m </p><p></p>";
    jQuery("#routing-result").append(divLeg);

    for(var l in plannedTripWithtRatings.plan.itineraries[0].legs){
        var legDistance = Math.round(100*(plannedTripWithtRatings.plan.itineraries[0].legs[l].distance))/100;

        if(plannedTripWithtRatings.plan.itineraries[0].legs[l].mode == "BICYCLE"){
            divLeg = "<p " + "id='leg" + l + "' class='bicycleLeg' style='display:none'> Tempo: " +
                MillisecondsToHoursMinutes(plannedTripWithtRatings.plan.itineraries[0].legs[l].duration) + ", Distância: " + legDistance + " m</p><p></p>";
        }
        else if(plannedTripWithtRatings.plan.itineraries[0].legs[l].mode == "WALK"){
            /* if(MyApp.LastPlannedTrip.hasWalkMode == false){
                MyApp.LastPlannedTrip.hasWalkMode = true;
            } */
            divLeg = "<p " + "id='leg" + l + "' class='walkLeg' style='display:none'>Tempo: " +
                MillisecondsToHoursMinutes(plannedTripWithtRatings.plan.itineraries[0].legs[l].duration) + ", Distância: " + legDistance + " m</p><p></p>";
        }

        jQuery("#routing-result").append(divLeg);

        var selectorDiv = "#leg" + l;

        jQuery(selectorDiv).on('mouseenter', {'selector': l}, function(event){
                //console.log("Enter " + event.plannedTripWithtRatings.selector);
                jQuery(this).addClass("leg_hover");
                MyApp.Layers.layersGroupLegs.eachLayer(function (layer) {
                    if(layer.id == event.data.selector){
                        highlightSelectedLeg(layer);
                        zoomToLayer(layer);
                        return;
                    }
                });
         });
         jQuery(selectorDiv).on('mouseleave', {'selector': l}, function(event){
                //console.log("Leave " + event.data.selector);
                jQuery(this).removeClass("leg_hover");
                MyApp.Layers.layersGroupLegs.eachLayer(function (layer) {
                    if(layer.id == event.data.selector){
                        layer.setStyle({color: layer.color, weight: 7});
                        return;
                    }
                });
         });

    }

    /* if(flagTripHasWalkMode){
        jQuery('.legLegend').show();
    }
    else{
        jQuery('.legLegend').hide();
    } */

    /* if(jQuery('#myTabs').height() > jQuery("#map").height()){
        jQuery('#myTabs').trigger('changeheight');
    } */
}

function saveTrip(defaultTripName, plannedTrip){
    jQuery.when(saveTripPlannedWithRatings(defaultTripName, plannedTrip)).then(function(result){
        //o resultado é um array :(
        processSaveTrip(result);
        //processNewSaveTrip(result2);
        jQuery.unblockUI();
    });
}

function saveTripPlannedWithRatings(defaultTripName, plannedTripWithRatings){
    var tripWithStreetEdgesArray = createAnArrayWithStreetEdges(plannedTripWithRatings.plan.itineraries[0]);

    if(tripWithStreetEdgesArray.length > 0){
        return jQuery.post("SaveTrip.php", {tripName: defaultTripName, streetEdges: JSON.stringify(tripWithStreetEdgesArray)}, "json");
    }else{
        jQuery("#routing-result").append('<div id="errorSaveTrip" class="error"></div>');
        jQuery("#errorSaveTrip").text("Este trajecto não é guardado pois é demasiado pequeno para ser classificado.");
        return 1;
    }
}

//Callback
function processSaveTrip(result){
    if(result.status == true){
        jQuery("#errorSaveTrip").remove();
        jQuery("#routing-result").append('<div id="sucessSaveTrip" class="success"></div>');
        jQuery("#sucessSaveTrip").text(result.text);

        setTimeout(function() {
            jQuery("#sucessSaveTrip").fadeOut().remove();
        }, 3000);
    }
    else if(result.status == false){
            if(jQuery("#errorSaveTrip").size() > 0){
                jQuery("#errorSaveTrip").text(result.text);
            }
            else{
                jQuery("#routing-result").append('<div id="errorSaveTrip" class="error"></div>');
                jQuery("#errorSaveTrip").text(result.text);
            }
    }
}

/*
function createAnArrayWithLegs(trip){
    var legsResult = new Array();

    var legs;

    if(trip.legs instanceof Array){
        legs = trip.legs;
    }
    else{
        legs = new Array();
        legs.push(trip.streetEdges);
    }

    var leg;
    for(var l in legs){
        leg = legs[l];
        if(leg.mode == "BICYCLE"){
            legsResult.push(new Array(new Array(leg.legGeometry.points, 1)));
        }
        else{
            legsResult.push(new Array(new Array(leg.legGeometry.points, 0)));
        }
    }

    return legsResult;
} */


function createAnArrayWithStreetEdges(trip){
    var streetEdgesResult = new Array();

    var streetEdges;

    if(trip.streetEdges instanceof Array){
        streetEdges = trip.streetEdges;
    }
    else{
        streetEdges = new Array();
        streetEdges.push(trip.streetEdges);
    }

    var streetEdge;
    for(var s in streetEdges){
        streetEdge = streetEdges[s];
        if(streetEdge.mode == "BICYCLE"){
            streetEdgesResult.push(new Array(streetEdge['id'], new Array(streetEdge.edgeGeometry.points, 1)));
        }
        else{
            streetEdgesResult.push(new Array(streetEdge['id'], new Array(streetEdge.edgeGeometry.points, 0)));
        }
    }

    return streetEdgesResult;
}


function highlightSelectedLeg(layer) {
    layer.setStyle({
        color: layer.color,
        weight: 10
    });
}

function zoomToLayer(layer) {
    MyApp.Map.fitBounds(layer.getBounds());
}

/* 1min = 60000 milliseconds */
function MillisecondsToHoursMinutes(ms) {
    var hrs = Math.floor(ms / 3600000);
    var mins = Math.floor(ms / 60000) % 60;

    var str = (hrs > 0 ? (hrs +" hr, ") : "") + mins + " min";
    return str;
}

//selected = 1 -> inclinação
//selected = 2 -> segurança/tráfego
function addLegendDropdownlist(selected){
    //var msg = "<p>Escolhe um critério para visualizar as classificações segundo o mesmo nos troços que constituem o trajecto:</p>";
    var dropdownlist = "<select id='legendToDisplayTrip'>" +
                       "<option value='0'>Ver trajecto passo a passo</option>";

    if(selected == 1){
        dropdownlist += "<option value='1' selected>Ver inclinação dos troços</option>" +
                        "<option value='2'>Ver tráfego dos troços</option>" +
                        "</select></br>";
    }
    else{
        dropdownlist += "<option value='1'>Ver inclinação dos troços</option>" +
                       "<option value='2' selected>Ver tráfego dos troços</option>" +
                       "</select></br>";
    }

    //jQuery("#routing-result").append(msg);
    jQuery("#routing-result").append(dropdownlist);

    jQuery("#legendToDisplayTrip").bind('change', function(){
        var selected = jQuery(this).val();
        if(selected == 0){
            showDefaultTrip();
        }
        else{
            if(selected == 1){
                showTripWithElevationClassifications();
            }
            else{
                showTripWithSafetyClassifications();
            }
        }
    });
}

function showDefaultTrip(){
    removeLayerGroup(MyApp.Layers.featureGroupStreetEdges);
    jQuery('#elevationLegend').remove();
    jQuery('#safetyLegend').remove();

    jQuery('.leg').show();
    jQuery('.walkLeg').show();
    jQuery('.bicycleLeg').show();

    if(MyApp.LastPlannedTrip.hasWalkMode){
        jQuery('.legLegend').show();
    }

    jQuery('#myTabs').trigger('changeheight');
    addLayerGroup(MyApp.Layers.layersGroupLegs);
}

function showTripWithElevationClassifications(){
    removeLayerGroup(MyApp.Layers.layersGroupLegs);

    jQuery('#safetyLegend').remove();
    jQuery('.leg').hide();
    jQuery('.walkLeg').hide();
    jQuery('.bicycleLeg').hide();
    jQuery('.legLegend').hide();

    MyApp.Layers.featureGroupStreetEdges.eachLayer(function(l){
        if(l.elevationClassification == -1){
            l.setStyle({color: MyApp.WithoutClassificationsColor});
        }
        else{
            l.setStyle({color: MyApp.ClassificationColors.elevation[l.elevationClassification - 1]});
        }
    });

    //se existe mensagem de erro
    if(jQuery("#errorSaveTrip").length){
        jQuery("#errorSaveTrip").before(MyApp.ElevationColorsLegend);
    }
    else{
        jQuery('#routing-result').append(MyApp.ElevationColorsLegend);
    }

    jQuery('#myTabs').trigger('changeheight');
    addLayerGroup(MyApp.Layers.featureGroupStreetEdges);
}

function showTripWithSafetyClassifications(){
    removeLayerGroup(MyApp.Layers.layersGroupLegs);
    jQuery('#elevationLegend').remove();

    jQuery('.leg').hide();
    jQuery('.walkLeg').hide();
    jQuery('.bicycleLeg').hide();
    jQuery('.legLegend').hide();

    MyApp.Layers.featureGroupStreetEdges.eachLayer(function(l){
        if(l.safetyClassification == -1){
            l.setStyle({color: MyApp.WithoutClassificationsColor});
        }
        else{
            l.setStyle({color: MyApp.ClassificationColors.safety[l.safetyClassification - 1]});
        }
    });

    //se existe mensagem de erro
    if(jQuery("#errorSaveTrip").length){
        jQuery("#errorSaveTrip").before(MyApp.SafetyColorsLegend);
    }
    else{
        jQuery('#routing-result').append(MyApp.SafetyColorsLegend);
    }

    jQuery('#myTabs').trigger('changeheight');
    addLayerGroup(MyApp.Layers.featureGroupStreetEdges);
}
