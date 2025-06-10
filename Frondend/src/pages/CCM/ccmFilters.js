import React, { useState } from "react";
import styles from "./ccmFilters.module.css";
import { FaFilter, FaTimes, FaCheckSquare, FaSquare } from "react-icons/fa";

const CcmFilters = ({
  conditionCaregapCountsFlow,
  onFilterChange,
  patientNamesFlow,
  hidePatientConditions = false,
  selectedDataset,
}) => {
  const patientNames = patientNamesFlow?.response || [];

  const patientConditions =
    conditionCaregapCountsFlow?.response?.[
      selectedDataset === 1 ? "patientConditionList1" : "patientConditionList2"
    ] || [];

  const [selectedAge, setSelectedAge] = useState(null);
  const [selectedConditions, setSelectedConditions] = useState([]);
  const [selectedPatients, setSelectedPatients] = useState([]);
  const [isMultiSelectAge, setIsMultiSelectAge] = useState(false);
  const [isMultiSelectConditions, setIsMultiSelectConditions] = useState(false);
  const [isMultiSelectPatients, setIsMultiSelectPatients] = useState(false);

  const handleAgeSelect = (age) => {
    const newAge = selectedAge === age ? null : age;
    setSelectedAge(newAge);
    sendPayload({
      age: newAge,
      patientCondition: selectedConditions,
      patientName: selectedPatients,
    });
  };

  const handleConditionSelect = (condition) => {
    let newConditions;
    if (isMultiSelectConditions) {
      newConditions = selectedConditions.includes(condition)
        ? selectedConditions.filter((item) => item !== condition)
        : [...selectedConditions, condition];
    } else {
      newConditions = selectedConditions.includes(condition) ? [] : [condition];
    }
    setSelectedConditions(newConditions);
    sendPayload({
      age: selectedAge,
      patientCondition: newConditions,
      patientName: selectedPatients,
    });
  };

  const handlePatientSelect = (patient) => {
    let newPatients;
    if (isMultiSelectPatients) {
      newPatients = selectedPatients.includes(patient)
        ? selectedPatients.filter((item) => item !== patient)
        : [...selectedPatients, patient];
    } else {
      newPatients = selectedPatients.includes(patient) ? [] : [patient];
    }
    setSelectedPatients(newPatients);
    sendPayload({
      age: selectedAge,
      patientCondition: selectedConditions,
      patientName: newPatients,
    });
  };

  const sendPayload = (filters) => {
    onFilterChange(filters);
  };

  const handleClearFilters = (section) => {
    switch (section) {
      case "age":
        setSelectedAge(null);
        sendPayload({
          age: null,
          patientCondition: selectedConditions,
          patientName: selectedPatients,
        });
        break;
      case "conditions":
        setSelectedConditions([]);
        sendPayload({
          age: selectedAge,
          patientCondition: [],
          patientName: selectedPatients,
        });
        break;
      case "patients":
        setSelectedPatients([]);
        sendPayload({
          age: selectedAge,
          patientCondition: selectedConditions,
          patientName: [],
        });
        break;
      default:
        break;
    }
  };

  const toggleMultiSelect = (section) => {
    switch (section) {
      case "age":
        setIsMultiSelectAge((prev) => !prev);
        break;
      case "conditions":
        setIsMultiSelectConditions((prev) => !prev);
        break;
      case "patients":
        setIsMultiSelectPatients((prev) => !prev);
        break;
      default:
        break;
    }
  };

  return (
    <div className={styles.sidebarContainer}>
      <div className="d-flex justify-content-between align-items-center mb-2">
        <h6 className={`${styles.sectionTitle} mb-0`}>Age Group</h6>
        <div className="d-flex align-items-center">
          <div
            className={`${styles.iconButton} ${
              isMultiSelectAge ? styles.active : ""
            }`}
            onClick={() => toggleMultiSelect("age")}
          >
            {isMultiSelectAge ? <FaCheckSquare /> : <FaSquare />}
          </div>
          <div
            className={`${styles.iconButton} ${
              selectedAge ? styles.active : ""
            }`}
            onClick={() => handleClearFilters("age")}
          >
            <FaTimes />
          </div>
        </div>
      </div>
      <div
        className={`${styles.filterItem} px-3 py-2 mb-3 ${
          selectedAge === "70" ? styles.selected : ""
        }`}
        onClick={() => handleAgeSelect("70")}
      >
        70
      </div>

      {!hidePatientConditions && (
        <>
          <div className="d-flex justify-content-between align-items-center mb-2">
            <h6 className={`${styles.sectionTitle} mb-0`}>
              Patient Conditions
            </h6>
            <div className="d-flex align-items-center">
              <div
                className={`${styles.iconButton} ${
                  isMultiSelectConditions ? styles.active : ""
                }`}
                onClick={() => toggleMultiSelect("conditions")}
              >
                {isMultiSelectConditions ? <FaCheckSquare /> : <FaSquare />}
              </div>
              <div
                className={`${styles.iconButton} ${
                  selectedConditions.length > 0 ? styles.active : ""
                }`}
                onClick={() => handleClearFilters("conditions")}
              >
                <FaTimes />
              </div>
            </div>
          </div>
          <div className="d-flex flex-column">
            <div className={`${styles.scrollableList} overflow-auto`}>
              {(patientConditions || []).map((condition, index) => (
                <div
                  key={index}
                  className={`${
                    styles.listItem
                  } d-flex justify-content-between align-items-center mb-2 ${
                    selectedConditions.includes(condition)
                      ? styles.selected
                      : ""
                  }`}
                  onClick={() => handleConditionSelect(condition)}
                >
                  <span>{condition}</span>
                </div>
              ))}
            </div>
          </div>
        </>
      )}

      <div className="d-flex justify-content-between align-items-center mb-2">
        <h6 className={`${styles.sectionTitle} mb-0`}>Patient Names</h6>
        <div className="d-flex align-items-center">
          <div
            className={`${styles.iconButton} ${
              isMultiSelectPatients ? styles.active : ""
            }`}
            onClick={() => toggleMultiSelect("patients")}
          >
            {isMultiSelectPatients ? <FaCheckSquare /> : <FaSquare />}
          </div>
          <div
            className={`${styles.iconButton} ${
              selectedPatients.length > 0 ? styles.active : ""
            }`}
            onClick={() => handleClearFilters("patients")}
          >
            <FaTimes />
          </div>
        </div>
      </div>
      <div className="d-flex flex-column">
        <div className={`${styles.scrollableList} overflow-auto`}>
          {patientNames.map((name, index) => (
            <div
              key={index}
              className={`${
                styles.listItem
              } d-flex justify-content-between align-items-center mb-2 ${
                selectedPatients.includes(name) ? styles.selected : ""
              }`}
              onClick={() => handlePatientSelect(name)}
            >
              <span>{name}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default CcmFilters;
