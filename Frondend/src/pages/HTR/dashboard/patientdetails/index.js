
import React from "react";
import styles from "./style.module.css"; // Importing CSS module
import HomeIcon from '@mui/icons-material/Home';
import { useRouter } from "next/router";

const index = () => {
    const router = useRouter()
    const data = [
        {
            facility: "Southport Center for Nursing & Rehabilitation LLC",
            state: "CT",
            unit: "BWing",
            room: "407-D",
            lastName: "Williams",
            firstName: "Annabell",
            dob: "04/29/1950",
            gender: "Female",
            stayStatus: "Adult Long term (>90 days)",
            payerSource: "Medicaid",
            admissionDate: "10/23/24",
            hosp12Months: "01/00/00",
            clinicalRisk: "13",
            advanceDirective: "Full code",
        },
        {
            facility: "Southport Center for Nursing & Rehabilitation LLC",
            state: "CT",
            unit: "BWing",
            room: "226-W",
            lastName: "Servidio",
            firstName: "James",
            dob: "11/22/1947",
            gender: "Male",
            stayStatus: "Adult Long term (>90 days)",
            payerSource: "Medicare",
            admissionDate: "11/21/24",
            hosp12Months: "11/14/24",
            clinicalRisk: "15",
            advanceDirective: "Full code",
        },
        {
            facility: "Southport Center for Nursing & Rehabilitation LLC",
            state: "CT",
            unit: "BWing",
            room: "227-D",
            lastName: "Nastasia",
            firstName: "Elizabeth",
            dob: "07/02/1923",
            gender: "Female",
            stayStatus: "Adult Long term (>90 days)",
            payerSource: "Medicare",
            admissionDate: "07/02/23",
            hosp12Months: "01/00/00",
            clinicalRisk: "12",
            advanceDirective: "Full code",
        },
        {
            facility: "Southport Center for Nursing & Rehabilitation LLC",
            state: "CT",
            unit: "BWing",
            room: "339-W",
            lastName: "Young",
            firstName: "Douglas",
            dob: "12/06/1958",
            gender: "Male",
            stayStatus: "Adult Long term (>90 days)",
            payerSource: "Medicare",
            admissionDate: "12/24/2024",
            hosp12Months: "12/20/2024, 02/23/2024",
            clinicalRisk: "16",
            advanceDirective: "Full code",
        },
    ];


    const handleTablePatientClick = () => {
        router.push('/HTR/dashboard')
    }


    return (
        <div className={styles.patientDetailsTab} >
            <div className={styles.tableContainer}>
                <h1 className={styles.titleHeader}>
                    GARDENS AT ORANGEVIlLE
                </h1>                <div style={{
                    padding: 30
                }} >
                    <HomeIcon
                        onClick={handleTablePatientClick}
                        style={{
                            color: '#00FFFF',
                            fontSize: 40,
                            cursor: 'pointer'

                        }} />
                </div>
                <table className={styles.table}>
                    <thead>
                        <tr>
                            <th>Facility</th>
                            <th>State</th>
                            <th>Unit</th>
                            <th>Room</th>
                            <th>Last Name</th>
                            <th>First Name</th>
                            <th>Date of Birth</th>
                            <th>Gender</th>
                            <th>Stay Status</th>
                            <th>Payer Source</th>
                            <th>Admission Date</th>
                            <th>Hosp 12 Months</th>
                            <th>Clinical Risk Indicator</th>
                            <th>Advance Directive</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.map((row, index) => (
                            <tr key={index} className={styles.tableRow}>
                                <td>{row.facility}</td>
                                <td>{row.state}</td>
                                <td>{row.unit}</td>
                                <td>{row.room}</td>
                                <td>{row.lastName}</td>
                                <td>{row.firstName}</td>
                                <td>{row.dob}</td>
                                <td>{row.gender}</td>
                                <td>{row.stayStatus}</td>
                                <td>{row.payerSource}</td>
                                <td>{row.admissionDate}</td>
                                <td>{row.hosp12Months}</td>
                                <td>{row.clinicalRisk}</td>
                                <td>{row.advanceDirective}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>

    );
};

export default index;