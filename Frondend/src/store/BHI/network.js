import { requestExternal, requestPortal } from "../../utils/network";

export async function bhiHome(payload) {
  console.log("bhi", payload);
  const options = {
    method: "POST",
    body: JSON.stringify(payload),
  };
  console.log("bhioptions", options);
  const data = await requestPortal(`/bhi/get/dashboard/details`, options);
  console.log("dataaa", data);
  return data;
}

export async function bhiPatient(payload) {
    console.log("valuers", payload);
    const options = {
      method: "POST",
      body: JSON.stringify(payload),
    };
    console.log("options", options);
    const data = await requestPortal(`/bhi/get/patients/details`, options);
    console.log("dataaa", data);
    return data;
  }