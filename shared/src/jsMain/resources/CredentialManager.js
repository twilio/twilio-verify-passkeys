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

function CredentialManager() {}

CredentialManager.createCredential = async (requestResponse) => {
  const { rp, user, pubKeyCredParams, attestation, timeout, challenge, authenticatorSelection } = JSON.parse(requestResponse)

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
          pubKeyCredParams: pubKeyCredParams,
          attestation: attestation,
          timeout: timeout,
          challenge: strToArrayBuffer(challenge),
          authenticatorSelection: authenticatorSelection,
          extensions: {
              credProps: true,
          }
      }
  }

  let credential = await navigator.credentials.create(createCredentialDefaultArgs);

  const { id, rawId, response, type, authenticatorAttachment } = credential;
  const { attestationObject, clientDataJSON } = response;

  const rawIdString = ArrayBufferToBase64(rawId);
  const attestationObjectString = ArrayBufferToBase64(attestationObject);
  const clientDataJSONString = ArrayBufferToBase64(clientDataJSON);

  return JSON.stringify({
      id: id,
      response: {
          attestationObject: attestationObjectString,
          clientDataJSON: clientDataJSONString,
          transports: ["internal"]
      },
      rawId: rawIdString,
      type: type,
      authenticatorAttachment: authenticatorAttachment,
  })
}

CredentialManager.getCredential = async (requestResponse) => {
  const { challenge, rpId, allowCredentials, userVerification } = JSON.parse(requestResponse)
  
  const publicKey = {
      challenge: Uint8Array.from(atob(challenge), c => c.charCodeAt(0)),
      rpId,
      allowCredentials,
      userVerification
  }

  const credential = await navigator.credentials.get({ publicKey });

  const { id, rawId, response, type, authenticatorAttachment } = credential
  const { authenticatorData, clientDataJSON, signature, userHandle } = response

  return JSON.stringify({
      id: id,
      rawId: ArrayBufferToBase64(rawId),
      type: type,
      authenticatorAttachment: authenticatorAttachment,
      response: {
          clientDataJSON: ArrayBufferToBase64(clientDataJSON),
          authenticatorData: ArrayBufferToBase64(authenticatorData),
          signature: ArrayBufferToBase64(signature),
          userHandle: ArrayBufferToBase64(userHandle)
      },
  })
}

if (module && module.exports) {
    module.exports = CredentialManager;
  }