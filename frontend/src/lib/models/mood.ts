import BaseModel, { setup } from "./base";
import { getLocalDate, getLocalDateString, type ISO8601, type UUID } from '$lib';
type MoodAPIObject = {
	moodDate: ISO8601;
	moodRating: number;
	notes: string | null;
	id?: number;
}
type MoodIDBObject = {
	id: number;
	date: ISO8601;
	rating: number;
	notes: string | null;
	user_id: UUID;
}
/**
 * A class for creating mood objects.
 */
export default class MoodModel extends BaseModel {
	date: Date | null;
	/** The rating of the mood. Must be an integer between 1 and 5. */
	rating: number | null;
	/**  The notes for the mood. Optional. */
	notes: string | null;
	constructor({ id = null, deserialize = false, record = null, date = null, rating = null, notes = null }: { id?: number, deserialize?: boolean, record?: MoodAPIObject|MoodIDBObject, date?: Date, rating?: number, notes?: string} = {}) {
		super({ runSetup: false });
		setup.bind(this, id, deserialize, record)();
		if (this.id && date) this.date = date;
		if (this.id && rating) this.rating = rating;
		if (this.id && notes) this.notes = notes;
	}

	static ERatingText = Object.freeze({
		1: "Awful",
		2: "Meh",
		3: "Balanced",
		4: "Energized",
		5: "Ecstatic"
	});

	get ratingText() {
		return MoodModel.ERatingText[this.rating];
	}

	/**
	 * When returning a mood to the API, serialize it to the API's format.
	 * @returns {Object} - An object containing the mood's data. Shape depends on whether the mood is on the server.
	 */
	serializeToAPI(): {moodEntries: MoodAPIObject[]}|MoodAPIObject {
		if (this.date && this.rating) {
			const data: MoodAPIObject = {
				moodDate: this.date.toISOString(),
				moodRating: this.rating,
				notes: this.notes ?? null
			};
			// Don't serialize the id if it's not set
			if (this.id) data.id = this.id;

			if (this._onServer) return data;

			return { moodEntries: [data] };
		}
	}

	/**
	 * When receiving a mood from the API, deserialize it from the API's format.
	 * @param {Object} record - An object containing the mood's data
	 * @param {number} record.id - The ID of the mood
	 * @param {String} record.moodDate - The date of the mood
	 * @param {Integer} record.moodRating - The rating of the mood
	 * @param {String} record.notes - The notes for the mood
	 */
	deserializeFromAPI(obj: MoodAPIObject|MoodIDBObject): void {
		const id = obj.id;
		super.baseDeserializeFromAPI({ id });
		const notes = obj?.notes ?? null;
		const isIDB = 'date' in obj;
		const date = isIDB ? obj.date : obj.moodDate;
		const rating = isIDB ? obj.rating : obj.moodRating;
		if (id && date && rating) {
			this.date = new Date(date);
			this.rating = rating;
			this.notes = notes;
		} else {
			throw new Error('Missing required fields for mood');
		}
	}
}