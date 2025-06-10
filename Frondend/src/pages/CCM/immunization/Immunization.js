import React, { useState, useEffect } from "react";
import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";
import Highcharts3D from "highcharts/highcharts-3d";
import { AiOutlineInsertRowAbove } from "react-icons/ai";
import { FaCheck, FaTimes, FaCheckSquare, FaSquare } from "react-icons/fa";
import styles from "./Immunization.module.css";

// Initialize Highcharts 3D module
if (typeof Highcharts === "object") {
  Highcharts3D(Highcharts);
}

// Define professional light color palettes for up to 10 values per chart
const colorPalettes = {
  PNEUMOCOCCAL: [
    "#90CAF9", "#A5D6A7", "#FFF59D", "#FFCC80", "#CE93D8",
    "#80CBC4", "#F48FB1", "#B0BEC5", "#E6EE9C", "#B39DDB"
  ],
  INFLUENZA: [
    "#81D4FA", "#AED581", "#FFAB91", "#FFD54F", "#BA68C8",
    "#4DB6AC", "#E57373", "#90A4AE", "#DCE775", "#9575CD"
  ],
  PREVNAR: [
    "#64B5F6", "#C5E1A5", "#FFCCBC", "#FFF176", "#AB47BC",
    "#26A69A", "#EF9A9A", "#CFD8DC", "#F0F4C3", "#7E57C2"
  ],
  "COVID VACCINE 1": [
    "#B3E5FC", "#DCEDC8", "#FFE0B2", "#FFF59D", "#E1BEE7",
    "#80DEEA", "#F8BBD0", "#ECEFF1", "#F9FBE7", "#D1C4E9"
  ],
  "COVID VACCINE 2": [
    "#4FC3F7", "#9CCC65", "#FF8A65", "#FFD740", "#9575CD",
    "#26C6DA", "#F06292", "#B0BEC5", "#D4E157", "#7986CB"
  ],
  "COVID BOOSTER": [
    "#29B6F6", "#8BC34A", "#FF7043", "#FFEB3B", "#7E57C2",
    "#00ACC1", "#EC407A", "#90A4AE", "#C0CA33", "#5C6BC0"
  ],
};

const Immunization = ({
  immunizationCountsFlow,
  singleImmunizationCountsFlow,
  onImmunizationClick,
  onConditionClick,
}) => {
  const [selectedChart, setSelectedChart] = useState(null);
  const [selectedConditions, setSelectedConditions] = useState([]);
  const [isMultiSelectConditions, setIsMultiSelectConditions] = useState(false);

  useEffect(() => {
    setSelectedConditions([]);
  }, [selectedChart]);

  const chartConfigs = [
    {
      title: "PNEUMOCOCCAL",
      data: Object.entries(immunizationCountsFlow?.response?.pneumococcal || {}).map(
        ([key, value], idx) => ({
          name: key,
          y: value,
          color: colorPalettes["PNEUMOCOCCAL"][idx % 10],
        })
      ),
      isDonut: false,
    },
    {
      title: "INFLUENZA",
      data: Object.entries(immunizationCountsFlow?.response?.influenza || {}).map(
        ([key, value], idx) => ({
          name: key,
          y: value,
          color: colorPalettes["INFLUENZA"][idx % 10],
        })
      ),
      isDonut: false,
    },
    {
      title: "PREVNAR",
      data: Object.entries(immunizationCountsFlow?.response?.prevnar || {}).map(
        ([key, value], idx) => ({
          name: key,
          y: value,
          color: colorPalettes["PREVNAR"][idx % 10],
        })
      ),
      isDonut: false,
    },
    {
      title: "COVID VACCINE 1",
      data: Object.entries(immunizationCountsFlow?.response?.covidVaccine1 || {}).map(
        ([key, value], idx) => ({
          name: key,
          y: value,
          color: colorPalettes["COVID VACCINE 1"][idx % 10],
        })
      ),
      isDonut: true,
    },
    {
      title: "COVID VACCINE 2",
      data: Object.entries(immunizationCountsFlow?.response?.covidVaccine2 || {}).map(
        ([key, value], idx) => ({
          name: key,
          y: value,
          color: colorPalettes["COVID VACCINE 2"][idx % 10],
        })
      ),
      isDonut: true,
    },
    {
      title: "COVID BOOSTER",
      data: Object.entries(immunizationCountsFlow?.response?.covidBooster || {}).map(
        ([key, value], idx) => ({
          name: key,
          y: value,
          color: colorPalettes["COVID BOOSTER"][idx % 10],
        })
      ),
      isDonut: true,
    },
  ];

  const getChartOptions = (config) => ({
    chart: {
      type: "pie",
      backgroundColor: "rgb(21, 25, 43)",
      height: "100%",
      options3d: { enabled: true, alpha: 45, beta: 0, depth: 50 },
    },
    title: {
      text: config.title,
      style: { color: "#ffffff", fontSize: "12px" },
    },
    tooltip: {
      pointFormat: "{point.name}: <b>{point.y}</b>",
      style: { color: "black" },
      backgroundColor: "white",
      borderColor: "#000",
      borderRadius: 5,
      padding: 10,
    },
    plotOptions: {
      pie: {
        innerSize: config.isDonut ? "45%" : "0%",
        depth: 50,
        dataLabels: {
          enabled: true,
          format: "{point.y}",
          style: { color: "#ffffff", fontSize: "10px" },
        },
        showInLegend: true,
      },
    },
    legend: { itemStyle: { color: "#ffffff" } },
    series: [{ name: "Responses", data: config.data }],
    credits: { enabled: false },
  });

  const handleImmunizationClick = (chartTitle) => {
    setSelectedChart(chartTitle);
    onImmunizationClick(chartTitle, selectedConditions);
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
    onConditionClick(newConditions);
  };

  const handleClearConditions = () => {
    setSelectedConditions([]);
    onConditionClick([]);
  };

  const selectedChartConfig = chartConfigs.find(
    (config) => config.title === selectedChart
  );

  return (
    <div className={styles.container}>
      {!selectedChart ? (
        <div className={styles.chartGrid}>
          {chartConfigs.map((config, index) => (
            <div key={index} className={styles.chartContainer}>
              <div
                className={styles.iconButton}
                onClick={() => handleImmunizationClick(config.title)}
              >
                <AiOutlineInsertRowAbove size={20} />
              </div>
              <HighchartsReact
                highcharts={Highcharts}
                options={getChartOptions(config)}
              />
            </div>
          ))}
        </div>
      ) : (
        <div className={styles.detailsContainer}>
          <button
            onClick={() => setSelectedChart(null)}
            className={styles.backButton}
          >
            Back
          </button>
          <div className={styles.detailsContent}>
            {selectedChartConfig && (
              <>
                <div className={styles.chartAndConditions}>
                  <div>
                    <HighchartsReact
                      highcharts={Highcharts}
                      options={getChartOptions(selectedChartConfig)}
                    />
                  </div>

                  <div className={styles.conditionFilters}>
                    <div className={styles.filterHeader}>
                      <h6 className={styles.filterTitle}>Conditions</h6>
                      <div className={styles.filterActions}>
                        <div
                          className={`${styles.filterButton} ${
                            isMultiSelectConditions ? styles.active : ""
                          }`}
                          onClick={() =>
                            setIsMultiSelectConditions((prev) => !prev)
                          }
                        >
                          {isMultiSelectConditions ? (
                            <FaCheckSquare />
                          ) : (
                            <FaSquare />
                          )}
                        </div>
                        <div
                          className={`${styles.filterButton} ${
                            selectedConditions.length > 0 ? styles.active : ""
                          }`}
                          onClick={handleClearConditions}
                        >
                          <FaTimes />
                        </div>
                      </div>
                    </div>
                    <div className={styles.conditionList}>
                      {selectedChartConfig.data.map((item) => (
                        <div
                          key={item.name}
                          className={`${styles.conditionItem} ${
                            selectedConditions.includes(item.name)
                              ? styles.selected
                              : ""
                          }`}
                          onClick={() => handleConditionSelect(item.name)}
                        >
                          {item.name}
                        </div>
                      ))}
                    </div>
                  </div>
                </div>

                <div className={styles.tableWrapper}>
                  <table className={styles.table}>
                    <thead>
                      <tr>
                        <th>Patient Name</th>
                        {selectedChartConfig.data.map((item) => (
                          <th key={item.name}>{item.name}</th>
                        ))}
                      </tr>
                    </thead>
                    <tbody>
                      {singleImmunizationCountsFlow?.response?.map(
                        (patient, idx) => (
                          <tr key={idx}>
                            <td>{patient.patientName}</td>
                            {selectedChartConfig.data.map((item) => (
                              <td key={item.name}>
                                {patient.immunizationName === item.name ? (
                                  <FaCheck color="green" />
                                ) : (
                                  <FaTimes color="red" />
                                )}
                              </td>
                            ))}
                          </tr>
                        )
                      )}
                    </tbody>
                  </table>
                </div>
              </>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default Immunization;
