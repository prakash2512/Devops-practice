import React, { useState, useEffect } from "react";
import styles from "./bhiFilters.module.css";
import { FaTimes, FaCheckSquare, FaSquare } from "react-icons/fa";

const BhiFilters = ({ dx = [], patientNames = [], onFilterChange }) => {
  const [selectedDiagnosis, setSelectedDiagnosis] = useState([]);
  const [selectedPatients, setSelectedPatients] = useState([]);
  const [selectedDx, setSelectedDx] = useState([]);
  const [isMultiDiagnosis, setIsMultiDiagnosis] = useState(false);
  const [isMultiPatient, setIsMultiPatient] = useState(false);
  const [isMultiDx, setIsMultiDx] = useState(false);

  useEffect(() => {
    const filters = {
      diagnosis: selectedDiagnosis,
      patientName: selectedPatients,
      inaccurateDx: selectedDx,
    };
    onFilterChange(filters);
  }, [selectedDiagnosis, selectedPatients, selectedDx]); // depend on individual states

  const handleSelect = (item, list, setList, isMulti) => {
    const newList = isMulti
      ? list.includes(item)
        ? list.filter((i) => i !== item)
        : [...list, item]
      : list.includes(item)
      ? []
      : [item];

    setList(newList);
  };

  const handleClear = (type) => {
    if (type === "diagnosis") setSelectedDiagnosis([]);
    else if (type === "patient") setSelectedPatients([]);
    else if (type === "dx") setSelectedDx([]);
  };

  const toggleMulti = (type) => {
    if (type === "diagnosis") setIsMultiDiagnosis((prev) => !prev);
    else if (type === "patient") setIsMultiPatient((prev) => !prev);
    else if (type === "dx") setIsMultiDx((prev) => !prev);
  };

  const renderFilter = (label, items, selected, setSelected, isMulti, key) => (
    <div className={styles.section}>
      <div className={styles.sectionHeader}>
        <span>{label}</span>
        <div className={styles.icons}>
          <div
            className={`${styles.iconButton} ${isMulti ? styles.active : ""}`}
            onClick={() => toggleMulti(key)}
          >
            {isMulti ? <FaCheckSquare /> : <FaSquare />}
          </div>
          <div
            title={selected.length > 0 ? "Clear selected filters" : ""}
            className={`${styles.iconButton} ${
              selected.length > 0 ? styles.active : ""
            }`}
            onClick={() => handleClear(key)}
          >
            <FaTimes />
          </div>
        </div>
      </div>
      <div className={styles.itemList}>
        {items.map((item, idx) => (
          <div
            key={idx}
            className={`${styles.item} ${
              selected.includes(item) ? styles.selected : ""
            }`}
            onClick={() => handleSelect(item, selected, setSelected, isMulti)}
          >
            {item}
          </div>
        ))}
      </div>
    </div>
  );

  return (
    <div className={styles.container}>
      {renderFilter(
        "INACCURATE DX",
        ["VALID DX"],
        selectedDx,
        setSelectedDx,
        isMultiDx,
        "dx"
      )}
      {renderFilter(
        "DIAGNOSIS",
        dx,
        selectedDiagnosis,
        setSelectedDiagnosis,
        isMultiDiagnosis,
        "diagnosis"
      )}
      {renderFilter(
        "PATIENT NAME",
        patientNames,
        selectedPatients,
        setSelectedPatients,
        isMultiPatient,
        "patient"
      )}
    </div>
  );
};

export default BhiFilters;
