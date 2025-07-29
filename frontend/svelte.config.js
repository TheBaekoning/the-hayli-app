import adapter from '@sveltejs/adapter-node';
import staticAdapter from '@sveltejs/adapter-static';
import { sveltePreprocess } from 'svelte-preprocess';

/** @type {import('@sveltejs/kit').Config} */
const config = {
	kit: {
		// adapter-auto only supports some environments, see https://kit.svelte.dev/docs/adapter-auto for a list.
		// If your environment is not supported or you settled on a specific environment, switch out the adapter.
		// See https://kit.svelte.dev/docs/adapters for more information about adapters.
		adapter: process.env.VITE_SSR === "true" ? adapter() : staticAdapter({
			fallback: 'index.html'
		}),
		alias: {
			$components: './src/components',
			$sass: './src/sass',
		},
	},
	preprocess: [
		//vitePreprocess(),
		sveltePreprocess(),
	],
};

export default config;
