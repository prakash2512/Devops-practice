import React, { useState } from "react";
import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";
import Highcharts3D from "highcharts/highcharts-3d";
import { AiOutlineInsertRowAbove } from "react-icons/ai";
import { FaCheck, FaTimes, FaCheckSquare, FaSquare } from "react-icons/fa";
import styles from "./Assessment.module.css";

// Initialize Highcharts 3D module
if (typeof Highcharts === "object") {
  Highcharts3D(Highcharts);
}

// Professional light color palettes for different charts
const colorPalettes = {
  default: [
    "#7cb5ec", "#434348", "#90ed7d", "#f7a35c", "#8085e9", 
    "#f15c80", "#e4d354", "#2b908f", "#f45b5b", "#91e8e1"
  ],
  directive: [
    "#6A8EAE", "#9BB1C8", "#C4D4E0", "#E1E8ED", "#A5C4D4",
    "#7D9EB2", "#B8D4E5", "#D9E6F2", "#8FB8DE", "#6C8BA7"
  ],
  carePlan: [
    "#88BB92", "#B4D3B2", "#D1E7DD", "#A3C9A8", "#7AA981",
    "#C5E0D8", "#9EC6B7", "#6B9C7D", "#B1D8B7", "#8FC299"
  ],
  risk: [
    "#FFB347", "#FFCC99", "#FFD8A8", "#FFA07A", "#FF8C69",
    "#FFD39B", "#FFA54F", "#FF8247", "#FFB76B", "#FF7F50"
  ],
  medical: [
    "#A2C4E0", "#9FC5E8", "#B6D7A8", "#D5A6BD", "#D9D2E9",
    "#A4C2F4", "#B4A7D6", "#C9DAF8", "#E6B8AF", "#A2B9C6"
  ]
};

const Assessment = ({
  assessmentCountsFlow,
  singleAssessmentCountsFlow,
  onAssessmentClick,
  onConditionClick,
}) => {
  const [selectedChart, setSelectedChart] = useState(null);
  const [selectedConditions, setSelectedConditions] = useState([]);
  const [isMultiSelectConditions, setIsMultiSelectConditions] = useState(false);

  // List of non-clickable charts
  const nonClickableCharts = [
    "ADVANCED DIRECTIVE(S)",
    "ADVANCED DIRECTIVE(S) TYPES",
    "SKIN RISK CATEGORY",
    "SKIN RISK SCORE",
  ];

  // Generate chartConfigs dynamically from assessmentCountsFlow
  const chartConfigs = [
    {
      title: "ADVANCED DIRECTIVE(S)",
      data: Object.entries(assessmentCountsFlow?.response?.advanceDirective || {}).map(
        ([key, value], index) => ({
          name: key,
          y: value,
          color: colorPalettes.directive[index % colorPalettes.directive.length],
        })
      ),
      type: "pie",
      isDonut: false,
    },
    {
      title: "ADVANCED DIRECTIVE(S) TYPES",
      data: Object.entries(assessmentCountsFlow?.response?.advanceDirectivesTypes || {}).map(
        ([key, value], index) => ({
          name: key,
          y: value,
          color: colorPalettes.directive[(index + 2) % colorPalettes.directive.length],
        })
      ),
      type: "pie",
      isDonut: false,
    },
    {
      title: "ADV CARE PLAN",
      data: Object.entries(assessmentCountsFlow?.response?.advCarePlan || {}).map(
        ([key, value], index) => ({
          name: key,
          y: value,
          color: colorPalettes.carePlan[index % colorPalettes.carePlan.length],
        })
      ),
      type: "pie",
      isDonut: false,
    },
    {
      title: "ADV DOCTORS ORDER",
      data: Object.entries(assessmentCountsFlow?.response?.advDoctorsOrder || {}).map(
        ([key, value], index) => ({
          name: key,
          y: value,
          color: colorPalettes.medical[index % colorPalettes.medical.length],
        })
      ),
      type: "pie",
      isDonut: false,
    },
    {
      title: "CATHETER",
      data: Object.entries(assessmentCountsFlow?.response?.catheter || {}).map(
        ([key, value], index) => ({
          name: key,
          y: value,
          color: colorPalettes.medical[(index + 3) % colorPalettes.medical.length],
        })
      ),
      type: "pie",
      isDonut: false,
    },
    {
      title: "CATHETER TYPE",
      data: Object.entries(assessmentCountsFlow?.response?.catheterType || {}).map(
        ([key, value], index) => ({
          name: key,
          y: value,
          color: colorPalettes.medical[(index + 1) % colorPalettes.medical.length],
        })
      ),
      type: "pie",
      isDonut: false,
    },
    {
      title: "CATHETER CARE PLAN",
      data: Object.entries(assessmentCountsFlow?.response?.catheterCarePlan || {}).map(
        ([key, value], index) => ({
          name: key,
          y: value,
          color: colorPalettes.carePlan[(index + 2) % colorPalettes.carePlan.length],
        })
      ),
      type: "pie",
      isDonut: false,
    },
    {
      title: "CATHETER INDICATION",
      data: Object.entries(assessmentCountsFlow?.response?.catheterIndication || {}).map(
        ([key, value], index) => ({
          name: key,
          y: value,
          color: colorPalettes.medical[(index + 4) % colorPalettes.medical.length],
        })
      ),
      type: "pie",
      isDonut: false,
    },
    {
      title: "FALL RISK ASSESSMENT",
      data: Object.entries(assessmentCountsFlow?.response?.fallRiskAssessment || {}).map(
        ([key, value], index) => ({
          name: key,
          y: value,
          color: colorPalettes.risk[index % colorPalettes.risk.length],
        })
      ),
      type: "pie",
      isDonut: false,
    },
    {
      title: "FALL RISK CARE PLAN",
      data: Object.entries(assessmentCountsFlow?.response?.fallRiskCarePlan || {}).map(
        ([key, value], index) => ({
          name: key,
          y: value,
          color: colorPalettes.carePlan[(index + 1) % colorPalettes.carePlan.length],
        })
      ),
      type: "pie",
      isDonut: false,
    },
    {
      title: "SKIN RISK ASSESSMENT",
      data: Object.entries(assessmentCountsFlow?.response?.skinRiskAssessment || {}).map(
        ([key, value], index) => ({
          name: key,
          y: value,
          color: colorPalettes.risk[(index + 2) % colorPalettes.risk.length],
        })
      ),
      type: "pie",
      isDonut: false,
    },
    {
      title: "SKIN RISK CATEGORY",
      data: Object.entries(assessmentCountsFlow?.response?.skinRiskCategory || {}).map(
        ([key, value], index) => ({
          name: key,
          y: value,
          color: colorPalettes.risk[(index + 3) % colorPalettes.risk.length],
        })
      ),
      type: "pie",
      isDonut: false,
    },
    {
      title: "SKIN RISK SCORE",
      data: Object.entries(assessmentCountsFlow?.response?.skinRiskStore || {}).map(
        ([key, value], index) => ({
          name: key,
          y: value,
          color: colorPalettes.risk[(index + 1) % colorPalettes.risk.length],
        })
      ),
      type: "pie",
      isDonut: false,
    },
    {
      title: "SKIN RISK CARE PLAN",
      data: Object.entries(assessmentCountsFlow?.response?.skinRiskCarePlan || {}).map(
        ([key, value], index) => ({
          name: key,
          y: value,
          color: colorPalettes.carePlan[(index + 3) % colorPalettes.carePlan.length],
        })
      ),
      type: "pie",
      isDonut: false,
    },
  ];

  const getChartOptions = (config) => ({
    chart: {
      type: config.type,
      backgroundColor: "rgb(21, 25, 43)",
      height: "100%",
      options3d: { enabled: true, alpha: 45, beta: 0, depth: 50 },
    },
    title: {
      text: config.title,
      style: { color: "#ffffff", fontSize: "10px" },
    },
    tooltip: {
      enabled: true,
      pointFormat: "{point.name}: <b>{point.y}</b>",
      style: { color: "black", fontSize: "12px" },
      backgroundColor: "white",
      borderColor: "#333",
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
    legend: {
      itemStyle: { color: "#ffffff", fontSize: "10px" },
    },
    series: [{ name: "Responses", data: config.data }],
    credits: { enabled: false },
  });

  // Handle assessment name click
  const handleAssessmentClick = (chartTitle) => {
    setSelectedChart(chartTitle);
    onAssessmentClick(chartTitle);
  };

  // Handle condition selection
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

  // Clear selected conditions
  const handleClearConditions = () => {
    setSelectedConditions([]);
    onConditionClick([]);
  };

  // Get the selected chart configuration
  const selectedChartConfig = chartConfigs.find(
    (config) => config.title === selectedChart
  );

  return (
    <div className={styles.container}>
      {!selectedChart ? (
        <div className={styles.chartGrid}>
          {chartConfigs.map((config, index) => (
            <div key={index} className={styles.chartContainer}>
              {/* Conditionally render the clickable icon */}
              {!nonClickableCharts.includes(config.title) && (
                <div
                  className={styles.iconButton}
                  onClick={() => handleAssessmentClick(config.title)}
                >
                  <AiOutlineInsertRowAbove size={20} />
                </div>
              )}
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
                {/* Chart and Conditions Container */}
                <div className={styles.chartAndConditions}>
                  <div>
                    <HighchartsReact
                      highcharts={Highcharts}
                      options={getChartOptions(selectedChartConfig)}
                    />
                  </div>

                  {/* Condition Filters */}
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

                {/* Table */}
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
                      {singleAssessmentCountsFlow?.response?.map(
                        (patient, idx) => (
                          <tr key={idx}>
                            <td>{patient.patientName}</td>
                            {selectedChartConfig.data.map((item) => (
                              <td key={item.name}>
                                {patient.assessmentName === item.name ? (
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

export default Assessment;