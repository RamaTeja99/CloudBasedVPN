import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

export const loginUser = async (credentials: { email: string; password: string }) => {
  return axios.post(`${API_URL}/auth/login`, credentials);
};

export const registerUser = async (credentials: { email: string; password: string }) => {
  return axios.post(`${API_URL}/auth/register`, credentials);
};

export const connectToVpn = async () => {
  return axios.post(`${API_URL}/vpn/connect`, {}, {
    headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
  });
};

export const checkSubscription = async () => {
  return axios.get(`${API_URL}/subscription`, {
    headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
  });
};

export const getAllUsers = async () => {
  return axios.get(`${API_URL}/admin/users`, {
    headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
  });
};
export const subscribeToPlan = async (
  plan: 'FREE' | 'MONTHLY' | 'YEARLY',
  amount?: number,
  currency?: string,
  receipt?: string,
  paymentId?: string
) => {
  return axios.post(
    `${API_URL}/subscription/subscribe`,
    { plan, amount, currency, receipt, paymentId },
    { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } }
  );
};