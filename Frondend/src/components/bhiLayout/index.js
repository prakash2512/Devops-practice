import React, { useState, useEffect } from "react";
import Image from "next/image";
import { useRouter } from "next/router";
import { connect } from "react-redux";
import { actions as yearActions } from "../../store/programs";
import { RiArrowDownSLine } from "react-icons/ri";
import logo from "../../../public/assets/images/logo.png";
import styles from "./style.module.css";

const BhiLayout = ({ children, yearAPI, yearFlow }) => {
  const router = useRouter();
  const [payload, setPayload] = useState(null);
  const [selectedMonthYear, setSelectedMonthYear] = useState("OCT-2024");
  const [availableYears, setAvailableYears] = useState([]);
  const [monthYearOptions, setMonthYearOptions] = useState([]);

  // Fetch years from API
  useEffect(() => {
    yearAPI();
  }, [yearAPI]);

  // Process API response and update available years
  useEffect(() => {
    if (yearFlow?.status === "SUCCESS" && yearFlow?.response) {
      // Sort years in descending order
      const years = [...yearFlow.response]
        .map(year => year.toString()) // Ensure year is string
        .sort((a, b) => b.localeCompare(a));
      
      setAvailableYears(years);
      
      // Generate month-year options
      const months = [
        "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
        "JUL", "AUG", "SEP", "OCT", "NOV", "DEC",
      ];
      const newOptions = years.flatMap(year => 
        months.map(month => `${month}-${year}`)
      );
      setMonthYearOptions(newOptions);
      
      // Update selected month-year if payload exists
      if (payload?.month && payload?.year) {
        setSelectedMonthYear(`${payload.month}-${payload.year}`);
      }
    }
  }, [yearFlow, payload]);

  // Load payload from localStorage
  useEffect(() => {
    const storedPayload = localStorage.getItem("payload");
    if (storedPayload) {
      try {
        const parsedPayload = JSON.parse(storedPayload);
        setPayload(parsedPayload);
        
        if (parsedPayload.month && parsedPayload.year) {
          setSelectedMonthYear(`${parsedPayload.month}-${parsedPayload.year}`);
        }
      } catch (error) {
        console.error("Failed to parse payload:", error);
      }
    }
  }, []);

  // Update localStorage when month-year changes
  useEffect(() => {
    if (selectedMonthYear && payload) {
      const [month, year] = selectedMonthYear.split('-');
      const updatedPayload = {
        ...payload,
        month,
        year
      };
      localStorage.setItem("payload", JSON.stringify(updatedPayload));
      setPayload(updatedPayload);
    }
  }, [selectedMonthYear]);

  const headerText = payload?.facilityName || "CHESTERTON MANOR";

  const handleNavigation = (path) => router.push(path);
  
  const handleSectionChange = (section) => {
    if (payload) {
      const updatedPayload = {
        ...payload,
        program: section
      };
      localStorage.setItem("payload", JSON.stringify(updatedPayload));
      setPayload(updatedPayload);
    }
    router.push(`/${section}`);
  };

  const handleBack = () => router.push('/programs');

  const currentSection = router.pathname.split("/")[1];
  const currentPage = router.pathname.split("/")[2];

  return (
    <div className={styles.layoutContainer}>
      <aside className={styles.sidebarContainer}>
        <div className={styles.dateCircle}>
          <div className={styles.selectWrapper}> {/* New wrapper for select + icon */}
            <select
              className={styles.monthDropdown}
              value={selectedMonthYear}
              onChange={(e) => setSelectedMonthYear(e.target.value)}
            >
              {monthYearOptions.length > 0 ? (
                monthYearOptions.map((option) => (
                  <option key={option} value={option}>
                    {option}
                  </option>
                ))
              ) : (
                <option value="">Loading months...</option>
              )}
            </select>
            <RiArrowDownSLine className={styles.dropdownIcon} /> {/* Dropdown icon */}
          </div>
        </div>

        <div className={styles.navButtonsContainer}>
          <div
            className={`${styles.hexButton} ${
              currentPage === "home" ? styles.activeHexButton : ""
            }`}
            onClick={() => handleNavigation("/BHI/home")}
          >
            HOME
          </div>
          <div
            className={`${styles.hexButton} ${
              currentPage === "patient" ? styles.activeHexButton : ""
            }`}
            onClick={() => handleNavigation("/BHI/patient")}
          >
            PATIENT
          </div>
        </div>

        <div className={styles.logoSection}>
          <Image
            src={logo}
            alt="Logo"
            className={styles.fullLogo}
            layout="intrinsic"
            width={120}
            height={120}
            priority
          />
        </div>
      </aside>

      <main className={styles.mainContent}>
        <header className={styles.header}>
          <div className={styles.headerLeft}>
            <button onClick={handleBack} className={styles.backButton}>
              Back
            </button>
            <div className={styles.programsContainer}>
              {["CCM", "BHI", "HTR", "PCM"].map((section) => (
                <div
                  key={section}
                  className={`${styles.sectionBox} ${
                    currentSection === section ? styles.activeSection : ""
                  }`}
                  onClick={() => handleSectionChange(section)}
                >
                  {section}
                </div>
              ))}
            </div>
          </div>
          <h1 className={styles.facilityName}>{headerText}</h1>
        </header>
        <section className={styles.pageContent}>{children}</section>
      </main>
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

export default enhancer(BhiLayout);