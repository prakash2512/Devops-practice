import React, { useState, useRef, useCallback } from "react";
import { ComposableMap, Geographies, Geography } from "react-simple-maps";
import styles from "./USMap.module.css";

const geoUrl = "https://cdn.jsdelivr.net/npm/us-atlas@3/states-10m.json";

const USMap = ({ highlightedState, highlightColor, defaultColor }) => {
  const [tooltip, setTooltip] = useState({
    content: "",
    x: 0,
    y: 0,
    visible: false,
  });
  const mapRef = useRef(null);

  const handleMouseMove = useCallback((e, stateName) => {
    if (!mapRef.current) return;

    const bounds = mapRef.current.getBoundingClientRect();
    setTooltip({
      content: stateName,
      x: e.clientX - bounds.left,
      y: e.clientY - bounds.top,
      visible: true,
    });
  }, []);

  const handleMouseLeave = useCallback(() => {
    setTooltip((prev) => ({ ...prev, visible: false }));
  }, []);

  const renderGeography = useCallback(
    (geo) => {
      const stateName = geo.properties.name;
      const isHighlighted =
        highlightedState &&
        stateName.toLowerCase() === highlightedState.toLowerCase();

      return (
        <Geography
          key={geo.rsmKey}
          geography={geo}
          fill={isHighlighted ? highlightColor : defaultColor}
          stroke="#999"
          onMouseMove={(e) => handleMouseMove(e, stateName)}
          onMouseLeave={handleMouseLeave}
          style={{
            default: { outline: "none" },
            hover: { outline: "none", cursor: "pointer" },
            pressed: { outline: "none" },
          }}
        />
      );
    },
    [
      highlightColor,
      defaultColor,
      highlightedState,
      handleMouseMove,
      handleMouseLeave,
    ]
  );

  return (
    <div className={styles.wrapper} ref={mapRef}>
      <h3 className={styles.title}>{highlightedState}</h3>

      {/* {tooltip.visible && ( */}
      <div
        className={styles.tooltip}
        style={{
          left: tooltip.x,
          top: tooltip.y,
          opacity: tooltip.visible ? 1 : 0,
          visibility: tooltip.visible ? "visible" : "hidden",
        }}
      >
        {tooltip.content}
      </div>

      <ComposableMap
        projection="geoAlbersUsa"
        width={800}
        height={500}
        style={{ width: "100%", height: "auto" }}
      >
        <Geographies geography={geoUrl}>
          {({ geographies }) => geographies.map(renderGeography)}
        </Geographies>
      </ComposableMap>
    </div>
  );
};

export default USMap;
