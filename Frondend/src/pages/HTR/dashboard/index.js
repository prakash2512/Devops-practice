import React, { useState, useEffect } from 'react';
import styles from './summary.module.css';
import ButtonComponent from '@/components/HTR/dashboard/buttons/ButtonComponent';
import BarChartCard from '@/components/HTR/dashboard/card/CardComponent';
import Sidebar from './Components/Sidebar/Sidebar';
import SummaryContent from './Components/Summary/SummaryContent';
import Patient from './Components/Patient/Patient';
import TableChartIcon from '@mui/icons-material/TableChart';
import { useRouter } from 'next/router';
import { connect } from 'react-redux';
import { actions as HTRActions } from '@/store/HTR';

function Index({ getDashboardDetailsApiCall }) {

    const [isSummaryClicked, setSummaryClicked] = useState(true);
    const [isPatientClicked, setPatientClicked] = useState(false);
    const [bodyContentHeight, setBodyContentHeight] = useState('auto');
    const [dashboardData, setdashboardData] = useState({})

    const router = useRouter()

    const headerCardData = [
        {
            title: 'Transfer by Category',
            values: Object.values(dashboardData?.transferByCategory || {}),
            categories: Object.keys(dashboardData?.transferByCategory || {}),
            width: 300,
            height: 150,
        },
        {
            title: 'Transfer by Disposition',
            values: Object.values(dashboardData?.transferByDisposition || {}),
            categories: Object.keys(dashboardData?.transferByDisposition || {}),
            width: 300,
            height: 150,
        },
        {
            title: 'Transfer by Stay Status',
            values: Object.values(dashboardData?.transferByStayStatus || {}),
            categories: Object.keys(dashboardData?.transferByStayStatus || {}),
            width: 300,
            height: 150,
        },
        {
            title: 'Length of Stay',
            values: Object.values(dashboardData?.lengthOfStay || {}),
            categories: Object.keys(dashboardData?.lengthOfStay || {}),
            width: 300,
            height: 150,
        },
        {
            title: 'Transfer by Payer Group',
            values: Object.values(dashboardData?.transferByPayerGroup || {}),
            categories: Object.keys(dashboardData?.transferByPayerGroup || {}),
            width: 300,
            height: 150,
        },
    ];


    useEffect(() => {
        fetchDashboardDetails()
    }, [])

    const handleSummaryBtnClick = () => {
        setSummaryClicked(true);
        setPatientClicked(false);
    };
    const handlePatientBtnClick = () => {
        setSummaryClicked(false);
        setPatientClicked(true);
    };

    const routesData = [
        {
            label: "CCM",
            route: '/CCM'
        },
        {
            label: "HTR",
            route: '/HTR'
        },
        {
            label: "BHI",
            route: '/BHI'
        },
        {
            label: "PCM",
            route: '/PCM'
        },
    ]

    const handleTablePatientClick = () => {
        router.push('/HTR/dashboard/patientdetails')
    }

    const cardData = {
        label: 'JUL-24',
        width: 180,
        height: 180,
        fontSize: 29,
        isMenu: true,
    };


    const fetchDashboardDetails = async () => {
        let payload = JSON.parse(localStorage.getItem('payload')); // Ensure payload is parsed as an object
        if (payload && payload.year) {
            payload.year = Number(payload.year); // Convert 'year' to a number
        }

        try {
            let res = await getDashboardDetailsApiCall(payload); // Pass the updated payload
            if (res.status === 'SUCCESS') {
                setdashboardData(res.response); // Update state with response data
            }
        } catch (error) {
            console.error('Error in getDashboardDetailsApiCall:', error);
        }
    };



    const sidebarBtnData = [
        {
            label: 'Summary',
            width: 130,
            height: 130,
            fontSize: 15,
            isMenu: false,
            state: isSummaryClicked,
            setState: setSummaryClicked,
            onClickFunc: handleSummaryBtnClick,
        },
        {
            label: 'Patient',
            width: 130,
            height: 130,
            fontSize: 15,
            isMenu: false,
            state: isPatientClicked,
            setState: setPatientClicked,
            onClickFunc: handlePatientBtnClick,
        },
    ];

    useEffect(() => {
        const updateBodyHeight = () => {
            const headerElement = document.querySelector(`.${styles.headerWrapper}`);
            if (headerElement) {
                const headerHeight = headerElement.offsetHeight;
                const bodyHeight = window.innerHeight - headerHeight;
                setBodyContentHeight(bodyHeight);
            }
        };

        // Initial calculation when component mounts
        updateBodyHeight();

        // Update body height on window resize
        window.addEventListener('resize', updateBodyHeight);

        // Cleanup the event listener when component unmounts
        return () => {
            window.removeEventListener('resize', updateBodyHeight);
        };
    }, []); // Empty dependency array ensures it runs only once when component mounts

    return (
        <div className={styles.htrSummaryWrapper}>
            <section className={styles.headerWrapper}>
                <h1 className={styles.titleHeader}>
                    GARDENS AT ORANGEVIlLE
                </h1>
                <div style={{
                    display: 'flex',
                    width: '100%',
                    justifyContent: 'space-between',
                    // paddingRight: '65px'
                }}
                >
                    <div className={styles.container}>
                        {
                            routesData.map((data, index) => {
                                return (
                                    <div key={index} className={styles.routeItem}>
                                        <div
                                            onClick={() => router.push(data.route)}
                                            className={styles.routeItemLabel}
                                        >
                                            {data.label}
                                        </div>
                                    </div>
                                );
                            })
                        }
                    </div>
                    <TableChartIcon
                        onClick={handleTablePatientClick}
                        style={{
                            color: '#00FFFF',
                            fontSize: 40,
                            cursor: 'pointer'

                        }} />
                </div>
                <header className={styles.headerSection}>
                    <div className={styles.menuWrapper}>
                        <ButtonComponent data={cardData} />
                    </div>
                    <div className={styles.headerCardWrapper}>
                        {headerCardData.map((dataa, index) => (
                            <BarChartCard key={index} data={dataa} />
                        ))}
                    </div>
                </header>
            </section>
            <section className={styles.bodyContent} style={{ height: bodyContentHeight }}>
                <div className={styles.sidebar}>
                    <Sidebar data={sidebarBtnData} />
                </div>
                <div className={styles.contentBody}>
                    {isSummaryClicked ? <SummaryContent data={dashboardData} /> : <Patient data={dashboardData} />}
                </div>
            </section>
        </div>
    );
}

const enhancer = connect(
    (state) => ({
        dashboardResponse: state
    }),
    {
        getDashboardDetailsApiCall: HTRActions.dashBoardDetailsAction
    }
)

export default enhancer(Index);
