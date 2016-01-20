
/* @Secured
 * Fetches the list of recent trips queried by the user. If the user
 * has requested route plan before, then the most recent trip is
 * rendered onto the map.
 *
*/
function addTripsToDropDownList(tripsDivId, message){
  jQuery.ajax({
    type: "GET",
    url: MyApp.Server.trips+"/list",
    beforeSend: function (request){
      request.setRequestHeader("Authorization", "Bearer "+Security.token);
    },
    success: function(result){
      askForStreetEdges(jQuery(tripsDivId).val(), message);
      return result;
    }
  });
}

/* @Secured
 * Given a specific trip identified by its id, this method fetches that trip's
 * details. More specifically, it fetches all the street edges associated with
 * that trip as well as their geometries. Finally, the trip is rendered onto
 * the map.
*/
function askForStreetEdges(idTrip, message){
  jQuery.ajax({
      type: "GET",
      url: MyApp.Server.trips+"/"+idTrip,
      beforeSend : function(request) {

        request.setRequestHeader("Authorization", "Bearer "+Security.token);

        jQuery.blockUI(
          {css: {
            border: 'none',
            padding: '15px',
            backgroundColor: '#000',
            '-webkit-border-radius': '10px',
            '-moz-border-radius': '10px',
            opacity: .5,
            color: '#fff'
            }, message: message });
      },
      success: function(result){
          if(MyApp.Layers.layersGroupStreetEdges != null){
             MyApp.Layers.layersGroupStreetEdges.clearLayers();
          }

          addStreetEdgesToMap(result.streetEdges);
          addMarkersToTab2(
            result.fromLocation[0].FromVertexLatitude,
            result.fromLocation[0].FromVertexLongitude,
            result.toLocation[0].ToVertexLatitude,
            result.toLocation[0].ToVertexLongitude);

          jQuery.unblockUI();
      }
  });
}
