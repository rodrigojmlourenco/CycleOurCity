/* Author: Nelson Nunes */

//variavel global
var MyApp = {};

//rectangulo que representa a área de planeamento de trajectos
MyApp.bounds = [[38.6915, -9.2295], [38.7957, -9.0896]];

//nao permite multiplas submissoes do formulario login
MyApp.AllowSubmitLogin = true;

//Safety and Elevation colors
MyApp.ClassificationColors = {
    elevation: new Array('#240002', '#FF2938', '#FFFF00','#99ccff','#3399ff', '#004c99'), /* azul */
    safety: new Array('#240002', '#FF2938', '#FFFF00', '#96E085', '#549345', '#124706')
}

MyApp.WithoutClassificationsColor = "gray";


MyApp.RatingScales = {
    safety: new Array("Tráfego motorizado normalmente acima dos 50km/h", "Tráfego motorizado normalmente não passa dos 50km/h", "Tráfego motorizado intenso, velocidades normalmente não passam dos 30km/h", "Tráfego motorizado pouco intenso, velocidades normalmente não passam dos 30km/h", "Nenhum tráfego motorizado permitido, peões frequentemente no caminho", "Nenhum tráfego motorizado permitido, poucos ou nenhuns peões no caminho"),
    elevation: new Array("Subida impraticável para a maioria das pessoas", "Subida com esforço", "Subida sem esforço", "Plano", "Descida suave", "Descida acentuada"),
    pave: new Array("Empedrado, calçada ou terra batida em boas condições", "Empedrado, calçada ou terra batida difícil de pedalar", "Asfalto/betuminoso em más condições (buracos, desniveis perigosos)", "Asfalto/betuminoso em boas condições"),
    rails: new Array("Não tem carris", "Tem carris ao nível do pavimento", "Tem carris salientes")
};

MyApp.SelectedLayer = {
    color: '#1b1224',
    weight: 10
};

//MyApp.URL = 'http://localhost:8080/opentripplanner-api-webapp/ws/plan?';
MyApp.URL = 'http://cycleourcity.org:8080/opentripplanner-api-webapp/ws/plan?';

MyApp.Server = {
  url   : 'http://localhost:8080/cycleourcity',
  auth  : url+'/auth',
  users : url+'/users',
  route : url+'/route',
  street: url+'/streets',
  trips : url+'/trip'
}

MyApp.Map = null;

MyApp.Layers = {
    //tab1
    layersGroupLegs: null,
    featureGroupStreetEdges: null,

    //tab2
    layersGroupStreetEdges: null
};

MyApp.SwitchLayerControl = null;

//Tab1
MyApp.Markers = {
    startAtMarker: null,
    endAtMarker: null
};

//Tab2
MyApp.MarkersTab2 = {
    startAtMarker: null,
    endAtMarker: null
};


MyApp.Polylines = {
    polylineStreetEdges: null
};

//StreetEdges Colors
MyApp.StreetEdgeColor = 'red';

//Legs Colors
MyApp.BicycleLegColor = 'black';
MyApp.WalkLegColor = 'yellow';

//MyApp.LegsObtainedWithoutRatings = 'yellow';

MyApp.LastPlannedTrip = {
    response: null,
    hasWalkMode: false
};

// usado para planear a partida e a chegada
MyApp.LastPointClicked = null;

MyApp.LastValidDeparture = {
    point: null,
    address: ""
};

MyApp.LastValidArrival = {
    point: null,
    address: ""
};

MyApp.Icons = {
    startFlagIcon: L.icon({
        iconUrl: 'images/marker-flag-start.png',
        iconSize: [43, 41],
        iconAnchor: [43, 41]
    }),
    endFlagIcon: L.icon({
        iconUrl: 'images/marker-flag-end.png',
        iconSize: [43, 41],
        iconAnchor: [43, 41]
    }),
    startIcon: L.icon({
        iconUrl: 'images/flag_marker_green.png',
        iconSize: [32, 37],
        iconAnchor: [16, 37]
    }),
    endIcon: L.icon({
        iconUrl: 'images/flag_marker_red.png',
        iconSize: [32, 37],
        iconAnchor: [16, 37]
    })
};

MyApp.ElevationColorsLegend = makeElevationLegend();
MyApp.SafetyColorsLegend = makeSafetyLegend();

MyApp.Spinner = {
    opts: {
            lines: 7, // The number of lines to draw
            length: 0, // The length of each line
            width: 5, // The line thickness
            radius: 4, // The radius of the inner circle
            corners: 1, // Corner roundness (0..1)
            rotate: 0, // The rotation offset
            direction: 1, // 1: clockwise, -1: counterclockwise
            color: '#000', // #rgb or #rrggbb or array of colors
            speed: 1, // Rounds per second
            trail: 60, // Afterglow percentage
            shadow: false, // Whether to render a shadow
            hwaccel: false, // Whether to use hardware acceleration
            className: 'spinner', // The CSS class to assign to the spinner
            zIndex: 2e9, // The z-index (defaults to 2000000000)
            top: '5', // Top position relative to parent in px
            left: '340' // Left position relative to parent in px P
    }
};

function makeElevationLegend(){
    var classes = new Array('red1','red2','yellow','blue1','blue2','blue3');
    var elevationColorsLegend = "<ul id='elevationLegend'>";

    for(var i = 0; i < 6; i++){
        elevationColorsLegend += "<li><span class='" + classes[i] + "'></span> " +
        MyApp.RatingScales.elevation[i] + "</li>";
    }

    elevationColorsLegend += "<li><span class='" + MyApp.WithoutClassificationsColor + "'></span> " +
    "Troço sem classificações" + "</li>";

    elevationColorsLegend += "</ul>";

    return elevationColorsLegend;
}


function makeSafetyLegend(){
    var classes = new Array('red1','red2','yellow','green1','green2','green3');
    var safetyColorsLegend = "<ul id='safetyLegend'>";

    for(var i = 5; i >= 0; i--){
        safetyColorsLegend += "<li><span class='" + classes[i] + "'></span> " +
        MyApp.RatingScales.safety[i] + "</li>";
    }

    safetyColorsLegend += "<li><span class='" + MyApp.WithoutClassificationsColor + "'></span> " +
    "Troço sem classificações" + "</li>";

    safetyColorsLegend += "</ul>";

    return safetyColorsLegend;
}

function setDepartureSpinnerOn(domId){
    jQuery(domId).spin(MyApp.Spinner.opts);
}

function setArrivalSpinnerOn(domId){
    jQuery(domId).spin(MyApp.Spinner.opts);
}

function setArrivalSpinnerOff(domId){
    jQuery(domId).spin(false);
}

function setDepartureSpinnerOff(domId){
    jQuery(domId).spin(false);
}
