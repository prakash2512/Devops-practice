import { createActionThunk } from "../../utils/redux";
import * as network from "./network";

export const facilitiesAction = createActionThunk(
  "FACILITIESRESPONSE",
  network.facilities
);