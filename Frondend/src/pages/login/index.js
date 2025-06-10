import React, { useEffect } from "react";
import { actions as dashboardActions } from "../../store/home";
import { connect, useSelector } from "react-redux";
import logo from "../../../public/assets/images/logo.png";
import Image from "next/image";
import { Form, Input, Button } from "antd/lib";
import { useRouter } from 'next/router';

const Login = ({ login,loginAPI }) => {
  const router = useRouter();

  const onFinish = (values) => {
    router.push("/facilities-list");
  };

  return (
    <div
      className="container d-flex align-items-center justify-content-center"
      style={{ height: "100vh" }}
    >
      <div className="row">
        <div className="col-12 col-md-6 d-none d-md-flex justify-content-center align-items-center">
          <Image
            src={logo}
            alt="Login Banner"
            className="img-fluid"
            width={500}
            height={500}
          />
        </div>

        <div className="col-12 col-md-6 d-flex flex-column justify-content-center p-4">
          <h2 className="text-center mb-4">Welcome Back!</h2>
          <Form
            name="login"
            layout="vertical"
            onFinish={onFinish}
            initialValues={{ remember: true }}
          >
            <Form.Item
              label="Email"
              name="email"
              rules={[{ required: true, message: "Please enter your email!" }]}
            >
              <Input
                type="email"
                placeholder="healthmedpro@gmail...."
                style={{ height: "45px" }}
              />
            </Form.Item>

            <Form.Item
              label="Password"
              name="password"
              rules={[{ required: true, message: "Please enter your password!" }]}
            >
              <Input.Password
                placeholder="*****"
                style={{ height: "45px" }}
              />
            </Form.Item>

            <Form.Item className="mt-5 d-flex justify-content-center">
              <Button
                type="primary"
                htmlType="submit"
                onClick={onFinish}
                style={{ background: "#d241aec9" }}
              >
                Login
              </Button>
            </Form.Item>
          </Form>
        </div>
      </div>
    </div>
  );
};

// const enhancer = connect( 
//   (state) => ({
//     login:console.log("state",state)

//   }),
//   {
//     loginAPI:dashboardActions.loginAction
//   }
// );

const enhancer = connect((state) => ({
  login: state
}), {
  loginAPI: dashboardActions.loginAction
})

export default enhancer(Login);
