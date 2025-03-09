import axios from "axios";
import Cookies from "js-cookie";

const API_URL = "http://localhost:8080/api";

interface LoginResponse {
  token: string;
  role: string;
  username: string;
}

interface SubscriptionResponse {
  isActive: boolean;
}

interface User {
  id: number;
  email: string;
  role: string;
  subscription?: {
    planType?: string;
  };
}

// Login user
export const loginUser = async (username: string, password: string): Promise<void> => {
  try {
    const response = await axios.post<LoginResponse>(`${API_URL}/auth/login`, null, {
      params: { username, password },
    });

    // Store JWT & Role in Cookies
    Cookies.set("token", response.data.token, { expires: 1 });
    Cookies.set("role", response.data.role, { expires: 1 });
    Cookies.set("username", response.data.username, { expires: 1 });

  } catch (err: unknown) {
    if (axios.isAxiosError(err)) {
      throw new Error(err.response?.data || "Login failed.");
    }
    throw new Error("An unexpected error occurred.");
  }
};

// Register user
export const registerUser = async (username: string, email: string, password: string): Promise<User> => {
  try {
    const response = await axios.post<User>(`${API_URL}/auth/register`, null, {
      params: { username, email, password },
    });
    return response.data;
  } catch (err: unknown) {
    if (axios.isAxiosError(err)) {
      throw new Error(err.response?.data || "Registration failed.");
    }
    throw new Error("An unexpected error occurred.");
  }
};

// Connect to VPN
export const connectToVpn = async (): Promise<{ status: string }> => {
  const response = await axios.post<{ status: string }>(
    `${API_URL}/vpn/connect`,
    {},
    { headers: { Authorization: `Bearer ${Cookies.get("token")}` } }
  );
  return response.data;
};

// Check Subscription
export const checkSubscription = async (): Promise<SubscriptionResponse> => {
  const response = await axios.get<SubscriptionResponse>(`${API_URL}/subscription`, {
    headers: { Authorization: `Bearer ${Cookies.get("token")}` },
  });
  return response.data;
};

// Get all users (Admin)
export const getAllUsers = async (): Promise<User[]> => {
  const response = await axios.get<User[]>(`${API_URL}/admin/users`, {
    headers: { Authorization: `Bearer ${Cookies.get("token")}` },
  });
  return response.data;
};

// Subscribe to a plan
export const subscribeToPlan = async (
  plan: "FREE" | "MONTHLY" | "YEARLY",
  amount?: number,
  currency?: string,
  receipt?: string,
  paymentId?: string
): Promise<void> => {
  await axios.post(
    `${API_URL}/subscription/subscribe`,
    { plan, amount, currency, receipt, paymentId },
    {
      headers: { Authorization: `Bearer ${Cookies.get("token")}` },
    }
  );
};
