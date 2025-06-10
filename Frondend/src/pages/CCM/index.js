import { useRouter } from "next/router";
import { useEffect } from "react";

const Ccm = () => {
  const router = useRouter();
  useEffect(() => {
    router.push("/CCM/population");
  });
}
export default Ccm;