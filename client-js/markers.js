//Error messages
var arrivalError = "Tem de definir o local de chegada dentro da área assinalada a laranja.";
var departureError = "Tem de definir o local de partida dentro da área assinalada a laranja.";
var geocodingError = "Não foi possível encontrar o endereço introduzido: ";
var plannerError = "Não foi possível planear o seu trajecto: ";
var plannerNotAvailableError = "O serviço de planeamento de trajectos está indisponível temporariamente. Por favor, tente mais tarde.";
var startOrEndPointNotDefined = "Para planear um trajecto precisa de definir os pontos de partida e chegada. ";

var arrivalTimer;
var departureTimer;

function isValidPoint(point){
    var minLat = MyApp.bounds[0][0];
    var maxLat = MyApp.bounds[1][0];
    var minLng = MyApp.bounds[0][1];
    var maxLng = MyApp.bounds[1][1];

    if((point.lat >= minLat) && (point.lat <= maxLat) && (point.lng >= minLng) && (point.lng <= maxLng)){
        return true;
    }
    else{
        return false;
    }
}


function addDepartureError(msg){
    if(jQuery("#departureError").size() == 0){
        //jQuery("#departure").append('<div id="departureError" class="error"></div>');
        jQuery("#switchLocations").before('<p id="departureError" class="error">' + msg + ' <br><br> Confirme que todas as palavras estão escritas correctamente.' +  '</p>');
        //jQuery("#departureError").text(msg);

        clearTimeout(departureTimer);
        departureTimer = setTimeout(function() {
                            jQuery("#departureError").fadeOut().remove();
                         }, 7000);
    }
}

function addArrivalError(msg){
    if(jQuery("#arrivalError").size() == 0){
        //jQuery("#arrival").append('<p id="arrivalError" class="error">' + msg + '</p>');
        jQuery("#bikeTriangle").before('<p id="arrivalError" class="error">' + msg + ' <br><br> Confirme que todas as palavras estão escritas correctamente.' +  '</p>');
        //jQuery("#arrivalError").text(msg);

        clearTimeout(arrivalTimer);
        arrivalTimer = setTimeout(function() {
                            jQuery("#arrivalError").fadeOut().remove();
                       }, 7000);
    }
}

function addPlannerError(msg){
    if(jQuery("#plannerError").size() == 0){
        jQuery("#tab1").append('<div id="plannerError" class="error"></div>');
        jQuery("#plannerError").text(msg);
        setTimeout(function() {
            jQuery("#plannerError").fadeOut().remove();
        }, 3500);
    }
}

function startAt(startDomId, title){
    var point = MyApp.LastPointClicked.latlng;
    var pointIsValid= isValidPoint(point);

    if(pointIsValid){
        if(MyApp.Markers.startAtMarker != null){
            MyApp.Markers.startAtMarker.setLatLng(point);
        }
        else{
            MyApp.Markers.startAtMarker = new L.Marker(point, {draggable: true, title: title, icon: MyApp.Icons.startFlagIcon});
            MyApp.Markers.startAtMarker.on('dragend', onDragEndStartMarker);
            MyApp.Map.addLayer(MyApp.Markers.startAtMarker);
        }

        isTwoLocationDefined();
        MyApp.LastValidDeparture.point = point;
        reverseGeosearchDeparture(point.lat, point.lng);
    }
    else{
        addDepartureError(departureError);
        //alert("Tem de definir o local de partida dentro da área assinalada a laranja.");
        if(MyApp.LastValidDeparture.point != null){
            MyApp.Markers.startAtMarker.setLatLng(MyApp.LastValidDeparture.point);
            jQuery(startDomId).val(MyApp.LastValidDeparture.address);
        }
    }
}

function onDragEndStartMarker(e, startDomId){
    var point = e.target._latlng;
    var pointIsValid= isValidPoint(point);

    if(pointIsValid){
        MyApp.LastValidDeparture.point = point;
        reverseGeosearchDeparture(point.lat, point.lng);
    }
    else{
        addDepartureError(departureError);
        //alert("Tem de definir o local de partida dentro da área assinalada a laranja.");
        MyApp.Markers.startAtMarker.setLatLng(MyApp.LastValidDeparture.point);
        jQuery(startDomId).val(MyApp.LastValidDeparture.address);
    }
}

function endAt(endDomId, title){
    var point = MyApp.LastPointClicked.latlng;
    var pointIsValid= isValidPoint(point);

    if(pointIsValid){
        if(MyApp.Markers.endAtMarker != null){
            MyApp.Markers.endAtMarker.setLatLng(point);
        }
        else{
            MyApp.Markers.endAtMarker = new L.Marker(point, {draggable: true, title: title, icon: MyApp.Icons.endFlagIcon});
            MyApp.Markers.endAtMarker.on('dragend', onDragEndEndMarker);
            MyApp.Map.addLayer(MyApp.Markers.endAtMarker);
        }

        isTwoLocationDefined();
        MyApp.LastValidArrival.point = point;
        reverseGeosearchArrival(point.lat, point.lng);
    }
    else{
        addArrivalError(arrivalError);
        //alert("Tem de definir o local de chegada dentro da área assinalada a laranja.");
        if(MyApp.LastValidArrival.point != null){
            MyApp.Markers.endAtMarker.setLatLng(MyApp.LastValidArrival.point);
            jQuery(endDomId).val(MyApp.LastValidArrival.address);
        }
    }
}

function onDragEndEndMarker(endDomId){
    var point = e.target._latlng;
    var pointIsValid= isValidPoint(point);

    if(pointIsValid){
        MyApp.LastValidArrival.point = point;
        reverseGeosearchArrival(point.lat, point.lng);
    }
    else{
        addArrivalError(arrivalError);
        //alert("Tem de definir o local de chegada dentro da área assinalada a laranja.");
        MyApp.Markers.endAtMarker.setLatLng(MyApp.LastValidArrival.point);
        jQuery(endDomId).val(MyApp.LastValidArrival.address);
    }

}

function switchStartAndEndLocation(startDomId, endDomId){
    //old
    var startLatLng = MyApp.Markers.startAtMarker.getLatLng();
    var departureAddress = MyApp.LastValidDeparture.address;

    MyApp.Markers.startAtMarker.setLatLng(MyApp.Markers.endAtMarker.getLatLng());
    MyApp.Markers.endAtMarker.setLatLng(startLatLng);

    MyApp.LastValidDeparture.point = MyApp.Markers.startAtMarker.getLatLng();
    MyApp.LastValidArrival.point = MyApp.Markers.endAtMarker.getLatLng();

    MyApp.LastValidDeparture.address = MyApp.LastValidArrival.address;
    MyApp.LastValidArrival.address = departureAddress;

    jQuery(startDomId).val(MyApp.LastValidDeparture.address);
    jQuery(endDomId).val(MyApp.LastValidArrival.address);
}

function addMarkersToTab2(from_lat, from_lng, to_lat, to_lng, title){
    if(MyApp.MarkersTab2.startAtMarker != null){
        MyApp.MarkersTab2.startAtMarker.setLatLng(new L.LatLng(from_lat, from_lng));
        if(!MyApp.Map.hasLayer(MyApp.MarkersTab2.startAtMarker)){
            MyApp.MarkersTab2.startAtMarker.addTo(MyApp.Map);
        }
    }
    else{
        MyApp.MarkersTab2.startAtMarker = new L.Marker(new L.LatLng(from_lat, from_lng), {draggable: false, title: title, icon: MyApp.Icons.startFlagIcon});
        //MyApp.Markers.startAtMarker.on('dragend', onDragEndStartMarker);
        MyApp.Map.addLayer(MyApp.MarkersTab2.startAtMarker);
    }

    if(MyApp.MarkersTab2.endAtMarker != null){
        MyApp.MarkersTab2.endAtMarker.setLatLng(new L.LatLng(to_lat, to_lng));
        if(!MyApp.Map.hasLayer(MyApp.MarkersTab2.endAtMarker)){
            MyApp.MarkersTab2.endAtMarker.addTo(MyApp.Map);
        }
    }
    else{
        MyApp.MarkersTab2.endAtMarker = new L.Marker(new L.LatLng(to_lat, to_lng), {draggable: false, title: title, icon: MyApp.Icons.endFlagIcon});
        //MyApp.Markers.endAtMarker.on('dragend', onDragEndEndMarker);
        MyApp.Map.addLayer(MyApp.MarkersTab2.endAtMarker);
    }
}

//After Geocoding...
function startAtPoint(point, address, title){
    if(MyApp.Markers.startAtMarker != null){
        MyApp.Markers.startAtMarker.setLatLng(point);
    }
    else{
        MyApp.Markers.startAtMarker = new L.Marker(point, {draggable: true, title: title, icon: MyApp.Icons.startFlagIcon});
        MyApp.Markers.startAtMarker.on('dragend', onDragEndStartMarker);
        MyApp.Map.addLayer(MyApp.Markers.startAtMarker);
    }

    isTwoLocationDefined();
    MyApp.LastValidDeparture.point = point;
    MyApp.LastValidDeparture.address = address;
    updateView();
}

function endAtPoint(point, address){
    if(MyApp.Markers.endAtMarker != null){
        MyApp.Markers.endAtMarker.setLatLng(point);
    }
    else{
        MyApp.Markers.endAtMarker = new L.Marker(point, {draggable: true, title: title, icon: MyApp.Icons.endFlagIcon});
        MyApp.Markers.endAtMarker.on('dragend', onDragEndEndMarker);
        MyApp.Map.addLayer(MyApp.Markers.endAtMarker);
    }

    isTwoLocationDefined();
    MyApp.LastValidArrival.point = point;
    MyApp.LastValidArrival.address = address;
    updateView()
}


function updateView(){
    var lats = [];
    var lngs = [];

    if(MyApp.Markers.startAtMarker != null){
        lats.push(MyApp.Markers.startAtMarker.getLatLng().lat);
        lngs.push(MyApp.Markers.startAtMarker.getLatLng().lng);
    }

    if(MyApp.Markers.endAtMarker != null){
        lats.push(MyApp.Markers.endAtMarker.getLatLng().lat);
        lngs.push(MyApp.Markers.endAtMarker.getLatLng().lng);
    }

    if(MyApp.Markers.startAtMarker != null || MyApp.Markers.endAtMarker != null){
        var bounds = getBounds(lats, lngs);
        MyApp.Map.fitBounds(bounds);
    }
}

function isTwoLocationDefined(domId){
    if(jQuery(domId).prop("disabled")){
        if(MyApp.Markers.startAtMarker != null && MyApp.Markers.endAtMarker != null){
            jQuery(domId).prop("disabled", false);
        }
    }
}
