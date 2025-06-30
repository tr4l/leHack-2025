'use server'

export async function getSymbolFor(name: string) {
  return Symbol.for(name);
}