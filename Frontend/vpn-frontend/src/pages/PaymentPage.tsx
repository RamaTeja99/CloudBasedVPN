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
      setError("Failed to subscribe. Please try again.");
    }
  };

  return (
    <div className="min-h-screen bg-background text-foreground">
      <div className="container py-12">
        <h1 className="text-3xl font-bold mb-8 text-dark-primary">Select a Payment Plan</h1>
        {error && <p className="text-red-500 mb-4">{error}</p>}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <Card
            className={`bg-dark-secondary cursor-pointer ${
              selectedPlan === "FREE" ? "border-dark-primary" : ""
            }`}
            onClick={() => setSelectedPlan("FREE")}
          >
            <CardHeader>
              <CardTitle className="text-dark-foreground">Free Plan</CardTitle>
              <CardDescription className="text-dark-accent">
                7 Days Free Trial
              </CardDescription>
            </CardHeader>
          </Card>
          <Card
            className={`bg-dark-secondary cursor-pointer ${
              selectedPlan === "MONTHLY" ? "border-dark-primary" : ""
            }`}
            onClick={() => setSelectedPlan("MONTHLY")}
          >
            <CardHeader>
              <CardTitle className="text-dark-foreground">Monthly Plan</CardTitle>
              <CardDescription className="text-dark-accent">
                ₹49/month
              </CardDescription>
            </CardHeader>
          </Card>
          <Card
            className={`bg-dark-secondary cursor-pointer ${
              selectedPlan === "YEARLY" ? "border-dark-primary" : ""
            }`}
            onClick={() => setSelectedPlan("YEARLY")}
          >
            <CardHeader>
              <CardTitle className="text-dark-foreground">Yearly Plan</CardTitle>
              <CardDescription className="text-dark-accent">
                ₹999/year
              </CardDescription>
            </CardHeader>
          </Card>
        </div>
        <Button
          onClick={handleSubscribe}
          className="mt-4 bg-dark-primary hover:bg-dark-secondary"
          disabled={!selectedPlan}
        >
          Subscribe
        </Button>
      </div>
    </div>
  );
}