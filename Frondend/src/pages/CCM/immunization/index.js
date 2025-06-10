import CcmLayout from "@/components/ccmLayout";
import Immunization from "./Immunization";
import CcmFilters from "../ccmFilters";
import { actions as patientDetailsActions } from "../../../store/CCM";
import { connect } from "react-redux";
import React, { useEffect, useState } from "react";
import { useRouter } from "next/router";
import styles from "./Index.module.css";

const Index = ({
  immunizationCountsAPI,
  immunizationCountsFlow,
  singleImmunizationCountsAPI,
  singleImmunizationCountsFlow,
  patientNamesAPI,
  patientNamesFlow,
}) => {
  const router = useRouter();
  const [payload, setPayload] = useState(null);
  const [filters, setFilters] = useState({ age: null, patientName: [] });
  const [immunizationName, setImmunizationName] = useState(null);
  const [immunizationConditions, setImmunizationConditions] = useState([]);
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
      immunizationCountsAPI(payload);
    }
  }, [payload, immunizationCountsAPI]);

  // Handle immunization name click
  const handleImmunizationClick = (chartTitle, conditions) => {
    const immunizationName = chartTitle
      .toLowerCase()
      .replace(/[()]/g, "") 
      .split(" ")
      .map((word, index) =>
        index === 0 ? word : word.charAt(0).toUpperCase() + word.slice(1)
      )
      .join("");

    setImmunizationName(immunizationName);
    setImmunizationConditions(conditions);

    const updatedPayload = {
      ...payload,  
      immunizationName,
      patientName: filters.patientName,
      immunizationCondition: conditions || [], // Initialize as empty array
    };

    singleImmunizationCountsAPI(updatedPayload);
  };

  // Handle condition selection
  const handleConditionClick = (conditions) => {
    setImmunizationConditions(conditions);

    const updatedPayload = {
      ...payload,
      immunizationName,
      patientName: filters.patientName,
      immunizationCondition: conditions,
    };

    singleImmunizationCountsAPI(updatedPayload);
  };

  // Handle filter changes
  const handleFilterChange = (newFilters) => {
    setFilters(newFilters);

    const updatedPayloadWithPatientNames = {
      ...payload,
      patientName: newFilters.patientName,
    };

    if (updatedPayloadWithPatientNames) {
      immunizationCountsAPI(updatedPayloadWithPatientNames);
    }

    const updatedPayloadWithImmunizationName = {
      ...payload,
      patientName: newFilters.patientName,
      immunizationName,
    };

    if (updatedPayloadWithImmunizationName) {
      singleImmunizationCountsAPI(updatedPayloadWithImmunizationName);
    }
  };

  return (
    <CcmLayout>
      <div className={styles.container}>
        {/* Main Content */}
        <div className={styles.mainContent}>
          <Immunization
            payload={payload}
            immunizationCountsFlow={immunizationCountsFlow}
            singleImmunizationCountsFlow={singleImmunizationCountsFlow}
            onImmunizationClick={handleImmunizationClick}
            onConditionClick={handleConditionClick} // Pass the condition click handler
          />
        </div>

        {/* Filters Sidebar */}
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
    immunizationCountsFlow: state?.ccmPatientDetails.immunizationCounts.data,
    singleImmunizationCountsFlow:
      state?.ccmPatientDetails.singleImmunizationCounts.data,
    patientNamesFlow: state?.ccmPatientDetails.patientNames.data,
  }),
  {
    immunizationCountsAPI: patientDetailsActions.immunizationCountsAction,
    singleImmunizationCountsAPI:
      patientDetailsActions.singleImmunizationCountsAction,
    patientNamesAPI: patientDetailsActions.patientNamesAction,
  }
);
export default enhancer(Index);
