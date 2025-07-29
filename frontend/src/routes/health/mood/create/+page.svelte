<svelte:options runes={true} />
<script lang="ts">
	import MoodForm from "$components/mood-form/mood-form.svelte";
	import { page } from '$app/stores';
	import { browser } from "$app/environment";
	import { fromStore } from "svelte/store";

	const p = fromStore(page);

	let moodDate = $derived(browser ? p.current.url.searchParams.get('moodDate') : null);
	let moodRating = $derived(browser ? parseInt(p.current.url.searchParams.get('moodRating')) : null);
	let notes = $derived(browser ? p.current.url.searchParams.get('notes') : null);
</script>

<main>
	<h1>Create Mood Entry</h1>
	<MoodForm create={true} {moodDate} {moodRating} {notes} />
</main>