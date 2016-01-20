var ClassifcationForm = {

  classify : function(id, elevation, safety, pavement, rails){

  }
}

function sumbitRating(elevation, safety, rails, pave) {
    var idsArray = returnIdsOfSelectedStreetEdges();
    var flagLastLayer = isLastLayer(idsArray.length);
    var idTrip = jQuery("#trips").find(':selected').val();

    jQuery.ajax({
       type: "POST",
       url: "InsertUserFeedback.php",
       data: {ids: JSON.stringify(idsArray), idTrip: idTrip, elevation: elevation, safety: safety, rails: rails, pave: pave, last: flagLastLayer},
       beforeSend : function() {
           jQuery.blockUI({css: {
                            border: 'none',
                            padding: '15px',
                            backgroundColor: '#000',
                            '-webkit-border-radius': '10px',
                            '-moz-border-radius': '10px',
                            opacity: .5,
                            color: '#fff'
                            }, message: "A guardar classificações..."});
       },
       dataType: "json",
       success: function(resp){
           if(resp.status == true){
                jQuery("#formToRate").remove();

                MyApp.Polylines.polylineStreetEdges = MyApp.Polylines.polylineStreetEdges.filter(function(el) {
                    return el.options.weight != 10;
                });

                MyApp.Layers.layersGroupStreetEdges.eachLayer(function (layer) {
                     if(layer.options.weight == 10){
                         MyApp.Layers.layersGroupStreetEdges.removeLayer(layer);
                         return;
                     }
                });

                jQuery.unblockUI();

                if(flagLastLayer){
                    jQuery("#trips option[value=" + idTrip + "]").remove();
                    removeStartAndEndMarkerOfTab2();
                    if(jQuery('#trips').val() != null){
                        //ha trajectos, por isso vou pedir os troços do trajecto mais recente (trajecto seleccionado na dropdown list)
                        askForStreetEdges(jQuery('#trips').val(), "A carregar o próximo trajecto mais recente..");
                    }
                }

                jQuery("#optionsToRate").append('<div id="sucessSubmitRating" class="success"></div>');
                jQuery("#sucessSubmitRating").text(resp.text);

                setTimeout(function() {
                    jQuery("#sucessSubmitRating").fadeOut().remove();
                }, 10000);
           }
           else{
               jQuery("#formToRate").remove();
               jQuery.unblockUI();
               alert(resp.text);
           }
       }
       /*error: function(resp){
           jQuery('#feedbackResult').addClass("feedbackResultError");
           //jQuery('#feedbackResult').text(resp);
       } */
    });
}
