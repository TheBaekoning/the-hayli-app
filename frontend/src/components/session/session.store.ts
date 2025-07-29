import { writable, readonly, type Writable, type Readable, get } from 'svelte/store';
import { apiFetch } from '$lib/api-fetch';
import { browser } from '$app/environment';
import { Preferences } from '@capacitor/preferences';
import type { APIResponse, SpringBootErrorResponse, User, UserAPIResponse, UserProfile, UserProfileAPIResponse } from '$lib';

export type Session = {
	userProfile: UserProfile | null | "loading";
	user: User | null | "loading";
	loggedIn: boolean | "loading";
};

const blankUser: Session = {
	userProfile: null,
	user: null,
	loggedIn: false
};

const _session: Writable<Session> = writable({userProfile: "loading", user: "loading", loggedIn: "loading"});
export const session: Readable<Session> = readonly(_session);

function getUserData(): Promise<[UserAPIResponse|SpringBootErrorResponse, UserProfileAPIResponse|SpringBootErrorResponse]> {
	const userReq: Promise<Response> = apiFetch({url: '/user'});
	const profileReq: Promise<Response> = apiFetch({url: '/user/profile'});
	return Promise.all([userReq, profileReq]).then(([ur, pr]) => Promise.all([ur.json(), pr.json()]));
}

export const lsKey = {key: 'sessionInfo'};

_session.subscribe(value => {
	if (browser){
		Preferences.set({...lsKey, value: JSON.stringify(value)});
	}
});

export async function restore(email?: string): Promise<Readable<Session>> {
	if (!browser) console.warn('restore attempted on server, but only allowed on client')
	if (browser){
		let {value: sessionInfo}: {value: string|Session} = await Preferences.get(lsKey);
		if (sessionInfo) {
			try {
				sessionInfo = JSON.parse(sessionInfo) as Session;
				if ((sessionInfo.userProfile !== null && sessionInfo.userProfile !== 'loading') && sessionInfo.loggedIn === true){
					_session.set(sessionInfo);
					return session;
				}
			} catch (e){
				console.error(e);
			}
		}
		const [user, userProfile] = await getUserData();
		if (typeof (userProfile as SpringBootErrorResponse)?.error === 'undefined' || typeof (userProfile as SpringBootErrorResponse)?.error !== 'string') {
			_session.set({
				userProfile: (userProfile as UserProfileAPIResponse).data,
				user: (user as UserAPIResponse).data,
				loggedIn: true
			});
			return session;
		}
	}
	_session.set(blankUser);
	return session;
}

export type LoginResponse = APIResponse & {
	data: {loginSuccess: boolean}
	error: {message: string} | null,
	meta: null
};

export async function login({ email, password }: { email: string, password: string }): Promise<Readable<Session>>{
	if (!browser) {
		console.warn('login attempted on server, but only allowed on client');
		_session.set(blankUser);
		return session;
	}
	let body: {[k: string]: string}|string = { email, password };
	body = JSON.stringify(body);
	const res = await apiFetch({
		url: '/login', 
		options: {
			method: 'POST',
			body
		}
	});
	if (res.ok) {
		await restore(email);
		return session;
	} else {
		_session.set(blankUser);
		return res.json().then((err: LoginResponse) => Promise.reject(err));
	}
}

export async function logout() {
	Preferences.remove(lsKey);
	_session.set(blankUser);
	return session;
}

export async function updateProfile(profile: { firstName: string, lastName: string, email?: string }): Promise<Readable<Session>> {
	if (!browser) {
		console.warn('updateProfile attempted on server, but only allowed on client');
		return session;
	}
	const res = await apiFetch({
		url: '/user/profile',
		options: {
			method: 'PATCH',
			body: JSON.stringify(profile)
		}
	});
	if (res.ok) {
		const sess = get(session);
		_session.update(s => ({...sess, userProfile: {...(sess.userProfile as UserProfile), ...profile}}));
		return session;
	} else {
		return res.json().then((err: APIResponse) => Promise.reject(err));
	}
}