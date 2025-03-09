import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { connectToVpn, checkSubscription } from "@/services/api";

export default function UserDashboard() {
  const [isSubscribed, setIsSubscribed] = useState(false);
  const [vpnStatus, setVpnStatus] = useState("Disconnected");
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const fetchSubscription = async () => {
      try {
        const response = await checkSubscription();
        setIsSubscribed(response.data.isActive);
        if (!response.data.isActive) {
          navigate("/payment");
        }
      } catch (err) {
        setError(`Failed to fetch subscription details.${err}`);
      } finally {
        setIsLoading(false);
      }
    };
    fetchSubscription();
  }, [navigate]);

  const handleConnect = async () => {
    if (!isSubscribed) {
      alert("Please subscribe to a plan to use VPN.");
      return;
    }
    try {
      const response = await connectToVpn();
      setVpnStatus(response.data.status);
    } catch (err) {
      setError(`Failed to connect to VPN.${err}`);
    }
  };

  if (isLoading) {
    return <div className="min-h-screen flex items-center justify-center text-white">Loading...</div>;
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-r from-blue-900 to-blue-600 p-6">
      <Card className="w-full max-w-lg bg-gray-900 shadow-2xl border border-gray-700 rounded-lg">
        <CardHeader>
          <CardTitle className="text-white text-center text-2xl font-semibold">CloudVPN - Secure Browsing</CardTitle>
          <p className="text-gray-400 text-center text-sm">Powered by AWS</p>
        </CardHeader>
        <CardContent>
          {error && <p className="text-red-500 text-center mb-4 font-medium">{error}</p>}
          <div className="text-center text-white text-lg mb-4">VPN Status: {vpnStatus}</div>
          <Button
            onClick={handleConnect}
            disabled={!isSubscribed}
            className="w-full bg-blue-600 hover:bg-blue-500 text-white py-2 rounded-lg font-semibold shadow-md"
          >
            Connect to VPN
          </Button>
          {!isSubscribed && (
            <p className="text-gray-400 text-sm text-center mt-4">
              You are not subscribed to any plan. <span className="text-blue-400 hover:underline cursor-pointer" onClick={() => navigate("/payment")} >Subscribe here</span>
            </p>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
