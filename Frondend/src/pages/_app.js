import "../styles/globals.css";
import "bootstrap/dist/css/bootstrap.css";
import { wrapper, store } from "../store/index";
import { Provider } from "react-redux";

function MyApp({ Component, pageProps }) {
  return (
      <Provider store={store}>
        <Component {...pageProps} />
      </Provider>
   
  );
}

export default wrapper.withRedux(MyApp);
