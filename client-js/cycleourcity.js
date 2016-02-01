/*******************************************************************************
********************************************************************************
********************************************************************************
*******************************************************************************/

function validateEmail(email) {
    var re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}

function validateUsername(username){
  var re = /^[a-z0-9_]{5,10}$/;
  return re.test(username);
}

function validatePassword(password){
  var re = /(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$/;
  return re.test(password);
}

/*******************************************************************************
********************************************************************************
********************************************************************************
*******************************************************************************/

function AuthService(url){

  //Private Attributes
  var token = false;
  var serviceURL = url + "/auth";

  this.getToken = function(){
    return token;
  };

  this.setToken = function(t){
    token = t;
  };

  this.getServiceURL = function(){
    return serviceURL;
  }
};

AuthService.prototype = {

  login : function(username, password){

    //Step 1 - Validate the fields to avoid unecessary requests
    if(!validateUsername(username))
      return {error: 1, msg : "Invalid Username"};

    if(!validatePassword(password))
      return {error: 2, msg : "Invalid Password"};

    //Step 2 - Perform the request
    jQuery.ajax({
      type : "POST",
      url  : this.getServiceURL()+"/login?user="+username+"&password="+password,
      contentType : "application/json",
      dataType: "json",
      success : function(data, xhr, status){
        result = JSON.parse(data);
        this.setToken(result.token);
        return true;
      },
      complete: function(xhr, status){
        return {error:3, msg: "Unable to login"};
      }
    });
  },

  logout : function(){
    if(!token)
      return {error : 1, msg : "No logged in to logout"};

    jQuery.ajax({
      type: "POST",
      url : this.getServiceURL()+"/logout",
      beforeSend: function(request){
        request.setRequestHeader("Authorization", "Bearer "+this.getToken());
      },
      success : function(data, xhr, status){
        this.setToken(false);
        return true;
      }
    });
    return false;
  },

  getHeaderToken : function(){
    return "Bearer " + this.getToken();
  }
};

/*******************************************************************************
*******************************************************************************/

function UserService(url){
  var serviceURL = url + "/users"

  this.getServiceURL = function(){
    return serviceURL;
  }
}

UserService.prototype = {

  registerUser : function(username, email, password, confirmPassword){

    //Step 1 - Validate the fields (white/blacklist)
    if(!validateUsername(username))
      return { error : 1, msg: "Invalid Username" };

    if(!validateEmail(email))
      return { error : 2, msg: "Invalid Email" };

    if(!validatePassword(password))
      return { error : 3, msg: "Invalid Password" };

    if(!(password == confirmPassword))
      return { error : 4, msg: "Passwords dont match" };

    //Step 2 - Encode the request as json string
    request = {
      username : username,
      email : email,
      password : password,
      confirmPassword : confirmPassword };

    request = JSON.stringify(request);
    //Step 3 - Perform the request jQuery.ajax(POST)
    jQuery.ajax({
      type: "POST",
      url : this.getServiceURL(),
      data: request,
      contentType: "application/json",
      success : function(result, status, xhr){
        return true;
      },
      complete: function(xhr, status){
        if(status != "success")
          return { error : 4, msg: "Invalid Password" };
        else
          return true;
      }
    });
  },

  changePassword : function(original, newPass, newConfirm, token){
    console.log("TODO: UserService - changePassword");
    //Step 1 - Validate the fields (white/blacklist)
    //Step 2 - Encode the request
    //Step 3 - Perform the request jQuery.ajax(POST) + Add token as header Authorization
    //Step 4 - Return true if successfull, false otherwise.
    return true;
  }
}

/*******************************************************************************
*******************************************************************************/
function PlanningService(serverURL){
  var serviceURL = serverURL + "/route";

  this.getServiceURL = function(){
    return serviceURL;
  }
}

PlanningService.prototype = {
  planRoute : function(from, to, safety, elevation, time){

    var request = {
      fromLat: from.lat, fromLon: from.lon,
      toLat : to.lat, toLon : to.lon,
      safetyPref : safety,
      elevationPref : elevation,
      timePref : time };

      /*
    jQuery.ajax({
      method: "POST",
      url : this.getServiceURL(),
      contentType : "application/json",
      dataType: "json",
      data : request,
      success : function(data, xhr, status){
        console.log(data);
        console.log(xhr);
        console.log(status);
        return false;
      },
      complete : function(xhr, status){
        console.log(xhr);
        console.log(status);
        return false;
      }
    }).done(function(data){console.log(data);});
    */
      /*
      var handler = function(data, status, xhr){
        console.log(data);
        console.log(xhr);
        console.log(status);
        return false;
      };

      jQuery.post(this.getServiceURL(),JSON.stringify(request),handler, "json");*/

      jQuery.ajax({
        method: "GET",
        url : this.getServiceURL()+"/test",
        contentType : "application/json",
        dataType: "jsonp",
        success : function(data, xhr, status){
          console.log(data);
          console.log(xhr);
          console.log(status);
          return false;
        },
        error: function(xhr, status, error) {
          //var err = eval("(" + xhr.responseText + ")");
          //alert(err.Message);
          console.log("xhr: "+xhr.responseText);
          console.log("status: "+status);
          console.log("error: "+error);
        }
      }).done(function(data){console.log(data);});

  },


  planAndSaveRoute : function(from, to, safety, elevation, time, token){
    console.log("TODO: PlanningService - planAndSaveRoute");
  }
}

/*******************************************************************************
*******************************************************************************/
function StreetRatingService(serverURL){
  var serviceURL = serverURL + "/streets";

  this.getServiceURL = function(){
    return serviceURL;
  }
}

StreetRatingService.prototype = {
  rateTrip : function(tripId, ratings, token){
    if(!token)
      return {error : 1, msg : "This operation requires an authenticated user."};

    jQuery.ajax({
      type: "POST",
      url : this.getServiceURL(),
      contentType : "application/json",
      dataType : "json",
      beforeSend : function(request){
        request.setRequestHeader("Authorization", "Bearer "+token);
      },
      success : function(data, xhr, status){
        return true;
      }
    });

    return { error : 2, msg : "Unable to classify this trip"};
  },

  getStreetGeometries : function(){
    jQuery.ajax({
      type: "GET",
      url : this.getServiceURL(),
      contentType: "application/json",
      dataType : "json",
      success : function(data, xhr, status){
        return JSON.parse(data);
      }
    });

    return {error : 1, msg: "Unable to fetch the streets geometries"}
  }
}

/*******************************************************************************
*******************************************************************************/
function TripService(serverURL){
  var serviceURL = serverURL + "/streets";

  this.getServiceURL = function(){
    return serviceURL;
  }
}

TripService.prototype = {
  listUserTrips : function(token){

    if(!token)
      return {error: 1, msg : "Unauthenticated user."}

    jQuery.ajax({
      type:"GET",
      url : this.getServiceURL()+"/list",
      contentType: "application/json",
      dataType : "json",
      beforeSend : function(request){
        request.setRequestHeader("Authorization", "Bearer "+token);
      },
      success : function(data, xhr, status){
        return JSON.parse(data);
      }
    });

    return {error :2 , msg : "Unable to complete the operation"};
  },

  getTripDetails : function(tripId, token){
    if(!token)
      return { error : 1 , msg : "Unauthenticated user"};

    jQuery.ajax({
      type:"GET",
      url : this.getServiceURL()+"/list/"+tripId,
      contentType:"application/json",
      dataType:"json",
      beforeSend : function(request){
        request.setRequestHeader("Authorization", "Bearer "+token);
      },
      success : function(data, xhr, status){
        return JSON.parse(data);
      }
    });

    return {error:2, msg:"UNable to complete the operation."};
  }
}


/*******************************************************************************
********************************************************************************
********************************************************************************
*******************************************************************************/

function CycleOurCity(serverURL) {
  this.serverURL  = serverURL;

  this.authService      = new AuthService(serverURL);
  this.userService      = new UserService(serverURL);
  this.plannerService   = new PlanningService(serverURL);
  this.tripService      = new TripService(serverURL);
  this.streetsService   = new StreetRatingService(serverURL);

  this.getServerURL = function(){
    return serverURL;
  };
};

CycleOurCity.prototype = {


  getAuthService : function(){
    return this.authService;
  },

  getUserService : function () {
    return this.userService;
  },

  getPlanningService : function(){
    return this.plannerService;
  },

  getStreetRatingService : function(){
    return this.streetsService;
  },

  getTripService : function(){
    return this.tripService;
  }
}

var coc = new CycleOurCity("http://localhost:8080/cycleourcity");
