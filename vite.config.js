import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Configuración lista para Vercel (build estático) y para previsualizar en cualquier host.
export default defineConfig({
  plugins: [react()],
  server: {
    host: true,
    port: 5173,
    strictPort: true,
    // Necesario para servir la app dentro de previews alojados (hosts dinámicos).
    allowedHosts: true,
  },
  preview: {
    host: true,
    port: 4173,
    strictPort: true,
    allowedHosts: true,
  },
  build: {
    outDir: 'dist',
    sourcemap: false,
    chunkSizeWarningLimit: 900,
  },
});
