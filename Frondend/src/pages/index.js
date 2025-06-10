import { useRouter } from "next/router";
import { useEffect } from "react";

const Component = () => {
  const router = useRouter();
  useEffect(() => {
    router.push("/login");
  });
};

export default Component;
