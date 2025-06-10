import { createActionThunk } from "../../utils/redux";
import * as network from "./network";

export const getYearAction = createActionThunk(
  "GETYEARACTION",
  network.getYear
);
