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

const createJs = async (username, domain) => {
    // const username = usernameElement.value

    try {
        const response = await fetch(`${domain}/registration/start`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username: username
            })
        });

        const responseJSON = await response.json();
        const { challenge, rp, user, pubKeyCredParams, factor_sid } = responseJSON;

        const createCredentialDefaultArgs = {
            publicKey: {
                rp: {
                    name: rp.name,
                    id: rp.id
                },
                user: {
                    id: strToArrayBuffer(user.id),
                    name: user.name,
                    displayName: user.displayName
                },
                pubKeyCredParams: [{ type: "public-key", alg: -7 }],
                attestation: "none",
                timeout: 600000,
                challenge: strToArrayBuffer(challenge),
                authenticatorSelection: {
                    residentKey: "preferred"
                },
                extensions: {
                    credProps: true,
                }
            }
        }

        let credential = await navigator.credentials.create(createCredentialDefaultArgs);


        const { id, rawId, authenticatorAttachment, response: pubKeyCredResponse, type } = credential;
        const { attestationObject, clientDataJSON } = pubKeyCredResponse;

        const rawIdString = ArrayBufferToBase64(rawId);
        const attestationObjectString = ArrayBufferToBase64(attestationObject);
        const clientDataJSONString = ArrayBufferToBase64(clientDataJSON);

        const verificationResponse = await fetch(`${domain}/registration/verification`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                id: id,
                attestationObject: attestationObjectString,
                rawId: rawIdString,
                type: type,
                clientDataJson: clientDataJSONString,
                transports: ["internal"]
            })
        });

        const verRespJSON = await verificationResponse.json();
        const { status } = verRespJSON
        return status
    } catch (error) {
        console.log(error);
    }
}

if (module && module.exports) {
  module.exports = createJs;
}