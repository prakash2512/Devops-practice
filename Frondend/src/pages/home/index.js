import React, { useEffect } from 'react'
import { actions as dashbaordActions } from "../../store/home";
import {connect} from "react-redux";

const Home = ({workFlowData, WorlFlow,loginAPI,}) => {
    console.log(WorlFlow);

    useEffect(() => {
        workFlowData()
    }, [])
  return (
    <div>Home</div>
  )
}
const enhancer = connect(
    (state) => ({
      login:state?.data
    }),
    {
      loginAPI:dashbaordActions.loginAction
    }
  );
export default enhancer(Home)