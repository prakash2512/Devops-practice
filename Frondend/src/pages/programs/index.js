import React, { useState, useEffect } from "react";
import { useRouter } from "next/router";
import { RiArrowLeftLine, RiArrowDownSLine } from "react-icons/ri";
import { connect } from "react-redux";
import Style from "./style.module.css";
import { actions as yearActions } from "../../store/programs";

const Programs = ({ yearAPI, yearFlow }) => {
  const router = useRouter();
  const [selectedMonth, setSelectedMonth] = useState("");
  const [selectedYear, setSelectedYear] = useState("");
  const [error, setError] = useState("");
  const [availableYears, setAvailableYears] = useState([]);

  useEffect(() => {
    yearAPI();
  }, [yearAPI]);

  useEffect(() => {
    if (yearFlow?.status === "SUCCESS" && yearFlow?.response) {
      // Sort years in descending order (newest first)
      const sortedYears = [...yearFlow.response].sort((a, b) =>
        b.localeCompare(a)
      );
      setAvailableYears(sortedYears);

      // Set the first year as default if none is selected
      if (sortedYears.length > 0 && !selectedYear) {
        setSelectedYear(sortedYears[0]);
      }
    }
  }, [yearFlow, selectedYear]);

  const handleProgramSelection = (program) => {
    if (!selectedMonth) {
      setError("Please select a month");
      return;
    }

    if (!selectedYear) {
      setError("Please select a year");
      return;
    }

    setError("");

    const facilityName = localStorage.getItem("facilityName");
    const payload = {
      facilityName,
      program,
      month: selectedMonth,
      year: selectedYear,
    };

    localStorage.setItem("payload", JSON.stringify(payload));
    router.push(`/${program}`);
  };

  const programs = [
    {
      name: "Chronic Care Management",
      shortName: "CCM",
      color: "ccm",
      icon: "/icons/ccm.svg",
    },
    {
      name: "Principle Care Management",
      shortName: "PCM",
      color: "pcm",
      icon: "/icons/pcm.svg",
    },
    {
      name: "Behavioral Health Integration",
      shortName: "BHI",
      color: "bhi",
      icon: "/icons/bhi.svg",
    },
    {
      name: "Hospital Transfer Report",
      shortName: "HTR",
      color: "htr",
      icon: "/icons/htr1.svg",
    },
  ];

  const months = [
    "JAN",
    "FEB",
    "MAR",
    "APR",
    "MAY",
    "JUN",
    "JUL",
    "AUG",
    "SEP",
    "OCT",
    "NOV",
    "DEC",
  ];

  return (
    <div className={`container-fluid ${Style.pageContainer}`}>
      <div className={`card shadow-lg p-4 text-center ${Style.mainBox}`}>
        {/* Back Button */}
        <button
          onClick={() => router.push("/facilities-list")}
          className={Style.back}
        >
          <RiArrowLeftLine className={Style.arrowIcon} /> Back
        </button>

        <h1 className={Style.heading}>Programs</h1>

        {/* Month and Year Selection */}
        <div className={`row ${Style.selectionRow}`}>
          <div className="col-6">
            <label className={Style.label}>Select the Month</label>
            <div className={Style.selectWrapper}>
              <select
                className={`${Style.select} ${
                  error === "Please select a month" ? Style.errorBorder : ""
                }`}
                value={selectedMonth}
                onChange={(e) => {
                  setSelectedMonth(e.target.value);
                  if (error === "Please select a month") setError("");
                }}
              >
                <option value="">Select</option>
                {months.map((month) => (
                  <option key={month} value={month}>
                    {month}
                  </option>
                ))}
              </select>
              <RiArrowDownSLine className={Style.downArrow} />
            </div>
            {error === "Please select a month" && (
              <div className={Style.errorMessage}>{error}</div>
            )}
          </div>
          <div className="col-6">
            <label className={Style.label}>Select the Year</label>
            <div className={Style.selectWrapper}>
              <select
                className={`${Style.select} ${
                  error === "Please select a year" ? Style.errorBorder : ""
                }`}
                value={selectedYear}
                onChange={(e) => {
                  setSelectedYear(e.target.value);
                  if (error === "Please select a year") setError("");
                }}
              >
                {availableYears.length === 0 ? (
                  <option value="">Loading years...</option>
                ) : (
                  availableYears.map((year) => (
                    <option key={year} value={year}>
                      {year}
                    </option>
                  ))
                )}
              </select>
              <RiArrowDownSLine className={Style.downArrow} />
            </div>
            {error === "Please select a year" && (
              <div className={Style.errorMessage}>{error}</div>
            )}
          </div>
        </div>

        {/* Error message display */}
        {error &&
          error !== "Please select a month" &&
          error !== "Please select a year" && (
            <div className={Style.errorMessage}>{error}</div>
          )}

        {/* Program Cards */}
        <div className={`row ${Style.cardRow}`}>
          {programs.map((program) => (
            <div className="col-12 col-sm-6 mb-4" key={program.shortName}>
              <div
                className={`card ${Style.programCard} ${Style[program.color]}`}
                onClick={() => handleProgramSelection(program.shortName)}
              >
                <div className="card-body d-flex align-items-center justify-content-between">
                  <div className={Style.programText}>
                    <h5 className={Style.programName}>{program.name}</h5>
                    <p className={Style.programShort}>({program.shortName})</p>
                  </div>
                  <img
                    src={program.icon}
                    alt={program.name}
                    className={Style.icon}
                  />
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

const enhancer = connect(
  (state) => ({
    yearFlow: state.programsYear.yearFlow.data,
  }),
  {
    yearAPI: yearActions.getYearAction,
  }
);
export default enhancer(Programs);
