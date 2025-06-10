import { requestExternal, requestPortal } from "../../utils/network";

export async function workFlow() {
  const options = {
    method: "GET",
    body: JSON.stringify({})
  };
  const data = await requestPortal(`comments`, options);
  return data;
}

export async function login(values) {
  console.log("valuers", values);
  const options = {
    method: "POST",
    body: JSON.stringify(values),
  };
  console.log("options", options);
  const data = await requestPortal(`/dbservice/login`, options);
  console.log("dataaa", data);
  return data;
}
