import moods from '$lib/stores/moodStore';
import { browser } from '$app/environment';
import type MoodModel from '$lib/models/mood';

export const prerender: boolean = false;

export async function load({ fetch, params: { id } }: { fetch: Function, params: { id: string } }): Promise<{ mood: MoodModel | null | Error}> {
	if (!browser) return { mood: null };
	return moods.find({ id: parseInt(id) }).then(mood => {
		return { mood };
	}, err => {
		console.error(err);
		return { mood: err };
	});
}