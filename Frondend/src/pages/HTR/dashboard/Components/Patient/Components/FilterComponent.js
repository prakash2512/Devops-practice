import React, { useState } from 'react';
import styles from './filter.module.css';

function FilterComponent({
    filterKeys,
    payload,
    handleAgeClick,
    handlePatientNameClick,
    handleProviderClick,
    handleStatusClick }) {

    return (
        <div className={styles.appContainer}>
            {/* Age Group */}
            <div className={styles.sectionContainer}>
                <div className={styles.sectionTitle}>AGE GROUP</div>
                <div
                    className={`${styles.optionContainer} ${payload.age === '0-18' ? styles.selected : ''}`}
                    onClick={() => handleAgeClick('0-18')}
                >
                    0-18
                </div>
                <div
                    className={`${styles.optionContainer} ${payload.age === '19-40' ? styles.selected : ''}`}
                    onClick={() => handleAgeClick('19-40')}
                >
                    19-40
                </div>
            </div>

            {/* Status */}
            <div className={styles.sectionContainer}>
                <div className={styles.sectionTitle}>STATUS</div>
                {filterKeys?.statusList?.map((data, index) => (
                    <div
                        key={index}
                        className={`${styles.optionContainer} ${payload.htrStatus === data ? styles.selected : ''}`}
                        onClick={() => handleStatusClick(data)}
                    >
                        {data}
                    </div>
                ))}
            </div>

            {/* Patient Name (Multi-select) */}
            <div className={styles.sectionContainer}>
                <div className={styles.sectionTitle}>PATIENT NAME</div>
                {filterKeys?.patientList?.map((data, index) => (
                    <div key={index} className={styles.optionContainer}>
                        <input
                            type="checkbox"
                            checked={payload.patientName.includes(data)}
                            onChange={() => handlePatientNameClick(data)}
                        />
                        {data}
                    </div>
                ))}
            </div>

            {/* Provider */}
            <div className={styles.sectionContainer}>
                <div className={styles.sectionTitle}>PROVIDER</div>
                {filterKeys?.providerList?.map((data, index) => (
                    <div
                        key={index}
                        className={`${styles.optionContainer} ${payload.htrProvider === data ? styles.selected : ''}`}
                        onClick={() => handleProviderClick(data)}
                    >
                        {data}
                    </div>
                ))}
            </div>
        </div>
    );
}

export default FilterComponent;
