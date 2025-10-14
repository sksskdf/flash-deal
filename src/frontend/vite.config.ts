import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import { versionResolverPlugin } from "./vite-plugin-version-resolver";

export default defineConfig({
  plugins: [react(), versionResolverPlugin()],
  resolve: {
    alias: {},
  },
  server: {
    port: 5173,
    host: true,
  },
});
