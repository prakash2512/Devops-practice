import React, { useEffect, useState } from "react";
import { Row, Col, Input, Select, Pagination, Card, Spin, Empty } from "antd/lib";
import { connect } from "react-redux";
import { useRouter } from "next/router";
import Style from "./style.module.css";
import { actions as facilitiesActions } from "../../store/facilities-list";

const { Option } = Select;
const { Meta } = Card;

const FacilityList = ({ facilitiesData, FacilitiesFlow }) => {
  const router = useRouter();

  const [search, setSearch] = useState("");
  const [selectedState, setSelectedState] = useState("");
  const [selectedCoordinator, setSelectedCareCoordinator] = useState("");
  const [filteredList, setFilteredList] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(50);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    facilitiesData().finally(() => setLoading(false));
  }, [facilitiesData]);

  useEffect(() => {
    if (FacilitiesFlow && FacilitiesFlow.response) {
      let temp = FacilitiesFlow.response.filter((facility) => {
        const matchesSearch = facility.facilityName
          .toLowerCase()
          .includes(search.toLowerCase());
        const matchesState = selectedState
          ? facility.state === selectedState
          : true;
        const matchesCoordinator = selectedCoordinator
          ? facility.coordinator === selectedCoordinator
          : true;
        return matchesSearch && matchesState && matchesCoordinator;
      });
      setFilteredList(temp);
      setCurrentPage(1);
    }
  }, [search, selectedState, selectedCoordinator, FacilitiesFlow]);

  const states =
    FacilitiesFlow && FacilitiesFlow.response
      ? [...new Set(FacilitiesFlow.response.map((facility) => facility.state))]
      : [];

  const careCoordinators =
    FacilitiesFlow && FacilitiesFlow.response
      ? [
          ...new Set(
            FacilitiesFlow.response.map((facility) => facility.coordinator)
          ),
        ]
      : [];

  const handleFacilityClick = (facility) => {
    localStorage.setItem("facilityName", facility.facilityName);
    router.push("/programs");
  };

  const handlePageChange = (page, size) => {
    setCurrentPage(page);
    setPageSize(size);
  };

  const paginatedFacilities = filteredList.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize
  );

  return (
    <div className={Style.container}>
      <h2 className={Style.heading}>Facilities Reports</h2>
      <div className={Style.filters}>
        {/* Search Input */}
        <Input
          placeholder="Search facilities..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className={Style.searchBar}
          allowClear
        />
        <Select
          placeholder="Select State"
          value={selectedState}
          onChange={(value) => setSelectedState(value)}
          className={Style.stateFilter}
          allowClear
        >
          <Option value="">All States</Option>
          {states.map((state) => (
            <Option key={state} value={state}>
              {state}
            </Option>
          ))}
        </Select>

        {/* Care Coordinator Filter */}
        <Select
          placeholder="Select Care Coordinator"
          value={selectedCoordinator}
          onChange={(value) => setSelectedCareCoordinator(value)}
          className={Style.stateFilter}
          allowClear
        >
          <Option value="">All Coordinators</Option>
          {careCoordinators.map((coordinator) => (
            <Option key={coordinator} value={coordinator}>
              {coordinator}
            </Option>
          ))}
        </Select>
      </div>

      {/* Pagination at the Top */}
      <div className={Style.paginationTop}>
        <Pagination
          current={currentPage}
          pageSize={pageSize}
          total={filteredList.length}
          onChange={handlePageChange}
          showSizeChanger
          onShowSizeChange={handlePageChange}
        />
      </div>

      {/* Loading Spinner */}
      <Spin spinning={loading} size="large">
        {/* Facility Cards */}
        {paginatedFacilities.length > 0 ? (
          <Row gutter={[16, 16]} className={Style.grid}>
            {paginatedFacilities.map((facility) => (
              <Col xs={24} sm={12} md={8} lg={6} key={facility.facilityName}>
                <Card
                  hoverable
                  className={Style.facilityCard}
                  onClick={() => handleFacilityClick(facility)}
                >
                  <Meta
                    title={
                      <div className={Style.facilityName}>
                        {facility.facilityName}
                      </div>
                    }
                    description={
                      <div>
                        <p className={Style.facilityState}>
                          State: {facility.state}
                        </p>
                        <p className={Style.facilityCoordinator}>
                          Coordinator: {facility.coordinator}
                        </p>
                      </div>
                    }
                  />
                </Card>
              </Col>
            ))}
          </Row>
        ) : (
          <Empty
            description={
              <span>{loading ? "Loading..." : "No facilities found"}</span>
            }
            className={Style.noData}
          />
        )}
      </Spin>

      {/* Pagination at the Bottom */}
      <div className={Style.paginationBottom}>
        <Pagination
          current={currentPage}
          pageSize={pageSize}
          total={filteredList.length}
          onChange={handlePageChange}
          showSizeChanger
          onShowSizeChange={handlePageChange}
        />
      </div>
    </div>
  );
};

const enhancer = connect(
  (state) => ({
    FacilitiesFlow: state.facilities.facilitiesList.data,
  }),
  {
    facilitiesData: facilitiesActions.facilitiesAction,
  }
);

export default enhancer(FacilityList);
