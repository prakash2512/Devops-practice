import { requestExternal, requestPortal } from "../../utils/network";

export async function getYear() {
  const options = {
    method: "GET",
    // body: JSON.stringify({})
  };
  const data = await requestPortal(`/ccm/get/years`, options);

  return data;
}
