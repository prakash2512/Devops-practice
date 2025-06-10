import { createActionThunk } from "../../utils/redux";
import * as network from "./network";

export const bhiHomeAction = createActionThunk(
  "BHIHOME_DATA",
  network.bhiHome
);

export const bhiPatientAction = createActionThunk(
  "BHIPATIENT_DATA",
  network.bhiPatient
);