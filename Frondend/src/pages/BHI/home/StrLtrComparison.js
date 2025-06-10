import React from "react";
import styles from "./StrLtrComparison.module.css";

const formatPercent = (value) => `${(parseFloat(value) * 100).toFixed(1)}%`;

const StrLtrComparison = ({ data }) => {
  const {
    shortNationAverage,
    shortStateAverage,
    shortAverage,
    longNationAverage,
    longStateAverage,
    longAverage,
  } = data || {};

  return (
    <div className={styles.comparisonContainer}>
      <div className={styles.strLtrWrapper}>
        {/* STR */}
        <div className={styles.strLtrBlock}>
          <div className={styles.strLtrTag}>STR</div>
          <div className={styles.hexGroup}>
            <div className={`${styles.hexagon} ${styles.nationalHex}`}>
              <div className={styles.hexContent}>
                <p className={styles.percent}>
                  {formatPercent(shortNationAverage)}
                </p>
                <p className={styles.label}>National</p>
              </div>
            </div>

            <div className={styles.commonDiamond}>
              <p className={styles.commonPercent}>
                {formatPercent(shortAverage)}
              </p>
            </div>

            <div className={`${styles.hexagon} ${styles.stateHex}`}>
              <div className={styles.hexContent}>
                <p className={styles.percent}>
                  {formatPercent(shortStateAverage)}
                </p>
                <p className={styles.label}>State</p>
              </div>
            </div>
          </div>
        </div>

        {/* LTR */}
        <div className={styles.strLtrBlock}>
          <div className={styles.strLtrTag}>LTR</div>
          <div className={styles.hexGroup}>
            <div className={`${styles.hexagon} ${styles.nationalHex}`}>
              <div className={styles.hexContent}>
                <p className={styles.percent}>
                  {formatPercent(longNationAverage)}
                </p>
                <p className={styles.label}>National</p>
              </div>
            </div>

            <div className={styles.commonDiamond}>
              <p className={styles.commonPercent}>
                {formatPercent(longAverage)}
              </p>
            </div>

            <div className={`${styles.hexagon} ${styles.stateHex}`}>
              <div className={styles.hexContent}>
                <p className={styles.percent}>
                  {formatPercent(longStateAverage)}
                </p>
                <p className={styles.label}>State</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default StrLtrComparison;
