import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import { versionResolverPlugin } from "./vite-plugin-version-resolver";
import path from "path";

export default defineConfig({
  plugins: [react(), versionResolverPlugin()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./"),
    },
  },
  server: {
    port: 5173,
    host: true,
  },
  optimizeDeps: {
    include: ["react", "react-dom", "react-router-dom"],
  },
});
