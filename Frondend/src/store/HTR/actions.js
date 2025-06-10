import { createActionThunk } from "../../utils/redux";
import * as network from "./network";

export const dashBoardDetailsAction = createActionThunk(
  "DASHBOARDDETAILSACTION",
  network.dashboardDetails
);

export const getFilterDetailsAction = createActionThunk(
  "GETFILTERDETAILSACTION",
  network.getFilterDetails
);
