import Dexie, { type EntityTable } from 'dexie';
import { 
	_tableDefs as moodTableDefs,
	_tableName as moodTableName,
	type IMood,
	type TableDefinitions
 } from '$lib/db/moods';
import MoodModel from '$lib/models/mood';
import { browser, dev } from '$app/environment';

export function devWarning(){
	if (dev) console.warn('Dexie not available in this environment');
}

class DB {
	db: Dexie;
	constructor(){
		if (!browser){
			devWarning();
			return;
		}
	
		this.db = new Dexie('Hayli') as Dexie & {
			[moodTableName]: EntityTable<IMood, 'id'>;
		};
		const tableDefs = {};

		function addTableDef({name, tableDefs: $tableDefs}: {name: string, tableDefs: TableDefinitions}){
			for(const ver in $tableDefs){
				const version = parseInt(ver);
				if (!(version in tableDefs)) tableDefs[version] = { schemas: {}, migrations: [] };
				tableDefs[version].schemas[name] = $tableDefs[version].schema;
				tableDefs[version].migrations = $tableDefs[version].migrations;
			}
		}

		const staticTableDefs: {name: string, tableDefs: TableDefinitions}[] = [ 
			{name: moodTableName, tableDefs: moodTableDefs}
		];

		for(const staticTableDef of staticTableDefs){
			addTableDef(staticTableDef);
		}
		for(let i = 0; i < Object.keys(tableDefs).length; i++){
			const version = i + 1;
			let ver = this.db.version(version).stores(tableDefs[version].schemas);
			if (Array.isArray(tableDefs[version].migrations) && tableDefs[version].migrations.length){
				for(let j = 0; j < tableDefs[version].migrations.length; j++){
					ver = ver.upgrade(tableDefs[version].migrations[j]);
				}
			}
		}
		this.db.open().then(() => {
			
		}, err => {
			console.error(err);
		});

		/* this.db.delete().then(() => {
			console.log('Database "deleted"');
		}, err => {
			console.error(err);
		}) */
	}
}

export const db = new DB();