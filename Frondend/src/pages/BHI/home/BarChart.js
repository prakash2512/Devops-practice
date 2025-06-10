import React, { useMemo } from "react";
import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";
import styles from "./BarChart.module.css";

const BarChart = ({ data }) => {
  const categories = useMemo(() => {
    return Object.keys(data || {}).map((key) =>
      key.length > 12 ? `${key.slice(0, 12)}...` : key
    );
  }, [data]);

  const values = useMemo(() => Object.values(data || {}), [data]);
  const fullNames = useMemo(() => Object.keys(data || {}), [data]);

  const barChartOptions = {
    chart: {
      type: "column",
      backgroundColor: "transparent",
      height: 350,
      width: 550,
    },
    title: { text: "" },
    xAxis: {
      categories,
      labels: {
        useHTML: true,
        formatter: function () {
          const fullName = fullNames[this.pos];
          return `<span title="${fullName}">${this.value}</span>`;
        },
        style: { color: "#fff", fontSize: "12px" },
      },
      lineColor: "#777",
      tickColor: "#777",
    },
    yAxis: {
      visible: false, // hides the y-axis completely
    },
    legend: {
      enabled: false,
    },
    tooltip: {
      useHTML: true,
      backgroundColor: "#ffffff", // white box
      borderColor: "#ccc",
      borderRadius: 5,
      shadow: true,
      style: {
        color: "#000", // black text
        fontSize: "13px",
      },
      formatter: function () {
        return `<strong>${fullNames[this.point.index]}</strong>: ${this.y}`;
      },
    },    
    plotOptions: {
      column: {
        borderRadius: 4,
        colorByPoint: true,
        dataLabels: {
          enabled: true,
          color: "#fff",
          style: {
            fontWeight: "bold",
            fontSize: "12px",
          },
        },
      },
    },
    series: [
      {
        name: "Diagnosis",
        data: values,
        colors: [
          "#B164A0",
          "#B18D4D",
          "#E95859",
          "#7BCDC6",
          "#A6AD6A",
          "#5EBC68",
        ],
      },
    ],
    credits: { enabled: false },
  };

  return (
    <div className={styles.hexTileLarge}>
      <div className={styles.hexContentSplit}>
        <div className={styles.chartWrapper}>
          <div className={styles.chartArea}>
            <HighchartsReact highcharts={Highcharts} options={barChartOptions} />
          </div>
        </div>
      </div>
    </div>
  );
};

export default BarChart;
