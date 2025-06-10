import { requestExternal, requestPortal } from "../../utils/network";

export async function facilities() {
  const options = {
    method: "GET",
  };
  console.log("options", options);
  const data = await requestPortal(`/file/get/all/stateAndFacilities`, options);
  console.log("dataaa", data);
  return data;
}
