window.fbAsyncInit = function () {
    console.log('Initialing Facebook SDK');

    FB.init({
        appId: '213527892138380', // App ID
//            channelUrl: '//WWW.YOUR_DOMAIN.COM/channel.html', // Channel File
        status: false, // check login status
        cookie: true, // enable cookies to allow the server to access the session
        xfbml: true  // parse XFBML
    });

    FB.Event.subscribe('auth.authResponseChange', function (response) {
        console.log('FB auth response changed:', response);

        // Here we specify what we do with the response anytime this event occurs.
        if (response.status === 'connected') {
            console.log('access token: ', response.authResponse.accessToken);
            console.log('expires in: ', response.authResponse.expiresIn);
            console.log('signed request: ', response.authResponse.signedRequest);

            console.log('The user is logged in');
        } else if (response.status === 'not_authorized') {
            console.log("The user doesn't authorize the app yet.");
        } else {
            console.log('The user is not logged into Facebook');
        }
    });
};

// Load the SDK asynchronously
(function (d) {
    console.log('Loading Facebook SDK');
    var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
    if (d.getElementById(id)) {
        return;
    }
    js = d.createElement('script');
    js.id = id;
    js.async = true;
    js.src = "http://connect.facebook.net/en_US/all.js";
    ref.parentNode.insertBefore(js, ref);
}(document));

$('#login').click(function () {
    var scopes = {scope: 'publish_actions, email, '};

    FB.login(function (response) {
        console.log('Login response: ', response);
    }, scopes);
});

$('#logout').click(function () {
    FB.logout(function() {
        console.log('Logout!');
    });
});