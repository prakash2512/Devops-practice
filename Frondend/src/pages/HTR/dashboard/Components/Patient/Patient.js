import React, { useEffect, useState } from 'react'
import styles from './patient.module.css'
import BarChartCard from '@/components/HTR/dashboard/card/CardComponent'
import TableComponent from '@/components/HTR/dashboard/table/Table'
import FilterComponent from './Components/FilterComponent'
import { connect } from 'react-redux'
import { actions as HTRActions } from '@/store/HTR'

function Patient({ getDashboardDetailsApiCall, getFilterDetailsApiCall }) {

    const [filterData, setFilterData] = useState({})
    const [dashboardData, setdashboardData] = useState({})


    console.log(dashboardData , 'dashboardData');
    

    let localPayload = JSON.parse(localStorage.getItem('payload'));

    const [payload, setPayload] = useState({
        facilityName: localPayload?.facilityName,
        month: localPayload?.month,
        age: '',
        htrStatus: '',
        patientName: [],
        htrProvider: ''
    });


    useEffect(() => {
        fetchDashboardDetails()
    }, [payload])


    const fetchDashboardDetails = async () => {

        try {
            let res = await getDashboardDetailsApiCall(payload)
            if (res.status == 'SUCCESS') {
                setdashboardData(res.response)
            }
        } catch (error) {
            console.log(error, 'getDashboardDetailsApiCall');

        }
    }
    // Function to update the age group
    const handleAgeClick = (age) => {
        setPayload((prev) => ({ ...prev, age }));
    };

    // Function to update the status
    const handleStatusClick = (status) => {
        setPayload((prev) => ({ ...prev, htrStatus: status }));
    };

    // Function to update selected patient names
    const handlePatientNameClick = (patient) => {
        setPayload((prev) => {
            const updatedPatients = prev.patientName.includes(patient)
                ? prev.patientName.filter((p) => p !== patient) // Remove if already selected
                : [...prev.patientName, patient]; // Add if not selected
            return { ...prev, patientName: updatedPatients };
        });
    };

    // Function to update the provider
    const handleProviderClick = (provider) => {
        setPayload((prev) => ({ ...prev, htrProvider: provider }));
    };

    useEffect(() => {
        fetchGetFilterDetailsData()
    }, [])

    const fetchGetFilterDetailsData = async () => {
        let localPayload = JSON.parse(localStorage.getItem('payload'));
        let facilityName = localPayload?.facilityName
        let month = localPayload?.month
        let payload = {
            facilityName: facilityName,
            month: month
        }
        try {
            let res = await getFilterDetailsApiCall(payload)
            if (res.status == 'SUCCESS') {
                setFilterData(res.response)
            }
        } catch (error) {
            console.error(error);
        }
    }

    let cardDataSection = {
        title: 'Category Sub-Classification',
        values: [7, 3],
        categories: ["Unavoidable", 'Opportunity'],
        width: 550,
        height: 484
    }

    let card2DataSection = {
        title: 'Transfer by Diagnosis',
        values: Object.values(dashboardData.transferByDiagnosis || {}),
        categories: Object.keys(dashboardData.transferByDiagnosis || {}),
        width: 550,
        height: 484
    }

    let cardioDataSection = {
        title: 'Cardio',
        values: Object.values(dashboardData.cardio || {}),
        categories: Object.keys(dashboardData.cardio || {}),
        width: 550,
        height: 250
    }

    let cardioPulmonaryDataSection = {
        title: 'Cardio Pulmonary',
        values: Object.values(dashboardData.cardioPulmonary || {}),
        categories: Object.keys(dashboardData.cardioPulmonary || {}),
        width: 550,
        height: 250
    }

    const patientsData = dashboardData.transferDetails

    return (
        <div className={styles.patietnTab} >
            <section className={styles.patientContentWrapperRow1}>
                <div className={styles.cardsWrapperrr}>
                    <div className={styles.barPatientContainer} >
                        <BarChartCard data={cardDataSection} />
                        <BarChartCard data={card2DataSection} />
                    </div>
                    {dashboardData.cardioPulmonary && dashboardData.cardio && <div className={styles.barPatientContainer} >
                        <BarChartCard data={cardioDataSection} />
                        <BarChartCard data={cardioPulmonaryDataSection} />
                    </div>}
                </div>
                <div className={styles.tableContainer} >
                    {patientsData && <TableComponent tableData={patientsData} />}
                </div>
                <div>
                    {filterData && <FilterComponent
                        filterKeys={filterData}
                        payload={payload}
                        handleAgeClick={handleAgeClick}
                        handleStatusClick={handleStatusClick}
                        handlePatientNameClick={handlePatientNameClick}
                        handleProviderClick={handleProviderClick} />}
                </div>
            </section>
            <section className={styles.patientContentWrapperRow2} >

            </section>
        </div>)
}
const enhancer = connect(
    (state) => ({
        dashboardResponse: state
    }),
    {
        getFilterDetailsApiCall: HTRActions.getFilterDetailsAction,
        getDashboardDetailsApiCall: HTRActions.dashBoardDetailsAction
    }
)

export default enhancer(Patient);
