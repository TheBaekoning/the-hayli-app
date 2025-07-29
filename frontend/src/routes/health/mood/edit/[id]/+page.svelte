<svelte:options runes={true} />
<script lang="ts">
	import MoodForm from '$components/mood-form/mood-form.svelte';
	import MoodModel from '$lib/models/mood';

	let { data }: { data: Record<string, MoodModel|null> } = $props();
</script>
<main class="mood-edit">
	{#if data.mood}
		<header>
			<h1>Edit: {data.mood.date.toLocaleDateString('en-us', { month: 'long', day: 'numeric', year: 'numeric'})}</h1>
		</header>
		<MoodForm moodEntry={data.mood} />
	{:else}
		<p>Loading…</p>
	{/if}
</main>