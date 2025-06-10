import React from "react";
import styles from "./HexStats.module.css";

const HexStats = ({ data }) => {
  const { totalPatients = 0, schizophreniaCount = 0, otherDx = 0 } = data || {};

  return (
    <div className={styles.statsContainer}>
      <div className={styles.hexRow}>
        <div className={styles.hexTileSmall}>
          <p className={styles.hexLabel}>Patient Population</p>
          <p className={styles.hexValue}>{totalPatients}</p>
        </div>
        <div className={styles.hexTileCenter}>
          <p className={styles.hexLabel}>Schizophrenia Dx</p>
          <p className={styles.hexValue}>{schizophreniaCount}</p>
        </div>
        <div className={styles.hexTileSmall}>
          <p className={styles.hexLabel}>Other Dx</p>
          <p className={styles.hexValue}>{otherDx}</p>
        </div>
      </div>
    </div>
  );
};

export default HexStats;
