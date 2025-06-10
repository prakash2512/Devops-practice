import React, { useEffect, useRef, useState } from "react";
import * as echarts from "echarts";
import {
  AiOutlineInsertRowAbove,
  AiOutlineCheck,
  AiOutlineClose,
  AiOutlinePrinter,
} from "react-icons/ai";
import { Button, Table, Spin, Modal, message } from "antd/lib";
import styles from "./Population.module.css";

const Population = ({
  conditionCaregapCountsFlow,
  onDatasetChange,
  isLoading,
  ccmDiagnosisPdfAPI,
  ccmDiagnosisPdfFlow,
}) => {
  const chartRef1 = useRef(null);
  const chartRef2 = useRef(null);
  const chartRef3 = useRef(null);
  const [showTable, setShowTable] = useState(false);
  const [selectedDataset, setSelectedDataset] = useState("dxDetails");
  const [pdfUrl, setPdfUrl] = useState(null);
  const [showPdfModal, setShowPdfModal] = useState(false);
  const [currentDiagnosis, setCurrentDiagnosis] = useState("");
  const [isPdfLoading, setIsPdfLoading] = useState(false);

  const dxDetails = conditionCaregapCountsFlow?.response?.[selectedDataset] || [];
  const conditionCareGapDetails =
    conditionCaregapCountsFlow?.response?.[
      `conditionCareGapDetails${selectedDataset === "dxDetails" ? "" : "2"}`
    ] || [];

  const patientPopulationData = dxDetails.map((item) => item.diagnosListCount) || [];
  const conditions = dxDetails.map((item) => item.convertedDiagnosList) || [];
  const rawDiagnosisNames = dxDetails.map((item) => item.diagnosList) || [];

  const handleDatasetChange = (dataset) => {
    setSelectedDataset(dataset);
    onDatasetChange(dataset === "dxDetails" ? 1 : 2);
  };

  const handlePrint = () => {
    const printContents = document.querySelector(`.${styles.tableScrollable}`).innerHTML;
    const originalContents = document.body.innerHTML;

    document.body.innerHTML = printContents;
    window.print();
    document.body.innerHTML = originalContents;
    window.location.reload();
  };

  const handleDiagnosisClick = async (convertedDiagnosisName) => {
    try {
      // Find the corresponding raw diagnosis name
      const diagnosisIndex = conditions.findIndex(
        (condition) => condition === convertedDiagnosisName
      );
      
      if (diagnosisIndex === -1) {
        message.warning("Diagnosis data not found");
        return;
      }

      const rawDiagnosisName = rawDiagnosisNames[diagnosisIndex];
      setCurrentDiagnosis(convertedDiagnosisName);
      setIsPdfLoading(true);
      
      const payload = {
        diagnosisName: rawDiagnosisName.trim(), // Using the raw diagnosis name for the API
      };
      
      await ccmDiagnosisPdfAPI(payload);
    } catch (error) {
      message.error(error.message || "Failed to load diagnosis PDF");
      setIsPdfLoading(false);
    }
  };

  // Handle PDF response
  useEffect(() => {
    if (!ccmDiagnosisPdfFlow) return;

    if (ccmDiagnosisPdfFlow.error) {
      message.error(ccmDiagnosisPdfFlow.error.message || "Failed to load PDF");
      setIsPdfLoading(false);
      return;
    }

    if (ccmDiagnosisPdfFlow.data) {
      try {
        if (ccmDiagnosisPdfFlow.data.size > 0) {
          const blob = new Blob([ccmDiagnosisPdfFlow.data], {
            type: "application/pdf",
          });
          const url = URL.createObjectURL(blob);
          setPdfUrl(url);
          setShowPdfModal(true);
        } else {
          message.warning("No PDF available for this diagnosis");
        }
      } catch (err) {
        console.error("Error processing PDF:", err);
        message.error("Failed to process PDF");
      } finally {
        setIsPdfLoading(false);
      }
    }
  }, [ccmDiagnosisPdfFlow]);

  const closePdfModal = () => {
    setShowPdfModal(false);
    if (pdfUrl) {
      URL.revokeObjectURL(pdfUrl);
      setPdfUrl(null);
    }
    setIsPdfLoading(false);
  };

  // Charts rendering effect
  useEffect(() => {
    if (!showTable && !isLoading && conditions.length > 0) {
      // Combine data for sorting
      const combinedData = conditions.map((condition, index) => ({
        convertedDiagnosis: condition,
        rawDiagnosis: rawDiagnosisNames[index],
        count: patientPopulationData[index],
      }));

      // Sort by count descending
      const sortedData = [...combinedData].sort((a, b) => b.count - a.count);

      const sortedConditions = sortedData.map((item) => item.convertedDiagnosis);
      const sortedPatientPopulationData = sortedData.map((item) => item.count);

      // Initialize and render Chart 1: Patient Population by DX
      const chartInstance1 = echarts.init(chartRef1.current);
      const option1 = {
        title: {
          text: "PATIENT POPULATION BY DX",
          left: "center",
          textStyle: {
            color: "#fff",
            fontSize: 12,
            fontFamily: "Poppins, sans-serif",
          },
        },
        tooltip: { trigger: "axis", axisPointer: { type: "shadow" } },
        grid: { left: 150 },
        xAxis: { show: false },
        yAxis: {
          type: "category",
          data: sortedConditions,
          axisLine: { lineStyle: { color: "#fff" } },
          axisLabel: {
            color: "#fff",
            fontSize: 12,
            fontFamily: "Poppins, sans-serif",
            interval: 0,
            formatter: (value) =>
              value.length > 15 ? `${value.substring(0, 15)}...` : value,
          },
        },
        series: [
          {
            type: "bar",
            data: sortedPatientPopulationData,
            itemStyle: { color: "#00A3FF" },
            label: {
              show: true,
              position: "right",
              color: "#fff",
              fontSize: 10,
              fontFamily: "Poppins, sans-serif",
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: "rgba(0, 0, 0, 0.5)",
              },
            },
          },
        ],
        backgroundColor: "rgb(21, 25, 43)",
      };

      chartInstance1.on("click", (params) => {
        handleDiagnosisClick(sortedConditions[params.dataIndex]);
      });
      chartInstance1.setOption(option1);

      // Initialize and render Chart 3: % of DX Condition
      const chartInstance3 = echarts.init(chartRef3.current);
      const option3 = {
        title: {
          text: "% OF DX CONDITION",
          left: "center",
          textStyle: {
            color: "#fff",
            fontSize: 12,
            fontFamily: "Poppins, sans-serif",
          },
        },
        tooltip: { trigger: "axis", axisPointer: { type: "shadow" } },
        grid: { left: 150 },
        xAxis: { show: false },
        yAxis: {
          type: "category",
          data: sortedConditions,
          axisLine: { lineStyle: { color: "#fff" } },
          axisLabel: {
            color: "#fff",
            fontSize: 12,
            fontFamily: "Poppins, sans-serif",
            interval: 0,
            formatter: (value) =>
              value.length > 15 ? `${value.substring(0, 15)}...` : value,
          },
        },
        series: [
          {
            type: "bar",
            data: sortedPatientPopulationData.map((value) =>
              (
                (value /
                  sortedPatientPopulationData.reduce(
                    (acc, curr) => acc + curr,
                    0
                  )) *
                100
              ).toFixed(2)
            ),
            itemStyle: { color: "#00FF00" },
            label: {
              show: true,
              position: "right",
              color: "#fff",
              fontSize: 10,
              fontFamily: "Poppins, sans-serif",
              formatter: "{c}%",
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: "rgba(0, 0, 0, 0.5)",
              },
            },
          },
        ],
        backgroundColor: "rgb(21, 25, 43)",
      };

      chartInstance3.on("click", (params) => {
        handleDiagnosisClick(sortedConditions[params.dataIndex]);
      });
      chartInstance3.setOption(option3);

      // Resize handler
      const handleResize = () => {
        chartInstance1.resize();
        chartInstance3.resize();
      };
      window.addEventListener("resize", handleResize);

      return () => {
        chartInstance1.dispose();
        chartInstance3.dispose();
        window.removeEventListener("resize", handleResize);
      };
    }
  }, [showTable, patientPopulationData, conditions, isLoading]);

  // Condition Care Gap chart effect
  useEffect(() => {
    if (!conditionCareGapDetails.length || isLoading) return;

    const chartInstance2 = echarts.init(chartRef2.current);
    
    // Prepare series data
    const allGapTypes = Array.from(
      new Set(
        conditionCareGapDetails.flatMap((item) => Object.keys(item.diagnosCount))
      )
    );

    const option2 = {
      title: {
        text: "CONDITION CARE GAP",
        left: "center",
        textStyle: {
          color: "#fff",
          fontSize: 12,
          fontFamily: "Poppins, sans-serif",
        },
      },
      tooltip: { trigger: "axis", axisPointer: { type: "shadow" } },
      legend: {
        data: allGapTypes,
        textStyle: { color: "#fff", fontFamily: "Poppins, sans-serif" },
        top: 20,
      },
      grid: { left: 150 },
      xAxis: { show: false },
      yAxis: {
        type: "category",
        data: conditionCareGapDetails.map((item) => item.convertedDiagnosList),
        axisLine: { lineStyle: { color: "#fff" } },
        axisLabel: {
          color: "#fff",
          fontSize: 12,
          fontFamily: "Poppins, sans-serif",
          interval: 0,
          formatter: (value) =>
            value.length > 15 ? `${value.substring(0, 15)}...` : value,
        },
      },
      series: allGapTypes.map((gap, index) => ({
        name: gap,
        type: "bar",
        stack: "total",
        data: conditionCareGapDetails.map(
          (item) => item.diagnosCount[gap] || 0
        ),
        itemStyle: {
          color: [
            "#4E79A7",
            "#F28E2B",
            "#E15759",
            "#76B7B2",
            "#59A14F",
            "#EDC948",
            "#B07AA1",
            "#FF9DA7",
            "#9C755F",
            "#BAB0AC",
          ][index % 10],
        },
        label: {
          show: true,
          position: "inside",
          color: "#fff",
          fontSize: 10,
          fontFamily: "Poppins, sans-serif",
          formatter: (params) => (params.value > 0 ? params.value : ""),
        },
      })),
      backgroundColor: "rgb(21, 25, 43)",
    };

    chartInstance2.on("click", (params) => {
      if (params.componentType === "series") {
        const diagnosis = conditionCareGapDetails[params.dataIndex].convertedDiagnosList;
        handleDiagnosisClick(diagnosis);
      }
    });

    chartInstance2.setOption(option2);
    const resizeHandler = () => chartInstance2.resize();
    window.addEventListener("resize", resizeHandler);

    return () => {
      chartInstance2.dispose();
      window.removeEventListener("resize", resizeHandler);
    };
  }, [conditionCareGapDetails, showTable, isLoading]);

  const renderToggleButtons = () => (
    <div className={styles.toggleButtons}>
      <Button
        type={selectedDataset === "dxDetails" ? "primary" : "default"}
        onClick={() => handleDatasetChange("dxDetails")}
        style={{
          marginRight: "8px",
          backgroundColor:
            selectedDataset === "dxDetails" ? "#00bcd4" : undefined,
          borderColor: selectedDataset === "dxDetails" ? "#00bcd4" : undefined,
        }}
      >
        DX 1
      </Button>
      <Button
        type={selectedDataset === "dxDetails2" ? "primary" : "default"}
        onClick={() => handleDatasetChange("dxDetails2")}
        style={{
          backgroundColor:
            selectedDataset === "dxDetails2" ? "#00bcd4" : undefined,
          borderColor: selectedDataset === "dxDetails2" ? "#00bcd4" : undefined,
        }}
      >
        DX 2
      </Button>
    </div>
  );

  const patientData = dxDetails.flatMap((item) =>
    item.patientDetailsList.map((patient) => ({
      key: patient.patientName,
      name: patient.patientName,
      dxList: { 
        DX1: item.convertedDiagnosList,
        rawDX1: item.diagnosList 
      },
      careGaps: { DX1: patient.careGaps?.split(",").map((gap) => gap.trim()) || [] },
      conditionCaregap: patient.conditionCaregap,
    }))
  );

  const columns = [
    { title: "Patient Name", dataIndex: "name", key: "name", align: "center" },
    {
      title: "DX_LIST",
      key: "dxList",
      align: "center",
      render: (_, record) => (
        <div 
          className={styles.dxLink}
          onClick={() => handleDiagnosisClick(record.dxList.DX1)}
        >
          {record.dxList.DX1}
        </div>
      ),
    },
    {
      title: "CAREGAPS",
      key: "careGaps",
      align: "center",
      render: (_, record) => (
        <div>
          <ul style={{ listStyle: "none", padding: 0, margin: 0 }}>
            {record.careGaps.DX1.map((gap, index) => (
              <li key={index}>- {gap}</li>
            ))}
          </ul>
        </div>
      ),
    },
    ...(
      conditionCaregapCountsFlow?.response?.[
        selectedDataset === "dxDetails"
          ? "patientConditionList1"
          : "patientConditionList2"
      ] || []
    ).map((condition) => ({
      title: condition,
      key: condition,
      align: "center",
      render: (_, record) => (
        <div style={{ textAlign: "center" }}>
          {record.conditionCaregap === condition ? (
            <AiOutlineCheck style={{ color: "green", fontSize: "20px" }} />
          ) : (
            <AiOutlineClose style={{ color: "red", fontSize: "20px" }} />
          )}
        </div>
      ),
    })),
  ];

  return (
    <div className={styles.container}>
      {renderToggleButtons()}
      {isLoading ? (
        <div className={styles.loader}>
          <Spin size="large" />
        </div>
      ) : showTable ? (
        <>
          <div className={styles.tableHeader}>
            <Button
              onClick={() => setShowTable(false)}
              className={styles.backButton}
            >
              Back
            </Button>
            <Button onClick={handlePrint} className={styles.printButton}>
              <AiOutlinePrinter size={20} />
            </Button>
          </div>
          <div className={styles.tableWrapper}>
            <div className={styles.chartFixed} ref={chartRef2} />
            <div className={styles.tableScrollable}>
              <Table
                columns={columns}
                dataSource={patientData}
                pagination={false}
                bordered
                components={{
                  header: {
                    cell: (props) => (
                      <th
                        style={{
                          background: "rgb(21, 25, 43)",
                          color: "#fff",
                          border: "1px solid #444",
                          padding: "8px",
                          fontFamily: "Poppins, sans-serif",
                        }}
                      >
                        {props.children}
                      </th>
                    ),
                  },
                  body: {
                    cell: (props) => (
                      <td
                        style={{
                          background: "rgb(21, 25, 43)",
                          color: "#fff",
                          border: "1px solid #444",
                          padding: "8px",
                          fontFamily: "Poppins, sans-serif",
                        }}
                      >
                        {props.children}
                      </td>
                    ),
                  },
                }}
              />
            </div>
          </div>
        </>
      ) : (
        <div className={styles.chartLayout}>
          <div className={styles.leftCharts}>
            <div className={styles.chart} ref={chartRef1} />
            <div className={styles.chart} ref={chartRef3} />
          </div>
          <div className={styles.rightChart}>
            <div className={styles.chart} ref={chartRef2} />
            <div
              className={styles.iconButton}
              onClick={() => setShowTable(true)}
            >
              <AiOutlineInsertRowAbove size={20} />
            </div>
          </div>
        </div>
      )}

      <Modal
        title={`Diagnosis Details: ${currentDiagnosis}`}
        open={showPdfModal}
        onCancel={closePdfModal}
        footer={null}
        width="90%"
        style={{ top: 20 }}
        bodyStyle={{ 
          height: "80vh", 
          padding: 0,
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          backgroundColor: "#f0f2f5"
        }}
        destroyOnClose
      >
        {isPdfLoading ? (
          <div style={{ textAlign: 'center' }}>
            <Spin size="large" />
            <p>Loading PDF...</p>
          </div>
        ) : pdfUrl ? (
          <iframe
            src={pdfUrl}
            width="100%"
            height="100%"
            style={{ border: "none" }}
            title="Diagnosis PDF"
          />
        ) : (
          <div style={{ textAlign: 'center', padding: '20px' }}>
            <AiOutlineClose style={{ color: 'red', fontSize: '48px', marginBottom: '16px' }} />
            <h3>File Not Found</h3>
            <p>No PDF available for this diagnosis</p>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default Population;