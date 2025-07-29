import type { ISO8601, UUID } from '$lib';
import Dexie from 'dexie';
export const _tableName = 'moods';

export type TableDefinitions = { [key: number]: { schema: string, migrations?: ((db: Dexie) => Promise<number>)[] } };

export interface IMood {
	id: number;
	date: ISO8601;
	rating: number;
	user_id: UUID;
}

export const _tableDefs: TableDefinitions = {
	1: {
		schema: "id,date,rating"
	},
	2: {
		schema: "id,date,rating,user_id"
	}
};