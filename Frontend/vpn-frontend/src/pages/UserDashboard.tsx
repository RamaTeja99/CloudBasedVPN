import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
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
        setError("Failed to fetch subscription details.");
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
      setError("Failed to connect to VPN.");
    }
  };

  if (isLoading) {
    return <div className="text-center">Loading...</div>;
  }

  return (
    <div className="min-h-screen bg-background text-foreground">
      <div className="container py-12">
        <h1 className="text-3xl font-bold mb-8 text-dark-primary">User Dashboard</h1>
        {error && <p className="text-red-500 mb-4">{error}</p>}
        <Card className="bg-dark-secondary">
          <CardHeader>
            <CardTitle className="text-dark-foreground">VPN Status: {vpnStatus}</CardTitle>
          </CardHeader>
          <CardContent>
            <Button
              onClick={handleConnect}
              disabled={!isSubscribed}
              className="bg-dark-primary hover:bg-dark-secondary"
            >
              Connect to VPN
            </Button>
            {!isSubscribed && (
              <p className="text-dark-accent mt-4">
                You are not subscribed to any plan. Please subscribe to use VPN.
              </p>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}