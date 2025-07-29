import BaseStore, { type ServerPayload } from './baseStore';
import { apiURL, apiFetch } from '../api-fetch';
import MoodModel from '$lib/models/mood';
import { _tableName } from '$lib/db/moods';
import { initialize } from '$lib/models/base';

export class MoodsStore extends BaseStore {
	name: string = _tableName;
	constructor(){
		super();
	}

	/**
	 * Gets the newest record ID
	 */
	static getNewestRecordId(): Promise<number>{
		const url = apiURL("/mood");
		url.searchParams.append("page", (1).toString());
		url.searchParams.append("pageSize", (1).toString());
		return apiFetch({url}).then(async resp => {
			if (resp.ok){
				const payloadObj: ServerPayload = await resp.json();
				const lastPage = payloadObj.data?.maxPages;
				url.searchParams.set("page", lastPage);
				const lastPageData: ServerPayload = await apiFetch({url}).then(resp => resp.json());
				if (lastPageData.data?.moodEntries?.length) return lastPageData.data.moodEntries[0].id;
			}
			return null;
		});
	}

	async _findAllDBFunc({ page, pageSize, start = null, end = null }: { page: number, pageSize: number, start?: Date, end?: Date }): Promise<MoodModel[]>{
		const collection = this._db[this.name]
			.where('date')
			.between(start || new Date(0), end || new Date())
			.and(mood => mood.user_id === this._user.uuid)
			.reverse()
			.offset((page - 1) * pageSize)
			.limit(pageSize);
		return collection.toArray();
	}

	/**
	 * Finds a single mood by ID
	 * @param {Object} findArgs
	 * @param {number} findArgs.id - The ID of the mood to find
	 * @returns {Promise} - A promise that resolves to the mood
	 * @throws {Error} - Throws an error if no mood is found
	 */
	async find({ id }: { id: number }): Promise<MoodModel|Error> {
		this._loading.set(true);
		const url = `/mood/${id}`;
		return this.baseFind({ id, url }).then(async payloadObj => {
			this._loading.set(false);
			const serverPayloadObj = payloadObj as ServerPayload;
			if (serverPayloadObj?.data?.moodEntries?.length){
				const record = initialize(MoodModel, { deserialize: true, record: serverPayloadObj.data.moodEntries[0] });
				this.save({record});
				return record;
			}
			if (payloadObj instanceof MoodModel) return payloadObj;
			this._error.set('No mood found');
			return Promise.reject(new Error('No mood found'));
		});
	}

	/**
	 * Finds all moods
	 * @param {Object} findAllArgs
	 * @param {number=1} findAllArgs.page - The page number to fetch
	 * @param {number=10} findAllArgs.pageSize - The number of items per page to fetch
	 * @param {ISO8601=null} findAllArgs.start - The ISO 8601 date string to start fetching from
	 * @param {ISO8601=null} findAllArgs.end - The ISO 8601 date string to end fetching from
	 * @param {URLSearchParams=null} findAllArgs.params - URLSearchParams of page, pageSize, start, and/or end. This takes precedence over the other properties.
	 * @returns {Promise<MoodModel[]>} - A promise that resolves to the moods
	 * @throws {Error} - Throws an error if no moods are found.
	 */
	async findAll({ page = 1, pageSize = 10, start = null, end = null, params = null }: { page?: number, pageSize?: number, start?: Date, end?: Date, params?: URLSearchParams } = {}): Promise<MoodModel[]> {
		this._loading.set(true);
		const url = apiURL("/mood");

		url.searchParams.append("page", page.toString());
		url.searchParams.append("pageSize", pageSize.toString());
		if (start) url.searchParams.append("start", start.toISOString().split('T')[0]);
		if (end) url.searchParams.append("end", end.toISOString().split('T')[0]);

		if (params?.size){
			for (const [key, value] of params){
				url.searchParams.set(key, value)
			}
		}

		return this.baseFindAll({ url: url.toString(), page, pageSize, orderBy: 'date', reverseOrder: true, dbFunc: this._findAllDBFunc.bind(this) }).then(async (payloadObj: ServerPayload | MoodModel[]) => {
			this._loading.set(false);
			if (Array.isArray(payloadObj)) {
				const allMoods = payloadObj.map((mood) => initialize(MoodModel, { deserialize: true, record: mood }));
				return allMoods;
			}
			const serverPayloadObj = payloadObj as ServerPayload;
			if ('moodEntries' in serverPayloadObj.data && Array.isArray(serverPayloadObj.data.moodEntries) && serverPayloadObj.data.moodEntries.length){
				const moods = serverPayloadObj.data.moodEntries.map(mood => {
					const someMood = initialize(MoodModel, { deserialize: true, record: mood }) as MoodModel;
					return someMood;
				}) as MoodModel[];
				// Set the user ID
				if (this._user?.uuid){
					moods.forEach(mood => mood._user_id = this._user.uuid);
				}
				this.baseSaveAll({records: moods, url: '/moods/', forceLocalPersist: true});
				return moods;
			}

			this._error.set('No moods found');
			return [];
		});
	}

	/**
	 * Saves a mood to the server and database
	 * @param {Object} saveArgs
	 * @param {MoodModel} saveArgs.record - The mood to save
	 * @param {Boolean=} saveArgs.forceLocalPersist - Persist the record to the database regardless if it's modified.
	 */
	async save({ record, forceLocalPersist = false }: { record: MoodModel, forceLocalPersist?: boolean }): Promise<boolean> {
		const url = apiURL( "/mood" ).toString();
		return super.baseSave({ record, url, getIdCallback: MoodsStore.getNewestRecordId, forceLocalPersist });
	}

	async delete({ record }: { record: MoodModel }): Promise<void> {
		const url = apiURL(`/mood/${record.id}`);
		return super.baseDelete({ record, url });
	}

}

const moods = new MoodsStore();

export default moods;