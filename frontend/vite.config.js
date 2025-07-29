import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vite';

export const config = {
	plugins: [sveltekit()],
	server: {
		port: 4200
	}
}

export default defineConfig(config);