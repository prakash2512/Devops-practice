import { combineReducers } from "redux";
import { handleActions } from "redux-actions";
import { assessmentCountsAction, conditionCaregapCountsAction, immunizationCountsAction, patientNamesAction, singleAssessmentCountsAction, singleImmunizationCountsAction,ccmDynamicAction, ccmDynamicEditAction, ccmSinglePatientDetailsAction, ccmUploadPdfAction, ccmViewPdfAction, ccmDiagnosisPdfAction } from "./actions";
import { ccmDiagnosisPdf, ccmUploadPdf, ccmViewPdf } from "./network";

const initialState = {
  loading: true,
  data: null,
  error: null,
};

const createReducer = (actionType) =>
  handleActions(
    {
      [actionType.STARTED]: (state, action) => ({
        ...state,
        loading: true,
        error: null,
      }),
      [actionType.SUCCEEDED]: (state, action) => ({
        ...state,
        loading: false,
        data: action.payload,
        error: null,
      }),
      [actionType.FAILED]: (state, action) => ({
        ...state,
        loading: false,
        error: action.payload,
      }),
    },
    initialState
  );

const rootReducer = combineReducers({
  patientNames:createReducer(patientNamesAction),
  conditionCaregapCounts:createReducer(conditionCaregapCountsAction),
  immunizationCounts:createReducer(immunizationCountsAction),
  singleImmunizationCounts:createReducer(singleImmunizationCountsAction),
  assessmentCounts:createReducer(assessmentCountsAction),
  singleAssessmentCounts:createReducer(singleAssessmentCountsAction),
  ccmDynamic:createReducer(ccmDynamicAction),
  ccmDynamicEdit:createReducer(ccmDynamicEditAction),
  ccmSinglePatientDetails:createReducer(ccmSinglePatientDetailsAction),
  ccmUploadPdf:createReducer(ccmUploadPdfAction),
  ccmViewPdf:createReducer(ccmViewPdfAction),
  ccmDiagnosisPdf:createReducer(ccmDiagnosisPdfAction)
});

export default rootReducer;
