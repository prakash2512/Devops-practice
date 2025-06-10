import React, { useEffect, useState } from "react";
import Highcharts from "highcharts";
import Highcharts3D from "highcharts/highcharts-3d";
import HighchartsReact from "highcharts-react-official";
import styles from "./index.module.css";
import BhiLayout from "@/components/bhiLayout";
import BhiFilters from "./BhiFilters";
import { connect } from "react-redux";
import { actions as bhiReportsActions } from "../../../store/BHI";

// Enable 3D charts
if (typeof Highcharts === "object") {
  Highcharts3D(Highcharts);
}

const chartOptions = (title, categories, data) => ({
  chart: {
    type: "column",
    options3d: {
      enabled: true,
      alpha: 15,
      beta: 15,
      depth: 50,
      viewDistance: 25,
    },
    backgroundColor: "transparent",
    height: "100%",
    margin: [7, 7, 7, 7],
  },
  title: { text: null },
  xAxis: {
    categories,
    labels: {
      style: { color: "#fff", fontSize: "8px" },
      y: 10,
    },
    gridLineWidth: 0,
    lineWidth: 0,
  },
  yAxis: {
    title: { text: null },
    labels: { enabled: false },
    gridLineWidth: 0,
    lineWidth: 0,
  },
  plotOptions: {
    column: {
      depth: 25,
      dataLabels: {
        enabled: true,
        style: { color: "#fff", textOutline: "none", fontSize: "8px" },
        y: -5,
      },
    },
  },
  tooltip: {
    backgroundColor: "#fff",
    style: { 
      color: "#000", 
      fontSize: "9px",
    },
  },
  legend: { enabled: false },
  series: [
    {
      name: title,
      data,
      color: {
        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
        stops: [
          [0, '#d18cf9'],
          [1, '#70305c']
        ]
      },
      showInLegend: true,
    },
  ],
  
  credits: { enabled: false },
});

const FilterChips = ({ title, options = [], selected, onChange }) => (
  <div className={styles.chipContainer}>
    {options.map((option) => (
      <div
        key={option}
        className={`${styles.chip} ${selected === option ? styles.selectedChip : ""}`}
        onClick={() => onChange(title, selected === option ? null : option)}
      >
        {option}
      </div>
    ))}
  </div>
);

const ChartHexagon = ({ title, countObj = {}, selectedValue, onFilterChange }) => {
  const categories = Object.keys(countObj);
  const data = categories.map((key) => countObj[key]);

  const chartFilterOptions = Object.keys(countObj); // Dynamic YES/NO based on countObj keys

  return (
    <div className={styles.chartHexContainer}>
      {chartFilterOptions.length > 0 && (
        <FilterChips
          title={title}
          options={chartFilterOptions}
          selected={selectedValue}
          onChange={onFilterChange}
        />
      )}
      <div className={`${styles.hexBox} ${styles.hexChartBox}`}>
        <div className={styles.chartWrapper}>
          <HighchartsReact
            highcharts={Highcharts}
            options={chartOptions(title, categories, data)}
          />
        </div>
      </div>
      <div className={styles.chartTitle}>{title}</div>
    </div>
  );
};


const Index = ({ bhiPatientsAPI, bhiPatientsFlow }) => {
  const [storedPayload, setStoredPayload] = useState(null);
  const [filters, setFilters] = useState({});
  const [chartFilters, setChartFilters] = useState({
    psychConsult: null,
    fallGx: null,
    gdr: null,
  });

  const data = bhiPatientsFlow?.response || {};

  useEffect(() => {
    const getPayload = localStorage.getItem("payload");
    const parsedPayload = getPayload ? JSON.parse(getPayload) : null;
    setStoredPayload(parsedPayload);

    if (parsedPayload) {
      bhiPatientsAPI(parsedPayload);
    }
  }, [bhiPatientsAPI]);

  useEffect(() => {
    if (storedPayload) {
      const finalPayload = {
        ...storedPayload,
        ...filters,
        ...Object.entries(chartFilters)
          .filter(([_, value]) => value !== null)
          .reduce((acc, [key, value]) => ({ ...acc, [key]: value }), {}),
      };
      bhiPatientsAPI(finalPayload);
    }
  }, [filters, chartFilters, storedPayload, bhiPatientsAPI]);

  const handleChartFilterChange = (type, value) => {
    const map = {
      "PSYCH CONSULT": "psychConsult",
      "FALL HX": "fallGx",
      GDR: "gdr",
    };
    setChartFilters((prev) => ({
      ...prev,
      [map[type]]: value,
    }));
  };

  return (
    <BhiLayout>
      <div className={styles.mainContentWrapper}>
        <div className={styles.pageContent}>
          <div className={styles.hexRow}>
            <div className={`${styles.hexBox} ${styles.hexStatBox}`}>
              <span className={styles.hexLabel}>PATIENT POPULATION</span>
              <span className={styles.hexValue}>{data.totalPatients ?? 0}</span>
            </div>
            <div className={`${styles.hexBox} ${styles.hexStatBox}`}>
              <span className={styles.hexLabel}>SCHIZOPHRENIA DX</span>
              <span className={styles.hexValue}>{data.schizophreniaCount ?? 0}</span>
            </div>
            <div className={`${styles.hexBox} ${styles.hexStatBox}`}>
              <span className={styles.hexLabel}>OTHER DX</span>
              <span className={styles.hexValue}>{data.otherDx ?? 0}</span>
            </div>

            <ChartHexagon
              title="PSYCH CONSULT"
              countObj={data.psychConsultCount}
              filterOptions={["YES", "NO"]}
              selectedValue={chartFilters.psychConsult}
              onFilterChange={handleChartFilterChange}
            />
            <ChartHexagon
              title="GDR"
              countObj={data.gdrCount}
              filterOptions={["YES", "NO"]}
              selectedValue={chartFilters.gdr}
              onFilterChange={handleChartFilterChange}
            />
            <ChartHexagon
              title="FALL HX"
              countObj={data.fallGxCount}
              filterOptions={["YES", "NO"]}
              selectedValue={chartFilters.fallGx}
              onFilterChange={handleChartFilterChange}
            />
          </div>

          <div className={styles.tableContainer}>
            <table className={styles.patientTable}>
              <thead>
                <tr>
                  <th>Patient name</th>
                  <th>APPROPRIATE_DIAGNOSIS</th>
                  <th>SCHIZO_DIAG_IN_FAC</th>
                  <th>ADMIT_W/_SCHIZO</th>
                  <th>PSYCH CONSULT</th>
                  <th>GDR</th>
                  <th>FALL HX</th>
                  <th>CARE_GAPS</th>
                </tr>
              </thead>
              <tbody>
                {data.bhiInfoEntityList?.map((patient, i) => (
                  <tr key={i}>
                    <td>{patient.patientName}</td>
                    <td>{patient.appropriateDiagnosis?.replace(/\n/g, ", ")}</td>
                    <td>{patient.schizophrenia_diagnosed_in_facility}</td>
                    <td>{patient.admittedWithSchizophrenia}</td>
                    <td>{patient.psychConsult}</td>
                    <td>{patient.gdr || ""}</td>
                    <td>
                      {[patient.fall_first, patient.fall_second, patient.fall_third]
                        .filter(Boolean)
                        .join("|")}
                    </td>
                    <td>
                      {patient.careGaps?.split("\n").map((line, idx) => (
                        <div key={idx}>{line}</div>
                      ))}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <div className={styles.sidebar}>
          <BhiFilters
            dx={data.dx || []}
            patientNames={data.patientNames || []}
            onFilterChange={(newFilters) => {
              setFilters(newFilters);
            }}
          />
        </div>
      </div>
    </BhiLayout>
  );
};

const enhancer = connect(
  (state) => ({
    bhiPatientsFlow: state.bhiReports.bhiPatientFlow.data,
  }),
  {
    bhiPatientsAPI: bhiReportsActions.bhiPatientAction,
  }
);

export default enhancer(Index);
