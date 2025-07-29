import moodStore from "$lib/stores/moodStore";
import MoodModel from "$lib/models/mood";
import { browser } from "$app/environment";

/**
 * Sort moods by date in descending order and returns a subset up to the limit
 * @param {MoodModel[]} moodRecords - The array of mood records to sort
 * @param {number} limit - Limit the size of the array
 */
function sortMoods(moodRecords: MoodModel[], limit: number = 1):  MoodModel[]{
	return moodRecords.sort((a, b) => a.date < b.date ? 1 : -1).slice(0, limit);
}

export async function load({ fetch, limit = 31 }: { fetch?: Function, limit?: number} = {}): Promise<{ moods: MoodModel[] }> {
	if (!browser) {
		console.warn('load attempted on server, but only allowed on client');
		return { moods: [] };
	}
	//if (get(session).loggedIn !== true) goto('/login');
	const pageSize = 31;
	const page = 1;
	return moodStore.findAll({ page, pageSize }).then(firstMoods => {
		if (firstMoods.length < pageSize && page > 1){
			return moodStore.findAll({ page: page - 1, pageSize }).then(secondMoods => ({
				moods: [...secondMoods, ...firstMoods]
			}));
		}
		return { moods: firstMoods };
	}).then(({moods}) => {
		const ret = {
			moods: sortMoods(moods, limit)
		};
		console.log('moods', ret);
		return ret;
	});
	/* const moodRecords: MoodModel[] = await moodStore.findAll({ page: 1, pageSize: 10 }).catch(err => {
		console.error(err);
		return [];
	});
	return { moods: sortMoods(moodRecords, limit) }; */
}