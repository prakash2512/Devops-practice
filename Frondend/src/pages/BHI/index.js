import { useRouter } from "next/router";
import { useEffect } from "react";

const Bhi = () => {
  const router = useRouter();
  useEffect(() => {
    router.push("/BHI/home");
  });
}
export default Bhi;