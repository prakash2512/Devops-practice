import React, { useEffect, useMemo, useState } from "react";
import CcmLayout from "@/components/ccmLayout";
import Population from "./Population";
import { connect } from "react-redux";
import { actions as patientDetailsActions } from "../../../store/CCM";
import CcmFilters from "../ccmFilters";
import styles from "./Index.module.css";

const Index = ({
  conditionCaregapCountsAPI,
  conditionCaregapCountsFlow,
  patientNamesAPI,
  patientNamesFlow,
  ccmDiagnosisPdfAPI,
  ccmDiagnosisPdfFlow,
}) => {
  const [filters, setFilters] = useState({
    age: null,
    patientCondition: [],
    patientName: [],
  });

  const [selectedDataset, setSelectedDataset] = useState(1);
  const [isLoading, setIsLoading] = useState(false);
  const [payload, setPayload] = useState(null);

  // Retrieve payload from local storage
  useEffect(() => {
    if (typeof window !== "undefined") {
      const getPayload = localStorage.getItem("payload");
      setPayload(getPayload ? JSON.parse(getPayload) : null);
    }
  }, []);

  console.log("Stored Payload:", payload);

  // Fetch condition caregap counts when storedPayload, filters, or dataset changes
  useEffect(() => {
    if (payload) {
      setIsLoading(true);
      const updatedPayload = {
        ...payload,
        ...filters,
        dx: selectedDataset,
      };

      console.log("Updated Payload with Filters and Dataset:", updatedPayload);
      conditionCaregapCountsAPI(updatedPayload)
        .catch((error) => {
          console.error("Error fetching condition caregap counts:", error);
        })
        .finally(() => {
          setIsLoading(false);
        });
    }
  }, [payload, filters, selectedDataset, conditionCaregapCountsAPI]);

  // Fetch patient names on component mount
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

  // Handle filter changes from CcmFilters
  const handleFilterChange = (newFilters) => {
    setFilters(newFilters);
  };

  // Handle dataset changes from Population
  const handleDatasetChange = (dataset) => {
    setSelectedDataset(dataset);

    // Reset patientCondition to an empty array when DX2 is selected
    if (dataset === 2) {
      setFilters((prevFilters) => ({
        ...prevFilters,
        patientCondition: [],
      }));
    }
  };

  return (
    <CcmLayout>
    
        {/* Main Content - Population Component */}
        {
          conditionCaregapCountsFlow?.loading ? <div>Loading....</div> :
          <div className={styles.container}>
            <div className={styles.populationContainer}>
          <Population
            payload={payload}
            conditionCaregapCountsFlow={conditionCaregapCountsFlow.data}
            onDatasetChange={handleDatasetChange}
            isLoading={isLoading}
            ccmDiagnosisPdfAPI={ccmDiagnosisPdfAPI} // Pass the API
            ccmDiagnosisPdfFlow={ccmDiagnosisPdfFlow}
          />
        </div>

        {/* Right Sidebar - CcmFilters Component */}
        <div className={styles.filtersContainer}>
          <CcmFilters
            conditionCaregapCountsFlow={conditionCaregapCountsFlow.data}
            patientNamesFlow={patientNamesFlow}
            onFilterChange={handleFilterChange}
            selectedDataset={selectedDataset}
          />
        </div>
        </div>
          
          

        }
      
      
    </CcmLayout>
  );
};

const enhancer = connect(
  (state) => ({
    conditionCaregapCountsFlow: state?.ccmPatientDetails.conditionCaregapCounts,
    patientNamesFlow: state?.ccmPatientDetails.patientNames.data,
    ccmDiagnosisPdfFlow: state?.ccmPatientDetails.ccmDiagnosisPdf,
  }),
  {
    conditionCaregapCountsAPI: patientDetailsActions.conditionCaregapCountsAction,
    patientNamesAPI: patientDetailsActions.patientNamesAction,
    ccmDiagnosisPdfAPI: patientDetailsActions.ccmDiagnosisPdfAction, // Add this action
  }
);
export default enhancer(Index);
