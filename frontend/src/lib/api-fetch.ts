import { goto } from '$app/navigation';
import { browser } from '$app/environment';
import { redirect } from '@sveltejs/kit';
import type { ISO8601 } from '$lib';

export const fakeURLBase = 'https://some.fake.base.url';

export type CSRF = {
	parameterName: string;
	headerName: string;
	token: string;
};

export function csrfToken(forceNewToken?: boolean): Promise<CSRF|false>{
	if (!browser) { console.error('csrfToken attempted on server, but only allowed on client'); }
	const token = window.sessionStorage.getItem('csrfToken');
	if (token && !forceNewToken) {
		try{
			const csrfObj: CSRF = JSON.parse(token);
			return Promise.resolve(csrfObj);
		} catch (e) {
			console.error(e);
		}
	}
	return fetch(import.meta.env.VITE_API_URL + '/csrf', {
		mode: 'cors',
		credentials: 'include'
	}).then(res => {
		if (!res.ok) return false;
		return res.json().then((json: CSRF) => {
			window.sessionStorage.setItem('csrfToken', JSON.stringify(json));
			return json;
		})
	});
}

/**
 * Fetches data from the API.
 * @param {Object} args
 * @param {String|URL} args.url - The URL to fetch the data from.
 * @param {Object} args.options - The fetch options to use.
 * @param {?Function} args.fetch - The fetch function to use. Defaults to the global fetch function.
 * @returns {Promise<Response>} - A promise that resolves to the response from the server or redirects to unauthed route if 403 is returned from server.
 */
export async function apiFetch({url, options = {}, fetch: fetchFn = fetch, _tryNewCSRF = false}: {url: string | URL, options?: RequestInit, fetch?: typeof fetch, _tryNewCSRF?: boolean}): Promise<Response> {
	if (!url) throw new Error('No URL provided.');
	const csrf = await csrfToken(_tryNewCSRF);
	if (!csrf) return Promise.reject(new Error('No CSRF token available. Engage offline mode.'));
	url = typeof url === 'string' ? apiURL(url) : url;
	if (url.hostname === fakeURLBase.replace('https://', '')){
		url.hostname = import.meta.env.VITE_API_URL.replace('https://', '');
	}
	if (!('mode' in options)) {
		options.mode = 'cors';
	}
	if (!('credentials' in options)) {
		options.credentials = 'include';
	}
	if (!('headers' in options)) {
		options.headers = new Headers();
	}
	if (!(options.headers instanceof Headers)) {
		options.headers = new Headers(options.headers);
	}
	if ('method' in options && ['GET', 'DELETE'].includes(options.method.toUpperCase())) {
		if (options.method.toUpperCase() === 'GET') {
			options.headers.delete(csrf.headerName);
			url.searchParams.set(csrf.parameterName, csrf.token);
		} else {
			options.headers.set(csrf.headerName, csrf.token);
		}
	} else {
		if (url.searchParams.has('csrf')) {
			url.searchParams.delete('csrf');
		}
		if (!options.headers.has('Content-Type')) {
			options.headers.set('Content-Type', 'application/json');
		}
		options.headers.set(csrf.headerName, csrf.token);
	}
	if ('body' in options){
		if (typeof options.body === 'object'){
			options.body = JSON.stringify(options.body);
		}
		try{
			if (JSON.parse(options.body)){
				(options.headers as Headers).set('Content-Type', 'application/json');
			}
		} catch (e){
			0;
		}
	}
	return fetchFn(url, options).then(resp => {
		if (resp.status === 403){
			if (!_tryNewCSRF) return apiFetch({url, options, fetch: fetchFn, _tryNewCSRF: true});
			//else goto('/');
		}
		return resp;
	});
}

export function apiURL(urlStr: string): URL {
	return new URL(urlStr, import.meta.env.VITE_API_URL);
}

export function getMoodUrlParams(isoDate: ISO8601|null = null): URLSearchParams {
	const params = new URLSearchParams();
	if (!isoDate) {
		isoDate = new Date().toISOString().split('T')[0];
		const date = new Date(isoDate);
		const offset = date.getTimezoneOffset() * 60 * 1000;
		isoDate = new Date(date.getTime() + offset).toISOString().split('T')[0];
	}
	params.set('start', isoDate);
	params.set('end', isoDate);
	params.set('page', (1).toString());
	params.set('pageSize', (1).toString());
	return params;
}