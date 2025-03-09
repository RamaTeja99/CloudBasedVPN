import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { subscribeToPlan } from "@/services/api";

export default function PaymentPage() {
  const [selectedPlan, setSelectedPlan] = useState<"FREE" | "MONTHLY" | "YEARLY" | null>(null);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleSubscribe = async () => {
    if (!selectedPlan) {
      setError("Please select a plan.");
      return;
    }

    try {
      await subscribeToPlan(selectedPlan);
      navigate("/dashboard");
    } catch (err) {
      setError(`Failed to subscribe. Please try again.${err}`);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-r from-blue-900 to-blue-600 p-6">
      <Card className="w-full max-w-xl bg-gray-900 shadow-2xl border border-gray-700 rounded-lg">
        <CardHeader>
          <CardTitle className="text-white text-center text-2xl font-semibold">Choose Your Plan</CardTitle>
          <CardDescription className="text-gray-400 text-center">Select a plan to get started with CloudVPN</CardDescription>
        </CardHeader>
        <CardContent>
          {error && <p className="text-red-500 text-center mb-4 font-medium">{error}</p>}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {["FREE", "MONTHLY", "YEARLY"].map((plan) => (
              <Card
                key={plan}
                className={`p-4 cursor-pointer transition-all duration-300 ${
                  selectedPlan === plan ? "border-2 border-blue-500" : "border-gray-700"
                } bg-gray-800 hover:bg-gray-700`}
                onClick={() => setSelectedPlan(plan as "FREE" | "MONTHLY" | "YEARLY")}
              >
                <CardHeader className="text-center">
                  <CardTitle className="text-white">
                    {plan === "FREE" ? "Free Trial" : plan === "MONTHLY" ? "Monthly Plan" : "Yearly Plan"}
                  </CardTitle>
                  <CardDescription className="text-gray-400">
                    {plan === "FREE" ? "7 Days Free" : plan === "MONTHLY" ? "₹49/month" : "₹999/year"}
                  </CardDescription>
                </CardHeader>
              </Card>
            ))}
          </div>
          <Button
            onClick={handleSubscribe}
            className="mt-6 w-full bg-blue-600 hover:bg-blue-500 text-white py-2 rounded-lg font-semibold shadow-md"
            disabled={!selectedPlan}
          >
            Subscribe Now
          </Button>
        </CardContent>
      </Card>
    </div>
  );
}
