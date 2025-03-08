import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";
import { useNavigate } from "react-router-dom";

export default function HomePage() {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-background text-foreground">
      <div className="container py-12">
        <h1 className="text-4xl font-bold text-center mb-4 text-dark-primary">
          Welcome to SecureVPN
        </h1>
        <p className="text-center text-dark-secondary mb-8">
          Your ultimate solution for secure and private internet browsing. Explore our features and get started today!
        </p>

        {/* Features Section */}
        <h2 className="text-3xl font-bold text-center mb-8 text-dark-primary">
          Features
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <Card className="bg-dark-secondary">
            <CardHeader>
              <CardTitle className="text-dark-foreground">Secure Browsing</CardTitle>
              <CardDescription className="text-dark-accent">
                Encrypt your internet traffic and protect your data.
              </CardDescription>
            </CardHeader>
          </Card>
          <Card className="bg-dark-secondary">
            <CardHeader>
              <CardTitle className="text-dark-foreground">Multiple Plans</CardTitle>
              <CardDescription className="text-dark-accent">
                Choose from flexible subscription plans.
              </CardDescription>
            </CardHeader>
          </Card>
          <Card className="bg-dark-secondary">
            <CardHeader>
              <CardTitle className="text-dark-foreground">User Activity Tracking</CardTitle>
              <CardDescription className="text-dark-accent">
                Monitor user activity in real-time.
              </CardDescription>
            </CardHeader>
          </Card>
        </div>

        {/* Call to Action */}
        <div className="text-center mt-12">
          <Button
            onClick={() => navigate("/register")}
            className="bg-dark-primary hover:bg-dark-secondary"
          >
            Get Started
          </Button>
        </div>
      </div>
    </div>
  );
}