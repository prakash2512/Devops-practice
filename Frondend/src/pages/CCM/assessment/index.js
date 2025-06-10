import CcmLayout from "@/components/ccmLayout";
import Assessment from "./Assessment";
import CcmFilters from "../ccmFilters";
import { actions as patientDetailsActions } from "../../../store/CCM";
import { connect } from "react-redux";
import React, { useEffect, useState } from "react";
import styles from "./Index.module.css";

const Index = ({
  assessmentCountsAPI,
  assessmentCountsFlow,
  singleAssessmentCountsAPI,
  singleAssessmentCountsFlow,
  patientNamesAPI,
  patientNamesFlow,
}) => {
  const [payload, setPayload] = useState(null);
  const [filters, setFilters] = useState({ age: null, patientName: [] });
  const [assessmentName, setAssessmentName] = useState(null);
  const [assessmentConditions, setAssessmentConditions] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (payload) {
      setIsLoading(true);
      patientNamesAPI(payload)
        .catch((error) => {
          console.error("Error fetching patient names:", error);
        })
        .finally(() => {
          setIsLoading(false);
        });
    }
  }, [payload, patientNamesAPI]);

  // Retrieve payload from local storage
  useEffect(() => {
    const getPayload = localStorage.getItem("payload");
    if (getPayload) {
      setPayload(JSON.parse(getPayload));
    }
  }, []);

  // Fetch data when payload changes
  useEffect(() => {
    if (payload) {
      assessmentCountsAPI(payload);
    }
  }, [payload, assessmentCountsAPI]);

  // Handle assessment name click
  const handleAssessmentClick = (chartTitle) => {
    const assessmentName = chartTitle
      .toLowerCase()
      .replace(/[()]/g, "")
      .split(" ")
      .map((word, index) =>
        index === 0 ? word : word.charAt(0).toUpperCase() + word.slice(1)
      )
      .join("");

    setAssessmentName(assessmentName);

    const updatedPayload = {
      ...payload,
      assessmentName,
      patientName: filters.patientName,
    };

    singleAssessmentCountsAPI(updatedPayload);
  };

  // Handle filter changes
  const handleFilterChange = (newFilters) => {
    setFilters(newFilters);

    const updatedPayloadWithPatientNames = {
      ...payload,
      patientName: newFilters.patientName,
    };

    if (updatedPayloadWithPatientNames) {
      assessmentCountsAPI(updatedPayloadWithPatientNames);
    }

    const updatedPayloadWithAssessmentName = {
      ...payload,
      patientName: newFilters.patientName,
      assessmentName,
    };

    if (updatedPayloadWithAssessmentName) {
      singleAssessmentCountsAPI(updatedPayloadWithAssessmentName);
    }
  };

  // Handle condition selection
  const handleConditionClick = (conditions) => {
    setAssessmentConditions(conditions);

    const updatedPayload = {
      ...payload,
      assessmentName,
      patientName: filters.patientName,
      patientCondition: conditions,
    };

    singleAssessmentCountsAPI(updatedPayload);
  };

  return (
    <CcmLayout>
      <div className={styles.container}>
        <div className={styles.mainContent}>
          <Assessment
            payload={payload}
            assessmentCountsFlow={assessmentCountsFlow}
            singleAssessmentCountsFlow={singleAssessmentCountsFlow}
            onAssessmentClick={handleAssessmentClick}
            onConditionClick={handleConditionClick} // Pass the condition handler
          />
        </div>
        <div className={styles.sidebar}>
          <CcmFilters
            patientNamesFlow={patientNamesFlow}
            onFilterChange={handleFilterChange}
            hidePatientConditions={true}
          />
        </div>
      </div>
    </CcmLayout>
  );
};

const enhancer = connect(
  (state) => ({
    assessmentCountsFlow: state?.ccmPatientDetails.assessmentCounts.data,
    singleAssessmentCountsFlow:
      state?.ccmPatientDetails.singleAssessmentCounts.data,
    patientNamesFlow: state?.ccmPatientDetails.patientNames.data,
  }),
  {
    assessmentCountsAPI: patientDetailsActions.assessmentCountsAction,
    singleAssessmentCountsAPI:
      patientDetailsActions.singleAssessmentCountsAction,
    patientNamesAPI: patientDetailsActions.patientNamesAction,
  }
);
export default enhancer(Index);
