import React, { useState, useEffect } from "react";
import styles from "./style.module.css";
import logo from "../../../public/assets/images/logo.png";
import Image from "next/image";
import { useRouter } from "next/router";
import { connect } from "react-redux";
import { actions as yearActions } from "../../store/programs";

const CcmLayout = ({ children, yearAPI, yearFlow }) => {
  const router = useRouter();
  const [payload, setPayload] = useState(null);
  const [selectedMonthYear, setSelectedMonthYear] = useState();
  const [availableYears, setAvailableYears] = useState([]);
  const [monthYearOptions, setMonthYearOptions] = useState([]);

  // Fetch years from API
  useEffect(() => {
    yearAPI();
  }, [yearAPI]);

  // Process API response and update available years
  useEffect(() => {
    if (yearFlow?.status === "SUCCESS" && yearFlow?.response) {
      const years = [...yearFlow.response].sort((a, b) => b.localeCompare(a));
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

  const headerText = payload?.facilityName || "AIKEN REHABILITATION AND CARE CENTER";

  const handleNavigation = (path) => {
    router.push(path);
  };

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

  const isCCMSelected = () => {
    const currentSection = router.pathname.split("/")[1];
    return (
      currentSection === "CCM" ||
      !["CCM", "BHI", "HTR", "PCM"].includes(currentSection)
    );
  };

  return (
    <div className={styles.layoutContainer}>
      <div className={`${styles.sidebarContainer} d-flex flex-column`}>
        <div className={styles.dateSection}>
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
        </div>

        <div className={styles.menuSection}>
          {["population", "immunization", "assessment", "dynamic","pdf"].map((item) => (
            <div
              key={item}
              className={`${styles.menuItem} ${
                router.pathname === `/CCM/${item}` ? styles.activeMenuItem : ""
              }`}
              onClick={() => handleNavigation(`/CCM/${item}`)}
            >
              {item.toUpperCase()}
            </div>
          ))}
        </div>

        <div className={`${styles.logoSection} mt-auto`}>
          <Image
            src={logo}
            alt="Logo"
            className={styles.fullLogo}
            layout="intrinsic"
            width={150}
            height={150}
            priority
          />
        </div>
      </div>

      <div className={styles.mainContent}>
        <div className={styles.headerContainer}>
          <div className={styles.boxContainer}>
            <button
              className={styles.back}
              onClick={() => handleNavigation("/programs")}
            >
              Back
            </button>
            {["CCM", "BHI", "HTR", "PCM"].map((section) => (
              <div
                key={section}
                className={`${styles.sectionBox} ${
                  router.pathname === `/${section}` ||
                  (section === "CCM" && isCCMSelected())
                    ? styles.activeSection
                    : ""
                }`}
                onClick={() => handleSectionChange(section)}
              >
                {section}
              </div>
            ))}
          </div>

          <h1 className={styles.headerText}>{headerText}</h1>
        </div>

        <div className={styles.contentWrapper}>
          <div className={styles.pageContent}>{children}</div>
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

export default enhancer(CcmLayout);