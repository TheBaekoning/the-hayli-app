import { session } from "$components/session/session.store";

export function setup(id, deserialize = false, record = null){
	session.subscribe((sess) => {
		if (!this._user_id && sess.loggedIn && sess.user !== 'loading') this._user_id = sess.user.uuid;
	});
	if (deserialize && record){
		this.deserializeFromAPI(record);
		return;
	}
	if (id) this.id = id;
}
/**
 * Creates a new model with a proxy to help with store methods/management
 */
export function initialize<T extends BaseModel>(cls: new() => T , ...a): T {
	/* console.log(cls);
	console.log(...a); */
	// @ts-ignore
	return new Proxy(new cls(...a), {
		set(model, prop, value){
			if ((prop !== '_modified' && prop !== '_user_id') && model[prop] !== value) model._modified = true;
			model[prop] = value;
			return true;
		}
	});
}
export default class BaseModel {
	constructor({id = null, deserialize = false, record = null, runSetup = true} = {}){
		if (runSetup) setup.bind(this, id, deserialize, record)();
	}
	id: number|null = null;
	/** Whether or not the object has been modified since the last save to server. */
	_modified: boolean = false;
	/** Whether or not the object has been saved to the server. */
	_onServer: boolean = false;
	/** Whether or not the object has been saved to the database. */
	_onDatabase: boolean = false;
	/** Associated User */
	_user_id: string|null = null;

	get _rawInstance(): object{
		return Object.fromEntries(Object.entries(this).filter(([key]) => !key.startsWith('_')).concat([['user_id', this._user_id]]));
	}

	/** 
	 * When returning an object to the API, serialize it to the API's format.
	*/
	serializeToAPI(): {id: number}|any {
		return { id: this.id };
	}

	/**
	 * When receiving an object from the API, deserialize it from the API's format.
	 * @param {Object} data - An object containing the object's data
	 * @param {number} data.id - The ID of the object
	 */
	baseDeserializeFromAPI({ id }: { id: number }) {
		this.id = id;
		this._modified = false;
		this._onServer = true;
	}
}