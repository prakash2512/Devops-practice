import React, { useEffect, useState } from 'react'
import styles from './summaryContent.module.css'
import BarChartCard from '@/components/HTR/dashboard/card/CardComponent'
import TableComponent from '@/components/HTR/dashboard/table/Table'
import PieChart from '../Charts/Piecharts/PieCharts'


function SummaryContent({ data }) {

    const [isPatient, setPatient] = useState(true)
console.log(data, 'dashboardData');

    let cardDataSection = {
        title: 'Transfer by Days',
        values: Object.values(data.transferByDays || {}),
        categories: Object.keys(data.transferByDays || {}),
        width: 550,
        height: 255
    }

    let card2DataSection = {
        title: 'Transfer by Shift',
        values: Object.values(data.transferByShift || {}),
        categories: Object.keys(data.transferByShift || {}),
        width: 550,
        height: 150
    }

    let cardData = {
        title: 'Category Sub Classification',
        values: Object.values(data.categorySubClassification || {}),
        categories: Object.keys(data.categorySubClassification || {}),
        width: 550,
        height: 484
    }


    let cardioDataSection = {
        title: 'Cardio',
        values: Object.values(data.cardio || {}),
        categories: Object.keys(data.cardio || {}),
        width: 550,
        height: 250
    }

    let cardioPulmonaryDataSection = {
        title: 'Cardio Pulmonary',
        values: Object.values(data.cardioPulmonary || {}),
        categories: Object.keys(data.cardioPulmonary || {}),
        width: 550,
        height: 250
    }

    const handleShowAdmitted = () => {
        setPatient(true)
    }
    const handleShowObservation = () => {
        setPatient(false)
    }


    // Function to convert date & time format
    const formatTransferData = (data) => {
        return data?.map(item => {
            const [date, time] = item.transferDateAndTime.split(", ");
            return {
                patientName: item.patientName,
                transferDate: date,
                transferTime: time
            };
        });
    };

    // Filter admitted and observation stay transfers
    const admittedTransfers = formatTransferData(data?.dispositionPatientDetails?.filter(item => item.transferByDisposition === "Admitted"));
    const observedTransfers = formatTransferData(data?.dispositionPatientDetails?.filter(item => item.transferByDisposition === "Observation Stay"));


    return (
        <div className={styles.summaryTab} >
            <section className={styles.summaryContentWrapperRow1}>
                <div className={styles.barContainer} >
                    <BarChartCard data={cardData} />
                </div>
                <div className={styles.tableContainer} >
                    <div className={styles.tableTypeWrapper} >
                        <div
                            onClick={handleShowAdmitted}
                            className={`${styles.tableType} 
                     ${isPatient ? styles.show : ''}`}
                        >
                            Admitted
                        </div>
                        <div
                            onClick={handleShowObservation}
                            className={`${styles.tableType}
                     ${!isPatient ? styles.show : ''}`}
                        >
                            Observation
                        </div>
                    </div>
                    {
                        (admittedTransfers?.length || observedTransfers?.length) && (
                            <TableComponent tableData={isPatient ? admittedTransfers : observedTransfers} />
                        )
                    }

                </div>
                <div className={styles.section2BarTab} >
                    <BarChartCard data={cardDataSection} />
                    <BarChartCard data={card2DataSection} />
                </div>
                <div>
                    <div className={styles.pieSectionTitle} >Hospitalization details</div>
                    <div className={styles.pieChartsComponentWrapper} >
                        <PieChart
                            data={[23.10, 25.30, 21.00]}
                            labels={['National', 'State', 'Neutral']}
                        />
                        <PieChart
                            data={[2.10, 8.30, 12.00]}
                            labels={['National', 'State', 'Neutral']}
                        />
                    </div>
                </div>
            </section>
            <section className={styles.summaryContentWrapperRow2} >
                {
                    data.cardioPulmonary
                    && data.cardio
                    &&
                    <div className={styles.cardWrapperr} >
                        <BarChartCard data={cardioDataSection} />
                        <BarChartCard data={cardioPulmonaryDataSection} />
                    </div>
                }
            </section>
        </div>
    );

}

export default SummaryContent