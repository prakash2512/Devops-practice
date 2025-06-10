import { createActionThunk } from "../../utils/redux";
import * as network from "./network";

export const patientNamesAction = createActionThunk(
  "PATIENTNAMESRESPONSE",
  network.patientNames
);

export const conditionCaregapCountsAction = createActionThunk(
  "CONDITIONCAREGAPCOUNTSRESPONSE",
  network.conditionCaregapCounts
);

export const immunizationCountsAction = createActionThunk(
  "IMMUNIZATIONCOUNTSRESPONSE",
  network.immunizationCounts
);

export const singleImmunizationCountsAction = createActionThunk(
  "SINGLEIMMUNIZATIONCOUNTSRESPONSE",
  network.singleImmunizationCounts
);

export const assessmentCountsAction = createActionThunk(
  "ASSESSMENTCOUNTSRESPONSE",
  network.assessmentCounts
);

export const singleAssessmentCountsAction = createActionThunk(
  "SINGLEASSESSMENTCOUNTSRESPONSE",
  network.singleAssessmentCounts
);

//dynamic
export const ccmDynamicAction = createActionThunk(
  "CCMDYNAMICRESPONSE",
  network.ccmDynamic
);

export const ccmDynamicEditAction = createActionThunk(
  "CCMDYNAMICEDITRESPONSE",
  network.ccmDynamicEdit
);

export const ccmSinglePatientDetailsAction = createActionThunk(
  "CCMSINGLEPATIENTDETAILSRESPONSE",
  network.ccmSinglePatientDetails
);

export const ccmUploadPdfAction = createActionThunk(
  "CCMUPLOADRESPONSE",
  network.ccmUploadPdf
);

export const ccmViewPdfAction = createActionThunk(
  "CCMVIEWPDFRESPONSE",
  network.ccmViewPdf
);

export const ccmDiagnosisPdfAction = createActionThunk(
  "CCMDIAGNOSISPDFRESPONSE",
  network.ccmDiagnosisPdf
);