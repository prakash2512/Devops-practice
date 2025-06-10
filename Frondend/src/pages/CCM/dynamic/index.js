import React, { useState, useEffect } from "react";
import { FaExternalLinkAlt, FaPencilAlt,FaTimes } from "react-icons/fa";
import { message } from "antd/lib";
import CcmLayout from "@/components/ccmLayout";
import styles from "./styles.module.css";
import PatientFilters from "./PatientFilters";
import { actions as ccmDynamicActions } from "../../../store/CCM";
import { actions as yearActions } from "../../../store/programs";
import { connect } from "react-redux";
import SinglePatientDetails from "./singlePatientDetails";

const PatientTable = ({
  isAdmin = true,
  ccmDynamicFlow,
  ccmDynamicAPI,
  ccmDynamicEditAPI,
  ccmDynamicEditFlow,
  yearAPI,
  yearFlow,
  ccmSinglePatientDetailsAPI,
  ccmSinglePatientDetailsFlow,
  ccmDiagnosisPdfAPI,
  ccmDiagnosisPdfFlow,
}) => {
  const [editCaregaps, setEditCaregaps] = useState({});
  const [editTransfer, setEditTransfer] = useState({});
  const [editedNotes, setEditedNotes] = useState({});
  const [editedTransfer, setEditedTransfer] = useState({});
  const [filterName, setFilterName] = useState(null);
  const [filtersVisible, setFiltersVisible] = useState(false);
  const [patients, setPatients] = useState([]);
  const [payload, setPayload] = useState([]);
  const [isUpdating, setIsUpdating] = useState(false);
  const [years, setYears] = useState([]);
  const [showSinglePatient, setShowSinglePatient] = useState(false);
  const [filters, setFilters] = useState(null);
  const [pdfUrl, setPdfUrl] = useState(null);
  const [showPdfModal, setShowPdfModal] = useState(false);
  const [currentDiagnosis, setCurrentDiagnosis] = useState('');
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (payload) {
      ccmDynamicAPI(payload);
    }
  }, [payload, ccmDynamicAPI]);

  useEffect(() => {
    yearAPI();
  }, [yearAPI]);

  useEffect(() => {
    if (yearFlow?.status === "SUCCESS" && yearFlow?.response) {
      setYears(yearFlow.response);
    }
  }, [yearFlow]);

  useEffect(() => {
    const getPayload = localStorage.getItem("payload");
    if (getPayload) {
      setPayload(JSON.parse(getPayload));
    }
  }, []);

  useEffect(() => {
    if (ccmDynamicFlow?.status === "SUCCESS" && ccmDynamicFlow?.response) {
      const formattedPatients = ccmDynamicFlow.response.map((patient) => ({
        id: patient.id,
        name: patient.patientName,
        diagnoses: [
          patient.diagnosList || "",
          patient.diagnosList2 || "",
        ].filter((dx) => dx),
        caregaps: [
          {
            dx: `DX1: ${patient.diagnosList || "No primary diagnosis"}`,
            notes: patient.careGaps ? [patient.careGaps] : [],
          },
          {
            dx: `DX2: ${patient.diagnosList2 || "No secondary diagnosis"}`,
            notes: patient.careGaps2 ? [patient.careGaps2] : [],
          },
        ].filter((gap) => gap.notes.length > 0),
        transfer: patient.residentHospitalized || "NO",
        erVisit: patient.erVisit || "NO",
        hospitalizedDx: patient.erVisit || "—",
      }));
      setPatients(formattedPatients);
      setLoading(false)
    }
  }, [ccmDynamicFlow]);

  useEffect(() => {
    if (ccmDiagnosisPdfFlow?.data) {
      try {
        const blob = new Blob([ccmDiagnosisPdfFlow.data], { type: 'application/pdf' });
        const url = URL.createObjectURL(blob);
        setPdfUrl(url);
        setShowPdfModal(true);
      } catch (err) {
        console.error('Error processing PDF:', err);
        message.error('Failed to load PDF');
      }
    } else if (ccmDiagnosisPdfFlow?.error) {
      message.error(ccmDiagnosisPdfFlow.error.message || 'Failed to load PDF');
    }
  }, [ccmDiagnosisPdfFlow]);

  const handleDiagnosisClick = async (diagnosisName) => {
    try {
      setCurrentDiagnosis(diagnosisName);
      const payload = {
        diagnosisName: diagnosisName.trim()
      };
      await ccmDiagnosisPdfAPI(payload);
    } catch (error) {
      message.error(error.message || 'Failed to load diagnosis PDF');
    }
  };

  const closePdfModal = () => {
    setShowPdfModal(false);
    if (pdfUrl) {
      URL.revokeObjectURL(pdfUrl);
      setPdfUrl(null);
    }
  };

  const handleApplyFilters = (filterPayload) => {
    setFilters(filterPayload);
    ccmSinglePatientDetailsAPI(filterPayload);
    setShowSinglePatient(true);
    setFiltersVisible(false);
  };

  const handleEditCaregaps = (idx) => {
    setEditCaregaps({ ...editCaregaps, [idx]: true });
    setEditedNotes({
      ...editedNotes,
      [idx]: JSON.parse(JSON.stringify(patients[idx].caregaps)),
    });
  };

  const handleEditTransfer = (idx) => {
    setEditTransfer({ ...editTransfer, [idx]: true });
    setEditedTransfer({
      ...editedTransfer,
      [idx]: patients[idx].transfer,
    });
  };

  const handleCancelCaregaps = (idx) => {
    setEditCaregaps({ ...editCaregaps, [idx]: false });
  };

  const handleCancelTransfer = (idx) => {
    setEditTransfer({ ...editTransfer, [idx]: false });
  };

  const hasCaregapsChanged = (idx) => {
    if (!editCaregaps[idx] || !editedNotes[idx]) return false;

    const originalCaregaps = patients[idx].caregaps;
    const currentCaregaps = editedNotes[idx];

    if (originalCaregaps.length !== currentCaregaps.length) return true;

    for (let i = 0; i < originalCaregaps.length; i++) {
      if (
        originalCaregaps[i].notes.join("") !== currentCaregaps[i].notes.join("")
      ) {
        return true;
      }
    }

    return false;
  };

  const handleUpdateCaregaps = async (idx) => {
    if (!hasCaregapsChanged(idx)) {
      message.warning("Please make changes before updating");
      return;
    }

    const patient = patients[idx];
    const editedCaregaps = editedNotes[idx];

    const payload = {
      id: patient.id,
      careGaps1: editedCaregaps[0]?.notes[0] || "",
      careGaps2: editedCaregaps[1]?.notes[0] || "",
      residentHospitalized: null,
    };

    setIsUpdating(true);
    try {
      const result = await ccmDynamicEditAPI(payload);
      if (result?.status === "SUCCESS") {
        message.success(result.message || "Caregaps updated successfully");
        const updatedPatients = [...patients];
        updatedPatients[idx].caregaps = editedNotes[idx];
        setPatients(updatedPatients);
        setEditCaregaps({ ...editCaregaps, [idx]: false });
      } else {
        throw new Error(result?.message || "Update failed");
      }
    } catch (error) {
      message.error(error.message);
    } finally {
      setIsUpdating(false);
    }
  };

  const handleUpdateTransfer = async (idx) => {
    if (editedTransfer[idx] === patients[idx].transfer) {
      message.warning("Please make changes before updating");
      return;
    }

    const patient = patients[idx];

    const payload = {
      id: patient.id,
      careGaps1: null,
      careGaps2: null,
      residentHospitalized: editedTransfer[idx],
    };

    setIsUpdating(true);
    try {
      const result = await ccmDynamicEditAPI(payload);
      if (result?.status === "SUCCESS") {
        message.success(
          result.message || "Transfer status updated successfully"
        );
        const updatedPatients = [...patients];
        updatedPatients[idx].transfer = editedTransfer[idx];
        setPatients(updatedPatients);
        setEditTransfer({ ...editTransfer, [idx]: false });
      } else {
        throw new Error(result?.message || "Update failed");
      }
    } catch (error) {
      message.error(error.message);
    } finally {
      setIsUpdating(false);
    }
  };

  const handleTransferChange = (idx, value) => {
    setEditedTransfer({ ...editedTransfer, [idx]: value });
  };

  const handleNoteChange = (pIdx, gIdx, nIdx, value) => {
    const updated = [...editedNotes[pIdx]];
    updated[gIdx].notes[nIdx] = value;
    setEditedNotes({ ...editedNotes, [pIdx]: updated });
  };

  const handlePatientClick = (name) => {
    setFilterName(name);
    setFiltersVisible(true);
  };

  const handleBackToTable = () => {
    setShowSinglePatient(false);
    setFilterName(null);
    setFilters(null);
  };

  const filteredPatients =
    filterName && !filtersVisible
      ? patients.filter((p) => p.name === filterName)
      : patients;

  return (
    <CcmLayout>
      {showSinglePatient ? (

ccmSinglePatientDetailsFlow.loading ? <div>Loading...</div> : 
        <SinglePatientDetails
          ccmSinglePatientDetailsFlow={ccmSinglePatientDetailsFlow.data}
          filters={filters}
          onBack={handleBackToTable}
        />
      ) : (
        loading ? <div>Loading .....</div> :
        <div className={styles.tableWrapper}>
          <div
            className={`${styles.tableContainer} ${
              filtersVisible ? styles.blurBackground : ""
            }`}
          >
            <div className={styles.tableScrollContainer}>
              <table className={styles.patientTable}>
                <thead className={styles.stickyHeader}>
                  <tr>
                    <th>PATIENT NAME</th>
                    <th>DX_LIST</th>
                    <th>CAREGAPS</th>
                    <th>TRANSFER</th>
                    <th>ER_VISIT</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredPatients.map((p, idx) => (
                    <tr
                      key={p.id || idx}
                      className={idx % 2 === 0 ? styles.evenRow : styles.oddRow}
                    >
                      <td>
                        <span
                          onClick={() => handlePatientClick(p.name)}
                          className={styles.patientNameLink}
                        >
                          {p.name}
                        </span>
                      </td>
                      <td>
                        {p.diagnoses.map((dx, i) => (
                          <div key={i} className={styles.dxLink}>
                            <a 
                              href="#"
                              onClick={(e) => {
                                e.preventDefault();
                                handleDiagnosisClick(dx);
                              }}
                            >
                              DX{i + 1}: {dx}{" "}
                              <FaExternalLinkAlt className={styles.icon} />
                            </a>
                          </div>
                        ))}
                      </td>
                      <td style={{ position: "relative" }}>
                        {isAdmin && !editCaregaps[idx] && !isUpdating && (
                          <div className={styles.editIconContainer}>
                            <FaPencilAlt
                              onClick={() => handleEditCaregaps(idx)}
                              className={styles.editIcon}
                            />
                          </div>
                        )}
                        {(editCaregaps[idx]
                          ? editedNotes[idx]
                          : p.caregaps
                        ).map((gap, gidx) => (
                          <div
                            key={`${p.id}-${gidx}`}
                            className={styles.caregapSection}
                          >
                            <strong>{gap.dx}</strong>
                            <ul>
                              {gap.notes.map((note, nidx) => (
                                <li key={`${p.id}-${gidx}-${nidx}`}>
                                  {isAdmin && editCaregaps[idx] ? (
                                    <textarea
                                      className={styles.noteInput}
                                      value={note}
                                      onChange={(e) =>
                                        handleNoteChange(
                                          idx,
                                          gidx,
                                          nidx,
                                          e.target.value
                                        )
                                      }
                                      disabled={isUpdating}
                                    />
                                  ) : (
                                    note
                                  )}
                                </li>
                              ))}
                            </ul>
                          </div>
                        ))}
                        {editCaregaps[idx] && (
                          <div className={styles.editButtons}>
                            <button
                              className={styles.updateBtn}
                              onClick={() => handleUpdateCaregaps(idx)}
                              disabled={isUpdating}
                            >
                              {isUpdating ? "Updating..." : "Update"}
                            </button>
                            <button
                              className={styles.cancelBtn}
                              onClick={() => handleCancelCaregaps(idx)}
                              disabled={isUpdating}
                            >
                              Cancel
                            </button>
                          </div>
                        )}
                      </td>
                      <td style={{ position: "relative" }}>
                        {isAdmin && !editTransfer[idx] && !isUpdating && (
                          <div className={styles.editIconContainer}>
                            <FaPencilAlt
                              onClick={() => handleEditTransfer(idx)}
                              className={styles.editIcon}
                            />
                          </div>
                        )}
                        {editTransfer[idx] ? (
                          <div className={styles.transferEditContainer}>
                            <select
                              value={editedTransfer[idx]}
                              className={styles.select} 
                              onChange={(e) =>
                                handleTransferChange(idx, e.target.value)
                              }
                              disabled={isUpdating}
                            >
                              <option value="">Select an option</option>
                              <option value="Yes">Yes</option>
                              <option value="No">No</option>
                            </select>
                            <div className={styles.editButtons}>
                              <button
                                className={styles.updateBtn}
                                onClick={() => handleUpdateTransfer(idx)}
                                disabled={isUpdating || !editedTransfer[idx]}
                              >
                                {isUpdating ? "Updating..." : "Update"}
                              </button>
                              <button
                                className={styles.cancelBtn}
                                onClick={() => handleCancelTransfer(idx)}
                                disabled={isUpdating}
                              >
                                Cancel
                              </button>
                            </div>
                          </div>
                        ) : (
                          p.transfer
                        )}
                      </td>
                      <td>{p.erVisit}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {filtersVisible && (
            <PatientFilters
              visible={filtersVisible}
              onClose={() => setFiltersVisible(false)}
              selectedName={filterName}
              onApply={handleApplyFilters}
              years={years}
            />
          )}

{showPdfModal && (
            <div className={styles.fullScreenPdfModal}>
              <div className={styles.pdfModalHeader}>
                <h3>Diagnosis Details: {currentDiagnosis}</h3>
                <button 
                  className={styles.closeButton}
                  onClick={closePdfModal}
                >
                  <FaTimes size={24} />
                </button>
              </div>
              <div className={styles.pdfViewerContainer}>
                {pdfUrl ? (
                  <iframe
                    src={pdfUrl}
                    width="100%"
                    height="100%"
                    title="Diagnosis PDF"
                    className={styles.pdfIframe}
                  />
                ) : (
                  <div className={styles.loadingPdf}>
                    <div className={styles.spinner}></div>
                    Loading PDF...
                  </div>
                )}
              </div>
            </div>
          )}
        </div>
      )}
    </CcmLayout>
  );
};


const enhancer = connect(
  (state) => ({
    ccmDynamicFlow: state.ccmPatientDetails.ccmDynamic.data,
    ccmDynamicEditFlow: state.ccmPatientDetails.ccmDynamicEdit.data,
    ccmSinglePatientDetailsFlow:
      state.ccmPatientDetails.ccmSinglePatientDetails,
    yearFlow: state.programsYear.yearFlow.data,
    ccmDiagnosisPdfFlow: state.ccmPatientDetails.ccmDiagnosisPdf,
  }),
  {
    ccmDynamicAPI: ccmDynamicActions.ccmDynamicAction,
    ccmDynamicEditAPI: ccmDynamicActions.ccmDynamicEditAction,
    ccmSinglePatientDetailsAPI: ccmDynamicActions.ccmSinglePatientDetailsAction,
    yearAPI: yearActions.getYearAction,
    ccmDiagnosisPdfAPI: ccmDynamicActions.ccmDiagnosisPdfAction,
  }
);

export default enhancer(PatientTable);