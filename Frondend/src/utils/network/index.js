import { checkStatus } from "./helper";
import { getStorage } from "../storages";
import { portalUrl, tokenKey } from "../config";

export async function requestPortal(url, options) {
  const token = await getStorage(tokenKey);
  const actualUrl = `${portalUrl}${url}`;
  const actualOptions = {
    ...options,
    headers: {
      Authorization: `${"Bearer" + " " + token}`,
      "Content-Type": "application/json",
    },
  };
  return fetch(actualUrl, actualOptions).then(checkStatus);
}

export async function requestPortalForm(url, options) {
  const token = await getStorage(tokenKey);
  const actualUrl = `${portalUrl}${url}`;
  const actualOptions = {
    ...options,
    headers: {
      Authorization: `${"Bearer" + " " + token}`,
      'Content-Type': 'multipart/form-data',
    },
  };
  return fetch(actualUrl, actualOptions).then(checkStatus);
}

export async function requestPortalPdf(url, options) {
  const token = await getStorage(tokenKey);
  const actualUrl = `${portalUrl}${url}`;
  const headers = {
    Authorization: `Bearer ${token}`,
    ...(options?.headers || {}),
  };

  // Don't set Content-Type for FormData, only for JSON
  if (!(options?.body instanceof FormData) && !headers["Content-Type"]) {
    headers["Content-Type"] = "application/json";
  }

  const actualOptions = {
    ...options,
    headers,
  };

  const response = await fetch(actualUrl, actualOptions);

  // If expecting PDF
  const contentType = response.headers.get("content-type") || "";
  if (response.ok && contentType.includes("application/pdf")) {
    return response.blob(); // return binary  
  }

  return checkStatus(response); // fall back for JSON/etc.
}


export async function requestPortalWithoutToken(url, options) {
  // const token = await getStorage(tokenKey);
  const actualUrl = `${portalUrl}${url}`;
  console.log(actualUrl, 'actualUrl')
  const actualOptions = {
    ...options,
    headers: {
      // Authorization: `${"Bearer" + " " + token}`,  
      "Content-Type": "application/json",
    },
  };
  return fetch(actualUrl, actualOptions).then(checkStatus);
}

export async function requestExternal(url, options, path) {
  const actualUrl = `${portalUrl}${url}`;
  const actualOptions = {
    ...options,
    body: JSON.stringify(body),
    headers: {
      Authorization: `${"Bearer" + " " + token}`,
      "Content-Type": "application/json",
    },
  };
  return fetch(actualUrl, actualOptions).then(checkStatus);
}
