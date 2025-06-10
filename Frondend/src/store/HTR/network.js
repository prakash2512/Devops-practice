import { requestExternal, requestPortal } from "../../utils/network";

//Population
export async function dashboardDetails(payload) {
  console.log("valuers", payload);
  const options = {
    method: "POST",
    body: JSON.stringify(payload),
  };
  const data = await requestPortal(`/htr/dashboard/details`, options);

  return data;
}


//Population
export async function getFilterDetails(payload) {
  console.log("valuers", payload);
  const options = {
    method: "POST",
    body: JSON.stringify(payload),
  };
  const data = await requestPortal(`/htr/getFilterDetails`, options);

  return data;
}
