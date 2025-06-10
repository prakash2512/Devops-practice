import React, { useState, useEffect } from "react";
import {
  Modal,
  Radio,
  Select,
  Checkbox,
  Button,
  Typography,
  message,
} from "antd/lib";
import styles from "./PatientFilters.module.css";

const { Title } = Typography;
const { Option } = Select;

const PatientFilters = ({ visible, onClose, selectedName, onApply, years }) => {
  const [monthType, setMonthType] = useState(null);
  const [selectedMonths, setSelectedMonths] = useState([]);
  const [selectedRange, setSelectedRange] = useState(null);
  const [selectedYears, setSelectedYears] = useState([]);
  const [programs, setPrograms] = useState([]);
  const [isValid, setIsValid] = useState(false);

  useEffect(() => {
    // Reset all fields when modal opens
    if (visible) {
      setMonthType(null);
      setSelectedMonths([]);
      setSelectedRange(null);
      setSelectedYears([]);
      setPrograms([]);
      setIsValid(false);
    }
  }, [visible]);

  useEffect(() => {
    // Validate form whenever any field changes
    const valid =
      monthType !== null &&
      ((monthType === "range" && selectedRange !== null) ||
        (monthType === "month" && selectedMonths.length > 0)) &&
      selectedYears.length > 0 &&
      programs.length > 0;
    setIsValid(valid);
  }, [monthType, selectedRange, selectedMonths, selectedYears, programs]);

  const resetAll = () => {
    setMonthType(null);
    setSelectedMonths([]);
    setSelectedRange(null);
    setSelectedYears([]);
    setPrograms([]);
    setIsValid(false);
  };

  const handleApply = () => {
    if (!isValid) {
      message.error("Please fill all required fields");
      return;
    }

    const payload = {
      patientName: [selectedName],
      facilityName:
        localStorage.getItem("facilityName") || "REGALCARE AT TAUNTON",
      year: selectedYears[0], // Using first selected year
      programTypes: programs,
    };

    if (monthType === "range") {
      payload.getLastMonths = parseInt(selectedRange);
    } else {
      payload.months = selectedMonths.map((m) => m.toUpperCase());
    }

    onApply(payload);
  };

  const programOptions = ["CCM", "BHI", "HTR"];
  const monthOptions = [
    "JAN",
    "FEB",
    "MAR",
    "APR",
    "MAY",
    "JUN",
    "JUL",
    "AUG",
    "SEP",
    "OCT",
    "NOV",
    "DEC",
  ];
  const rangeOptions = [
    { value: "3", label: "3 Month" },
    { value: "6", label: "6 Month" },
    { value: "9", label: "9 Month" },
  ];

  return (
    <Modal
      title={<span className={styles.modalTitle}>Filters</span>}
      open={visible}
      onCancel={onClose}
      footer={null}
      width={600}
      className={styles.modalContainer}
    >
      <div className={styles.section}>
        <Title level={5} className={styles.label}>
          Preferred Month <span className={styles.required}>*</span>
        </Title>
        <div className={styles.monthRow}>
          <Radio.Group
            value={monthType}
            onChange={(e) => {
              setMonthType(e.target.value);
              if (e.target.value !== "range") {
                setSelectedRange(null);
              }
              if (e.target.value !== "month") {
                setSelectedMonths([]);
              }
            }}
          >
            <div className={styles.radioOption}>
              <Radio value="range" className={styles.radioButton} />
              <div className={styles.rangeButtons}>
                {rangeOptions.map((range) => (
                  <Button
                    key={range.value}
                    type={
                      monthType === "range" && selectedRange === range.value
                        ? "primary"
                        : "default"
                    }
                    className={styles.monthButton}
                    onClick={() => {
                      setMonthType("range");
                      setSelectedRange(range.value);
                    }}
                    disabled={monthType !== "range"}
                  >
                    {range.label}
                  </Button>
                ))}
              </div>
            </div>
            <div className={styles.radioOption}>
              <Radio value="month" className={styles.radioButton} />
              <Select
                mode="multiple"
                className={styles.dropdown}
                placeholder="Select months"
                disabled={monthType !== "month"}
                value={selectedMonths}
                onChange={setSelectedMonths}
                style={{ width: "100%" }}
              >
                {monthOptions.map((m) => (
                  <Option key={m} value={m}>
                    {m}
                  </Option>
                ))}
              </Select>
            </div>
          </Radio.Group>
        </div>
      </div>

      <div className={styles.section}>
        <Title level={5} className={styles.label}>
          Year <span className={styles.required}>*</span>
        </Title>
        <Select
          mode="multiple"
          placeholder="Select year"
          className={styles.dropdown}
          value={selectedYears}
          onChange={setSelectedYears}
          style={{ width: "100%" }}
        >
          {years &&
            years.map((y) => (
              <Option key={y} value={y}>
                {y}
              </Option>
            ))}
        </Select>
      </div>

      <div className={styles.section}>
        <Title level={5} className={styles.label}>
          Programs <span className={styles.required}>*</span>
        </Title>
        <div className={styles.checkboxGroup}>
          {programOptions.map((program) => (
            <Checkbox
              key={program}
              checked={programs.includes(program)}
              onChange={(e) => {
                const checked = e.target.checked;
                setPrograms(
                  checked
                    ? [...programs, program]
                    : programs.filter((p) => p !== program)
                );
              }}
              className={styles.checkbox}
            >
              {program}
            </Checkbox>
          ))}
        </div>
      </div>

      <div className={styles.buttonRow}>
        <Button onClick={resetAll} className={styles.resetButton}>
          Reset All
        </Button>
        <Button
          type="primary"
          className={styles.applyButton}
          onClick={handleApply}
          disabled={!isValid}
        >
          Apply
        </Button>
      </div>
    </Modal>
  );
};

export default PatientFilters;
