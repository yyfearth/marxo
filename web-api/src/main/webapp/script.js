document.addEventListener("DOMContentLoaded", onReady);

function onReady() {
    document.removeEventListener("DOMContentLoaded", onReady);
    var anchorElement = document.querySelector('#login');
    anchorElement.href = "http://test@example.com:test@" + location.host + "/api/workflows";
}
