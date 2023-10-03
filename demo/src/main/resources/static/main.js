 $.get("/user", function(data) {
    $("#user").html(data.name);
    $(".unauthenticated").hide()
    $(".authenticated").show()
}).fail(function() {
    $(".error").html('An error occurred while fetching the data.(/user)');
});

$.ajaxSetup({
  beforeSend : function(xhr, settings) {
    if (settings.type == 'POST' || settings.type == 'PUT' || settings.type == 'DELETE') {
      if (!(/^http:.*/.test(settings.url) || /^https:.*/.test(settings.url))) {
        // Only send the token to relative URLs i.e. locally.
        xhr.setRequestHeader("X-XSRF-TOKEN", Cookies.get('XSRF-TOKEN'));
  		console.log("CSRF Token:", Cookies.get('XSRF-TOKEN'));
      }
    }
  }
});

async function logout() {
    let csrfToken = "";
    await fetch("/csrftoken", {
        method:'GET'
    })
    .then(res => res.text())
    .then(str => {
        csrfToken = str;
    });

    let response = "";
    await fetch("/logout", {
        method: 'POST'
    ,   body: new FormData()
    ,   headers: {
            'X-XSRF-TOKEN' : csrfToken
        }
    })
    .then(response =>  response.text())
    .then(data => {
        response = data;
    });

	$("#user").html('');
	$(".error").html('');
	$(".unauthenticated").show();
	$(".authenticated").hide();
    console.log(response);
}

$.get("/error", function(data) {
    if (data) {
        $(".error").html(data);
    } else {
        $(".error").html('');
    }
}).fail(function() {
    $(".error").html('An error occurred while fetching the data.(/error)');
});