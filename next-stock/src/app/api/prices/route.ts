import { NextResponse } from "next/server";

export async function GET() {
  const mockPrices = Array.from({ length: 20 }, () => Math.random() * 100);
  return NextResponse.json(mockPrices);
}