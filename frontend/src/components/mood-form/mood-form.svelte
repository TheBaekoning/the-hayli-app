<svelte:options runes={true} />
<script lang="ts">
	import Input from '$components/inputs/input.svelte';
	import { getMoodUrlParams } from '$lib/api-fetch';
	import { goto } from '$app/navigation';
	import MoodModel from '$lib/models/mood';
	import moods from '$lib/stores/moodStore';
	import MoodRating from "./rating.svelte";
	import { getLocalDate, type ISO8601 } from '$lib';
	import { initialize } from '$lib/models/base';

	type props = {
		moodEntry?: MoodModel|null,
		moodRating?: number|null,
		moodDate?: ISO8601|null,
		notes?: string|null,
		create?: boolean
	};

	let { moodEntry = null, moodRating = null, moodDate = null, notes = null, create = false }: props = $props();

	let error: string|null = $state(null);
	let success: string|null = $state(null);
	let submitting: boolean = $state(false);
	let created: boolean = $state(false);

	let allowEdit = $derived((moodEntry && !create) || (!moodEntry && create));
	let suppliedDate = $derived(moodEntry?.date || moodDate || '');
	let suppliedNotes = $derived(moodEntry?.notes || notes);

	let initialDateValue: ISO8601 = $state('');
	let chosenValue: number = $state(
		(typeof moodEntry !== 'undefined' && moodEntry !== null ?
			moodEntry.rating :
			moodRating) || 
		3
	);
	$effect(() => {
		// Adjust supplied date time to the local date time
		// The date input will display the date in the local timezone
		if (typeof suppliedDate == 'string' && suppliedDate.length > 0) {
			const date = getLocalDate(suppliedDate);
			initialDateValue = date.toISOString().split('T')[0];
		} else if (suppliedDate instanceof Date) {
			const localDate = getLocalDate(suppliedDate.toISOString());
			initialDateValue = localDate.toISOString().split('T')[0];
		}
	});

	async function submitMoodEntry(e) {
		e.preventDefault();

		const record: MoodModel = create ? initialize(MoodModel) : moodEntry;
		
		record.date = new Date((document.querySelector('[name="moodDate"]') as HTMLInputElement).value);

		const notes = (document.querySelector('[name="notes"]') as HTMLTextAreaElement).value;
		record.notes = notes;
		
		const rating = chosenValue;
		record.rating = rating;

		// This won't appear to persist if editing an existing mood entry.
		// By setting the moodEntry's properties, the form will persist the values.
		if (moodEntry){
			moodEntry.date = record.date;
			moodEntry.notes = notes;
			moodEntry.rating = rating;
		}
		
		// Keep below the value collecting segment or else the doc.query won't find anything.
		submitting = true;
		error = null;
		success = null;
		
		moods.save({ record }).then(successfullySaved => {
			if (successfullySaved) {
				success = create ? 'Mood entry created' : 'Mood entry updated';
				created = create;
			} else {
				error = 'There was an error submitting the mood entry';
				console.error(error);
			}
		}, err => {
			error = err.message;
		}).finally(() => {
			submitting = false;
		});
	}

	function gotoCreateMood(val: string): void {
		const url = new URL('/health/mood/create', window.location.origin);
		url.searchParams.set('moodDate', val);
		goto(url, { replaceState: true });
	}

	function checkMoodExists(e): void {
		const val = e.target.value;
		const params = getMoodUrlParams(val);
		moods.findAll({params}).then(moods => {
			if (moods.length === 0) {
				gotoCreateMood(val);
				return;
			}
			goto(`/mood/edit/${moods[0].id.toString()}`, { replaceState: true });
		}, err => {
			gotoCreateMood(val);
		});
	}

</script>

{#if error}
	{error}
{/if}
{#if success}
	<p>{success}. <a href="/health/mood">View moods.</a></p>
{/if}
{#if submitting}
	<div class="loading"><span class="sr-visible">Submitting mood…</span></div>
{:else}
	{#if !created}
		<form onsubmit={submitMoodEntry}>
			<Input label="Date" name="moodDate" type="date" required={true} value={initialDateValue} on:change={checkMoodExists} />
			{#if allowEdit}
				<MoodRating bind:chosenValue />
				<Input type="textarea" label="Notes" name="notes" required={false} value={suppliedNotes} maxlength={280} />
			{:else}
				<p>You have already entered a mood for this date. Please select a different date or you may edit the mood</p>
			{/if}
			<button disabled={!allowEdit}>Submit</button>
		</form>
	{/if}
{/if}

<style lang="scss">
	@media (min-width: 1000px){
		form{
			display: grid;
			grid-template: "rating date" auto 
						   "rating notes" auto
						   "rating submit" auto
						   "rating .     " / 1fr 3fr;
			gap: 30px 60px;
			:global(.input-group){
				grid-area: date;
				&:has(textarea){
					grid-area: notes;
				}
			}
			> button{
				grid-area: submit;
			}
			:global(.mood-rating-container){
				grid-area: rating;
			}
		}
	}
</style>