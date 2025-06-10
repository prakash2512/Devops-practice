import React, { useEffect, useMemo, useState } from "react";
import dynamic from "next/dynamic";
import BhiLayout from "@/components/bhiLayout";
import DonutChart from "./DonutChart";
import BarChart from "./BarChart";
import HexStats from "./HexStats";
import StrLtrComparison from "./StrLtrComparison";
import styles from "./index.module.css";
import { connect } from "react-redux";
import { actions as bhiReportsActions } from "../../../store/BHI";

const USMap = dynamic(() => import("./UsMap"), { ssr: false });

const BhiHome = ({ bhiReportsAPI, bhiReportsFlow }) => {
  const [payload, setPayload] = useState(null);

  useEffect(() => {
    const getPayload = localStorage.getItem("payload");
    const parsedPayload = getPayload ? JSON.parse(getPayload) : null;
    setPayload(parsedPayload);

    if (parsedPayload) {
      bhiReportsAPI(parsedPayload);
    }
  }, [bhiReportsAPI]);

  console.log("bhi", bhiReportsFlow);

  return (
    <BhiLayout>
      <div className={styles.mainContainer}>
        {/* Top Row */}
        <div className={styles.topRow}>
          <div className={styles.donutContainer}>
          <DonutChart data={bhiReportsFlow?.response?.dxPercentage} />
          </div>
          <div className={styles.hexStatsContainer}>
          <HexStats data={bhiReportsFlow?.response} />
          </div>
        </div>

        {/* Bottom Row */}
        <div className={styles.bottomRow}>
          <div className={styles.barContainer}>
          <BarChart data={bhiReportsFlow?.response?.dxCounts} />
          </div>
          <div className={styles.strLtrContainer}>
          <StrLtrComparison data={bhiReportsFlow?.response?.shortAndLongData?.[0]} />
          </div>
          <div className={styles.mapContainer}>
            <USMap
              highlightedState={
                bhiReportsFlow?.response?.shortAndLongData?.[0]?.state
              }
              highlightColor="#B164A0"
              defaultColor="#30243f"
            />
          </div>
        </div>
      </div>
    </BhiLayout>
  );
};

const enhancer = connect(
  (state) => ({
    bhiReportsFlow: state.bhiReports.bhiHomeFlow.data,
  }),
  {
    bhiReportsAPI: bhiReportsActions.bhiHomeAction,
  }
);
export default enhancer(BhiHome);
