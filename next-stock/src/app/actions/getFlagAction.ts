'use server'

export async function getFlag(password: string) {
  if (password !== "PASSW0RD") {
    return "Invalid password";
  }
  return process.env["THE_FLAG"] || "Default TS Flag";
}
