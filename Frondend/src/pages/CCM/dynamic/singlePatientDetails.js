import React, { useState } from "react";
import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";
import { RiArrowLeftLine } from "react-icons/ri";
import styles from "./singlePatientDetails.module.css";

const COLORS = ["#0a3d91", "#337ab7", "#5bc0de", "#5cb85c"];

const PolarChart = ({ data }) => {
  const cx = 50,
    cy = 50,
    r = 40;

  if (data.length === 0) return null;

  if (data.length === 1) {
    return (
      <div className={styles.physicianChartContainer}>
        <svg viewBox="0 0 100 100" className={styles.physicianChart}>
          <circle cx={cx} cy={cy} r={r + 5} fill="#5BC9FF" />
          <circle cx={cx} cy={cy} r={r} fill={COLORS[0]} />
          <text x={cx} y={cy - 5} className={styles.label}>
            {data[0].name}
          </text>
          <text x={cx} y={cy + 10} className={styles.count}>
            {data[0].count}
          </text>
        </svg>
      </div>
    );
  }

  const anglePer = 360 / data.length;

  const polarToCartesian = (angleDeg, radius = r) => {
    const angle = ((angleDeg - 90) * Math.PI) / 180.0;
    return {
      x: cx + radius * Math.cos(angle),
      y: cy + radius * Math.sin(angle),
    };
  };

  const describeArc = (startAngle, endAngle) => {
    const start = polarToCartesian(endAngle);
    const end = polarToCartesian(startAngle);
    const largeArcFlag = endAngle - startAngle <= 180 ? "0" : "1";

    return [
      `M ${cx} ${cy}`,
      `L ${start.x} ${start.y}`,
      `A ${r} ${r} 0 ${largeArcFlag} 0 ${end.x} ${end.y}`,
      "Z",
    ].join(" ");
  };

  return (
    <div className={styles.physicianChartContainer}>
      <svg viewBox="0 0 100 100" className={styles.physicianChart}>
        <circle cx={cx} cy={cy} r={r + 5} fill="#5BC9FF" />
        <circle cx={cx} cy={cy} r={r} fill="white" />

        {data.map((program, i) => {
          const startAngle = i * anglePer;
          const endAngle = (i + 1) * anglePer;
          const midAngle = startAngle + anglePer / 2;
          const textPos = polarToCartesian(midAngle, r * 0.6);

          return (
            <g key={program.name}>
              <path
                d={describeArc(startAngle, endAngle)}
                fill={COLORS[i % COLORS.length]}
              />
              <text x={textPos.x} y={textPos.y} className={styles.label}>
                {program.name}
              </text>
              <text x={textPos.x} y={textPos.y + 10} className={styles.count}>
                {program.count}
              </text>
            </g>
          );
        })}
      </svg>
    </div>
  );
};

const PatientTable = ({ patientData, filters }) => {
  if (!patientData || !filters) return null;

  const transformData = () => {
    const { ccm, bhi, htr } = patientData;
    const records = [];

    const processRecords = (data, program) => {
      if (!data) return;
      Object.entries(data).forEach(([month, monthRecords]) => {
        if (monthRecords) {
          monthRecords.forEach(record => {
            records.push({ ...record, month, program });
          });
        }
      });
    };

    processRecords(ccm, 'CCM');
    processRecords(bhi, 'BHI');
    processRecords(htr, 'HTR');

    return records;
  };

  const allRecords = transformData();
  const { programTypes = [] } = filters;

  const filteredRecords = allRecords.filter(record => 
    programTypes.includes(record.program)
  );

  const recordsByMonth = {};
  filteredRecords.forEach(record => {
    if (!recordsByMonth[record.month]) {
      recordsByMonth[record.month] = [];
    }
    recordsByMonth[record.month].push(record);
  });

  return (
    <div className={styles.tableContainer}>
      <table className={styles.table}>
        <thead>
          <tr>
            <th>PATIENT NAME</th>
            <th>MONTH/YEAR</th>
            <th>PROGRAM</th>
            <th>DIAGNOSIS</th>
            <th>CAREGAPS</th>
            <th className={styles.analysisHeaderCell}>HOSPITALIZATION ANALYSIS</th>
          </tr>
        </thead>
        <tbody>
          {Object.entries(recordsByMonth).map(([month, monthRecords]) => (
            <React.Fragment key={month}>
              {monthRecords.map((record, idx) => (
                <tr key={`${record.program}-${month}-${idx}`}>
                  {idx === 0 && (
                    <>
                      <td rowSpan={monthRecords.length}>{record.patientName}</td>
                      <td rowSpan={monthRecords.length}>{`${month} ${filters.year}`}</td>
                    </>
                  )}
                  <td>{record.program}</td>
                  <td>
                    {record.diagnosList && <div>DX1: {record.diagnosList}</div>}
                    {record.diagnosList2 && <div>DX2: {record.diagnosList2}</div>}
                    {record.diagnosis && <div>DX: {record.diagnosis}</div>}
                    {record.shortDx && <div>DX: {record.shortDx}</div>}
                  </td>
                  <td>
                    {record.program === 'CCM' && (
                      <>
                        {record.diagnosList && record.careGaps && (
                          <>
                            <strong>DX1: {record.diagnosList}</strong>
                            <ul className={styles.careGapList}><li>{record.careGaps}</li></ul>
                          </>
                        )}
                        {record.diagnosList2 && record.careGaps2 && (
                          <>
                            <strong>DX2: {record.diagnosList2}</strong>
                            <ul className={styles.careGapList}><li>{record.careGaps2}</li></ul>
                          </>
                        )}
                      </>
                    )}
                    {record.program === 'BHI' && record.shortDx && record.careGaps && (
                      <>
                        <strong>DX: {record.shortDx}</strong>
                        <ul className={styles.careGapList}><li>{record.careGaps}</li></ul>
                      </>
                    )}
                    {record.program === 'HTR' && (
                      <div className={styles.caregapCell}>
                        <div className={styles.subHeader}>HOSPITALIZED REASON</div>
                        <div>{record.report || 'N/A'}</div>
                      </div>
                    )}
                  </td>
                  <td className={styles.analysisCell}>
                    {record.program === 'CCM' && (
                      <>
                        <div className={styles.subHeader}>ADMITTED</div>
                        <div>{record.residentHospitalized || 'NO'}</div>
                      </>
                    )}
                    {record.program === 'BHI' && (
                      <>
                        <div className={styles.subHeader}>ADMITTED</div>
                        <div>{record.admittedWithSchizophrenia || 'NO'}</div>
                      </>
                    )}
                    {record.program === 'HTR' && (
                      <div className={styles.htrContainer}>
                        <div className={styles.htrColumn}>
                          <div className={styles.subHeader}>TRANSFER DATE</div>
                          <div>{record.transferDate ? new Date(record.transferDate).toLocaleDateString() : 'N/A'}</div>
                        </div>
                        <div className={styles.htrColumn}>
                          <div className={styles.subHeader}>DISPOSITION</div>
                          <div>{record.transferByDisposition || 'N/A'}</div>
                        </div>
                        <div className={styles.htrColumn}>
                          <div className={styles.subHeader}>STATUS</div>
                          <div>{record.status || 'N/A'}</div>
                        </div>
                      </div>
                    )}
                  </td>
                </tr>
              ))}
            </React.Fragment>
          ))}
        </tbody>
      </table>
    </div>
  );
};

const SinglePatientDetails = ({ ccmSinglePatientDetailsFlow, filters, onBack }) => {
  const [monthIndex, setMonthIndex] = useState(0);
  const patientData = ccmSinglePatientDetailsFlow?.response;
  const { patientName, gender, dob, age, ccm, bhi, htr, htrDispositionCount } = patientData || {};

  const prepareProgramData = () => {
    const programData = [];
    
    const addProgramData = (programName, data) => {
      if (filters?.programTypes?.includes(programName)) {
        const count = data ? Object.values(data).reduce((acc, records) => 
          acc + (records ? records.length : 0), 0) : 0;
        if (count > 0) programData.push({ name: programName, count });
      }
    };

    addProgramData('CCM', ccm);
    addProgramData('BHI', bhi);
    addProgramData('HTR', htr);
    
    return programData;
  };

  const programData = prepareProgramData();

  const prepareHtrData = () => {
    if (!filters?.programTypes?.includes('HTR') || !htrDispositionCount) return null;
    
    const validMonths = Object.entries(htrDispositionCount)
      .filter(([_, value]) => value !== null)
      .map(([month]) => month);
    
    if (validMonths.length === 0) return null;

    const dispositionTypes = new Set();
    Object.values(htrDispositionCount).forEach(monthData => {
      if (monthData) Object.keys(monthData).forEach(type => dispositionTypes.add(type));
    });

    const series = Array.from(dispositionTypes).map(type => ({
      name: type,
      data: validMonths.map(month => htrDispositionCount[month]?.[type] || 0),
      color: COLORS[Array.from(dispositionTypes).indexOf(type) % COLORS.length]
    }));

    return { months: validMonths, series };
  };

  const htrData = prepareHtrData();
  const visibleMonths = htrData?.months?.slice(monthIndex, monthIndex + 3) || [];
  const visibleSeries = htrData?.series?.map(series => ({
    ...series,
    data: series.data.slice(monthIndex, monthIndex + 3)
  })) || [];

  const htrOptions = htrData ? {
    chart: { 
      type: 'column', 
      height: 250,
      backgroundColor: 'white'
    },
    credits: { enabled: false },
    title: { text: null },
    xAxis: { 
      categories: visibleMonths, 
      title: { text: null },
      lineColor: '#e0e0e0'
    },
    yAxis: {
      min: 0,
      title: { text: null },
      gridLineColor: '#f0f0f0'
    },
    legend: { 
      align: 'center', 
      verticalAlign: 'bottom',
      itemStyle: { fontSize: '12px' }
    },
    plotOptions: { 
      column: { 
        stacking: 'normal',
        pointWidth: 30,
        borderWidth: 0
      } 
    },
    tooltip: {
      shared: true,
      formatter: function () {
        let s = `<b>${this.x}</b>`;
        this.points.forEach(p => {
          s += `<br/><span style="color:${p.color}">\u25CF</span> ${p.series.name}: ${p.y}`;
        });
        return s;
      },
      useHTML: true,
    },
    series: visibleSeries
  } : null;

  const handleMonthNavigation = (direction) => {
    const newIndex = direction === 'next' ? monthIndex + 1 : monthIndex - 1;
    if (newIndex >= 0 && newIndex + 3 <= htrData?.months?.length) {
      setMonthIndex(newIndex);
    }
  };

  if (!patientData || !filters) {
    return <div className={styles.noData}>No patient data available</div>;
  }

  return (
    <div className={styles.container}>
      <button className={styles.backButton} onClick={onBack}>
        <RiArrowLeftLine size={22} />
        Back
      </button>

      <div className={styles.topSection}>
        <div className={styles.detailsCard}>
          <h3 className={styles.cardTitle}>GENERAL DETAILS</h3>
          <div className={styles.detailGrid}>
            <p><strong>NAME:</strong> {patientName}</p>
            <p><strong>DOB:</strong> {dob}</p>
            <p><strong>AGE:</strong> {age}</p>
            <p><strong>SEX:</strong> {gender}</p>
          </div>
        </div>

        {programData.length > 0 && (
          <div className={styles.physicianVisitContainer}>
            <h3 className={styles.cardTitle}>NO OF PHYSICIAN VISITS</h3>
            <PolarChart data={programData} />
          </div>
        )}

        {htrOptions && (
          <div className={styles.chartCard}>
            <h3 className={styles.cardTitle}>HTR ANALYTICS(DISPOSITION)</h3>
            {htrData.months.length > 3 && (
              <div className={styles.scrollButtons}>
                <button 
                  onClick={() => handleMonthNavigation('prev')} 
                  disabled={monthIndex === 0}
                >
                  {'<'}
                </button>
                <button 
                  onClick={() => handleMonthNavigation('next')} 
                  disabled={monthIndex + 3 >= htrData.months.length}
                >
                  {'>'}
                </button>
              </div>
            )}
            <HighchartsReact highcharts={Highcharts} options={htrOptions} />
          </div>
        )}
      </div>
      
      <PatientTable patientData={patientData} filters={filters} />
    </div>
  );
};

export default SinglePatientDetails;