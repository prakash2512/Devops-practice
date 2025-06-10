import React from 'react';
import styles from './table.module.css';

function TableComponent({ tableData }) {
    // Get the headers dynamically by extracting the keys from the first object of the data array
    const headers = Object.keys(tableData[0]);

    return (
        <div className={styles.tableWrapper}>
            <table className={styles.table}>
                <thead>
                    <tr className={styles.tableRow} >
                        {/* Render headers dynamically */}
                        {headers.map((header) => (
                            <th key={header}>{header.replace(/([A-Z])/g, ' $1').toUpperCase()}</th>
                        ))}
                    </tr>
                </thead>
                <tbody>
                    {/* Render rows dynamically */}
                    {tableData.map((row, index) => (
                        <tr key={index}>
                            {headers.map((header) => (
                                <td key={header}>{row[header]}</td>
                            ))}
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}


export default TableComponent;
