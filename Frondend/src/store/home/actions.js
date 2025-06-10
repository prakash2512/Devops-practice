import { createActionThunk } from "../../utils/redux";
import * as network from "./network";

export const workFlowAction = createActionThunk(
  "WORKFLOW_DATA",
  network.workFlow
);

export const loginAction = createActionThunk(
  "LOGINRESPONSE",
  network.login
);