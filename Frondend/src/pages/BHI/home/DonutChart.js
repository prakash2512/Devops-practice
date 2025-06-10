import React from "react";
import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";
import Image from "next/image";
import BrainIcon from "../../../../public/icons/BrainIcon.svg";
import styles from "./DonutChart.module.css";

const DonutChart = ({ data }) => {
  const chartData = Object.entries(data || {}).map(([name, value]) => ({
    name,
    y: value,
  }));

  const donutOptions = {
    chart: {
      type: "pie",
      backgroundColor: "transparent",
      height: 350,
      spacing: [10, 10, 10, 10],
    },
    title: { text: "" },
    tooltip: {
      enabled: true,
      style: { zIndex: 9999 },
      pointFormat: "<b>{point.percentage:.1f}%</b>",
    },
    plotOptions: {
      pie: {
        innerSize: "40%",
        borderWidth: 0,
        dataLabels: {
          enabled: true,
          format: "{point.percentage:.1f}%",
          distance: -30,
          style: {
            color: "#fff",
            fontSize: "13px",
            fontWeight: "bold",
            textOutline: "none",
          },
        },
        showInLegend: true,
      },
    },
    legend: {
      enabled: true,
      layout: "vertical",
      align: "right",
      verticalAlign: "middle",
      itemMarginBottom: 18,
      useHTML: true, // Enable HTML for title tag
      itemStyle: {
        color: "#fff",
        fontWeight: "bold",
        fontSize: "12px",
        overflow: "hidden",
        textOverflow: "ellipsis",
        whiteSpace: "nowrap",
      },
      labelFormatter: function () {
        const maxLength = 13;
        const truncated = this.name.length > maxLength
          ? this.name.substring(0, maxLength) + "…"
          : this.name;
        return `<span title="${this.name}">${truncated}</span>`;
      },
      symbolRadius: 0,
      symbolHeight: 10,
      symbolWidth: 10,
    },    
    series: [
      {
        name: "Diagnosis",
        colorByPoint: true,
        data: chartData,
      },
    ],
    credits: { enabled: false },
  };

  return (
    <div className={styles.hexGrid}>
      <div className={styles.hexTileLarge}>
        <div className={styles.hexContentSplit}>
          <div className={styles.donutWithLegend}>
            <div className={styles.chartContainer}>
              <HighchartsReact highcharts={Highcharts} options={donutOptions} />
              <div className={styles.chartIconOverlay}>
                <Image src={BrainIcon} alt="Brain Icon" width={80} height={80} />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DonutChart;
