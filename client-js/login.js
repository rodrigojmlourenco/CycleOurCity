var authURL =

var Security;

//TODO: falta apanhar o token e guardá-lo
//TODO: validate the user and passwork (whitelist/blacklist)
function loginUser(user, password){

  jQuery.ajax({
    type: "POST",
    url: MyApp.Server.auth+"/login?user="+user+"&password="+password,
    success: function(data, textStatus, xhr){
      /*
       if(msg.status == true){
           addLogOut(msg.text);
           jQuery("#myTabs").tabs('enable', 1);
       }
       else if(msg.status == false){
           if(jQuery("#error").size() > 0){
               //ja existe
               jQuery("#error").text(msg.text);
           }
           else{
               jQuery("#signin_menu").append('<div id="error" class="error"></div>');
               jQuery("#error").text(msg.text);
           }

           MyApp.AllowSubmitLogin = true;
       }
       */
    },
    complete: function(xhr, textStatus){

    }
  });
}

function logout(){
  jQuery.ajax({
    type : "POST",
    url : MyApp.Server.auth+"/logout",
    beforeSend: function(request){
      request.setRequestHeader("Authorization", "Bearer "+Security.token);
    },
    success: function(data, textStatus, xhr){

    },
    complete: function(xhr, textStatus){

    }
  });
}
