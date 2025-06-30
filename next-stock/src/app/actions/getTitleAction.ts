'use server'

export async function getTitle() {
  const servers = [
    "Apache",
    "Nginx",
    "IIS",
    "LiteSpeed",
    "GWS",
    "Tomcat",
    "Caddy",
    "Node.js",
    "OpenResty",
    "Cloudflare"
  ];

  const randomServer = servers[Math.floor(Math.random() * servers.length)];
  const randomTitle = `Wall Street ${randomServer} Action Price Graph`;

  return randomTitle;
}