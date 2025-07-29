import { sequence } from '@sveltejs/kit/hooks';

/** @type {import('@sveltejs/kit').Handle} */
async function jsMIME({ event, resolve }){
	const response = await resolve(event);
	if (event.url.pathname.includes('service-worker')){
		response.headers.set('Content-Type', 'application/javascript');
	}
	return response;
}

export const handle = sequence(jsMIME);