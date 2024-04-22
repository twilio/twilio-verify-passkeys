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

const authenticateJs = async (domain) => {
    try {
        const response = await fetch(`${domain}authentication/start`);
        const responseJSON = await response.json();
        const { challenge, rpId, allowCredentials, timeout, userVerification } = responseJSON.publicKey
        const publicKey = {
            challenge: Uint8Array.from(atob(challenge), c => c.charCodeAt(0)),
            rpId,
            allowCredentials,
            userVerification
        }

        navigator.credentials.get({ publicKey })
            .then(async (publicKeyCredential) => {
                const { id, rawId, response, type, authenticatorAttachment } = publicKeyCredential
                const { authenticatorData, clientDataJSON, signature, userHandle } = response

                const authentication = await fetch('${domain}/authentication/verification', {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        id: id,
                        rawId: ArrayBufferToBase64(rawId),
                        type: type,
                        clientDataJson: ArrayBufferToBase64(clientDataJSON),
                        authenticatorData: ArrayBufferToBase64(authenticatorData),
                        signature: ArrayBufferToBase64(signature),
                        userHandle: ArrayBufferToBase64(userHandle)
                    })
                });

                const authenticationJSON = await authentication.json();
                const {status, identity} = authenticationJSON
                return status
            })
            .catch((err) => {
                return("Something goes wrong or maybe you dont have a passkey for this application yet")
            });
    } catch (error) {
        return(err)
    }
}

if (module && module.exports) {
  module.exports = authenticateJs;
}