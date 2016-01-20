/* Author: Nelson Nunes
 *
 * CHANGES (@Author: Rodrigo Lourenço)
 * Some of the methods were modified in order to become independent of the
 * DOM elements' identifier. Additionally, some methods were also modified
 * in order to become country independent.
*/
function parseJSON(data) {
    if (data.length == 0)
        return [];

    var results = [];
    for (var i = 0; i < data.length; i++)
        results.push({
            lng: data[i].lon,
            lat: data[i].lat,
            name: data[i].display_name
        });

    return results;
}

//countrycodes: "PT"
//viewbox: "-9.2295,38.7957,-9.0896,38.6915"
function geosearchDeparture(query, domGeocodeId, domErrorId, countryCode, viewbox){

    setDepartureSpinnerOn();
    jQuery.getJSON('http://nominatim.openstreetmap.org/search?',
                    {q: query, format: 'json', countrycodes: countryCode, viewbox : viewbox},
                    function(data) {
                        var results = parseJSON(data);
                        var flagSuccess = false;

                        for(var index = 0; index < results.length; index++){
                            var point = new L.LatLng(results[index].lat, results[index].lng);
                            if(isValidPoint(point)){
                                startAtPoint(point, query);
                                flagSuccess = true;
                                break;
                            }
                        }

                       setDepartureSpinnerOff();

                       if(!flagSuccess){
                           jQuery("#"+domErrorId).remove();
                            addDepartureError(geocodingError + '"' + query + '"');

                            if(MyApp.LastValidDeparture.point != null){
                                jQuery("#"+domGeocodeId).val(MyApp.LastValidDeparture.address);
                            }
                            else{
                                jQuery("#"+domGeocodeId).val("");
                            }
                       }
                    });
}

//countryCodes: "PT"
//viewbox: "-9.2295,38.7957,-9.0896,38.6915"
function geosearchArrival(query, domGeocodeId, domErrorId, countryCode, viewbox){
    setArrivalSpinnerOn();
    jQuery.getJSON('http://nominatim.openstreetmap.org/search?',
                    {q: query, format: 'json', countrycodes: countryCode, viewbox : viewbox},
                    function(data) {
                        var results = parseJSON(data);
                        var flagSuccess = false;

                        for(var index = 0; index < results.length; index++){
                            var point = new L.LatLng(results[index].lat, results[index].lng);
                            if(isValidPoint(point)){
                                endAtPoint(point, query);
                                flagSuccess = true;
                                break;
                            }
                        }

                        setArrivalSpinnerOff();

                        if(!flagSuccess){
                            jQuery("#"+domErrorId).remove();
                            addArrivalError(geocodingError + '"' + query + '"');
                            //alert("Não foi possível encontrar a morada introduzida: " + query)

                            if(MyApp.LastValidArrival.point != null){
                                jQuery("#"+domGeocodeId).val(MyApp.LastValidArrival.address);
                            }
                            else{
                                jQuery("#"+domGeocodeId).val("");
                            }
                        }
                    });
}

function reverseGeosearchDeparture(lat, lng, domGeocoderId){
    setDepartureSpinnerOn();
    jQuery.getJSON('http://open.mapquestapi.com/nominatim/v1/reverse.php?',
        {
        format: 'json',
        lat: lat,
        lon: lng,
        zoom: 18,
        addressdetails: 1,
        osm_type: 'W'
        },
        function(data) {
            var address;

            if(data.address.hasOwnProperty('road')){
                address = data.address.road;
            }
            else{
                address = data.display_name;
            }

            MyApp.LastValidDeparture.address = address;
            jQuery("#"+domGeocoderId).val(address);
            setDepartureSpinnerOff();

            //console.log(data);
        }
    );
}

function reverseGeosearchArrival(lat, lng, domGeocoderId){
    setArrivalSpinnerOn();
    jQuery.getJSON('http://open.mapquestapi.com/nominatim/v1/reverse.php?',
        {
        format: 'json',
        lat: lat,
        lon: lng,
        zoom: 18,
        addressdetails: 1,
        osm_type: 'W'
        },
        function(data) {
            var address;

            if(data.address.hasOwnProperty('road')){
                address = data.address.road;
            }
            else{
                address = data.display_name;
            }

            MyApp.LastValidArrival.address = address;
            jQuery("#"+domGeocoderId).val(address);
            setArrivalSpinnerOff();
        }
    );
}
