"use client";

import { useEffect, useState } from "react";
import { Line } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

export default function ClientGraph() {
  const [prices, setPrices] = useState([]);

  useEffect(() => {
    const fetchPrices = async () => {
      try {
        const response = await fetch("/api/prices");
        const data = await response.json();
        setPrices(data);
      } catch (error) {
        console.error("Failed to fetch prices:", error);
      }
    };

    if (prices.length === 0) {
      fetchPrices();
    }
  }, [prices]);

  const data = {
    labels: prices.map((_, index) => `Point ${index + 1}`),
    datasets: [
      {
        label: "Stock Prices",
        data: prices,
        borderColor: "#007bff",
        backgroundColor: "rgba(0, 123, 255, 0.2)",
      },
    ],
  };

  return <Line data={data} />;
}