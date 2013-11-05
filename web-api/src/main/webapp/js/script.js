"use strict";

document.addEventListener("DOMContentLoaded", onReady);

function onReady() {
    document.removeEventListener("DOMContentLoaded", onReady);

    var email = encodeURIComponent('test@example.com');
    var password = encodeURIComponent('B4driGpKjDrtdKaAoA8nUmm+D2Pl3kxoF5POX0sGSk4');
    var uri = location.protocol + '//' + email + ':' + password + '@' + location.host + location.pathname;

    $('#api-list').find('a').each(function (index, element) {
        var link = $(element).attr('href');
        $(element).attr('href', uri + link);
    });
}
