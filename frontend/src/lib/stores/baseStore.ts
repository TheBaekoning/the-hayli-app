import { writable, readonly } from "svelte/store";
import { apiFetch } from "../api-fetch";
import { browser } from "$app/environment";
import { db, devWarning } from "$lib/db/db";
import type { Dexie } from "dexie";
import BaseModel from "$lib/models/base";
import { session, type Session } from '$components/session/session.store';
import type { User } from "$lib";

type AnyObj = {[key: string]: any};
export type ServerPayload = {
	data: AnyObj|null;
	meta: AnyObj|null;
	error: AnyObj|null;
}

export interface Store {
	name: string
	sessionSubscription: (session: Session) => void
}

/**
 * @class BaseStore - A base class for creating stores that interact with a database and/or an API.
 * @property {String} name - The name of the store.
 * @property {Object} _db - The Dexie database instance.
 * @property {Number} _dbVersion - The version of the Dexie database.
 */
export default class BaseStore implements Store {
	_db: Dexie = db.db;
	/**
	 * Table name for the store. 
	 */
	name: string = "base";
	sessionSubscription(session: Session){
		this._user = session.loggedIn ? (session.user as User) : null;
	}
	constructor() {
		if (!browser){
			devWarning();
		}
		session.subscribe(this.sessionSubscription.bind(this));
	}

	_model = BaseModel;

	/** 
	 * Writable store for the data.
	 * @package 
	 */
	_data = writable({});
	/**
	 * Writable store for the loading state.
	 * @package
	 */
	_loading = writable(false);
	/**
	 * Writable store for the error state.
	 * @package
	 */
	_error = writable(null);
	/**
	 * The user object.
	 * @package
	 */
	_user: User | null = null;
	/**
	 * Read-only store for the data.
	 */
	data = readonly(this._data);
	/**
	 * Read-only store for the loading state.
	 */
	loading = readonly(this._loading);
	/**
	 * Read-only store for the error state.
	 */
	error = readonly(this._error);

	/**
	 * An object to store the last time a query was made to the server.
	 * Keys are URLs and values are timestamps.
	 * @package
	 */
	_historicalQueries: {[key: string]: number} = {};

	/** 
	 * @type {Object[][]} - An array of versions which have an array of table schemas to be instantiated by Dexie.
	 * @see https://dexie.org/docs/Version/Version.stores()
	 */
	static _tableDefs = [
		// version 1
		[
			{
				[this.name]: "id"
			}
		]
	]

	/**
	 * Checks if the data we have for the URL is stale.
	 * @param {object} checkStaleArgs
	 * @param {boolean} checkStaleArgs.forceStale - Whether to force the data to be fetched from the server. Defaults to false.
	 * @param {string} checkStaleArgs.url - The URL to check.
	 * @package
	 */
	checkStale({forceStale = false, url}: {forceStale: boolean, url: string}): boolean {
		const now = Date.now();
		const staleTime = 1000 * 60 * 5; // 5 minutes
		const lastQuery = this._historicalQueries[url];
		if (forceStale || !lastQuery || now - lastQuery > staleTime) {
			return true;
		}
		return false;
	}

	/**
	 * Finds a single record in the database or fetches it from the server.
	 * @param {Object} findArgs
	 * @param {number} findArgs.id - The ID of the data to find.
	 * @param {String} findArgs.url - The URL to fetch the data from.
	 * @param {Boolean} findArgs.forceStale - Whether to force the data to be fetched from the server. Defaults to false.
	 * @returns {Promise<ServerPayload>} - A promise that resolves to the data returned from either the database or the server.
	 */
	async baseFind({ id, url, forceStale = false }: { id: number, url: string, forceStale?: boolean }): Promise<ServerPayload|boolean> {
		// If the data is stale, fetch it from the server
		if (this.checkStale({ forceStale, url })){
			const data = await this.get({ url }).catch(err => {
				console.error(err);
				return false;
			});
			if (data) return data;
		}
		function getError(err){
			console.error(err);
			return Promise.reject(err);
		}
		// Try to get the data from the db
		try{
			return this._db[this.name].get(id).then(item => item, err => {
				// If the data is not in the db, fetch it from the server
				console.error(err);
				return this.get({ url }).catch(getError);
			});
		} catch (e) {
			// If there is an error, especially from trying to get a non-existent table, fetch it from the server
			console.error(e);
			return this.get({ url }).catch(getError);
		}
	}
	/**
	 * Finds all records in the database or fetches them from the server.
	 * @param {Object} findAllArgs
	 * @param {String} findAllArgs.url - The URL to fetch the data from.
	 * @param {number} findAllArgs.page - The page number to fetch.
	 * @param {number} findAllArgs.pageSize - The number of items per page to fetch.
	 * @param {Boolean?} findAllArgs.forceStale - Whether to force the data to be fetched from the server. Defaults to false.
	 * @param {String?} findAllArgs.orderBy - The field to order the data by. Defaults to 'id'.
	 * @param {Boolean?} findAllArgs.reverseOrder - The direction to order the data by. Defaults to false.
	 * @param {Function?} findAllArgs.dbFunc - The callback to get the data from the database.
	 * @returns {Promise<ServerPayload>} - A promise that resolves to the data returned from either the database or the server.
	 */
	async baseFindAll({ url, page, pageSize, forceStale = false, orderBy = 'id', reverseOrder = false, dbFunc }: { url: string, page: number, pageSize: number, forceStale?: boolean, orderBy?: string, reverseOrder?: boolean, dbFunc?: Function }): Promise<ServerPayload|BaseModel[]> {
		if (this.checkStale({ url, forceStale })){
			return this.get({ url });
		}
		try {
			if (typeof dbFunc === 'function') {
				return dbFunc({ page, pageSize });
			}

			let collection = this._db[this.name].orderBy(orderBy);
			if (reverseOrder) collection = collection.reverse();
			collection = collection.offset((page - 1) * pageSize).limit(pageSize);

			return collection.toArray();
		} catch (e) {
			console.error(e);
			return this.get({ url });
		}
	}

	/**
	 * Fetches data from the server.
	 * @param {Object} getArgs
	 * @param {String} getArgs.url - The URL to fetch the data from.
	 * @returns {Promise<ServerPayload>} - A promise that resolves to the data returned from the server.
	 */
	async get({ url }: { url: string }): Promise<ServerPayload>{
		return apiFetch({...arguments[0]}).then(res => {
			this._historicalQueries[url] = Date.now();
			return res.json();
		});
	}

	/**
	 * Persist the record to local IndexedDB.
	 * @param {{record: BaseModel}} - The record to save to IndexedDB
	 * @return {Promise<boolean>} - A promise that resolves to the success of the operation.
	 * @throws {Error} - Throws error if the db save fails.
	 */
	async persistLocally({ record }: { record: BaseModel }): Promise<boolean>{
		if (browser && record.id) {
			return this._db[this.name].put(record._rawInstance).then(() => {
				record._onDatabase = true;
				return true;
			}).catch(err => {
				console.error(err);
				throw new Error(err);
			});
		} else if (browser) {
			console.error('No ID to save to database', record);
		};
		return false;
	}

	/**
	 * Saves a record to the database.
	 * @param {object} saveArgs
	 * @param {BaseModel} saveArgs.record - The model to save.
	 * @param {string} saveArgs.url - The URL to save the data to.
	 * @param {function} saveArgs.getIdCallback - The callback to get the ID of the record.
	 * @param {boolean?} saveArgs.forceLocalPersist - Force saving to the local database.
	 * @returns {Promise<boolean>} - A promise that resolves when the data is saved.
	 * @throws {Error} - Throws an error if the data cannot be saved.
	 */
	async baseSave({ record, url, getIdCallback, forceLocalPersist = false }: { record: BaseModel, url: string, getIdCallback?: Function, forceLocalPersist?: boolean }): Promise<boolean>{
		if (record._modified){
			const serialized = record.serializeToAPI();
			if (!serialized) throw new Error('Record cannot be serialized');
			const body = JSON.stringify(serialized);
			await apiFetch({ url, options: {
				method: record._onServer ? 'PATCH' : 'POST',
				body
			}}).then(res => {
				if (res.ok){
					if (record._onServer) return;
					record._onServer = true;
					if (typeof getIdCallback === 'function'){
						return getIdCallback().then(newId => {
							console.log('newId', newId);
							record.id = newId;
						});
					}
					return;
				}
				return Promise.reject(new Error(res.statusText));
			}).catch(err => {
				console.error(err);
				throw new Error(err);
			});
		}
		
		if (record._modified || forceLocalPersist){
			record._modified = false;
			return this.persistLocally({record})
		}
		
		console.warn('No changes to save', record);
		return false;
	}

	/**
	 * Saves all records to the database.
	 * @param {Object} saveAllArgs
	 * @param {BaseModel[]} saveAllArgs.records - The models to save.
	 * @param {string} saveAllArgs.url - The URL to save the data to.
	 * @param {Boolean} saveAllArgs.forceLocalPersist - Persist to local database regardless if the records are modified.
	 * @returns {Promise<boolean[]>} - A promise that resolves to an array of booleans indicating the success of the operation.
	 * @throws {Error} - Throws an error if the data cannot be saved.
	 * @throws {Error} - Throws an error if there are no records to save.
	 */
	async baseSaveAll({ records, url, forceLocalPersist = false }: { records: BaseModel[], url: string, forceLocalPersist?: boolean }): Promise<boolean[]>{
		if (!records.length) throw new Error('No records to save');
		const saves: Array<Promise<boolean>> = [];
		records.forEach(record => {
			if (record._modified || forceLocalPersist){
				saves.push(this.baseSave({ record, url, forceLocalPersist }));
			}
		});
		return Promise.all(saves);
		/* if (!browser) return devWarning();
		// May need to wrap this in a transaction
		return this._db[this.name].bulkPut(records).catch(err => {
			console.error(err);
			throw new Error(err);
		}); */
	}

	/**
	 * Deletes a record from the database and the server
	 * @param {{record: BaseModel, url: string|URL}} deleteArgs
	 */
	async baseDelete({ record, url }: { record: BaseModel, url: string|URL }): Promise<void>{
		if (!record.id) throw new Error('No ID to delete');
		await apiFetch({ url, options: {
			method: 'DELETE'
		}}).then(res => {
			if (res.ok){
				record._onServer = false;
				return this._db[this.name].delete(record.id).catch(err => {
					throw new Error(err);
				});
			}
			return Promise.reject(new Error(res.statusText));
		}).catch(err => {
			console.error(err);
			throw new Error(err);
		});
	}
}