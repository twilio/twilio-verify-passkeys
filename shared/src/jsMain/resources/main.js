let sessionUsername = sessionStorage.getItem("session");

const loadApp = (username) => {
    document.getElementById("welcome").innerHTML = `Welcome ${username}`
    document.getElementById("modal").classList.add("invisible");
    document.getElementById("container").classList.add("invisible");
    document.getElementById("app").classList.remove("invisible");
}

if (sessionUsername) {
    loadApp(sessionUsername)
}

const usernameElement = document.getElementById("usr_input");
const errorElement = document.getElementById("error");
const continueButton = document.getElementById("continue");
window.intlTelInput(usernameElement, {
    initialCountry: "us",
    showSelectedDialCode: true,
    utilsScript: "https://cdn.jsdelivr.net/npm/intl-tel-input@19.5.3/build/js/utils.js",
});

const checkAvalibility = () => {
    const username = usernameElement.value
    if(username) {
        continueButton.classList.add("enable");
        continueButton.disabled = false;
    } else {
        continueButton.classList.remove("enable");
        continueButton.disabled = true;
    }
}

function ArrayBufferToBase64(buffer) {
    // Convert ArrayBuffer to base64 string
    const binary = String.fromCharCode.apply(null, new Uint8Array(buffer));
    let base64String = btoa(binary);

    // Replace characters not allowed in base64url with their corresponding URL-safe alternatives
    base64String = base64String.replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, '');

    return base64String;
}

const strToArrayBuffer = (str) => {
    const decodedString = atob(str);

    const buffer = new Uint8Array(decodedString.length);
    for (let i = 0; i < buffer.length; i++) {
        buffer[i] = decodedString.charCodeAt(i);
    }
    return buffer;
}

function stringToArrayBuffer(str) {
    const encoder = new TextEncoder();
    const uint8Array = encoder.encode(str);
    return uint8Array.buffer;
}

const login = () => {
    const authenticationCard = document.getElementById("container");
    const passkeyCard = document.getElementById("modal");
    authenticationCard.classList.add("invisible");
    passkeyCard.classList.remove("invisible");
}

const logOut = () => {
    sessionStorage.removeItem("session");
    document.getElementById("app").classList.add("invisible");
    document.getElementById("container").classList.remove("invisible");
}

if (module && module.exports) {
  module.exports = signUp;
}
