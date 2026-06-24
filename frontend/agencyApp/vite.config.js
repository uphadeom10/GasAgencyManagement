import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import dotenv from 'dotenv';
dotenv.config({
  path: './.env',
});
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:7072',
        changeOrigin: true,
      },
      '/masters': {
        target: 'http://localhost:7072',
        changeOrigin: true,
      },
      '/inventory': {
        target: 'http://localhost:7072',
        changeOrigin: true,
      },
      '/dailyAssignment': {
        target: 'http://localhost:7072',
        changeOrigin: true,
      }
    }
  },
});