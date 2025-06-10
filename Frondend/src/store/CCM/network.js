import { requestExternal, requestPortal, requestPortalForm, requestPortalPdf } from "../../utils/network";

export async function patientNames(payload) {
  const options = {
    method: "POST",
    body: JSON.stringify(payload),
  };
  console.log("options", options);
  const data = await requestPortal(`/ccm/get/patientsNames`, options);
  console.log("dataa", data);
  return data;
}

//Population
export async function conditionCaregapCounts(payload) {
  console.log("valuers", payload);
  const options = {
    method: "POST",
    body: JSON.stringify(payload),
  };
  console.log("options", options);
  const data = await requestPortal(`/ccm/get/populationAndConditionCareGap/count`, options);
  console.log("dataa", data);
  return data;
}

//Immunization
export async function immunizationCounts(payload) {
  console.log("valuers", payload);
  const options = {
    method: "POST",
    body: JSON.stringify(payload),
  };
  console.log("options", options);
  const data = await requestPortal(`/ccm/getImmunizationDetails`, options);
  console.log("dataa", data);
  return data;
}

export async function singleImmunizationCounts(payload) {
  console.log("valuers", payload);
  const options = {
    method: "POST",
    body: JSON.stringify(payload),
  };
  console.log("options", options);
  const data = await requestPortal(`/ccm/singleImmunizationDetails`, options);
  console.log("dataa", data);
  return data;
}

//Assessment
export async function assessmentCounts(payload) {
  console.log("valuers", payload);
  const options = {
    method: "POST",
    body: JSON.stringify(payload),
  };
  console.log("options", options);
  const data = await requestPortal(`/ccm/getAssessmentDetails`, options);
  console.log("dataa", data);
  return data;
}

export async function singleAssessmentCounts(payload) {
  console.log("valuers", payload);
  const options = {
    method: "POST",
    body: JSON.stringify(payload),
  };
  console.log("options", options);
  const data = await requestPortal(`/ccm/singleAssessmentDetails`, options);
  console.log("dataa", data);
  return data;
}

//dynamic
export async function ccmDynamic(payload) {
  console.log("valuers", payload);
  const options = {
    method: "POST",
    body: JSON.stringify(payload),
  };
  console.log("options", options);
  const data = await requestPortal(`/ccm/get/details`, options);
  console.log("dataa", data);
  return data;
}

export async function ccmDynamicEdit(payload) {
  console.log("valuers", payload);
  const options = {
    method: "POST",
    body: JSON.stringify(payload),
  };
  console.log("options", options);
  const data = await requestPortal(`/ccm/edit/program/filter`, options);
  console.log("dataa", data);
  return data;
}

export async function ccmSinglePatientDetails(payload) {
  console.log("valuers", payload);
  const options = {
    method: "POST",
    body: JSON.stringify(payload),
  };
  console.log("options", options);
  const data = await requestPortal(`/ccm/patients/program/filter`, options);
  console.log("dataa", data);
  return data;  
}

//pdf
export async function ccmUploadPdf(payload) {
  console.log("valuers", payload);
  const options = {
    method: "POST",
    body: payload,
  };
  console.log("options", options);
  const data = await requestPortalForm(`/ccm/upload/pdf`, options);
  console.log("dataa", data);
  return data;
}

export async function ccmViewPdf(payload) {
  console.log("valuers", payload);
  const options = {
    method: "POST",
    body: JSON.stringify(payload),
  };
  console.log("options", options);
  const data = await requestPortalPdf(`/ccm/download/pdf`, options);
  console.log("dataa", data);
  return data;
}

export async function ccmDiagnosisPdf(payload) {
  console.log("valuers", payload);
  const options = {
    method: "POST",
    body: JSON.stringify(payload),
  };
  console.log("options", options);
  const data = await requestPortalPdf(`/ccm/download/diagnosisPdf`, options);
  console.log("dataa", data);
  return data;
}